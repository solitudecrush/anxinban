#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
清理自动注释脚本产生的冗余、重复或低质量注释。

主要修复点：
1. 删除重复出现的类级 Javadoc（保留第一个）；
2. 删除方法上重复的 Javadoc（保留注解前的那一个，删除注解后的通用注释）；
3. 为依赖注入字段补充更有意义的描述；
4. 删除某些多余的空行。
"""

import re
import sys
from pathlib import Path
from typing import List

# 字段名 -> 中文描述（用于替换“字段含义待补充”）
INJECTED_DESC = {
    "Repository": "数据访问仓库，用于持久化操作",
    "Service": "业务服务实例",
    "Mapper": "数据映射器实例",
    "Properties": "配置属性实例",
    "Client": "客户端实例",
    "Template": "模板实例",
}


def describe_injected_field(name: str) -> str:
    """根据字段名后缀生成依赖注入字段的描述。"""
    for suffix, desc in INJECTED_DESC.items():
        if name.endswith(suffix):
            return desc
    return ""


def fix_field_comment(line: str) -> str:
    """如果字段注释是通用描述且字段名为依赖注入模式，则替换为更具体的描述。"""
    match = re.match(r"^(\s*/\*\*\s+)(字段含义待补充)(\s+\*/\s*)$", line)
    if not match:
        return line
    prefix, _, suffix = match.groups()
    return line  # 暂时保留，由调用方根据字段名替换


def cleanup_file(path: Path) -> None:
    content = path.read_text(encoding="utf-8")
    lines = content.splitlines(keepends=True)

    # 1. 合并/删除重复的类级 Javadoc：
    #    如果 package 行之后出现两个 /** ... */ 块，并且中间只有 import/空行，则保留第二个（通常更详细）
    package_idx = -1
    for i, line in enumerate(lines):
        if line.strip().startswith("package "):
            package_idx = i
            break

    if package_idx >= 0:
        # 找到 package 后的所有 Javadoc 块
        doc_blocks = []
        in_doc = False
        start = -1
        for i in range(package_idx + 1, len(lines)):
            stripped = lines[i].strip()
            if stripped.startswith("/**") and not in_doc:
                in_doc = True
                start = i
            if in_doc and stripped.endswith("*/"):
                doc_blocks.append((start, i))
                in_doc = False
                start = -1
            # 如果遇到类声明，停止搜索
            if re.match(r"^\s*(public|class|interface|enum|@)\s", stripped):
                break

        if len(doc_blocks) >= 2:
            # 保留最后一个类级 Javadoc，删除前面的（它们通常是脚本生成的通用注释）
            # 但要删除包括其前后空行
            remove_ranges = []
            for start, end in doc_blocks[:-1]:
                s = start
                while s > package_idx and lines[s - 1].strip() == "":
                    s -= 1
                e = end
                while e + 1 < len(lines) and lines[e + 1].strip() == "":
                    e += 1
                remove_ranges.append((s, e + 1))
            # 从后往前删除
            new_lines = []
            last_end = 0
            for s, e in sorted(remove_ranges):
                new_lines.extend(lines[last_end:s])
                last_end = e
            new_lines.extend(lines[last_end:])
            lines = new_lines

    # 2. 删除方法上重复的 Javadoc：
    #    模式：一个方法上方有 /**...*/，然后有注解行，然后又有 /**...*/，再是方法声明。
    #    保留第一个 Javadoc，删除第二个。
    new_lines = []
    i = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        # 检查是否是 Javadoc 开始
        if stripped.startswith("/**"):
            doc_start = i
            doc_end = i
            in_doc = True
            for j in range(i + 1, len(lines)):
                if lines[j].strip().endswith("*/"):
                    doc_end = j
                    break
            # 向前看：如果前面已经有 Javadoc/注解/方法，则可能是重复
            # 简单策略：如果本 Javadoc 后面紧跟着的是注解 + 方法，且再后面几行内又有一个方法声明，
            # 并且前面已经存在 Javadoc，则删除本 Javadoc。
            # 这里采用更稳妥的策略：如果本 Javadoc 的缩进比正常方法注释大（出现在注解之后），则删除。
            doc_indent = len(line) - len(line.lstrip())
            next_non_empty = doc_end + 1
            while next_non_empty < len(lines) and lines[next_non_empty].strip() == "":
                next_non_empty += 1

            if next_non_empty < len(lines):
                next_line = lines[next_non_empty]
                next_indent = len(next_line) - len(next_line.lstrip())
                # 如果 Javadoc 缩进明显大于后续行的缩进（例如 Javadoc 在注解内部），则删除
                if doc_indent > next_indent + 4:
                    # 跳过此 Javadoc 块及其后的空行
                    i = next_non_empty
                    continue

        new_lines.append(line)
        i += 1

    lines = new_lines

    # 3. 修复依赖注入字段的通用注释
    new_lines = []
    i = 0
    while i < len(lines):
        line = lines[i]
        new_lines.append(line)
        # 如果当前行是字段声明，且前一行是“字段含义待补充”
        field_match = re.match(r"^(\s*)(private|protected|public|final)\s+[\w<>,?\s]+\s+(\w+)\s*;", line.strip())
        if field_match:
            fname = field_match.group(3)
            desc = describe_injected_field(fname)
            if desc and len(new_lines) >= 2:
                prev_line = new_lines[-2]
                if "字段含义待补充" in prev_line:
                    indent = len(prev_line) - len(prev_line.lstrip())
                    new_lines[-2] = f"{indent * ' '}/** {desc} */\n"
        i += 1

    lines = new_lines

    new_content = "".join(lines)
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
        print("用法: python cleanup_comments.py <文件或目录> ...")
        sys.exit(1)
    main(sys.argv[1:])
