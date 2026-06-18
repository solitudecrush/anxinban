#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
为 Controller 方法补充 Javadoc。

根据方法上的 @GetMapping/@PostMapping/@PutMapping/@DeleteMapping/@RequestMapping
以及方法名、参数、返回值生成通用但有意义的中文 Javadoc，插入到注解之前。
"""

import re
import sys
import os
from pathlib import Path
from typing import List, Tuple

MAPPING_PATTERN = re.compile(
    r"@((?:Get|Post|Put|Delete|Request)Mapping)\s*\(?([^)]*)\)?"
)
METHOD_PATTERN = re.compile(
    r"^(\s*)(public|protected|private)\s+([\w<>,?\s]+)\s+(\w+)\s*\((.*?)\)\s*(throws\s+[\w,\s]+)?\s*\{?\s*$"
)


def extract_mapping_info(lines: List[str], method_idx: int) -> Tuple[str, str]:
    """
    从方法声明行向前查找最近的 Mapping 注解，返回 (HTTP 方法, 路径)。
    """
    http_method = "请求"
    url_path = ""
    j = method_idx - 1
    while j >= 0:
        stripped = lines[j].strip()
        if stripped == "":
            j -= 1
            continue
        # 遇到 Javadoc 开始或方法/类声明则停止
        if stripped.startswith("/**"):
            break
        if re.match(r"^(public|protected|private|class|interface|enum)\s", stripped):
            break
        match = MAPPING_PATTERN.search(stripped)
        if match:
            annotation_name = match.group(1)
            args = match.group(2).strip()
            if annotation_name == "GetMapping":
                http_method = "GET"
            elif annotation_name == "PostMapping":
                http_method = "POST"
            elif annotation_name == "PutMapping":
                http_method = "PUT"
            elif annotation_name == "DeleteMapping":
                http_method = "DELETE"
            elif annotation_name == "RequestMapping":
                http_method = "HTTP"
            # 提取路径
            path_match = re.search(r'"([^"]+)"', args)
            if path_match:
                url_path = path_match.group(1)
            break
        j -= 1
    return http_method, url_path


def describe_method(method_name: str, http_method: str, path: str) -> str:
    """根据方法名和映射信息生成方法描述。"""
    if method_name.startswith("get") or method_name.startswith("find") or method_name.startswith("query"):
        return f"查询接口，处理 {http_method} {path} 请求。"
    if method_name.startswith("create") or method_name.startswith("add") or method_name.startswith("save"):
        return f"新增接口，处理 {http_method} {path} 请求。"
    if method_name.startswith("update"):
        return f"更新接口，处理 {http_method} {path} 请求。"
    if method_name.startswith("delete") or method_name.startswith("remove"):
        return f"删除接口，处理 {http_method} {path} 请求。"
    if method_name.startswith("login"):
        return f"登录接口，处理 {http_method} {path} 请求。"
    if method_name.startswith("register"):
        return f"注册接口，处理 {http_method} {path} 请求。"
    if "upload" in method_name.lower():
        return f"文件上传接口，处理 {http_method} {path} 请求。"
    return f"处理 {http_method} {path} 请求。"


def parse_params(params_str: str) -> List[Tuple[str, str]]:
    """解析方法参数列表，返回 [(类型, 名称), ...]。"""
    params = []
    if not params_str.strip():
        return params
    # 去掉注解
    no_annotations = re.sub(r"@\w+(?:\([^)]*\))?\s+", "", params_str)
    for part in no_annotations.split(","):
        part = part.strip()
        if not part:
            continue
        tokens = part.split()
        if len(tokens) >= 2:
            ptype = " ".join(tokens[:-1])
            pname = tokens[-1]
            params.append((ptype, pname))
    return params


def process_file(path) -> None:
    path = Path(str(path))
    content = path.read_text(encoding="utf-8")
    lines = content.splitlines(keepends=True)

    insertions = []  # (idx, doc_lines)

    for i, line in enumerate(lines):
        match = METHOD_PATTERN.match(line)
        if not match:
            continue

        indent, modifier, ret_type, method_name, params_str, throws = match.groups()

        # 如果该方法已经有 Javadoc，则跳过
        if i > 0:
            prev_has_doc = False
            j = i - 1
            while j >= 0:
                stripped = lines[j].strip()
                if stripped == "":
                    j -= 1
                    continue
                if stripped.startswith("/**"):
                    prev_has_doc = True
                    break
                if stripped.startswith("@"):
                    j -= 1
                    continue
                break
            if prev_has_doc:
                continue

        http_method, url_path = extract_mapping_info(lines, i)
        description = describe_method(method_name, http_method, url_path)
        params = parse_params(params_str)

        doc_lines = [f"{indent}/**\n", f"{indent} * {description}\n"]
        if params:
            doc_lines.append(f"{indent} *\n")
            for ptype, pname in params:
                doc_lines.append(f"{indent} * @param {pname} {pname} 参数\n")
        if ret_type and ret_type.strip() != "void":
            doc_lines.append(f"{indent} * @return 处理结果\n")
        doc_lines.append(f"{indent} */\n")

        # 找到注解之前的插入位置（在第一个 Mapping 注解之前）
        insert_idx = i
        j = i - 1
        while j >= 0:
            stripped = lines[j].strip()
            if stripped == "":
                j -= 1
                continue
            if MAPPING_PATTERN.search(stripped):
                insert_idx = j
                j -= 1
                continue
            if stripped.startswith("@"):
                j -= 1
                continue
            break

        insertions.append((insert_idx, doc_lines))

    if not insertions:
        print(f"[无变化] {path}")
        return

    # 从后往前插入，避免索引偏移
    insertions.sort(key=lambda x: x[0], reverse=True)
    for idx, doc_lines in insertions:
        lines = lines[:idx] + doc_lines + lines[idx:]

    new_content = "".join(lines)
    if new_content != content:
        with open(str(path), "w", encoding="utf-8") as f:
            f.write(new_content)
        print(f"[已处理] {path}")
    else:
        print(f"[无变化] {path}")


def main(paths: List[str]) -> None:
    for p in paths:
        target = Path(p)
        if target.is_file() and target.suffix == ".java":
            process_file(target)
        elif target.is_dir():
            for root, _, files in os.walk(str(target)):
                for filename in sorted(files):
                    if filename.endswith(".java") and filename != "package-info.java":
                        process_file(os.path.join(root, filename))


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法: python add_controller_method_docs.py <文件或目录> ...")
        sys.exit(1)
    main(sys.argv[1:])
