package com.anxinban.seeders;

import com.anxinban.entity.ChatRecord;
import com.anxinban.entity.ItemFindLog;
import com.anxinban.entity.MusicLog;
import com.anxinban.mapper.ChatRecordRepository;
import com.anxinban.mapper.ItemFindLogRepository;
import com.anxinban.mapper.MusicLogRepository;
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
 * @author anxinban-team
 * @since 0.0.1-SNAPSHOT
 */
@Component
public class HealthDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(HealthDataSeeder.class);
    private static final String FIXED_USER_ID = "1";
    private static final int TOTAL_DAYS = 30;

    // ==================== 陪伴记录 — 近7天详细消息（daysAgo 0~6，0=今天） ====================
    // 情绪分布设计：
    //   今天(0)：开心+平静 占多数 → 日维度"平稳"
    //   近7天整体：焦虑+低落+孤单+思念 占多数 → 周/月维度"焦虑"
    // 仅使用6种情绪标签：开心、平静、低落、焦虑、孤单、思念

    private static final Map<Integer, List<String[]>> DETAILED_CHAT_DATA = new LinkedHashMap<>();

    static {
        // ---- daysAgo=6（6天前 2026-07-16）----
        DETAILED_CHAT_DATA.computeIfAbsent(6, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"唉，今天又没睡好，半夜醒了好几次", "低落"},
                        new String[]{"心里闷得慌，说不上来为什么，就是觉得难受", "低落"},
                        new String[]{"血压药好像快吃完了，也不知道啥时候去买", "焦虑"},
                        new String[]{"孩子说好周末来接我，也不知道会不会又忘了", "思念"}
                ));
        // ---- daysAgo=5（5天前 2026-07-17）----
        DETAILED_CHAT_DATA.computeIfAbsent(5, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"儿子说这周末来看我，又说不来了，算了习惯了", "低落"},
                        new String[]{"天热得睡不着，开风扇又怕着凉，折腾到半夜", "焦虑"},
                        new String[]{"一天到晚连个说话的人都没有，就电视陪着我", "孤单"},
                        new String[]{"隔壁老李搬去儿子家住了，以后连个下棋的人都没了", "孤单"}
                ));
        // ---- daysAgo=4（4天前 2026-07-18）----
        DETAILED_CHAT_DATA.computeIfAbsent(4, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"老张住院了，听说是心脏的事，我这两天心里也慌慌的", "焦虑"},
                        new String[]{"老人提到睡眠质量下降，影响白天状态", "焦虑"},
                        new String[]{"人老了就是不中用，走两步就喘", "低落"},
                        new String[]{"晚上翻来覆去睡不着，脑子里想的都是以前的事", "低落"}
                ));
        // ---- daysAgo=3（3天前 2026-07-19）----
        DETAILED_CHAT_DATA.computeIfAbsent(3, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"晚饭就我一个人，随便热了口剩饭", "孤单"},
                        new String[]{"老伴走了三年了，昨天梦到她了，还是老样子", "思念"},
                        new String[]{"一个人坐着坐着就发呆，连电视都忘了开", "低落"},
                        new String[]{"身体一天不如一天，也不知道还能撑多久", "焦虑"}
                ));
        // ---- daysAgo=2（2天前 2026-07-20）----
        DETAILED_CHAT_DATA.computeIfAbsent(2, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"电视也没什么好看的，换个台换个台还是那样", "低落"},
                        new String[]{"楼下的门锁好像坏了，晚上睡觉总觉得不安全", "焦虑"},
                        new String[]{"老人询问就医事宜，表现出对健康的担忧", "焦虑"},
                        new String[]{"膝盖疼得厉害，走路都费劲，也没人陪我去医院", "低落"}
                ));
        // ---- daysAgo=1（昨天 2026-07-21）— 开心1+平静2+低落1+焦虑1 → 正向为主但含少量负向 ----
        DETAILED_CHAT_DATA.computeIfAbsent(1, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"孙女视频给我看了她画的画，画得真像啊", "开心"},
                        new String[]{"下午在阳台晒了会儿太阳，顺便织了几行毛衣", "平静"},
                        new String[]{"晚饭后听了一会儿老歌，心里舒坦了些", "平静"},
                        new String[]{"今天不想出门，外面闹哄哄的，没意思", "低落"},
                        new String[]{"天气说变就变，老寒腿又开始隐隐作痛了", "焦虑"}
                ));
        // ---- daysAgo=0（今天 2026-07-22）— 积极情绪为主 → 日维度"平稳" ----
        DETAILED_CHAT_DATA.computeIfAbsent(0, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"今天天气不错，在小区里散了会儿步，桂花开了真香", "开心"},
                        new String[]{"中午烧了条红烧鱼，手艺还没退步，一个人也要好好吃饭", "开心"},
                        new String[]{"下午听了会儿收音机，评书讲的是三国，听得入迷", "平静"},
                        new String[]{"邻居李阿姨送了几个粽子过来，说是她闺女包的，真是热心", "开心"},
                        new String[]{"晚上给阳台的花浇了水，月季又开了两朵，心情舒畅", "平静"}
                ));

        // ---- 早期关键日期（用于月维度 keyEvents 展示）----
        // daysAgo=8（2026-07-14）
        DETAILED_CHAT_DATA.computeIfAbsent(8, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"老人提到最近晚上总是睡不着，有些烦躁", "焦虑"},
                        new String[]{"白天也没什么精神，看电视看着看着就睡着了", "低落"},
                        new String[]{"药还有几天就吃完了，得找人帮忙去开", "焦虑"}
                ));
        // daysAgo=13（2026-07-09）
        DETAILED_CHAT_DATA.computeIfAbsent(13, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"老朋友生病住院的消息让老人情绪低落，AI安慰并建议打电话问候", "低落"},
                        new String[]{"今天天热，胃口不好，就喝了点粥", "低落"},
                        new String[]{"翻出了以前的老相册，看了好久", "思念"}
                ));
        // daysAgo=20（2026-07-02）
        DETAILED_CHAT_DATA.computeIfAbsent(20, k -> new ArrayList<>())
                .addAll(List.of(
                        new String[]{"老人说好几天没人说话有些闷，AI主动播放了他喜欢的戏曲", "孤单"},
                        new String[]{"楼下在施工，吵得人心烦", "焦虑"},
                        new String[]{"中午在阳台上坐了一会儿，晒晒太阳也挺好", "平静"}
                ));
    }

    // 近7天详细数据的情绪分布总结：
    // 今天(0)：开心3 + 平静2 → 正向100% → 日维度"平稳" ✓
    // daysAgo 1-6 共25条（每天4-5条）：全为负向（焦虑+低落+孤单+思念），仅 daysAgo 1 含3条正向
    // 实际 daysAgo 0-6（7天）：正向(开心+平静)≈8，负向(焦虑+低落+孤单+思念)≈22
    // 负向占比 ≈ 73% > 50% → 周/月维度"焦虑" ✓

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

        log.info("========== 健康数据种子生成完成 ==========");
    }

    /**
     * 清空所有三张表的旧数据。
     */
    private void clearAll() {
        itemFindLogRepository.deleteAll();
        chatRecordRepository.deleteAll();
        musicLogRepository.deleteAll();
        log.info("已清空 3 张表旧数据");
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
     * 近7天 + 早期关键日期使用详细数据，其余日期使用程序化生成的数据。
     */
    private void seedChatRecords(String userId, LocalDate today) {
        List<ChatRecord> records = new ArrayList<>();

        // 过去30天：daysAgo = 29（最早）~ 0（今天）
        for (int daysAgo = 29; daysAgo >= 0; daysAgo--) {
            LocalDate date = today.minusDays(daysAgo);
            List<String[]> dayRecords;

            if (daysAgo <= 6 || DETAILED_CHAT_DATA.containsKey(daysAgo)) {
                // 近7天 + 早期关键日期：使用详细数据
                dayRecords = DETAILED_CHAT_DATA.getOrDefault(daysAgo, new ArrayList<>());
            } else {
                // 更早的23天：程序化生成（情绪倾向焦虑/低落为主，与近7天的趋势一致但略有变化）
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
        log.info("已生成 {} 条陪伴记录（含近7天详细+前23天补充，覆盖30天）", records.size());
    }

    /**
     * 为更早的日期（daysAgo 7~29）生成程序化陪伴记录。
     * 情绪分布：低落~30%、焦虑~25%、平静~20%、孤单~10%、思念~10%、开心~5%
     * 整体趋势偏负向，与周/月维度"焦虑"标签一致。
     */
    private List<String[]> generateEarlyDayRecords(int daysAgo) {
        List<String[]> records = new ArrayList<>();

        // 使用 daysAgo 作为种子，确保同一天的数据固定
        int base = daysAgo % 20;

        // 每条记录：(消息模板, 情绪标签)
        String[][] templates = {
                {"晚上翻来覆去睡不着，脑子里乱糟糟的", "焦虑"},
                {"白天没什么精神，坐着坐着就发呆", "低落"},
                {"楼下在修路，吵得人心烦意乱", "焦虑"},
                {"今天做了个简单的午饭，随便对付一口", "平静"},
                {"翻出老照片看了看，那时候真好", "思念"},
                {"一个人坐在阳台上，看着楼下人来人往", "孤单"},
                {"腿脚不太方便，想出去走走又怕摔倒", "低落"},
                {"听了一会儿老歌，心里舒坦了些", "平静"},
                {"药快吃完了，得找人帮忙去社区医院开", "焦虑"},
                {"中午在小区长椅上坐了一会儿，晒晒太阳", "平静"},
                {"又想起以前上班的时候，同事们挺好的", "思念"},
                {"楼上的小年轻半夜还在闹，睡不好觉", "低落"},
                {"电视里放的是二十年前的电视剧，那时候多好", "思念"},
                {"今天胃口不太好，就喝了碗稀饭", "低落"},
                {"手机又不会用了，按来按去也打不开视频", "焦虑"},
                {"邻居家孙子来了，在走廊里跑来跑去挺热闹", "开心"},
                {"今天跟护工聊了几句，有人说话的感觉真好", "平静"},
                {"下雨了，膝盖又开始疼了，老毛病了", "低落"},
                {"儿子打电话来了，虽然就几分钟，心里暖暖的", "开心"},
                {"记性越来越差，刚放的东西转头就忘了", "焦虑"},
        };

        // 每天生成 3 条记录，从模板中选取
        for (int i = 0; i < 3; i++) {
            int idx = (base + i * 7) % templates.length;
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
}
