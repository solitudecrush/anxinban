#!/usr/bin/env python3
"""Generate chat_records SQL to match the updated HealthDataSeeder design.
All 3 dimensions (day/week/month) will naturally produce "焦虑" emotion label."""

DATE = "2026-07-22"  # today's date
USER_ID = "1"

# ===== Detailed chat data for key days (matches HealthDataSeeder DETAILED_CHAT_DATA) =====
DETAILED_CHAT = {
    # daysAgo 0 = today = 2026-07-22 — 焦虑×2, 低落×1, 开心×1, 平静×1
    0: [
        ("昨晚又没睡好，翻来覆去的，膝盖疼得厉害", "焦虑"),
        ("今天头昏沉沉的，不知道是不是血压又高了，唉", "焦虑"),
        ("今天没怎么出门，一个人在家待着有点闷得慌", "低落"),
        ("中午煮了碗面，还卧了个荷包蛋，一个人也要好好吃", "开心"),
        ("下午在阳台晒了会儿太阳，织了几行毛衣，也算有件事做", "平静"),
    ],
    # daysAgo 1 = 2026-07-21
    1: [
        ("昨晚翻来覆去睡不着，膝盖疼得厉害，贴了膏药也不管用", "焦虑"),
        ("今天不太想动，也没什么胃口，就喝了碗粥", "低落"),
        ("孙女视频给我看了她画的画，画得真像啊，心里暖暖的", "开心"),
        ("下午晒了会儿太阳，织了几行毛衣，也算有点事做", "平静"),
    ],
    # daysAgo 2 = 2026-07-20
    2: [
        ("楼下门锁好像坏了，一个人在家总觉得不踏实", "焦虑"),
        ("电视换来换去也没个好看的，关了又太安静了", "低落"),
        ("好久没见孙子了，也不知道长高了没有", "思念"),
        ("晚上听了会儿老歌，心里舒坦了些，也算有个伴", "平静"),
    ],
    # daysAgo 3 = 2026-07-19
    3: [
        ("晚饭就自己，热了口剩饭对付了一下", "孤单"),
        ("老伴走了三年了，昨天又梦到了，还是老样子", "思念"),
        ("身体一天不如一天，这儿疼那儿疼的", "焦虑"),
        ("今天又是在屋里坐了一天，连电视都忘了开", "低落"),
    ],
    # daysAgo 4 = 2026-07-18
    4: [
        ("老张住院了说是心脏的事，我心里也七上八下的", "焦虑"),
        ("人老了真不中用，走两步就喘，也不知道还能撑多久", "焦虑"),
        ("翻到老伴以前纳的鞋垫，眼泪就下来了，哎", "思念"),
        ("下午在阳台坐了一会儿，织了几针毛衣，心里安静了些", "平静"),
    ],
    # daysAgo 5 = 2026-07-17
    5: [
        ("儿子说周末来，又说加班来不了，算了，习惯了", "低落"),
        ("一天没出门，也不知道出去干啥", "低落"),
        ("晚上睡不着，脑子里全是乱七八糟的事，心慌", "焦虑"),
        ("对门老刘搬儿子家去了，楼道更冷清了", "孤单"),
    ],
    # daysAgo 6 = 2026-07-16
    6: [
        ("唉，一晚上醒了三四回，也不知道咋回事", "焦虑"),
        ("血压药快吃完了，也没人帮我去开，愁得慌", "焦虑"),
        ("一整天也没干啥，坐着坐着天就黑了", "低落"),
        ("连个说话的人都没有，就剩电视在那响着", "孤单"),
    ],
    # daysAgo 8 = 2026-07-14
    8: [
        ("好几天没睡好觉了，晚上越想越精神，白天又犯困", "焦虑"),
        ("今天头昏沉沉的，也不知道是不是血压又上来了", "焦虑"),
        ("一个人坐着就发呆，啥也不想干", "低落"),
    ],
    # daysAgo 13 = 2026-07-09
    13: [
        ("老李住院了听说还挺严重，想想自己也这岁数了", "低落"),
        ("今天天热，胃口不好，就喝了碗绿豆汤", "低落"),
        ("翻出了以前的老相册，那时候多好啊", "思念"),
    ],
    # daysAgo 20 = 2026-07-02
    20: [
        ("好几天没人跟我说句话了，闷得心里发慌", "孤单"),
        ("楼下装修咚咚咚的，吵得人心烦", "焦虑"),
        ("中午在阳台晒了会儿太阳，暖洋洋的也挺好", "平静"),
    ],
    # daysAgo 25 = 2026-06-27
    25: [
        ("晚上打雷下雨，一个人在家怪害怕的", "焦虑"),
        ("腿疼了好几天了，不想去医院，去了也是排队", "低落"),
        ("儿子说下个月回来看我，也不知道能不能真回来", "思念"),
    ],
}

# ===== Templates for remaining days (matching generateEarlyDayRecords) =====
TEMPLATES = [
    # 开心 (5)
    ("今天煮了碗面，放了点葱花还挺香的", "开心"),
    ("社区小王来坐了坐，有人说话真好", "开心"),
    ("阳台上的月季又开了一朵，红艳艳的", "开心"),
    ("中午做了个红烧豆腐，手艺还没丢", "开心"),
    ("隔壁送了几个粽子来，热乎乎的挺好吃", "开心"),
    # 平静 (7)
    ("下午在阳台坐了一会儿，晒晒太阳", "平静"),
    ("听了一会儿收音机，刚好放到喜欢的老歌", "平静"),
    ("晚上织了几行毛衣，也算有点事做", "平静"),
    ("今天没出门，就在屋里收拾了一下", "平静"),
    ("看了会儿电视，有个节目讲养花的，挺好", "平静"),
    ("中午眯了一会儿，虽然没睡着也算休息了", "平静"),
    ("晚饭后下楼扔了趟垃圾，顺便透了口气", "平静"),
    # 低落 (6)
    ("一整天没出门，也不知道在屋里干啥", "低落"),
    ("今天没什么胃口，就喝了几口粥", "低落"),
    ("人老了就这样，一天不如一天了", "低落"),
    ("今天又是什么都没干，坐着坐着就天黑了", "低落"),
    ("心里闷闷的，也说不上来为什么", "低落"),
    ("唉，觉得什么都没意思，连电视都不想开", "低落"),
    # 焦虑 (6)
    ("头又晕了，不知道是不是血压高了", "焦虑"),
    ("晚上老醒，醒了就睡不着，越想越精神", "焦虑"),
    ("记性越来越差了，刚放的东西转头就找不到", "焦虑"),
    ("胸口有时候闷得慌，也不敢跟孩子们说", "焦虑"),
    ("药还有几天就吃完了，也不想去医院排队", "焦虑"),
    ("半夜醒了就再也睡不着，睁眼到天亮", "焦虑"),
    # 孤单 (3)
    ("一天又过去了，连个说话的人都没有", "孤单"),
    ("一个人吃饭，一个人看电视，干啥都是一个人", "孤单"),
    ("楼下挺热闹的，就我这屋里安安静静", "孤单"),
    # 思念 (3)
    ("翻到老伴的照片，看了好一会儿", "思念"),
    ("好久没见孙子了，也不知道还认不认得我", "思念"),
    ("以前这时候正跟老伴一起看电视呢，唉", "思念"),
]


def generate_early_day_records(days_ago):
    """Generate 1-2 records for early days, matching Java logic."""
    base = days_ago % 31
    count = 1 if (base % 3 == 0) else 2
    records = []
    for i in range(count):
        idx = (base + i * 11) % len(TEMPLATES)
        records.append(TEMPLATES[idx])
    return records


def generate_sql():
    from datetime import date, timedelta

    today = date.fromisoformat(DATE)
    all_records = {}

    for days_ago in range(29, -1, -1):
        d = today - timedelta(days=days_ago)
        date_str = d.isoformat()

        if days_ago in DETAILED_CHAT:
            all_records[date_str] = DETAILED_CHAT[days_ago]
        else:
            all_records[date_str] = generate_early_day_records(days_ago)

    # Emit SQL
    lines = []
    lines.append("-- ===================================================")
    lines.append("-- Chat records seed data (anxiety-leaning)")
    lines.append(f"-- Base date: {DATE}, 30 days coverage")
    lines.append("-- 日维度: 焦虑40%+低落20%+开心20%+平静20% → 焦虑")
    lines.append("-- 周维度: negative~76% → 焦虑")
    lines.append("-- 月维度: negative~65% → 焦虑")
    lines.append("-- ===================================================")
    lines.append("")
    lines.append("DELETE FROM chat_records WHERE date >= '2026-06-23' AND date <= '2026-07-22';")
    lines.append("")

    lines.append("INSERT INTO chat_records (user_id, date, message, emotion, created_at) VALUES")

    all_values = []
    for date_str in sorted(all_records.keys()):
        for msg, emotion in all_records[date_str]:
            escaped_msg = msg.replace("'", "\\'")
            all_values.append(
                f"('{USER_ID}', '{date_str}', '{escaped_msg}', '{emotion}', '{date_str} 10:00:00')"
            )

    lines.append(",\n".join(all_values) + ";")
    lines.append("")

    # Summary
    total = sum(len(v) for v in all_records.values())
    lines.append(f"-- Total: {total} records across {len(all_records)} days")
    lines.append(f"-- Generated from HealthDataSeeder.java design")

    return "\n".join(lines), all_records


if __name__ == "__main__":
    import os

    sql, records = generate_sql()
    output_path = "/var/www/anxinban-backend/scripts/insert_chat_records_anxiety.sql"
    with open(output_path, "w", encoding="utf-8") as f:
        f.write(sql)

    total = sum(len(v) for v in records.values())
    print(f"✅ SQL written: {output_path}")
    print(f"📊 {total} total records across {len(records)} days")

    # Verify distribution
    from collections import Counter
    all_emotions = []
    for date_str in sorted(records.keys()):
        for msg, emotion in records[date_str]:
            all_emotions.append(emotion)

    counter = Counter(all_emotions)
    t = sum(counter.values())
    print(f"\n📊 Full month distribution ({t} records):")
    for e in ["开心", "平静", "低落", "焦虑", "孤单", "思念"]:
        c = counter.get(e, 0)
        print(f"   {e}: {c} ({round(c/t*100)}%)")

    # Today
    today_records = records.get(DATE, [])
    today_emotions = [e for _, e in today_records]
    today_counter = Counter(today_emotions)
    tt = len(today_records)
    pos = today_counter.get("开心", 0) + today_counter.get("平静", 0)
    neg = today_counter.get("焦虑", 0) + today_counter.get("低落", 0) + today_counter.get("孤单", 0) + today_counter.get("思念", 0)
    print(f"\n📊 Today ({DATE}): {tt} records")
    print(f"   Positive: {pos}/{tt} = {round(pos/tt*100)}%")
    print(f"   Negative: {neg}/{tt} = {round(neg/tt*100)}%")
    print(f"   → Label: {'焦虑' if neg/tt > 0.5 else '平稳'}")
