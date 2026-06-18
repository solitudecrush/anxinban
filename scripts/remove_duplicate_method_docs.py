#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
删除方法上重复生成的 Javadoc。

问题：原始代码在 @Mapping 注解前已有 Javadoc，自动化脚本又在注解后插入了通用 Javadoc。
本脚本会识别“注解前已有 Javadoc，注解后又出现 Javadoc”的模式，并删除后者。
"""

import re
import sys
from pathlib import Path
from typing import List

ANNOTATION_PATTERN = re.compile(r"^\s*@[A-Za-z]")
METHOD_PATTERN = re.compile(r"^\s*(public|protected|private|static|final|\s)*[\w<>,?\s]+\s+\w+\s*\(")
CLASS_OR_INTERFACE_PATTERN = re.compile(r"^\s*(public\s+)?(class|interface|enum)\s+")


def find_javadoc_blocks(lines: List[str]) -> List[tuple]:
    """返回所有 Javadoc 块的起止索引 [(start, end), ...]。"""
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
    """判断一行是否为方法声明（粗略）。"""
    return METHOD_PATTERN.match(line) is not None and not line.strip().startswith("@")


def cleanup_file(path: Path) -> None:
    content = path.read_text(encoding="utf-8")
    lines = content.splitlines(keepends=True)

    blocks = find_javadoc_blocks(lines)
    if not blocks:
        print(f"[无变化] {path}")
        return

    # 对每个方法，找到其声明行，然后向前看：保留最后一个在注解之前的 Javadoc，删除之后的 Javadoc
    remove_indices = set()

    for i, line in enumerate(lines):
        if is_method_declaration(line):
            # 向前查找本方法之前的所有 Javadoc 块和注解行
            preceding_blocks = []
            j = i - 1
            while j >= 0:
                stripped = lines[j].strip()
                if stripped == "":
                    j -= 1
                    continue
                # 如果遇到类声明或另一个方法声明，停止
                if CLASS_OR_INTERFACE_PATTERN.match(lines[j]) or is_method_declaration(lines[j]):
                    break
                # 记录 Javadoc 块
                for idx, (b_start, b_end) in enumerate(blocks):
                    if b_end == j:
                        preceding_blocks.append((b_start, b_end))
                        break
                j -= 1

            if len(preceding_blocks) >= 2:
                # 保留第一个（最靠近注解的），删除其余的
                # 因为 preceding_blocks 是从后往前收集的，第一个元素是离方法最近的
                for b_start, b_end in preceding_blocks[1:]:
                    for k in range(b_start, b_end + 1):
                        remove_indices.add(k)
                    # 同时删除该 Javadoc 块前后的空行
                    k = b_end + 1
                    while k < len(lines) and lines[k].strip() == "":
                        remove_indices.add(k)
                        k += 1
                    k = b_start - 1
                    while k >= 0 and lines[k].strip() == "":
                        remove_indices.add(k)
                        k -= 1

    if not remove_indices:
        print(f"[无变化] {path}")
        return

    new_lines = [line for idx, line in enumerate(lines) if idx not in remove_indices]
    new_content = "".join(new_lines)
    if new_content != content:
        path.write_text(new_content, encoding="utf-8")
        print(f"[已清理] {path}")
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
        print("用法: python remove_duplicate_method_docs.py <文件或目录> ...")
        sys.exit(1)
    main(sys.argv[1:])
