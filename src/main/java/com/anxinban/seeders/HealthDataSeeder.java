package com.anxinban.seeders;

import com.anxinban.entity.AiServiceRecord;
import com.anxinban.entity.ChatRecord;
import com.anxinban.entity.ItemFindLog;
import com.anxinban.entity.MusicLog;
import com.anxinban.entity.SleepRecord;
import com.anxinban.mapper.AiServiceRecordRepository;
import com.anxinban.mapper.ChatRecordRepository;
import com.anxinban.mapper.ItemFindLogRepository;
import com.anxinban.mapper.MusicLogRepository;
import com.anxinban.mapper.SleepRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康数据模拟种子脚本 — 为 music_logs / chat_records / item_find_logs
 * 三张表生成过去 30 天的模拟数据（满足日/周/月三个维度的查询需求）。
 *
 * <p>数据为固定内容（非随机），确保每次生成的模拟数据一致。
 * 执行策略：每次启动时清空旧数据并重新生成。
 * 使用条件：通过 spring.profiles.active=seed 激活，或直接调用 {@link #seed()} 方法。</p>
 *
 * <p>情绪设计：以负向情绪（焦虑、低落、孤单、思念）为主导，
 * 日/周/月三个维度均判定为"焦虑"。</p>
 *
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Component
public class HealthDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HealthDataSeeder.class);
    private static final String FIXED_USER_ID = "elder_001";
    private static final int TOTAL_DAYS = 30;

    // ==================== 陪伴对话记录 — 30天数据 ====================
    //
    // 情绪维度设计目标（焦虑导向）：
    //   ┌────────┬──────┬──────┬──────┬──────┬──────┬──────┐
    //   │  维度  │ 开心 │ 平静 │ 低落 │ 焦虑 │ 孤单 │ 思念 │
    //   ├────────┼──────┼──────┼──────┼──────┼──────┼──────┤
    //   │ 日(今) │ 20%  │ 20%  │ 20%  │ 40%  │  0%  │  0%  │
    //   │ 周(7)  │ 10%  │ 13%  │ 20%  │ 30%  │ 13%  │ 13%  │
    //   │ 月(30) │ ~12% │ ~15% │ ~22% │ ~28% │ ~12% │ ~13%  │
    //   └────────┴──────┴──────┴──────┴──────┴──────┴──────┘
    //
    // 仅使用6种情绪标签：开心、平静、低落、焦虑、孤单、思念
    // 对话风格：口语化、短句（5-20字）、体现焦虑+需要陪伴

    private static final Map<Integer, List<String[]>> DETAILED_CHAT_DATA = new LinkedHashMap<>();

    static {
        // ==================== daysAgo=6（2026-07-16）— 焦虑2+低落1+孤单1 → 4条 ====================
        DETAILED_CHAT_DATA.computeIfAbsent(6, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"唉，一晚上醒了三四回，也不知道咋回事", "焦虑"},
                        new String[]{"血压药快吃完了，也没人帮我去开，愁得慌", "焦虑"},
                        new String[]{"一整天也没干啥，坐着坐着天就黑了", "低落"},
                        new String[]{"对门老刘搬走了，连个串门的人都没了", "孤单"}
                ));
        // ==================== daysAgo=5（2026-07-17）— 焦虑1+低落1+孤单1+思念1 → 4条 ====================
        DETAILED_CHAT_DATA.computeIfAbsent(5, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"天气预报说明天降温，家里也不知道有没有人帮我拿厚被子", "焦虑"},
                        new String[]{"午饭就热了口剩饭，一个人吃什么都一样", "低落"},
                        new String[]{"孩子们都忙，我也不想打扰他们，算了", "孤单"},
                        new String[]{"翻到老伴的照片，看了好一会儿，心里酸酸的", "思念"}
                ));
        // ==================== daysAgo=4（2026-07-18）— 焦虑1+低落1+思念1+平静1 → 4条 ====================
        DETAILED_CHAT_DATA.computeIfAbsent(4, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"老张住院了，听说要做手术，我这心里也跟着慌", "焦虑"},
                        new String[]{"今天不想出门，外面闹哄哄的，没意思", "低落"},
                        new String[]{"以前老李在的时候还能下下棋，现在没人了", "思念"},
                        new String[]{"今天把阳台的花浇了，长出新叶子了", "平静"}
                ));
        // ==================== daysAgo=3（2026-07-19）— 焦虑1+低落1+孤单1+思念1 → 4条 ====================
        DETAILED_CHAT_DATA.computeIfAbsent(3, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"膝盖疼得厉害，走路都费劲，也没人陪我去医院看看", "焦虑"},
                        new String[]{"人老了就是没用，走两步就喘，什么事都做不了", "低落"},
                        new String[]{"晚饭就我一个人，随便热了口剩饭", "孤单"},
                        new String[]{"好久没见孙子了，也不知道长高了多少", "思念"}
                ));
        // ==================== daysAgo=2（2026-07-20）— 焦虑1+低落1+孤单1+思念1+平静1 → 5条 ====================
        DETAILED_CHAT_DATA.computeIfAbsent(2, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"楼下门锁好像坏了，一个人在家总觉得不踏实", "焦虑"},
                        new String[]{"电视换来换去也没个好看的，关了又太安静了", "低落"},
                        new String[]{"一天到晚连个说话的人都没有，就电视陪着我", "孤单"},
                        new String[]{"想老伴了，以前这时候她总会给我泡杯茶", "思念"},
                        new String[]{"晚上听了会儿老歌，心里舒坦了些", "平静"}
                ));
        // ==================== daysAgo=1（昨天 2026-07-21）— 焦虑1+开心2+平静1 → 4条 ====================
        DETAILED_CHAT_DATA.computeIfAbsent(1, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"这两天头晕得厉害，怕摔倒了没人知道", "焦虑"},
                        new String[]{"隔壁小王帮我提了桶水，真是个热心肠", "开心"},
                        new String[]{"孙女视频给我看了她画的画，画得真像啊，心里暖暖的", "开心"},
                        new String[]{"下午在阳台晒了会儿太阳，顺便织了几行毛衣", "平静"}
                ));
        // ==================== daysAgo=0（今天 2026-07-22）— 焦虑2+低落1+开心1+平静1 → 5条 ====================
        // 正向40% vs 负向60%（低落1+焦虑2=3）→ 负向>50% → "焦虑" 4.0分
        DETAILED_CHAT_DATA.computeIfAbsent(0, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"昨晚又没睡好，三点多醒了就一直没睡着，翻来覆去的", "焦虑"},
                        new String[]{"血压最近一直偏高，也不知道要不要加药，心里没底", "焦虑"},
                        new String[]{"今天不太想动，也没什么胃口，就喝了碗粥", "低落"},
                        new String[]{"中午烧了条红烧鱼，手艺还没退步，一个人也要好好吃饭", "开心"},
                        new String[]{"下午晒了会儿太阳，暖洋洋的，打了个盹", "平静"}
                ));

        // ==================== 关键早期日期（用于月维度 keyEvents 展示） ====================
        // daysAgo=8（2026-07-14）— 焦虑2+低落1 → 3条
        DETAILED_CHAT_DATA.computeIfAbsent(8, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"好几天没睡好觉了，晚上越想越精神，白天又犯困", "焦虑"},
                        new String[]{"今天头昏沉沉的，也不知道是不是血压又上来了", "焦虑"},
                        new String[]{"一个人坐着就发呆，啥也不想干", "低落"}
                ));
        // daysAgo=10（2026-07-12）— 孤单1+低落1+焦虑1 → 3条
        DETAILED_CHAT_DATA.computeIfAbsent(10, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"好几天没人跟我说句话了，闷得心里发慌", "孤单"},
                        new String[]{"今天又是在屋里坐了一天，连电视都忘了开", "低落"},
                        new String[]{"半夜醒了就再也睡不着，睁眼到天亮", "焦虑"}
                ));
        // daysAgo=13（2026-07-09）— 焦虑1+低落1+思念1 → 3条
        DETAILED_CHAT_DATA.computeIfAbsent(13, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"老李住院了听说还挺严重，想想自己也这岁数了", "低落"},
                        new String[]{"今天天热，胃口不好，就喝了碗绿豆汤", "低落"},
                        new String[]{"翻出了以前的老相册，那时候多好啊", "思念"}
                ));
        // daysAgo=16（2026-07-06）— 焦虑1+孤单1+思念1 → 3条
        DETAILED_CHAT_DATA.computeIfAbsent(16, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"这几天胸口闷得慌，也不敢跟孩子们说", "焦虑"},
                        new String[]{"一天又过去了，连个说话的人都没有", "孤单"},
                        new String[]{"老板娘以前这时候该喊我吃饭了，唉", "思念"}
                ));
        // daysAgo=20（2026-07-02）— 孤单1+焦虑1+平静1 → 3条
        DETAILED_CHAT_DATA.computeIfAbsent(20, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"楼下装修咚咚咚的，吵得人心烦", "焦虑"},
                        new String[]{"楼下挺热闹的，就我这屋里安安静静", "孤单"},
                        new String[]{"中午在阳台晒了会儿太阳，暖洋洋的也挺好", "平静"}
                ));
        // daysAgo=23（2026-06-29）— 焦虑1+低落1+思念1 → 3条
        DETAILED_CHAT_DATA.computeIfAbsent(23, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"记性越来越差了，刚放的东西转头就找不到", "焦虑"},
                        new String[]{"唉，觉得什么都没意思，连电视都不想开", "低落"},
                        new String[]{"好久没见孙子了，也不知道还认不认得我", "思念"}
                ));
        // daysAgo=27（2026-06-25）— 焦虑1+低落1+孤单1 → 3条
        DETAILED_CHAT_DATA.computeIfAbsent(27, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"晚上打雷下雨，一个人在家怪害怕的", "焦虑"},
                        new String[]{"腿疼了好几天了，不想去医院，去了也是排队", "低落"},
                        new String[]{"一个人吃饭，一个人看电视，干啥都是一个人", "孤单"}
                ));
        // daysAgo=29（2026-06-23）— 焦虑1+低落1+思念1 → 3条
        DETAILED_CHAT_DATA.computeIfAbsent(29, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"药还有几天就吃完了，也不想去医院排队", "焦虑"},
                        new String[]{"人老了就这样，一天不如一天了", "低落"},
                        new String[]{"想老伴了，以前这时候她总会给我泡杯茶", "思念"}
                ));
    }

    // ==================== 情绪分布实际对比（基于以上数据） ====================
    // 日维度（today, 5条）：开心1+平静1+低落1+焦虑2 → positive=2(40%), negative=3(60%)>50% → "焦虑" 4.0分 ✓
    // 周维度（7天, 30条）：开心3+平静4+低落6+焦虑9+孤单4+思念4
    //   → positive=7(23%), negative=23(77%)>50% → "焦虑" 2.3分 ✓
    // 月维度（30天, ~114条）：整体 negative≈70% > 50% → "焦虑" ~3.0分 ✓

    // ==================== 音乐日志 — 固定播放记录 ====================
    // (daysAgo, 歌曲名, 时长分钟, 场景)

    private static final Object[][] MUSIC_DATA = {
            {6, "小夜曲", 25, "助眠"},
            {6, "高山流水", 15, "放松"},
            {5, "高山流水", 62, "放松"},
            {4, "白噪音-雨声", 55, "助眠"},
            {3, "午后放松钢琴", 71, "放松"},
            {3, "高山流水", 20, "放松"},
            {2, "小夜曲", 12, "助眠"},
            {1, "高山流水", 35, "放松"},
            {0, "白噪音-森林", 30, "助眠"},
            {0, "高山流水", 22, "放松"},
    };

    // ==================== 物品寻找记录 — 100% 找到，带位置 ====================

    private static final Object[][] ITEM_FIND_DATA = {
            {6, "遥控器", 45, "客厅沙发上"},
            {6, "水杯", 78, "卧室梳妆台上"},
            {5, "衣架", 120, "卧室床上"},
            {4, "花瓶", 63, "客厅柜子上"},
            {4, "电话", 205, "客厅电视机旁"},
            {3, "遥控器", 92, "客厅沙发上"},
            {2, "披萨", 34, "客厅茶几上"},
            {2, "水杯", 156, "卧室梳妆台上"},
            {1, "电话", 183, "客厅电视机旁"},
            {0, "衣架", 47, "卧室床上"},
    };

    @Autowired
    private MusicLogRepository musicLogRepository;

    @Autowired
    private ChatRecordRepository chatRecordRepository;

    @Autowired
    private ItemFindLogRepository itemFindLogRepository;

    @Autowired
    private AiServiceRecordRepository aiServiceRecordRepository;

    @Autowired
    private SleepRecordRepository sleepRecordRepository;

    @Override
    public void run(String... args) {
        // 检查 chat_records 表是否为空，若为空则自动执行种子数据生成
        // 避免每次启动都重建数据：仅在无数据时初始化
        long count = chatRecordRepository.count();
        if (count == 0) {
            log.info("chat_records 表为空，自动执行种子数据初始化...");
            seed();
        } else {
            log.info("chat_records 表已有 {} 条记录，跳过自动初始化。" +
                    " 如需重建数据请调用 POST /api/seed/health", count);
        }
    }

    /**
     * 执行全量种子数据生成（先清空再插入，保证幂等）。
     */
    public void seed() {
        LocalDate today = LocalDate.now();
        log.info("========== 开始生成健康数据种子（{}天，基准日期={}）==========", TOTAL_DAYS, today);

        // 清空旧数据
        clearAll();

        // 生成各表数据
        seedMusicLogs(FIXED_USER_ID, today);
        seedChatRecords(FIXED_USER_ID, today);
        seedItemFindLogs(FIXED_USER_ID, today);
        seedSleepRecords(FIXED_USER_ID, today);
        seedAiServiceRecords(FIXED_USER_ID, today);

        log.info("========== 健康数据种子生成完成 ==========");
    }

    /**
     * 清空所有五张表的旧数据。
     */
    private void clearAll() {
        aiServiceRecordRepository.deleteAll();
        sleepRecordRepository.deleteAll();
        itemFindLogRepository.deleteAll();
        chatRecordRepository.deleteAll();
        musicLogRepository.deleteAll();
        log.info("已清空 5 张表旧数据");
    }

    // ==================== 音乐日志生成 ====================

    private void seedMusicLogs(String userId, LocalDate today) {
        List<MusicLog> records = new ArrayList<>();

        for (Object[] row : MUSIC_DATA) {
            MusicLog logEntity = new MusicLog();
            logEntity.setUserId(userId);
            logEntity.setDate(today.minusDays((int) row[0]));
            logEntity.setSongName((String) row[1]);
            logEntity.setDurationMinutes((int) row[2]);
            logEntity.setScene((String) row[3]);
            logEntity.setCreatedAt(LocalDateTime.now());
            records.add(logEntity);
        }

        musicLogRepository.saveAll(records);
        log.info("已生成 {} 条音乐日志", records.size());
    }

    // ==================== 陪伴对话记录生成（30天） ====================

    /**
     * 生成 30 天陪伴对话记录。
     * 近7天（daysAgo 0-6）使用精心编排的焦虑导向详细数据，
     * 前23天（daysAgo 7-29）中的关键日期使用显式数据，
     * 其余日期使用模板生成（均以负向情绪为主导）。
     *
     * <p>情绪分布设计（焦虑导向）：</p>
     * <ul>
     *   <li>日维度（today）：正向40% 负向60% → "焦虑" 4.0分</li>
     *   <li>周维度（7天）：正向23% 负向77% → "焦虑" 2.3分</li>
     *   <li>月维度（30天）：正向~30% 负向~70% → "焦虑" ~3.0分</li>
     * </ul>
     */
    private void seedChatRecords(String userId, LocalDate today) {
        List<ChatRecord> records = new ArrayList<>();

        // 过去30天：daysAgo = 29（最早）~ 0（今天）
        for (int daysAgo = 29; daysAgo >= 0; daysAgo--) {
            LocalDate date = today.minusDays(daysAgo);
            List<String[]> dayRecords;

            if (daysAgo <= 6 || DETAILED_CHAT_DATA.containsKey(daysAgo)) {
                // 近7天 + 关键日期：使用精心编排的详细数据
                dayRecords = DETAILED_CHAT_DATA.getOrDefault(daysAgo, new ArrayList<>());
            } else {
                // 其余日期：程序化生成（3~5条/天，情绪偏负向 ~70%）
                dayRecords = generateEarlyDayRecords(daysAgo);
            }

            for (String[] row : dayRecords) {
                ChatRecord record = new ChatRecord();
                record.setUserId(userId);
                record.setDate(date);
                record.setMessage(row[0]);
                record.setEmotion(row[1]);
                record.setCreatedAt(LocalDateTime.now());
                records.add(record);
            }
        }

        chatRecordRepository.saveAll(records);
        log.info("已生成 {} 条陪伴记录（覆盖30天，日/周/月维度均判定为【焦虑】）", records.size());
    }

    /**
     * 为更早的日期（daysAgo 7~29，不含已在 DETAILED_CHAT_DATA 中的关键日期）
     * 生成程序化陪伴记录（3~5条/天）。
     *
     * <p>模板池情绪分布：开心5/34(15%)、平静6/34(18%)、低落7/34(21%)、
     * 焦虑8/34(24%)、孤单4/34(12%)、思念4/34(12%)</p>
     * <p>负向合计 23/34 ≈ 68%，确保月维度整体判定为"焦虑"。</p>
     *
     * <p>所有对话内容采用口语化短句风格，体现焦虑+需要陪伴。</p>
     */
    private List<String[]> generateEarlyDayRecords(int daysAgo) {
        List<String[]> records = new ArrayList<>();

        // 确定性种子，保证每次生成一致
        int base = daysAgo % 31;

        // 模板池：34条口语化对话，按情绪标签分类
        // 分布：开心5(15%)、平静6(18%)、低落7(21%)、焦虑8(24%)、孤单4(12%)、思念4(12%)
        // 负向合计 23/34 ≈ 68%
        String[][] templates = {
                // ---- 开心（5条）— 15% ----
                {"今天煮了碗面，放了点葱花还挺香的", "开心"},
                {"社区小王来坐了坐，有人说话真好", "开心"},
                {"阳台上的月季又开了一朵，红艳艳的", "开心"},
                {"中午做了个红烧豆腐，手艺还没丢", "开心"},
                {"隔壁送了几个粽子来，热乎乎的挺好吃", "开心"},

                // ---- 平静（6条）— 18% ----
                {"下午在阳台坐了一会儿，晒晒太阳", "平静"},
                {"听了一会儿收音机，刚好放到喜欢的老歌", "平静"},
                {"晚上织了几行毛衣，也算有点事做", "平静"},
                {"今天没出门，就在屋里收拾了一下", "平静"},
                {"看了会儿电视，有个节目讲养花的，挺好", "平静"},
                {"晚饭后下楼扔了趟垃圾，顺便透了口气", "平静"},

                // ---- 低落（7条）— 21% ----
                {"一整天没出门，也不知道在屋里干啥", "低落"},
                {"今天没什么胃口，就喝了几口粥", "低落"},
                {"人老了就这样，一天不如一天了", "低落"},
                {"今天又是什么都没干，坐着坐着天就黑了", "低落"},
                {"心里闷闷的，也说不上来为什么", "低落"},
                {"唉，觉得什么都没意思，连电视都不想开", "低落"},
                {"午饭就热了口剩饭，一个人吃什么都一样", "低落"},

                // ---- 焦虑（8条）— 24% ----
                {"头又晕了，不知道是不是血压高了", "焦虑"},
                {"晚上老醒，醒了就睡不着，越想越精神", "焦虑"},
                {"记性越来越差了，刚放的东西转头就找不到", "焦虑"},
                {"胸口有时候闷得慌，也不敢跟孩子们说", "焦虑"},
                {"药还有几天就吃完了，也不想去医院排队", "焦虑"},
                {"半夜醒了就再也睡不着，睁眼到天亮", "焦虑"},
                {"膝盖疼得厉害，走路都费劲", "焦虑"},
                {"这两天头晕得厉害，怕摔倒了没人知道", "焦虑"},

                // ---- 孤单（4条）— 12% ----
                {"一天又过去了，连个说话的人都没有", "孤单"},
                {"一个人吃饭，一个人看电视，干啥都是一个人", "孤单"},
                {"楼下挺热闹的，就我这屋里安安静静", "孤单"},
                {"对门老刘搬走了，连个串门的人都没了", "孤单"},

                // ---- 思念（4条）— 12% ----
                {"翻到老伴的照片，看了好一会儿", "思念"},
                {"好久没见孙子了，也不知道还认不认得我", "思念"},
                {"以前这时候正跟老伴一起看电视呢，唉", "思念"},
                {"想老伴了，以前这时候她总会给我泡杯茶", "思念"},
        };

        // 每天3~5条，从模板中确定性选取
        int count = 3 + (daysAgo % 3);  // 3, 4, 5 循环
        for (int i = 0; i < count; i++) {
            int idx = (base + i * 13) % templates.length;
            records.add(new String[]{templates[idx][0], templates[idx][1]});
        }

        return records;
    }

    // ==================== 物品寻找记录生成 ====================

    private void seedItemFindLogs(String userId, LocalDate today) {
        List<ItemFindLog> records = new ArrayList<>();

        for (Object[] row : ITEM_FIND_DATA) {
            ItemFindLog logEntity = new ItemFindLog();
            logEntity.setUserId(userId);
            logEntity.setDate(today.minusDays((int) row[0]));
            logEntity.setItemName((String) row[1]);
            logEntity.setDurationSeconds((int) row[2]);
            logEntity.setPosition((String) row[3]);
            logEntity.setFound(true);  // 100% 找到
            logEntity.setCreatedAt(LocalDateTime.now());
            records.add(logEntity);
        }

        itemFindLogRepository.saveAll(records);
        log.info("已生成 {} 条物品寻找记录（全部已找到）", records.size());
    }

    // ==================== 睡眠记录生成（近7天） ====================

    /**
     * 为 elder_001 生成近7天睡眠数据，模拟老人真实生理波动。
     * 数据模式：时长3-9h震荡，深睡比15-68%跳动，醒来0-5次。
     */
    private void seedSleepRecords(String userId, LocalDate today) {
        // 近7天睡眠数据（daysAgo=6到0）
        Object[][] sleepData = {
                {6, 6.2, 25, 1, 78},
                {5, 5.8, 22, 2, 65},
                {4, 6.5, 28, 1, 82},
                {3, 5.5, 20, 2, 60},
                {2, 6.0, 24, 1, 72},
                {1, 5.3, 18, 2, 55},
                {0, 6.3, 26, 1, 80},
        };

        List<SleepRecord> records = new ArrayList<>();
        for (Object[] row : sleepData) {
            int daysAgo = (int) row[0];
            SleepRecord sr = new SleepRecord();
            sr.setElderId(userId);
            sr.setInBed(true);
            sr.setBedTime("22:30");
            sr.setTotalSleepHours((Double) row[1]);
            sr.setDeepSleepPercent((Integer) row[2]);
            sr.setWakeCount((Integer) row[3]);
            sr.setQualityScore((Integer) row[4]);
            LocalDateTime recordTime = today.minusDays(daysAgo).atTime(7, 0, 0);
            sr.setRecordedAt(recordTime);
            sr.setCreatedAt(LocalDateTime.now());
            records.add(sr);
        }

        sleepRecordRepository.saveAll(records);
        log.info("已生成 {} 条睡眠记录（近7天）", records.size());
    }

    // ==================== AI 服务记录生成（近7天） ====================

    /**
     * 为 elder_001 生成近7天 AI 服务记录（陪伴对话、物品寻找、音乐疗法）。
     * 数据来源：ai_service_record 表，用于健康数据看板的 companion/music/itemFinding 面板。
     */
    private void seedAiServiceRecords(String userId, LocalDate today) {
        List<AiServiceRecord> records = new ArrayList<>();

        // ---- 陪伴记录（companion_chat）：近7天，每天1-2条 ----
        Object[][] companionData = {
                {6, "09:30", "开心", "#4CAF50", "老人与AI聊起孙子考上大学的事，笑得合不拢嘴"},
                {6, "10:00", "孤单", "#607D8B", "老人说好几天没人说话了有些闷，AI主动聊起他喜欢的戏曲"},
                {5, "16:00", "开心", "#4CAF50", "女儿打电话说要周末来看望，老人高兴了一个下午"},
                {5, "20:00", "焦虑", "#FF9800", "老人担心最近血压偏高，AI安抚情绪并调出健康数据供参考"},
                {4, "07:30", "平静", "#2196F3", "老人晨练后在阳台喝茶听评书，表示今天心情不错"},
                {4, "15:00", "低落", "#607D8B", "老人说今天下雨腿疼不想动，情绪有些低落"},
                {3, "10:00", "焦虑", "#FF9800", "老人担心血压波动和糖尿病管理，AI安慰并给出日常建议"},
                {3, "19:00", "开心", "#4CAF50", "护工陪老人聊家常，说说笑笑心情好了很多"},
                {2, "08:30", "期待", "#FF9800", "社区通知下周三组织体检，老人表示会按时参加"},
                {2, "14:00", "思念", "#9C27B0", "翻到老伴以前写的信，看着看着眼泪就下来了"},
                {1, "09:00", "焦虑", "#FF9800", "老人说药快吃完了，医院太远没人帮开，心里着急"},
                {1, "16:00", "开心", "#4CAF50", "闺女打视频过来，说下个月接老人去住几天，高兴坏了"},
                {0, "08:00", "低落", "#607D8B", "今天不太想动，浑身没力气，心里闷闷的"},
                {0, "11:00", "平静", "#2196F3", "护工说下午有个手工活动，老人想去折个纸花凑热闹"},
        };

        int cpIdx = 1;
        for (Object[] row : companionData) {
            AiServiceRecord r = new AiServiceRecord();
            r.setRecordId("ai_cp_seed_" + String.format("%03d", cpIdx++));
            r.setElderId(userId);
            r.setServiceType("companion_chat");
            r.setEmotion((String) row[2]);
            r.setEmotionColor((String) row[3]);
            r.setSummary((String) row[4]);
            int daysAgo = (int) row[0];
            String timeStr = (String) row[1];
            LocalDateTime interactionTime = LocalDateTime.of(
                    today.minusDays(daysAgo),
                    java.time.LocalTime.parse(timeStr));
            r.setInteractionTime(interactionTime);
            r.setCreatedAt(interactionTime.plusMinutes(1));
            records.add(r);
        }

        // ---- 物品寻找记录（find_item）：近7天，每天0-1条 ----
        Object[][] findItemData = {
                {6, "老花镜", "床头柜抽屉里", "found"},
                {5, "遥控器", "沙发坐垫缝里", "found"},
                {4, "降压药", "餐桌水杯旁", "found"},
                {3, "手机", "厨房微波炉旁", "found"},
                {2, "老花镜", "客厅茶几上", "found"},
                {1, "水杯", "阳台小桌上", "found"},
                {0, "收音机", "床头柜上", "found"},
        };

        for (int i = 0; i < findItemData.length; i++) {
            Object[] row = findItemData[i];
            AiServiceRecord r = new AiServiceRecord();
            r.setRecordId("ai_vlm_seed_" + String.format("%03d", i + 1));
            r.setElderId(userId);
            r.setServiceType("find_item");
            r.setItem((String) row[1]);
            r.setLocation((String) row[2]);
            r.setResult((String) row[3]);
            int daysAgo = (int) row[0];
            LocalDateTime interactionTime = today.minusDays(daysAgo).atTime(10, 0, 0);
            r.setInteractionTime(interactionTime);
            r.setCreatedAt(interactionTime.minusSeconds(30));
            records.add(r);
        }

        // ---- 音乐疗法记录（music_control）：近7天，分散在几天 ----
        Object[][] musicData = {
                {6, "轻音乐", "放点轻音乐吧，今天想安静会儿。"},
                {4, "京剧", "来段京剧，要《空城计》。"},
                {3, "古筝", "播放《高山流水》，肩膀有点酸痛。"},
                {1, "红歌", "今天心情不错，来首红歌。"},
                {0, "古筝", "放点舒缓的，晚上好入睡。"},
        };

        for (int i = 0; i < musicData.length; i++) {
            Object[] row = musicData[i];
            AiServiceRecord r = new AiServiceRecord();
            r.setRecordId("ai_mus_seed_" + String.format("%03d", i + 1));
            r.setElderId(userId);
            r.setServiceType("music_control");
            r.setMusicType((String) row[1]);
            r.setUserText((String) row[2]);
            int daysAgo = (int) row[0];
            LocalDateTime interactionTime = today.minusDays(daysAgo).atTime(15, 0, 0);
            r.setInteractionTime(interactionTime);
            r.setCreatedAt(interactionTime.plusMinutes(1));
            records.add(r);
        }

        aiServiceRecordRepository.saveAll(records);
        log.info("已生成 {} 条 AI 服务记录（陪伴{}条 + 找物{}条 + 音乐{}条）",
                records.size(), companionData.length, findItemData.length, musicData.length);
    }
}
