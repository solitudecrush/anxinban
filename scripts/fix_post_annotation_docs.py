#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
删除“注解行之后、方法声明之前”的错位通用 Javadoc。

典型错误模式：
    @PostMapping("/login")
        /**
         * login 方法。
         */
    public ApiResponse<LoginResponse> login(...) { ... }

本脚本会识别这种模式（注解行之后紧跟 Javadoc，再紧跟方法声明），
并将该 Javadoc 块删除。如果该 Javadoc 块之前不存在注解，则保留（可能是正常注释）。
"""

import re
import sys
from pathlib import Path
from typing import List, Tuple

ANNOTATION_PATTERN = re.compile(r"^\s*@[A-Za-z]")
METHOD_PATTERN = re.compile(r"^\s*(public|protected|private)\s+[\w<>,?\s]+\s+\w+\s*\(")
CLASS_PATTERN = re.compile(r"^\s*(public\s+)?(class|interface|enum)\s+")


def find_javadoc_blocks(lines: List[str]) -> List[Tuple[int, int]]:
    blocks = []
    i = 0
    while i < len(lines):
        if lines[i].strip().startswith("/**"):
            start = i
            while i < len(lines) and not lines[i].strip().endswith("*/"):
                i += 1
            end = i
            blocks.append((start, end))
        i += 1
    return blocks


def is_method_declaration(line: str) -> bool:
    return METHOD_PATTERN.match(line) is not None


def cleanup_file(path: Path) -> None:
    content = path.read_text(encoding="utf-8")
    lines = content.splitlines(keepends=True)
    blocks = find_javadoc_blocks(lines)

    remove_blocks = set()

    for b_start, b_end in blocks:
        # 条件 1：该 Javadoc 块之后是否紧接方法声明（中间只有空行）
        i = b_end + 1
        method_immediately_after = False
        while i < len(lines):
            stripped = lines[i].strip()
            if stripped == "":
                i += 1
                continue
            if is_method_declaration(lines[i]):
                method_immediately_after = True
            break

        if not method_immediately_after:
            continue

        # 条件 2：该 Javadoc 块之前是否紧接注解行（中间只有空行）
        j = b_start - 1
        annotation_immediately_before = False
        while j >= 0:
            stripped = lines[j].strip()
            if stripped == "":
                j -= 1
                continue
            if ANNOTATION_PATTERN.match(lines[j]):
                annotation_immediately_before = True
            break

        if annotation_immediately_before:
            remove_blocks.add((b_start, b_end))

    if not remove_blocks:
        print(f"[无变化] {path}")
        return

    remove_indices = set()
    for b_start, b_end in remove_blocks:
        for k in range(b_start, b_end + 1):
            remove_indices.add(k)
        # 删除其后的空行
        k = b_end + 1
        while k < len(lines) and lines[k].strip() == "":
            remove_indices.add(k)
            k += 1
        # 删除其前的空行
        k = b_start - 1
        while k >= 0 and lines[k].strip() == "":
            remove_indices.add(k)
            k -= 1

    new_lines = [line for idx, line in enumerate(lines) if idx not in remove_indices]
    new_content = "".join(new_lines)
    if new_content != content:
        path.write_text(new_content, encoding="utf-8")
        print(f"[已修复] {path}")
    else:
        print(f"[无变化] {path}")


def main(paths: List[str]) -> None:
    for p in paths:
        target = Path(p)
        if target.is_file() and target.suffix == ".java":
            cleanup_file(target)
        elif target.is_dir():
            for java_file in sorted(target.rglob("*.java")):
                cleanup_file(java_file)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法: python fix_post_annotation_docs.py <文件或目录> ...")
        sys.exit(1)
    main(sys.argv[1:])
