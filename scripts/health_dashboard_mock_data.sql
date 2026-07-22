-- ============================================================
-- 健康数据看板 - 数据库迁移与模拟数据脚本
-- 版本: 2026-07-21
-- 说明: 为"个人中心-健康数据看板"功能提供模拟数据
--       1. 给 sleep_record 表添加 total_sleep_hours 字段（如不存在）
--       2. 为 elder_001 插入近7天睡眠数据（强波动）
--       3. 为 elder_001 插入近30天 ai_service_record 数据
--          - 陪伴记录 25 条（近30天，多种情绪）
--          - 物品寻找 8 条
--          - 音乐疗法 5 条
--       数量层级: 陪伴记录(25) >> 找物品(8) >> 音乐疗法(5)
-- ============================================================

-- total_sleep_hours 字段已存在，无需重复添加

-- ============================================================
-- 为 elder_001 插入近7天睡眠数据（2026-07-15 ~ 2026-07-21）
-- 模拟老人真实生理波动：时长3-9h震荡，深睡比15-75%跳动，醒来0-5次
-- 注意: id 由数据库自增生成，无需手动指定
-- ============================================================
INSERT INTO sleep_record (elder_id, in_bed, bed_time, total_sleep_hours, deep_sleep_percent, wake_count, quality_score, recorded_at, created_at) VALUES
('elder_001', 1, '22:30', 6.8,  42, 1, 78, '2026-07-15 07:00:00', '2026-07-15 07:01:00'),
('elder_001', 1, '23:00', 4.2,  18, 4, 45, '2026-07-16 06:30:00', '2026-07-16 06:31:00'),
('elder_001', 1, '21:45', 8.5,  55, 0, 90, '2026-07-17 07:15:00', '2026-07-17 07:16:00'),
('elder_001', 1, '22:00', 3.5,  12, 5, 32, '2026-07-18 05:45:00', '2026-07-18 05:46:00'),
('elder_001', 1, '22:15', 7.2,  38, 2, 75, '2026-07-19 07:00:00', '2026-07-19 07:01:00'),
('elder_001', 1, '23:30', 5.1,  25, 3, 58, '2026-07-20 07:30:00', '2026-07-20 07:31:00'),
('elder_001', 1, '22:00', 9.1,  68, 0, 95, '2026-07-21 06:45:00', '2026-07-21 06:46:00');

-- ============================================================
-- 为 elder_001 插入近30天陪伴记录（companion_chat）— 25 条
-- 情绪多样：开心/满足/平静/低落/焦虑/思念/孤单/期待/感动...
-- 时间范围: 2026-07-01 ~ 2026-07-20（近期数据）
-- ============================================================
INSERT INTO ai_service_record (record_id, elder_id, service_type, user_text, ai_reply, emotion, emotion_color, item, location, result, summary, music_type, interaction_time, created_at) VALUES
('ai_cp_dash_001', 'elder_001', 'companion_chat', NULL, NULL, '开心', '#4CAF50', NULL, NULL, NULL, '老人与AI聊起孙子考上大学的事，笑得合不拢嘴', NULL, '2026-07-01 09:30:00', '2026-07-01 09:31:00'),
('ai_cp_dash_002', 'elder_001', 'companion_chat', NULL, NULL, '满足', '#4CAF50', NULL, NULL, NULL, '老人自己做了一顿红烧肉，吃得心满意足，和AI分享做菜心得', NULL, '2026-07-02 12:00:00', '2026-07-02 12:01:00'),
('ai_cp_dash_003', 'elder_001', 'companion_chat', NULL, NULL, '低落', '#9C27B0', NULL, NULL, NULL, '老人说今天下雨腿疼不想动，情绪有些低落，AI播放了戏曲安慰', NULL, '2026-07-03 15:00:00', '2026-07-03 15:01:00'),
('ai_cp_dash_004', 'elder_001', 'companion_chat', NULL, NULL, '平静', '#2196F3', NULL, NULL, NULL, '老人边晒太阳边和AI聊天气，心情平和惬意', NULL, '2026-07-04 10:00:00', '2026-07-04 10:01:00'),
('ai_cp_dash_005', 'elder_001', 'companion_chat', NULL, NULL, '期待', '#FF9800', NULL, NULL, NULL, '社区通知下周三组织体检，老人表示会按时参加', NULL, '2026-07-05 08:30:00', '2026-07-05 08:31:00'),
('ai_cp_dash_006', 'elder_001', 'companion_chat', NULL, NULL, '开心', '#4CAF50', NULL, NULL, NULL, '女儿打电话说要周末来看望，老人高兴了一个下午', NULL, '2026-07-06 16:00:00', '2026-07-06 16:01:00'),
('ai_cp_dash_007', 'elder_001', 'companion_chat', NULL, NULL, '感动', '#E91E63', NULL, NULL, NULL, '邻居送来亲手包的粽子，老人感动得眼眶湿润，说社区邻里真好', NULL, '2026-07-07 11:00:00', '2026-07-07 11:01:00'),
('ai_cp_dash_008', 'elder_001', 'companion_chat', NULL, NULL, '焦虑', '#FF9800', NULL, NULL, NULL, '老人担心最近血压偏高，AI安抚情绪并调出健康数据供参考', NULL, '2026-07-08 09:00:00', '2026-07-08 09:01:00'),
('ai_cp_dash_009', 'elder_001', 'companion_chat', NULL, NULL, '安心', '#8BC34A', NULL, NULL, NULL, '查看血压记录后发现数值正常，老人松了一口气，心情好转', NULL, '2026-07-09 09:30:00', '2026-07-09 09:31:00'),
('ai_cp_dash_010', 'elder_001', 'companion_chat', NULL, NULL, '自豪', '#4CAF50', NULL, NULL, NULL, '老人在阳台种的花开了，拍照给AI看，分享养花的成就感', NULL, '2026-07-01 10:00:00', '2026-07-01 10:01:00'),
('ai_cp_dash_011', 'elder_001', 'companion_chat', NULL, NULL, '孤单', '#607D8B', NULL, NULL, NULL, '老人说好几天没人说话了有些闷，AI主动聊起他喜欢的戏曲', NULL, '2026-07-02 14:00:00', '2026-07-02 14:01:00'),
('ai_cp_dash_012', 'elder_001', 'companion_chat', NULL, NULL, '开心', '#4CAF50', NULL, NULL, NULL, 'AI帮老人接通了儿子的视频电话，父子聊了半小时', NULL, '2026-07-03 19:00:00', '2026-07-03 19:01:00'),
('ai_cp_dash_013', 'elder_001', 'companion_chat', NULL, NULL, '满足', '#4CAF50', NULL, NULL, NULL, '老人今天去社区活动中心下棋赢了两局，心情特别好', NULL, '2026-07-04 16:30:00', '2026-07-04 16:31:00'),
('ai_cp_dash_014', 'elder_001', 'companion_chat', NULL, NULL, '疲惫', '#795548', NULL, NULL, NULL, '老人今天去超市走了不少路说有点累，AI提醒早点休息', NULL, '2026-07-05 20:00:00', '2026-07-05 20:01:00'),
('ai_cp_dash_015', 'elder_001', 'companion_chat', NULL, NULL, '温暖', '#4CAF50', NULL, NULL, NULL, '老人回忆年轻时在工厂的往事，AI认真倾听并称赞那个年代的人', NULL, '2026-07-06 10:30:00', '2026-07-06 10:31:00'),
('ai_cp_dash_016', 'elder_001', 'companion_chat', NULL, NULL, '开心', '#4CAF50', NULL, NULL, NULL, '今天是老人生日，AI唱了生日歌，老人笑得像个孩子', NULL, '2026-07-07 08:00:00', '2026-07-07 08:01:00'),
('ai_cp_dash_017', 'elder_001', 'companion_chat', NULL, NULL, '平静', '#2196F3', NULL, NULL, NULL, '老人晨练后在阳台喝茶听评书，表示今天心情不错', NULL, '2026-07-08 07:30:00', '2026-07-08 07:31:00'),
('ai_cp_dash_018', 'elder_001', 'companion_chat', NULL, NULL, '低落', '#9C27B0', NULL, NULL, NULL, '老朋友生病住院的消息让老人情绪低落，AI安慰并建议打电话问候', NULL, '2026-07-09 11:00:00', '2026-07-09 11:01:00'),
('ai_cp_dash_019', 'elder_001', 'companion_chat', NULL, NULL, '欣慰', '#8BC34A', NULL, NULL, NULL, '老人给老朋友打完电话知道对方好转了，心情放松了许多', NULL, '2026-07-10 15:00:00', '2026-07-10 15:01:00'),
('ai_cp_dash_020', 'elder_001', 'companion_chat', NULL, NULL, '着急', '#FF5722', NULL, NULL, NULL, '老人的医保卡找不到了很着急，AI安抚情绪并建议先找找抽屉', NULL, '2026-07-11 09:00:00', '2026-07-11 09:01:00'),
('ai_cp_dash_021', 'elder_001', 'companion_chat', NULL, NULL, '放松', '#00BCD4', NULL, NULL, NULL, '医保卡找到后老人舒了一口气，AI播放轻音乐帮助放松', NULL, '2026-07-12 09:30:00', '2026-07-12 09:31:00'),
('ai_cp_dash_022', 'elder_001', 'companion_chat', NULL, NULL, '开心', '#4CAF50', NULL, NULL, NULL, '女儿寄来的新衣服到了，老人试穿后非常满意', NULL, '2026-07-13 14:00:00', '2026-07-13 14:01:00'),
('ai_cp_dash_023', 'elder_001', 'companion_chat', NULL, NULL, '平静', '#2196F3', NULL, NULL, NULL, '老人看了一下午电视，和AI聊了聊剧情，说今天生活很规律', NULL, '2026-07-14 17:00:00', '2026-07-14 17:01:00'),
('ai_cp_dash_024', 'elder_001', 'companion_chat', NULL, NULL, '期待', '#FF9800', NULL, NULL, NULL, '老人说周末女儿要带孙子来，已经开始计划做什么菜了', NULL, '2026-07-15 10:00:00', '2026-07-15 10:01:00'),
('ai_cp_dash_025', 'elder_001', 'companion_chat', NULL, NULL, '开心', '#4CAF50', NULL, NULL, NULL, '昨晚睡眠质量好，老人早上精神焕发，夸AI推荐的助眠音乐有用', NULL, '2026-07-20 08:00:00', '2026-07-20 08:01:00');

-- ============================================================
-- 为 elder_001 插入物品寻找记录（find_item）— 8 条
-- 时间分散在近两周
-- ============================================================
INSERT INTO ai_service_record (record_id, elder_id, service_type, user_text, ai_reply, emotion, emotion_color, item, location, result, summary, music_type, interaction_time, created_at) VALUES
('ai_vlm_dash_001', 'elder_001', 'find_item', '我的眼镜放哪了？早上起来找不到了。', '眼镜在床头柜上的眼镜盒旁边，您昨晚睡前放那儿的。', NULL, NULL, '眼镜', '床头柜', 'found', NULL, NULL, '2026-07-10 07:15:00', '2026-07-10 07:16:00'),
('ai_vlm_dash_002', 'elder_001', 'find_item', '遥控器在哪？我要看新闻。', '遥控器在沙发坐垫缝里，您昨晚看电视时可能滑进去了。', NULL, NULL, '遥控器', '沙发坐垫缝', 'found', NULL, NULL, '2026-07-12 19:00:00', '2026-07-12 19:01:00'),
('ai_vlm_dash_003', 'elder_001', 'find_item', '我的降压药找不到了，明明放在桌上的。', '药盒在餐桌水杯旁边，您吃完早饭顺手放那儿了，别着急。', NULL, NULL, '降压药', '餐桌水杯旁', 'found', NULL, NULL, '2026-07-14 08:30:00', '2026-07-14 08:31:00'),
('ai_vlm_dash_004', 'elder_001', 'find_item', '手机不知道放哪儿了，能帮我找找吗？', '手机在厨房微波炉旁边，您热牛奶的时候拿过去忘了带回来。', NULL, NULL, '手机', '厨房微波炉旁', 'found', NULL, NULL, '2026-07-15 09:45:00', '2026-07-15 09:46:00'),
('ai_vlm_dash_005', 'elder_001', 'find_item', '老花镜又找不到了，真是老了记性不好。', '老花镜在客厅茶几上，您看报的时候放在那儿了。', NULL, NULL, '老花镜', '客厅茶几', 'found', NULL, NULL, '2026-07-16 15:00:00', '2026-07-16 15:01:00'),
('ai_vlm_dash_006', 'elder_001', 'find_item', '我的手套少了一只，出门散步要戴。', '另一只手套在鞋柜旁边的地上，可能是您换鞋时掉的。帮您捡起来了。', NULL, NULL, '手套', '鞋柜旁地上', 'found', NULL, NULL, '2026-07-17 07:00:00', '2026-07-17 07:01:00'),
('ai_vlm_dash_007', 'elder_001', 'find_item', '水杯呢？刚泡的茶想端过来。', '保温杯在阳台小桌上，您晒太阳时拿出去的。', NULL, NULL, '保温杯', '阳台小桌', 'found', NULL, NULL, '2026-07-18 10:30:00', '2026-07-18 10:31:00'),
('ai_vlm_dash_008', 'elder_001', 'find_item', '我的收音机呢？早上还想听新闻的。', '收音机在床头柜上，您昨晚睡前还听了一会儿。', NULL, NULL, '收音机', '床头柜', 'found', NULL, NULL, '2026-07-20 06:30:00', '2026-07-20 06:31:00');

-- ============================================================
-- 为 elder_001 插入音乐疗法记录（music_control）— 5 条
-- 时间分散在近两周
-- ============================================================
INSERT INTO ai_service_record (record_id, elder_id, service_type, user_text, ai_reply, emotion, emotion_color, item, location, result, summary, music_type, interaction_time, created_at) VALUES
('ai_mus_dash_001', 'elder_001', 'music_control', '放点轻音乐吧，今天想安静会儿。', '好的爷爷，为您播放班得瑞的轻音乐，旋律优美适合放松。', NULL, NULL, NULL, NULL, NULL, NULL, '轻音乐', '2026-07-09 14:00:00', '2026-07-09 14:01:00'),
('ai_mus_dash_002', 'elder_001', 'music_control', '来段京剧，要《空城计》。', '好嘞，正在为您播放马连良的《空城计》选段。我正在城楼观山景……经典唱段百听不厌。', NULL, NULL, NULL, NULL, NULL, NULL, '京剧', '2026-07-12 16:00:00', '2026-07-12 16:01:00'),
('ai_mus_dash_003', 'elder_001', 'music_control', '睡前放点助眠的音乐吧。', '为您播放自然白噪音——溪流与鸟鸣，音量已调低，20分钟后自动关闭。祝您好梦。', NULL, NULL, NULL, NULL, NULL, NULL, '白噪音/助眠', '2026-07-15 21:30:00', '2026-07-15 21:31:00'),
('ai_mus_dash_004', 'elder_001', 'music_control', '今天心情不错，来首红歌。', '爷爷心情好我们也开心！为您播放《歌唱祖国》，您跟着哼几句吧！', NULL, NULL, NULL, NULL, NULL, NULL, '红歌', '2026-07-18 10:00:00', '2026-07-18 10:01:00'),
('ai_mus_dash_005', 'elder_001', 'music_control', '肩膀有点酸痛，放点舒缓的。', '好的，为您播放古筝曲《高山流水》，旋律舒缓有助于放松肌肉。', NULL, NULL, NULL, NULL, NULL, NULL, '古筝', '2026-07-20 15:30:00', '2026-07-20 15:31:00');
