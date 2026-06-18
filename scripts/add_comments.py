#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
批量为 Java 源文件补充中文 Javadoc/行内注释。

说明：
- 本脚本适用于 DTO、Entity、Mapper、Controller、Service、MQTT 等所有 Java 类；
- 对于复杂业务方法，脚本会生成通用注释，建议在关键方法上再人工细化；
- 脚本会尽量保持原有代码不变，仅插入或更新 Javadoc 块。
"""

import os
import re
import sys
from pathlib import Path
from typing import List, Tuple

# 常用字段名 -> 中文描述的映射，用于自动生成字段/方法注释
FIELD_DESC = {
    "id": "唯一标识，主键",
    "createdAt": "记录创建时间",
    "updatedAt": "记录最后更新时间",
    "createTime": "记录创建时间",
    "updateTime": "记录最后更新时间",
    "status": "状态标识",
    "type": "类型标识",
    "name": "名称",
    "phone": "手机号",
    "password": "密码",
    "newPassword": "新密码",
    "oldPassword": "原密码",
    "verifyCode": "短信验证码",
    "email": "邮箱地址",
    "avatar": "头像 URL",
    "gender": "性别",
    "age": "年龄",
    "birthday": "出生日期",
    "address": "地址",
    "communityId": "所属社区/机构 ID",
    "elderId": "关联老人用户 ID",
    "userId": "关联用户 ID",
    "staffId": "关联工作人员 ID",
    "familyId": "关联家属用户 ID",
    "deviceId": "关联设备 ID",
    "houseId": "房屋/家庭 ID",
    "room": "房间名称/编号",
    "building": "楼栋",
    "floor": "楼层",
    "unit": "单元",
    "relation": "关系描述",
    "role": "角色",
    "content": "内容",
    "title": "标题",
    "remark": "备注",
    "description": "描述",
    "code": "状态码",
    "message": "提示信息",
    "data": "数据载荷",
    "list": "数据列表",
    "total": "总记录数",
    "page": "当前页码",
    "pageSize": "每页大小",
    "timestamp": "时间戳",
    "level": "级别",
    "category": "分类",
    "source": "来源",
    "target": "目标",
    "result": "结果",
    "score": "评分/分数",
    "value": "数值",
    "unit": "单位",
    "threshold": "阈值",
    "enabled": "是否启用",
    "deleted": "是否已删除",
    "isRead": "是否已读",
    "isHandled": "是否已处理",
    "handledBy": "处理人 ID",
    "handledAt": "处理时间",
    "processed": "是否已处理",
    "processedBy": "处理人",
    "processedAt": "处理时间",
    "startedAt": "开始时间",
    "endedAt": "结束时间",
    "startTime": "开始时间",
    "endTime": "结束时间",
    "triggerTime": "触发时间",
    "notifyTime": "通知时间",
    "location": "位置信息",
    "longitude": "经度",
    "latitude": "纬度",
    "heartRate": "心率",
    "bloodPressure": "血压",
    "systolic": "收缩压",
    "diastolic": "舒张压",
    "bloodOxygen": "血氧饱和度",
    "temperature": "体温",
    "sleepScore": "睡眠质量评分",
    "stepCount": "步数",
    "fallDetected": "是否检测到跌倒",
    "smokeDetected": "是否检测到烟雾",
    "emergency": "是否为紧急情况",
    "deviceType": "设备类型",
    "deviceName": "设备名称",
    "deviceStatus": "设备状态",
    "serialNumber": "设备序列号",
    "macAddress": "MAC 地址",
    "ipAddress": "IP 地址",
    "online": "是否在线",
    "lastOnlineAt": "最后在线时间",
    "lastOnlineTime": "最后在线时间",
    "battery": "电池电量",
    "batteryLevel": "电池电量百分比",
    "signal": "信号强度",
    "payload": "消息负载/原始数据",
    "topic": "MQTT 主题",
    "qos": "MQTT QoS 等级",
    "retain": "是否保留消息",
    "clientId": "客户端 ID",
    "brokerHost": "Broker 主机地址",
    "brokerPort": "Broker 端口",
    "username": "用户名",
    "action": "动作/操作类型",
    "command": "控制命令",
    "params": "参数",
    "requestId": "请求 ID",
    "responseId": "响应 ID",
    "conversationId": "会话 ID",
    "sessionId": "会话 ID",
    "intent": "意图",
    "confidence": "置信度",
    "answer": "回答内容",
    "advice": "建议内容",
    "analysis": "分析结果",
    "summary": "摘要",
    "detail": "详情",
    "imageUrl": "图片 URL",
    "videoUrl": "视频 URL",
    "audioUrl": "音频 URL",
    "fileUrl": "文件 URL",
    "url": "URL 地址",
    "path": "路径",
    "size": "大小",
    "count": "数量",
    "index": "索引",
    "order": "排序",
    "sort": "排序字段",
    "keyword": "关键词",
    "startDate": "开始日期",
    "endDate": "结束日期",
    "date": "日期",
    "time": "时间",
    "datetime": "日期时间",
    "duration": "持续时长",
    "interval": "间隔",
    "frequency": "频率",
    "priority": "优先级",
    "severity": "严重等级",
    "reason": "原因",
    "solution": "解决方案",
    "operator": "操作人",
    "operatorId": "操作人 ID",
    "note": "备注",
    "feedback": "反馈",
    "comment": "评论",
    "rating": "评分",
    "tags": "标签",
    "metadata": "元数据",
    "extra": "扩展信息",
    "ext": "扩展信息",
}


def describe_field(name: str) -> str:
    """根据字段名生成最贴切的描述。"""
    # 精确匹配
    if name in FIELD_DESC:
        return FIELD_DESC[name]
    # 去掉常见前缀/后缀再匹配
    stripped = name
    for prefix in ("get", "set", "is", "has"):
        if stripped.startswith(prefix):
            stripped = stripped[len(prefix):]
            break
    if stripped:
        stripped_lower = stripped[0].lower() + stripped[1:]
        if stripped_lower in FIELD_DESC:
            return FIELD_DESC[stripped_lower]
    # 包含关系（优先最长匹配）
    matches = []
    for key, desc in FIELD_DESC.items():
        if key.lower() in name.lower():
            matches.append((len(key), desc))
    if matches:
        matches.sort(reverse=True)
        return matches[0][1]
    return "字段含义待补充"


def describe_class(class_name: str, package: str) -> str:
    """根据类名和包名生成类级描述。"""
    if class_name.endswith("Dto") or class_name.endswith("DTO"):
        return f"{class_name[:-3]} 数据传输对象（DTO），用于层与层之间的数据传递。"
    if class_name.endswith("Request"):
        return f"{class_name[:-7]} 请求参数封装类，用于接收前端传入的数据。"
    if class_name.endswith("Response"):
        return f"{class_name[:-8]} 响应数据封装类，用于向前端返回处理结果。"
    if "Repository" in class_name:
        return f"{class_name.replace('Repository', '')} 数据访问接口，基于 Spring Data JPA 实现持久化操作。"
    if "entity" in package:
        return f"{class_name} 实体类，对应数据库中的一张业务表。"
    if class_name.endswith("Service"):
        return f"{class_name[:-7]} 业务服务类，处理 {class_name[:-7]} 领域的业务逻辑。"
    if class_name.endswith("Controller"):
        return f"{class_name[:-10]} REST 控制器，提供 {class_name[:-10]} 相关的 HTTP API。"
    if class_name.endswith("Config"):
        return f"{class_name[:-6]} 配置类，用于装配 Spring Bean 或应用运行时参数。"
    if class_name.endswith("Properties"):
        return f"{class_name[:-10]} 配置属性类，用于绑定 application.properties 中的配置项。"
    if class_name.endswith("Test") or class_name.endswith("Tests"):
        return f"{class_name} 测试类，用于验证对应业务逻辑的正确性。"
    if "mqtt" in package and "dto" in package:
        return f"{class_name} MQTT 消息 DTO，定义设备与云端交互的消息结构。"
    if "mqtt" in package and "simulator" in package:
        return f"{class_name} 设备模拟器，用于在开发/测试阶段模拟硬件设备行为。"
    if "mqtt" in package:
        return f"{class_name} MQTT 模块核心类，负责物联网设备通信相关功能。"
    return f"{class_name} 类。"


def collect_annotations(lines: List[str], start_idx: int) -> Tuple[int, List[str]]:
    """
    从 start_idx 向前收集连续的空行和注解行，返回这些行的起始索引和行列表。
    用于将字段 Javadoc 插入到注解之前。
    """
    collected = []
    idx = start_idx - 1
    while idx >= 0:
        stripped = lines[idx].strip()
        if stripped == "" or stripped.startswith("@"):
            collected.insert(0, lines[idx])
            idx -= 1
        else:
            break
    return idx + 1, collected


def previous_is_doc(lines: List[str], start_idx: int) -> bool:
    """判断指定行之前是否存在 Javadoc 注释（跳过空行）。"""
    idx = start_idx - 1
    while idx >= 0:
        stripped = lines[idx].strip()
        if stripped == "":
            idx -= 1
            continue
        if stripped.endswith("*/"):
            return True
        return False
    return False


def process_java_file(content: str, class_name: str, package: str) -> str:
    """为 Java 文件补充类、字段、方法注释。"""
    lines = content.splitlines(keepends=True)

    # 在 package 语句之后、第一个 import/类声明之前插入类 Javadoc（若不存在）
    package_end = 0
    for i, line in enumerate(lines):
        if line.strip().startswith("package "):
            package_end = i + 1
            break

    has_class_doc = False
    for i in range(package_end, min(package_end + 5, len(lines))):
        if lines[i].strip().startswith("/**"):
            has_class_doc = True
            break

    if not has_class_doc:
        class_doc = [
            "\n",
            "/**\n",
            f" * {describe_class(class_name, package)}\n",
            " *\n",
            " * @author 安心伴开发团队\n",
            " * @since 0.0.1-SNAPSHOT\n",
            " */\n",
        ]
        # 在 package 后如果有空行则保留一个空行
        if package_end < len(lines) and lines[package_end].strip() == "":
            lines = lines[:package_end] + class_doc + lines[package_end + 1:]
        else:
            lines = lines[:package_end] + ["\n"] + class_doc + lines[package_end:]

    # 处理字段和方法
    field_pattern = re.compile(r"^(\s*)(private|protected|public)\s+([\w<>,?\s]+)\s+(\w+)\s*;")
    method_pattern = re.compile(r"^(\s*)(public|protected|private)\s+([\w<>,?\s]+)\s+(\w+)\s*\(")
    # 识别方法参数列表（简单版本，忽略泛型嵌套括号）
    param_pattern = re.compile(r"([\w<>,?\s]+)\s+(\w+)")

    new_lines = []
    i = 0
    while i < len(lines):
        line = lines[i]
        line_stripped = line.strip()

        # 字段
        field_match = field_pattern.match(line)
        if field_match:
            indent, modifier, ftype, fname = field_match.groups()
            if not previous_is_doc(new_lines, len(new_lines)):
                desc = describe_field(fname)
                doc_lines = [f"{indent}/** {desc} */\n"]
                # 将注释插入到注解之前（收集前面的注解行）
                ann_start, ann_lines = collect_annotations(new_lines, len(new_lines))
                if ann_lines:
                    new_lines = new_lines[:ann_start] + doc_lines + ann_lines + new_lines[ann_start + len(ann_lines):]
                else:
                    new_lines.extend(doc_lines)
            new_lines.append(line)
            i += 1
            continue

        # 方法
        method_match = method_pattern.match(line)
        if method_match and not line_stripped.startswith("@"):
            indent, modifier, mtype, mname = method_match.groups()
            if not previous_is_doc(new_lines, len(new_lines)):
                doc_indent = indent + "    " if indent else "    "
                doc = [f"{doc_indent}/**\n"]
                if mname.startswith("get"):
                    prop = mname[3:]
                    prop = prop[0].lower() + prop[1:] if prop else prop
                    doc.append(f"{doc_indent} * 获取{describe_field(prop)}。\n")
                    doc.append(f"{doc_indent} *\n")
                    doc.append(f"{doc_indent} * @return {describe_field(prop)}\n")
                elif mname.startswith("set"):
                    prop = mname[3:]
                    prop = prop[0].lower() + prop[1:] if prop else prop
                    doc.append(f"{doc_indent} * 设置{describe_field(prop)}。\n")
                    doc.append(f"{doc_indent} *\n")
                    doc.append(f"{doc_indent} * @param {prop} {describe_field(prop)}\n")
                elif mname.startswith("is"):
                    prop = mname[2:]
                    prop = prop[0].lower() + prop[1:] if prop else prop
                    doc.append(f"{doc_indent} * 判断是否{describe_field(prop)}。\n")
                    doc.append(f"{doc_indent} *\n")
                    doc.append(f"{doc_indent} * @return 是否{describe_field(prop)}\n")
                elif mname == "main":
                    doc.append(f"{doc_indent} * 应用入口方法。\n")
                    doc.append(f"{doc_indent} *\n")
                    doc.append(f"{doc_indent} * @param args 命令行参数\n")
                elif mname == class_name:
                    doc.append(f"{doc_indent} * 构造方法。\n")
                    # 尝试提取构造参数
                    paren_start = line.find("(")
                    paren_end = line.find(")", paren_start)
                    if paren_start > 0 and paren_end > paren_start:
                        params_str = line[paren_start + 1:paren_end]
                        params = param_pattern.findall(params_str)
                        if params:
                            doc.append(f"{doc_indent} *\n")
                            for ptype, pname in params:
                                doc.append(f"{doc_indent} * @param {pname} {describe_field(pname)}\n")
                else:
                    doc.append(f"{doc_indent} * {mname} 方法。\n")
                    # 尝试提取方法参数
                    paren_start = line.find("(")
                    paren_end = line.find(")", paren_start)
                    if paren_start > 0 and paren_end > paren_start:
                        params_str = line[paren_start + 1:paren_end]
                        params = param_pattern.findall(params_str)
                        if params:
                            doc.append(f"{doc_indent} *\n")
                            for ptype, pname in params:
                                doc.append(f"{doc_indent} * @param {pname} {describe_field(pname)}\n")
                doc.append(f"{doc_indent} */\n")
                new_lines.extend(doc)
            new_lines.append(line)
            i += 1
            continue

        new_lines.append(line)
        i += 1

    return "".join(new_lines)


def process_file(path: Path) -> None:
    """处理单个 Java 文件。"""
    content = path.read_text(encoding="utf-8")
    package_match = re.search(r"package\s+([\w.]+);", content)
    if not package_match:
        return
    package = package_match.group(1)

    # 匹配第一个 public class/interface/enum
    class_match = re.search(r"public\s+(?:class|interface|enum)\s+(\w+)", content)
    if not class_match:
        return
    class_name = class_match.group(1)

    new_content = process_java_file(content, class_name, package)
    if new_content != content:
        path.write_text(new_content, encoding="utf-8")
        print(f"[已处理] {path}")
    else:
        print(f"[无变化] {path}")


def main(paths: List[str]) -> None:
    for p in paths:
        target = Path(p)
        if target.is_file() and target.suffix == ".java":
            process_file(target)
        elif target.is_dir():
            for java_file in sorted(target.rglob("*.java")):
                process_file(java_file)


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("用法: python add_comments.py <文件或目录> ...")
        sys.exit(1)
    main(sys.argv[1:])
