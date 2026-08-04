*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agent_conversation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `conversation_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `agent_type` varchar(255) DEFAULT NULL,
  `user_text` varchar(255) DEFAULT NULL,
  `intent` varchar(255) DEFAULT NULL,
  `agent_reply` varchar(255) DEFAULT NULL,
  `risk_level` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `conversation_id` (`conversation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='智能体对话表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `agent_intent_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `intent_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `source` varchar(255) DEFAULT NULL,
  `user_text` varchar(255) DEFAULT NULL,
  `intent` varchar(255) DEFAULT NULL,
  `confidence` double NOT NULL COMMENT '置信度',
  `handled_by` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `intent_id` (`intent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='意图识别日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_advice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `advice_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `advice_type` varchar(255) DEFAULT NULL,
  `input_summary` varchar(255) DEFAULT NULL,
  `advice_content` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `advice_id` (`advice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI建议表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_analysis_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `community_suggestion` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `elder_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `elder_reply` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `family_notice` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `model` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `need_alarm` bit(1) DEFAULT NULL,
  `need_work_order` bit(1) DEFAULT NULL,
  `record_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `risk_level` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `risk_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `source` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `suggestion` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `work_order_type` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `analyzed_at` datetime(6) DEFAULT NULL,
  `generated_alarm_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `scope` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKox8am24xu0c6wvbvtx6mhjsvb` (`record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `alarm_event` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `alarm_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  `alarm_type` varchar(255) NOT NULL,
  `alarm_level` varchar(255) NOT NULL,
  `alarm_status` varchar(255) DEFAULT NULL,
  `description` varchar(255) NOT NULL COMMENT '描述',
  `building` varchar(255) DEFAULT NULL,
  `room_number` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  `snapshot_url` varchar(255) NOT NULL DEFAULT '' COMMENT '抓拍快照URL',
  `handler` varchar(255) DEFAULT NULL,
  `handler_name` varchar(255) DEFAULT NULL,
  `handle_remark` varchar(255) NOT NULL DEFAULT '' COMMENT '处理备注',
  `is_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已读',
  `created_at` datetime NOT NULL COMMENT '发生时间',
  `resolved_at` datetime NOT NULL DEFAULT '1970-01-01 00:00:00' COMMENT '解决时间(未处理用占位)',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `location` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `alarm_id` (`alarm_id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='报警事件表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `camera_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approved_at` bigint DEFAULT NULL,
  `can_view` int NOT NULL DEFAULT '0' COMMENT '是否允许查看：1=允许，0=不允许',
  `camera_type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `elder_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `expired_at` datetime(6) DEFAULT NULL,
  `reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reject_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `request_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `request_time` datetime(6) DEFAULT NULL,
  `staff_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `staff_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `staff_phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK12iu4bvh938mgo05pvdkaebgx` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `camera_view_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `camera_request_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `camera_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `duration` int NOT NULL,
  `staff_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `view_time` datetime(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cloud_agent` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `agent_id` varchar(255) NOT NULL,
  `agent_type` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `last_heartbeat` datetime NOT NULL COMMENT '最后心跳时间',
  `ip` varchar(255) DEFAULT NULL,
  `device_count` int NOT NULL DEFAULT '0' COMMENT '设备总数',
  `connected_devices` int NOT NULL DEFAULT '0' COMMENT '已连接设备数',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `agent_id` (`agent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='云端智能体表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `device` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `device_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `device_type` varchar(255) NOT NULL,
  `device_name` varchar(255) DEFAULT NULL,
  `location` varchar(255) NOT NULL COMMENT '安装位置',
  `building` varchar(255) DEFAULT NULL,
  `room` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `battery_level` int NOT NULL DEFAULT '0' COMMENT '电量(摄像头等有线设备填0)',
  `last_online_time` datetime NOT NULL COMMENT '最后在线时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `battery` int DEFAULT NULL,
  `last_online` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `online` bit(1) DEFAULT NULL,
  `type` varchar(255) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `device_id` (`device_id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `elder_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `elder_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `age` int NOT NULL COMMENT '年龄',
  `gender` varchar(255) DEFAULT NULL,
  `address` varchar(255) NOT NULL COMMENT '住址',
  `building` varchar(255) DEFAULT NULL,
  `room_number` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL COMMENT '密码',
  `health_status` varchar(255) DEFAULT NULL,
  `health_status_text` varchar(255) DEFAULT NULL,
  `health_note` varchar(255) NOT NULL COMMENT '健康备注/标签',
  `family_phone` varchar(255) DEFAULT NULL,
  `community_id` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) NOT NULL COMMENT '头像URL',
  `has_camera` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有摄像头',
  `camera_auth_until` bigint NOT NULL DEFAULT '0' COMMENT '监控授权到期时间戳',
  `camera_pending` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有待审批监控申请',
  `last_online` datetime NOT NULL COMMENT '最后在线时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `camera_auth_type` varchar(255) DEFAULT NULL,
  `contact_name` varchar(255) DEFAULT NULL,
  `contact_phone` varchar(255) DEFAULT NULL,
  `room` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `elder_id` (`elder_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='老人用户表';

/*!40101 SET character_set_client = @saved_cs_client */;

CREATE TABLE `emergency_contact` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `contact_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `relation` varchar(255) DEFAULT NULL,
  `is_primary` tinyint(1) DEFAULT '0' COMMENT '是否主要联系人',
  `sort_order` int DEFAULT '0' COMMENT '排序',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `contact_id` (`contact_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='紧急联系人表';

/*!40101 SET character_set_client = @saved_cs_client */;

CREATE TABLE `family_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `family_id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL COMMENT '密码',
  `elder_id` varchar(255) DEFAULT NULL,
  `relation` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `family_id` (`family_id`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='家属表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `health_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `record_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `hospitalization_info` varchar(255) DEFAULT NULL,
  `medical_history` varchar(255) DEFAULT NULL,
  `allergy_history` varchar(255) DEFAULT NULL,
  `common_medications` varchar(255) DEFAULT NULL,
  `blood_type` varchar(255) DEFAULT NULL,
  `remarks` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `record_id` (`record_id`),
  UNIQUE KEY `elder_id` (`elder_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='健康记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `home_control_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `control_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `device_id` varchar(255) DEFAULT NULL,
  `command` varchar(255) NOT NULL COMMENT '控制指令',
  `source_agent` varchar(255) DEFAULT NULL,
  `result` varchar(255) NOT NULL COMMENT '执行结果',
  `created_at` datetime NOT NULL COMMENT '执行时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `control_id` (`control_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='家居控制日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `local_agent` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `agent_id` varchar(255) NOT NULL,
  `agent_type` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `last_heartbeat` datetime NOT NULL COMMENT '最后心跳时间',
  `ip` varchar(255) DEFAULT NULL,
  `device_count` int NOT NULL DEFAULT '0' COMMENT '设备总数',
  `connected_devices` int NOT NULL DEFAULT '0' COMMENT '已连接设备数',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `agent_id` (`agent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='本地智能体表';

/*!40101 SET character_set_client = @saved_cs_client */;

CREATE TABLE `music_intervention` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `intervention_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `trigger_reason` varchar(255) NOT NULL COMMENT '触发原因',
  `music_type` varchar(255) DEFAULT NULL,
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `duration_minutes` int NOT NULL DEFAULT '0' COMMENT '持续时长(分钟)',
  `before_state` varchar(255) NOT NULL DEFAULT '' COMMENT '干预前状态',
  `after_state` varchar(255) NOT NULL DEFAULT '' COMMENT '干预后状态',
  `result` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `intervention_id` (`intervention_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='音乐干预表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `notification_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `user_type` varchar(255) DEFAULT NULL,
  `notification_type` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `content` varchar(255) DEFAULT NULL COMMENT '内容',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `building` varchar(255) DEFAULT NULL,
  `room` varchar(255) DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `request_id` varchar(255) DEFAULT NULL,
  `elder_id` varchar(255) DEFAULT NULL,
  `related_id` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `notification_id` (`notification_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sensor_data` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `elder_id` varchar(255) NOT NULL,
  `device_id` varchar(255) NOT NULL,
  `sensor_type` varchar(255) NOT NULL,
  `value` double NOT NULL COMMENT '数值',
  `unit` varchar(255) DEFAULT NULL,
  `is_abnormal` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否异常',
  `timestamp` datetime NOT NULL COMMENT '数据时间戳',
  `created_at` datetime NOT NULL COMMENT '入库时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=164 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='传感器数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `service_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `request_id` varchar(255) NOT NULL,
  `family_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `request_type` varchar(255) DEFAULT NULL,
  `content` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `related_order_id` varchar(255) DEFAULT NULL,
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '拒绝原因',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `request_id` (`request_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='服务请求表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sleep_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bed_time` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `deep_sleep_percent` int DEFAULT NULL,
  `elder_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL,
  `in_bed` bit(1) NOT NULL,
  `quality_score` int DEFAULT NULL,
  `recorded_at` datetime(6) NOT NULL,
  `wake_count` int DEFAULT NULL,
  `total_sleep_hours` double DEFAULT NULL COMMENT '总睡眠时长(小时)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sos_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sos_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `trigger_time` datetime DEFAULT NULL COMMENT '触发时间',
  `status` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL COMMENT '位置',
  `handler_id` varchar(255) DEFAULT NULL,
  `handled_time` datetime DEFAULT NULL COMMENT '处理时间',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `sos_id` (`sos_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='SOS记录表';

/*!40101 SET character_set_client = @saved_cs_client */;

CREATE TABLE `staff_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `staff_id` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL COMMENT '密码',
  `role` varchar(255) DEFAULT NULL,
  `community_id` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `staff_id` (`staff_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工表';
/*!40101 SET character_set_client = @saved_cs_client */;

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
CREATE TABLE `work_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` varchar(255) NOT NULL,
  `elder_id` varchar(255) NOT NULL,
  `order_type` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` varchar(255) DEFAULT NULL,
  `creator_id` varchar(255) DEFAULT NULL,
  `handler_id` varchar(255) DEFAULT NULL,
  `handler_name` varchar(255) DEFAULT NULL,
  `handler_phone` varchar(255) DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `service_request_id` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `alarm_id` varchar(255) DEFAULT NULL,
  `family_request_id` varchar(255) DEFAULT NULL,
  `finish_time` datetime(6) DEFAULT NULL,
  `from_family` bit(1) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `work_order_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='工单表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_service_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `record_id` varchar(50) NOT NULL,
  `elder_id` varchar(64) NOT NULL,
  `service_type` varchar(50) NOT NULL,
  `user_text` text,
  `ai_reply` text,
  `emotion` varchar(50) DEFAULT NULL,
  `emotion_color` varchar(20) DEFAULT NULL,
  `item` varchar(100) DEFAULT NULL,
  `location` varchar(200) DEFAULT NULL,
  `result` varchar(50) DEFAULT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `music_type` varchar(50) DEFAULT NULL,
  `interaction_time` datetime NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `record_id` (`record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI服务记录表（统一陪伴对话/找物/音乐控制）';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_records` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) NOT NULL,
  `date` date NOT NULL,
  `message` text NOT NULL,
  `emotion` varchar(20) NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='对话记录表（情绪分析数据源）';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_find_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) NOT NULL,
  `date` date NOT NULL,
  `item_name` varchar(100) NOT NULL,
  `duration_seconds` int DEFAULT NULL,
  `found` tinyint(1) NOT NULL,
  `position` varchar(100) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='物品寻找日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `music_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) NOT NULL,
  `date` date NOT NULL,
  `song_name` varchar(200) NOT NULL,
  `duration_minutes` int DEFAULT NULL,
  `scene` varchar(50) DEFAULT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='音乐播放日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

-- ===== Static Reference Data =====
INSERT INTO `elder_user` (`elder_id`,`name`,`age`,`gender`,`address`,`building`,`room_number`,`phone`,`password`,`health_status`,`health_status_text`,`health_note`,`family_phone`,`community_id`,`avatar`,`has_camera`,`camera_auth_until`,`camera_pending`,`last_online`,`created_at`,`update_time`) VALUES
('elder_001','张三',78,'男','北京市朝阳区建国路88号','1号楼','1-201','13912345601','123456','warning','关注','高血压,糖尿病','13800138002','community_001','/uploads/avatar/elder_001.jpg',1,0,0,'2025-05-17 09:00:00','2025-01-10 09:00:00','2025-05-17 10:00:00'),
('elder_002','李四',82,'女','北京市海淀区中关村大街1号','2号楼','2-301','13912345602','123456','danger','高危','心脏病,骨质疏松','13900139002','community_001','/uploads/avatar/elder_002.jpg',1,0,1,'2025-05-17 08:30:00','2025-01-15 10:30:00','2025-05-17 10:00:00'),
('elder_003','王五',75,'男','北京市西城区金融街2号','3号楼','3-401','13912345603','123456','normal','正常','轻度认知障碍','13700137002','community_001','/uploads/avatar/elder_003.jpg',1,0,0,'2025-05-17 08:30:00','2025-02-01 14:00:00','2025-05-17 10:00:00'),
('elder_004','赵六',80,'女','北京市东城区东直门外大街10号','4号楼','4-102','13912345604','123456','warning','关注','糖尿病,关节炎','13600136002','community_001','/uploads/avatar/elder_004.jpg',1,0,0,'2025-05-17 07:00:00','2025-03-01 09:00:00','2025-05-17 10:00:00'),
('elder_005','孙七',76,'男','北京市丰台区南三环中路12号','5号楼','5-502','13912345605','123456','danger','高危','脑梗后遗症,高血压','13500135002','community_001','/uploads/avatar/elder_005.jpg',0,0,1,'2025-05-16 22:00:00','2025-03-15 10:00:00','2025-05-17 09:00:00'),
('100','默认老人',80,'男','北京市朝阳区默认地址','1号楼','1-001','13900000000','123456','normal','正常','无','13800000000','community_001','/uploads/avatar/default.png',1,0,0,'2025-01-01 08:00:00','2025-01-01 08:00:00','2025-01-01 08:00:00');

INSERT INTO `staff_user` (`staff_id`,`username`,`name`,`phone`,`password`,`role`,`community_id`,`avatar`,`created_at`,`update_time`) VALUES
('staff_001','zhangjianguo','张建国','13800138001','123456','supervisor','community_001','/uploads/avatar/staff_001.jpg','2025-01-05 08:00:00','2025-05-17 10:00:00'),
('staff_002','lixiuying','李秀英','13800138002','123456','staff','community_001','/uploads/avatar/staff_002.jpg','2025-01-08 09:00:00','2025-05-17 09:30:00'),
('staff_003','wangqiang','王强','13800138003','123456','staff','community_001','/uploads/avatar/staff_003.jpg','2025-01-10 10:00:00','2025-05-17 08:00:00'),
('staff_004','admin','系统管理员','13800138000','123456','admin','community_001','/uploads/avatar/admin.jpg','2025-01-01 08:00:00','2025-05-17 10:00:00'),
('STF-f65acafd','13900001111','李新成','13900001111','123456','staff',NULL,NULL,'2026-06-20 07:55:53','2026-06-20 07:55:53');

INSERT INTO `family_user` (`family_id`,`name`,`phone`,`password`,`elder_id`,`relation`,`avatar`,`created_at`,`update_time`) VALUES
('family_001','张小明','13800138011','123456','elder_001','儿子','/uploads/avatar/family_001.jpg','2025-01-10 09:00:00','2025-05-17 10:00:00'),
('family_002','李小红','13900139012','123456','elder_002','女儿','/uploads/avatar/family_002.jpg','2025-01-15 10:30:00','2025-05-17 10:00:00'),
('family_003','王小刚','13700137013','123456','elder_003','侄子','/uploads/avatar/family_003.jpg','2025-02-01 14:00:00','2025-05-17 10:00:00'),
('family_004','赵小花','13600136014','123456','elder_004','女儿','/uploads/avatar/family_004.jpg','2025-03-01 09:00:00','2025-05-17 10:00:00'),
('family_005','孙小强','13500135015','123456','elder_005','孙子','/uploads/avatar/family_005.jpg','2025-03-15 10:00:00','2025-05-17 10:00:00');

INSERT INTO `device` (`device_id`,`elder_id`,`device_type`,`device_name`,`location`,`building`,`room`,`status`,`battery_level`,`last_online_time`,`created_at`,`update_time`) VALUES
('dev_001','elder_001','手环','智能手环','随身佩戴','1号楼','1-201','online',78,'2025-05-17 09:45:00','2025-01-10 09:00:00','2025-05-17 09:45:00'),
('dev_002','elder_001','摄像头','客厅摄像头','客厅','1号楼','1-201','online',0,'2025-05-17 09:00:00','2025-01-10 09:00:00','2025-05-17 09:00:00'),
('dev_003','elder_001','门锁','智能指纹门锁','大门','1号楼','1-201','online',85,'2025-05-17 09:30:00','2025-01-10 09:00:00','2025-05-17 09:30:00'),
('dev_004','elder_002','手环','智能手环','随身佩戴','2号楼','2-301','online',62,'2025-05-17 09:50:00','2025-01-15 10:30:00','2025-05-17 09:50:00'),
('dev_005','elder_002','摄像头','客厅摄像头','客厅','2号楼','2-301','online',0,'2025-05-17 09:15:00','2025-01-15 10:30:00','2025-05-17 09:15:00'),
('dev_006','elder_002','烟感','厨房烟感','厨房','2号楼','2-301','online',0,'2025-05-17 09:00:00','2025-01-15 10:30:00','2025-05-17 09:00:00'),
('dev_007','elder_003','手环','智能手环','随身佩戴','3号楼','3-401','online',45,'2025-05-17 08:30:00','2025-02-01 14:00:00','2025-05-17 08:30:00'),
('dev_008','elder_003','摄像头','客厅摄像头','客厅','3号楼','3-401','online',0,'2025-05-17 09:15:00','2025-02-01 14:00:00','2025-05-17 09:15:00'),
('dev_009','elder_003','床垫传感器','智能床垫','卧室','3号楼','3-401','offline',0,'2025-05-15 22:00:00','2025-02-01 14:00:00','2025-05-15 22:00:00'),
('dev_010','elder_004','手环','智能手环','随身佩戴','4号楼','4-102','online',55,'2025-05-17 08:00:00','2025-03-01 09:00:00','2025-05-17 08:00:00'),
('dev_011','elder_004','摄像头','卧室摄像头','卧室','4号楼','4-102','online',0,'2025-05-17 07:30:00','2025-03-01 09:00:00','2025-05-17 07:30:00'),
('dev_012','elder_005','手环','智能手环','随身佩戴','5号楼','5-502','online',30,'2025-05-16 22:00:00','2025-03-15 10:00:00','2025-05-16 22:00:00'),
('dev_013','elder_002','窗帘','智能窗帘电机','卧室','2号楼','2-301','online',0,'2025-05-17 09:00:00','2025-01-15 10:30:00','2025-05-17 09:00:00'),
('dev_014','elder_001','灯光','智能吸顶灯','客厅','1号楼','1-201','online',0,'2025-05-17 09:00:00','2025-01-10 09:00:00','2025-05-17 09:00:00'),
('dev_015','elder_004','蜂鸣器','紧急报警蜂鸣器','客厅','4号楼','4-102','online',0,'2025-05-17 08:00:00','2025-03-01 09:00:00','2025-05-17 08:00:00'),
('dev_016','elder_005','烟感','客厅烟感','客厅','5号楼','5-502','online',0,'2025-05-16 22:00:00','2025-03-15 10:00:00','2025-05-16 22:00:00'),
('dev_017','elder_003','烟感','厨房烟感','厨房','3号楼','3-401','online',0,'2025-03-01 10:00:00','2025-03-01 10:00:00','2025-05-17 09:00:00');

INSERT INTO `health_record` (`record_id`,`elder_id`,`hospitalization_info`,`medical_history`,`allergy_history`,`common_medications`,`blood_type`,`remarks`,`created_at`,`update_time`) VALUES
('hr_001','elder_001','2024年3月因高血压住院7天','高血压10年,糖尿病5年','对青霉素过敏','降压药,降糖药,阿司匹林','A','需定期监测血糖血压','2025-01-10 09:00:00','2025-05-17 10:00:00'),
('hr_002','elder_002','2024年8月因心脏病住院15天','心脏病3年,骨质疏松','无','硝酸甘油,钙片','B','需注意跌倒风险','2025-01-15 10:30:00','2025-05-17 10:00:00'),
('hr_003','elder_003','无住院记录','轻度认知障碍','对花粉过敏','银杏叶片','O','记忆力减退需关注','2025-02-01 14:00:00','2025-05-17 10:00:00'),
('hr_004','elder_004','2025年1月因糖尿病足住院5天','糖尿病8年,关节炎','无','胰岛素,止痛药','AB','行动不便需辅助','2025-03-01 09:00:00','2025-05-17 10:00:00'),
('hr_005','elder_005','2024年12月因脑梗住院30天','脑梗后遗症,高血压','对磺胺类药物过敏','降压药,抗凝药','A','需定期康复训练','2025-03-15 10:00:00','2025-05-17 10:00:00');

-- sensor_data: 1629 rows
INSERT INTO `sensor_data` (`elder_id`,`device_id`,`sensor_type`,`value`,`unit`,`is_abnormal`,`timestamp`,`created_at`) VALUES
('elder_001','dev_001','blood_pressure_sys',135.0,'mmHg',0,'2025-05-17 08:30:00','2025-05-17 08:30:00'),
('elder_001','dev_001','blood_pressure_dia',85.0,'mmHg',0,'2025-05-17 08:30:00','2025-05-17 08:30:00'),
('elder_001','dev_001','blood_pressure_sys',132.0,'mmHg',0,'2025-05-16 08:30:00','2025-05-16 08:30:00'),
('elder_001','dev_001','blood_pressure_dia',84.0,'mmHg',0,'2025-05-16 08:30:00','2025-05-16 08:30:00'),
('elder_002','dev_004','blood_pressure_sys',145.0,'mmHg',0,'2025-05-17 07:00:00','2025-05-17 07:00:00'),
('elder_002','dev_004','blood_pressure_dia',90.0,'mmHg',0,'2025-05-17 07:00:00','2025-05-17 07:00:00'),
('elder_002','dev_004','blood_pressure_sys',148.0,'mmHg',0,'2025-05-16 07:00:00','2025-05-16 07:00:00'),
('elder_002','dev_004','blood_pressure_dia',92.0,'mmHg',0,'2025-05-16 07:00:00','2025-05-16 07:00:00'),
('elder_003','dev_007','blood_pressure_sys',128.0,'mmHg',0,'2025-05-17 08:00:00','2025-05-17 08:00:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',0,'2025-05-17 08:00:00','2025-05-17 08:00:00'),
('elder_003','dev_007','blood_pressure_sys',125.0,'mmHg',0,'2025-05-16 08:00:00','2025-05-16 08:00:00'),
('elder_003','dev_007','blood_pressure_dia',80.0,'mmHg',0,'2025-05-16 08:00:00','2025-05-16 08:00:00'),
('elder_004','dev_010','blood_pressure_sys',138.0,'mmHg',0,'2025-05-17 07:30:00','2025-05-17 07:30:00'),
('elder_004','dev_010','blood_pressure_dia',88.0,'mmHg',0,'2025-05-17 07:30:00','2025-05-17 07:30:00'),
('elder_005','dev_012','blood_pressure_sys',150.0,'mmHg',0,'2025-05-16 22:00:00','2025-05-16 22:00:00'),
('elder_005','dev_012','blood_pressure_dia',95.0,'mmHg',0,'2025-05-16 22:00:00','2025-05-16 22:00:00'),
('elder_004','dev_010','blood_pressure_sys',140.0,'mmHg',0,'2025-05-16 07:30:00','2025-05-16 07:30:00'),
('elder_004','dev_010','blood_pressure_dia',86.0,'mmHg',0,'2025-05-16 07:30:00','2025-05-16 07:30:00'),
('elder_001','dev_001','heart_rate',88.0,'bpm',1,'2026-08-02 00:55:00','2026-08-02 00:55:00'),
('elder_001','dev_001','spo2',91.0,'%',1,'2026-08-02 00:55:00','2026-08-02 00:55:00'),
('elder_001','dev_001','blood_pressure_sys',146.0,'mmHg',1,'2026-08-02 00:55:00','2026-08-02 00:55:00'),
('elder_001','dev_001','blood_pressure_dia',93.0,'mmHg',1,'2026-08-02 00:55:00','2026-08-02 00:55:00'),
('elder_001','dev_001','temperature',36.9,'C',1,'2026-08-02 00:55:00','2026-08-02 00:55:00'),
('elder_001','dev_001','heart_rate',106.0,'bpm',0,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_001','dev_001','blood_pressure_sys',161.0,'mmHg',0,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_001','dev_001','blood_pressure_dia',105.0,'mmHg',0,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_001','dev_001','temperature',36.9,'C',0,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_001','dev_001','heart_rate',105.0,'bpm',1,'2026-08-02 02:58:00','2026-08-02 02:58:00'),
('elder_001','dev_001','spo2',90.0,'%',1,'2026-08-02 02:58:00','2026-08-02 02:58:00'),
('elder_001','dev_001','blood_pressure_sys',152.0,'mmHg',1,'2026-08-02 02:58:00','2026-08-02 02:58:00'),
('elder_001','dev_001','blood_pressure_dia',98.0,'mmHg',1,'2026-08-02 02:58:00','2026-08-02 02:58:00'),
('elder_001','dev_001','temperature',36.8,'C',1,'2026-08-02 02:58:00','2026-08-02 02:58:00'),
('elder_001','dev_001','heart_rate',115.0,'bpm',1,'2026-08-02 03:37:00','2026-08-02 03:37:00'),
('elder_001','dev_001','spo2',91.0,'%',1,'2026-08-02 03:37:00','2026-08-02 03:37:00'),
('elder_001','dev_001','blood_pressure_sys',168.0,'mmHg',1,'2026-08-02 03:37:00','2026-08-02 03:37:00'),
('elder_001','dev_001','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 03:37:00','2026-08-02 03:37:00'),
('elder_001','dev_001','temperature',37.4,'C',1,'2026-08-02 03:37:00','2026-08-02 03:37:00'),
('elder_001','dev_001','heart_rate',114.0,'bpm',1,'2026-08-02 04:01:00','2026-08-02 04:01:00'),
('elder_001','dev_001','spo2',93.0,'%',1,'2026-08-02 04:01:00','2026-08-02 04:01:00'),
('elder_001','dev_001','blood_pressure_sys',153.0,'mmHg',1,'2026-08-02 04:01:00','2026-08-02 04:01:00'),
('elder_001','dev_001','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 04:01:00','2026-08-02 04:01:00'),
('elder_001','dev_001','temperature',37.0,'C',1,'2026-08-02 04:01:00','2026-08-02 04:01:00'),
('elder_001','dev_001','heart_rate',104.0,'bpm',1,'2026-08-02 05:34:00','2026-08-02 05:34:00'),
('elder_001','dev_001','spo2',89.0,'%',1,'2026-08-02 05:34:00','2026-08-02 05:34:00'),
('elder_001','dev_001','blood_pressure_sys',153.0,'mmHg',1,'2026-08-02 05:34:00','2026-08-02 05:34:00'),
('elder_001','dev_001','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 05:34:00','2026-08-02 05:34:00'),
('elder_001','dev_001','temperature',37.4,'C',1,'2026-08-02 05:34:00','2026-08-02 05:34:00'),
('elder_001','dev_001','heart_rate',85.0,'bpm',0,'2026-08-02 06:41:00','2026-08-02 06:41:00'),
('elder_001','dev_001','spo2',93.0,'%',0,'2026-08-02 06:41:00','2026-08-02 06:41:00'),
('elder_001','dev_001','blood_pressure_sys',142.0,'mmHg',0,'2026-08-02 06:41:00','2026-08-02 06:41:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 06:41:00','2026-08-02 06:41:00'),
('elder_001','dev_001','temperature',37.2,'C',0,'2026-08-02 06:41:00','2026-08-02 06:41:00'),
('elder_001','dev_001','heart_rate',89.0,'bpm',0,'2026-08-02 07:31:00','2026-08-02 07:31:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-08-02 07:31:00','2026-08-02 07:31:00'),
('elder_001','dev_001','blood_pressure_sys',148.0,'mmHg',0,'2026-08-02 07:31:00','2026-08-02 07:31:00'),
('elder_001','dev_001','blood_pressure_dia',83.0,'mmHg',0,'2026-08-02 07:31:00','2026-08-02 07:31:00'),
('elder_001','dev_001','temperature',37.3,'C',0,'2026-08-02 07:31:00','2026-08-02 07:31:00'),
('elder_001','dev_001','heart_rate',90.0,'bpm',1,'2026-08-02 08:52:00','2026-08-02 08:52:00'),
('elder_001','dev_001','spo2',90.0,'%',1,'2026-08-02 08:52:00','2026-08-02 08:52:00'),
('elder_001','dev_001','blood_pressure_sys',138.0,'mmHg',1,'2026-08-02 08:52:00','2026-08-02 08:52:00'),
('elder_001','dev_001','blood_pressure_dia',84.0,'mmHg',1,'2026-08-02 08:52:00','2026-08-02 08:52:00'),
('elder_001','dev_001','temperature',36.7,'C',1,'2026-08-02 08:52:00','2026-08-02 08:52:00'),
('elder_001','dev_001','heart_rate',99.0,'bpm',1,'2026-08-02 09:46:00','2026-08-02 09:46:00'),
('elder_001','dev_001','spo2',90.0,'%',1,'2026-08-02 09:46:00','2026-08-02 09:46:00'),
('elder_001','dev_001','blood_pressure_sys',142.0,'mmHg',1,'2026-08-02 09:46:00','2026-08-02 09:46:00'),
('elder_001','dev_001','blood_pressure_dia',92.0,'mmHg',1,'2026-08-02 09:46:00','2026-08-02 09:46:00'),
('elder_001','dev_001','temperature',36.9,'C',1,'2026-08-02 09:46:00','2026-08-02 09:46:00'),
('elder_001','dev_001','heart_rate',100.0,'bpm',0,'2026-08-02 10:35:00','2026-08-02 10:35:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-08-02 10:35:00','2026-08-02 10:35:00'),
('elder_001','dev_001','blood_pressure_sys',132.0,'mmHg',0,'2026-08-02 10:35:00','2026-08-02 10:35:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 10:35:00','2026-08-02 10:35:00'),
('elder_001','dev_001','temperature',36.9,'C',0,'2026-08-02 10:35:00','2026-08-02 10:35:00'),
('elder_001','dev_001','heart_rate',84.0,'bpm',1,'2026-08-02 11:26:00','2026-08-02 11:26:00'),
('elder_001','dev_001','spo2',91.0,'%',1,'2026-08-02 11:26:00','2026-08-02 11:26:00'),
('elder_001','dev_001','blood_pressure_sys',132.0,'mmHg',1,'2026-08-02 11:26:00','2026-08-02 11:26:00'),
('elder_001','dev_001','blood_pressure_dia',90.0,'mmHg',1,'2026-08-02 11:26:00','2026-08-02 11:26:00'),
('elder_001','dev_001','temperature',36.8,'C',1,'2026-08-02 11:26:00','2026-08-02 11:26:00'),
('elder_001','dev_001','heart_rate',101.0,'bpm',1,'2026-08-02 12:08:00','2026-08-02 12:08:00'),
('elder_001','dev_001','spo2',90.0,'%',1,'2026-08-02 12:08:00','2026-08-02 12:08:00'),
('elder_001','dev_001','blood_pressure_sys',138.0,'mmHg',1,'2026-08-02 12:08:00','2026-08-02 12:08:00'),
('elder_001','dev_001','blood_pressure_dia',84.0,'mmHg',1,'2026-08-02 12:08:00','2026-08-02 12:08:00'),
('elder_001','dev_001','temperature',37.2,'C',1,'2026-08-02 12:08:00','2026-08-02 12:08:00'),
('elder_001','dev_001','heart_rate',95.0,'bpm',0,'2026-08-02 13:30:00','2026-08-02 13:30:00'),
('elder_001','dev_001','spo2',93.0,'%',0,'2026-08-02 13:30:00','2026-08-02 13:30:00'),
('elder_001','dev_001','blood_pressure_sys',145.0,'mmHg',0,'2026-08-02 13:30:00','2026-08-02 13:30:00'),
('elder_001','dev_001','blood_pressure_dia',93.0,'mmHg',0,'2026-08-02 13:30:00','2026-08-02 13:30:00'),
('elder_001','dev_001','temperature',36.7,'C',0,'2026-08-02 13:30:00','2026-08-02 13:30:00'),
('elder_001','dev_001','heart_rate',91.0,'bpm',0,'2026-08-02 14:04:00','2026-08-02 14:04:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-08-02 14:04:00','2026-08-02 14:04:00'),
('elder_001','dev_001','blood_pressure_sys',133.0,'mmHg',0,'2026-08-02 14:04:00','2026-08-02 14:04:00'),
('elder_001','dev_001','blood_pressure_dia',93.0,'mmHg',0,'2026-08-02 14:04:00','2026-08-02 14:04:00'),
('elder_001','dev_001','temperature',37.4,'C',0,'2026-08-02 14:04:00','2026-08-02 14:04:00'),
('elder_001','dev_001','heart_rate',89.0,'bpm',0,'2026-08-02 15:41:00','2026-08-02 15:41:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-08-02 15:41:00','2026-08-02 15:41:00'),
('elder_001','dev_001','blood_pressure_sys',136.0,'mmHg',0,'2026-08-02 15:41:00','2026-08-02 15:41:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 15:41:00','2026-08-02 15:41:00'),
('elder_001','dev_001','temperature',37.1,'C',0,'2026-08-02 15:41:00','2026-08-02 15:41:00'),
('elder_001','dev_001','heart_rate',95.0,'bpm',0,'2026-08-02 16:29:00','2026-08-02 16:29:00'),
('elder_001','dev_001','spo2',92.0,'%',0,'2026-08-02 16:29:00','2026-08-02 16:29:00'),
('elder_001','dev_001','blood_pressure_sys',140.0,'mmHg',0,'2026-08-02 16:29:00','2026-08-02 16:29:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 16:29:00','2026-08-02 16:29:00'),
('elder_001','dev_001','temperature',37.1,'C',0,'2026-08-02 16:29:00','2026-08-02 16:29:00'),
('elder_001','dev_001','heart_rate',95.0,'bpm',0,'2026-08-02 17:16:00','2026-08-02 17:16:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-08-02 17:16:00','2026-08-02 17:16:00'),
('elder_001','dev_001','blood_pressure_sys',144.0,'mmHg',0,'2026-08-02 17:16:00','2026-08-02 17:16:00'),
('elder_001','dev_001','blood_pressure_dia',85.0,'mmHg',0,'2026-08-02 17:16:00','2026-08-02 17:16:00'),
('elder_001','dev_001','temperature',36.8,'C',0,'2026-08-02 17:16:00','2026-08-02 17:16:00'),
('elder_001','dev_001','heart_rate',100.0,'bpm',1,'2026-08-02 18:54:00','2026-08-02 18:54:00'),
('elder_001','dev_001','spo2',91.0,'%',1,'2026-08-02 18:54:00','2026-08-02 18:54:00'),
('elder_001','dev_001','blood_pressure_sys',140.0,'mmHg',1,'2026-08-02 18:54:00','2026-08-02 18:54:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',1,'2026-08-02 18:54:00','2026-08-02 18:54:00'),
('elder_001','dev_001','temperature',37.0,'C',1,'2026-08-02 18:54:00','2026-08-02 18:54:00'),
('elder_001','dev_001','heart_rate',101.0,'bpm',1,'2026-08-02 19:53:00','2026-08-02 19:53:00'),
('elder_001','dev_001','spo2',90.0,'%',1,'2026-08-02 19:53:00','2026-08-02 19:53:00'),
('elder_001','dev_001','blood_pressure_sys',131.0,'mmHg',1,'2026-08-02 19:53:00','2026-08-02 19:53:00'),
('elder_001','dev_001','blood_pressure_dia',90.0,'mmHg',1,'2026-08-02 19:53:00','2026-08-02 19:53:00'),
('elder_001','dev_001','temperature',36.8,'C',1,'2026-08-02 19:53:00','2026-08-02 19:53:00'),
('elder_001','dev_001','heart_rate',85.0,'bpm',0,'2026-08-02 20:41:00','2026-08-02 20:41:00'),
('elder_001','dev_001','spo2',92.0,'%',0,'2026-08-02 20:41:00','2026-08-02 20:41:00'),
('elder_001','dev_001','blood_pressure_sys',132.0,'mmHg',0,'2026-08-02 20:41:00','2026-08-02 20:41:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 20:41:00','2026-08-02 20:41:00'),
('elder_001','dev_001','temperature',37.0,'C',0,'2026-08-02 20:41:00','2026-08-02 20:41:00'),
('elder_001','dev_001','heart_rate',95.0,'bpm',0,'2026-08-02 21:47:00','2026-08-02 21:47:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-08-02 21:47:00','2026-08-02 21:47:00'),
('elder_001','dev_001','blood_pressure_sys',156.0,'mmHg',0,'2026-08-02 21:47:00','2026-08-02 21:47:00'),
('elder_001','dev_001','blood_pressure_dia',96.0,'mmHg',0,'2026-08-02 21:47:00','2026-08-02 21:47:00'),
('elder_001','dev_001','temperature',37.5,'C',0,'2026-08-02 21:47:00','2026-08-02 21:47:00'),
('elder_001','dev_001','heart_rate',94.0,'bpm',0,'2026-08-02 22:11:00','2026-08-02 22:11:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-08-02 22:11:00','2026-08-02 22:11:00'),
('elder_001','dev_001','blood_pressure_sys',148.0,'mmHg',0,'2026-08-02 22:11:00','2026-08-02 22:11:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 22:11:00','2026-08-02 22:11:00'),
('elder_001','dev_001','temperature',37.2,'C',0,'2026-08-02 22:11:00','2026-08-02 22:11:00'),
('elder_001','dev_001','heart_rate',109.0,'bpm',1,'2026-08-02 23:31:00','2026-08-02 23:31:00'),
('elder_001','dev_001','spo2',89.0,'%',1,'2026-08-02 23:31:00','2026-08-02 23:31:00'),
('elder_001','dev_001','blood_pressure_sys',149.0,'mmHg',1,'2026-08-02 23:31:00','2026-08-02 23:31:00'),
('elder_001','dev_001','blood_pressure_dia',95.0,'mmHg',1,'2026-08-02 23:31:00','2026-08-02 23:31:00'),
('elder_001','dev_001','temperature',37.5,'C',1,'2026-08-02 23:31:00','2026-08-02 23:31:00'),
('elder_002','dev_004','heart_rate',92.0,'bpm',1,'2026-08-02 00:58:00','2026-08-02 00:58:00'),
('elder_002','dev_004','spo2',90.0,'%',1,'2026-08-02 00:58:00','2026-08-02 00:58:00'),
('elder_002','dev_004','blood_pressure_sys',144.0,'mmHg',1,'2026-08-02 00:58:00','2026-08-02 00:58:00'),
('elder_002','dev_004','blood_pressure_dia',96.0,'mmHg',1,'2026-08-02 00:58:00','2026-08-02 00:58:00'),
('elder_002','dev_004','temperature',36.8,'C',1,'2026-08-02 00:58:00','2026-08-02 00:58:00'),
('elder_002','dev_004','heart_rate',113.0,'bpm',1,'2026-08-02 01:17:00','2026-08-02 01:17:00'),
('elder_002','dev_004','spo2',88.0,'%',1,'2026-08-02 01:17:00','2026-08-02 01:17:00'),
('elder_002','dev_004','blood_pressure_sys',170.0,'mmHg',1,'2026-08-02 01:17:00','2026-08-02 01:17:00'),
('elder_002','dev_004','blood_pressure_dia',103.0,'mmHg',1,'2026-08-02 01:17:00','2026-08-02 01:17:00'),
('elder_002','dev_004','temperature',37.2,'C',1,'2026-08-02 01:17:00','2026-08-02 01:17:00'),
('elder_002','dev_004','heart_rate',119.0,'bpm',1,'2026-08-02 02:46:00','2026-08-02 02:46:00'),
('elder_002','dev_004','spo2',89.0,'%',1,'2026-08-02 02:46:00','2026-08-02 02:46:00'),
('elder_002','dev_004','blood_pressure_sys',167.0,'mmHg',1,'2026-08-02 02:46:00','2026-08-02 02:46:00'),
('elder_002','dev_004','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 02:46:00','2026-08-02 02:46:00'),
('elder_002','dev_004','temperature',37.5,'C',1,'2026-08-02 02:46:00','2026-08-02 02:46:00'),
('elder_002','dev_004','heart_rate',109.0,'bpm',0,'2026-08-02 03:04:00','2026-08-02 03:04:00'),
('elder_002','dev_004','spo2',93.0,'%',0,'2026-08-02 03:04:00','2026-08-02 03:04:00'),
('elder_002','dev_004','blood_pressure_sys',170.0,'mmHg',0,'2026-08-02 03:04:00','2026-08-02 03:04:00'),
('elder_002','dev_004','blood_pressure_dia',104.0,'mmHg',0,'2026-08-02 03:04:00','2026-08-02 03:04:00'),
('elder_002','dev_004','temperature',37.2,'C',0,'2026-08-02 03:04:00','2026-08-02 03:04:00'),
('elder_002','dev_004','heart_rate',105.0,'bpm',0,'2026-08-02 04:04:00','2026-08-02 04:04:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-08-02 04:04:00','2026-08-02 04:04:00'),
('elder_002','dev_004','blood_pressure_sys',170.0,'mmHg',0,'2026-08-02 04:04:00','2026-08-02 04:04:00'),
('elder_002','dev_004','blood_pressure_dia',105.0,'mmHg',0,'2026-08-02 04:04:00','2026-08-02 04:04:00'),
('elder_002','dev_004','temperature',37.3,'C',0,'2026-08-02 04:04:00','2026-08-02 04:04:00'),
('elder_002','dev_004','heart_rate',112.0,'bpm',1,'2026-08-02 05:59:00','2026-08-02 05:59:00'),
('elder_002','dev_004','spo2',92.0,'%',1,'2026-08-02 05:59:00','2026-08-02 05:59:00'),
('elder_002','dev_004','blood_pressure_sys',165.0,'mmHg',1,'2026-08-02 05:59:00','2026-08-02 05:59:00'),
('elder_002','dev_004','blood_pressure_dia',101.0,'mmHg',1,'2026-08-02 05:59:00','2026-08-02 05:59:00'),
('elder_002','dev_004','temperature',37.2,'C',1,'2026-08-02 05:59:00','2026-08-02 05:59:00'),
('elder_002','dev_004','heart_rate',90.0,'bpm',1,'2026-08-02 06:14:00','2026-08-02 06:14:00'),
('elder_002','dev_004','spo2',91.0,'%',1,'2026-08-02 06:14:00','2026-08-02 06:14:00'),
('elder_002','dev_004','blood_pressure_sys',142.0,'mmHg',1,'2026-08-02 06:14:00','2026-08-02 06:14:00'),
('elder_002','dev_004','blood_pressure_dia',87.0,'mmHg',1,'2026-08-02 06:14:00','2026-08-02 06:14:00'),
('elder_002','dev_004','temperature',37.0,'C',1,'2026-08-02 06:14:00','2026-08-02 06:14:00'),
('elder_002','dev_004','heart_rate',95.0,'bpm',1,'2026-08-02 07:26:00','2026-08-02 07:26:00'),
('elder_002','dev_004','spo2',89.0,'%',1,'2026-08-02 07:26:00','2026-08-02 07:26:00'),
('elder_002','dev_004','blood_pressure_sys',140.0,'mmHg',1,'2026-08-02 07:26:00','2026-08-02 07:26:00'),
('elder_002','dev_004','blood_pressure_dia',95.0,'mmHg',1,'2026-08-02 07:26:00','2026-08-02 07:26:00'),
('elder_002','dev_004','temperature',37.2,'C',1,'2026-08-02 07:26:00','2026-08-02 07:26:00'),
('elder_002','dev_004','heart_rate',91.0,'bpm',0,'2026-08-02 08:19:00','2026-08-02 08:19:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-08-02 08:19:00','2026-08-02 08:19:00'),
('elder_002','dev_004','blood_pressure_sys',147.0,'mmHg',0,'2026-08-02 08:19:00','2026-08-02 08:19:00'),
('elder_002','dev_004','blood_pressure_dia',97.0,'mmHg',0,'2026-08-02 08:19:00','2026-08-02 08:19:00'),
('elder_002','dev_004','temperature',37.2,'C',0,'2026-08-02 08:19:00','2026-08-02 08:19:00'),
('elder_002','dev_004','heart_rate',95.0,'bpm',0,'2026-08-02 09:45:00','2026-08-02 09:45:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-08-02 09:45:00','2026-08-02 09:45:00'),
('elder_002','dev_004','blood_pressure_sys',153.0,'mmHg',0,'2026-08-02 09:45:00','2026-08-02 09:45:00'),
('elder_002','dev_004','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 09:45:00','2026-08-02 09:45:00'),
('elder_002','dev_004','temperature',37.2,'C',0,'2026-08-02 09:45:00','2026-08-02 09:45:00'),
('elder_002','dev_004','heart_rate',88.0,'bpm',1,'2026-08-02 10:04:00','2026-08-02 10:04:00'),
('elder_002','dev_004','spo2',89.0,'%',1,'2026-08-02 10:04:00','2026-08-02 10:04:00'),
('elder_002','dev_004','blood_pressure_sys',154.0,'mmHg',1,'2026-08-02 10:04:00','2026-08-02 10:04:00'),
('elder_002','dev_004','blood_pressure_dia',98.0,'mmHg',1,'2026-08-02 10:04:00','2026-08-02 10:04:00'),
('elder_002','dev_004','temperature',37.1,'C',1,'2026-08-02 10:04:00','2026-08-02 10:04:00'),
('elder_002','dev_004','heart_rate',92.0,'bpm',1,'2026-08-02 11:55:00','2026-08-02 11:55:00'),
('elder_002','dev_004','spo2',90.0,'%',1,'2026-08-02 11:55:00','2026-08-02 11:55:00'),
('elder_002','dev_004','blood_pressure_sys',153.0,'mmHg',1,'2026-08-02 11:55:00','2026-08-02 11:55:00'),
('elder_002','dev_004','blood_pressure_dia',92.0,'mmHg',1,'2026-08-02 11:55:00','2026-08-02 11:55:00'),
('elder_002','dev_004','temperature',37.1,'C',1,'2026-08-02 11:55:00','2026-08-02 11:55:00'),
('elder_002','dev_004','heart_rate',105.0,'bpm',1,'2026-08-02 12:21:00','2026-08-02 12:21:00'),
('elder_002','dev_004','spo2',91.0,'%',1,'2026-08-02 12:21:00','2026-08-02 12:21:00'),
('elder_002','dev_004','blood_pressure_sys',151.0,'mmHg',1,'2026-08-02 12:21:00','2026-08-02 12:21:00'),
('elder_002','dev_004','blood_pressure_dia',96.0,'mmHg',1,'2026-08-02 12:21:00','2026-08-02 12:21:00'),
('elder_002','dev_004','temperature',37.2,'C',1,'2026-08-02 12:21:00','2026-08-02 12:21:00'),
('elder_002','dev_004','heart_rate',99.0,'bpm',0,'2026-08-02 13:16:00','2026-08-02 13:16:00'),
('elder_002','dev_004','spo2',94.0,'%',0,'2026-08-02 13:16:00','2026-08-02 13:16:00'),
('elder_002','dev_004','blood_pressure_sys',151.0,'mmHg',0,'2026-08-02 13:16:00','2026-08-02 13:16:00'),
('elder_002','dev_004','blood_pressure_dia',96.0,'mmHg',0,'2026-08-02 13:16:00','2026-08-02 13:16:00'),
('elder_002','dev_004','temperature',36.9,'C',0,'2026-08-02 13:16:00','2026-08-02 13:16:00'),
('elder_002','dev_004','heart_rate',100.0,'bpm',1,'2026-08-02 14:52:00','2026-08-02 14:52:00'),
('elder_002','dev_004','spo2',89.0,'%',1,'2026-08-02 14:52:00','2026-08-02 14:52:00'),
('elder_002','dev_004','blood_pressure_sys',144.0,'mmHg',1,'2026-08-02 14:52:00','2026-08-02 14:52:00'),
('elder_002','dev_004','blood_pressure_dia',87.0,'mmHg',1,'2026-08-02 14:52:00','2026-08-02 14:52:00'),
('elder_002','dev_004','temperature',36.8,'C',1,'2026-08-02 14:52:00','2026-08-02 14:52:00'),
('elder_002','dev_004','heart_rate',95.0,'bpm',1,'2026-08-02 15:06:00','2026-08-02 15:06:00'),
('elder_002','dev_004','spo2',89.0,'%',1,'2026-08-02 15:06:00','2026-08-02 15:06:00'),
('elder_002','dev_004','blood_pressure_sys',140.0,'mmHg',1,'2026-08-02 15:06:00','2026-08-02 15:06:00'),
('elder_002','dev_004','blood_pressure_dia',88.0,'mmHg',1,'2026-08-02 15:06:00','2026-08-02 15:06:00'),
('elder_002','dev_004','temperature',36.9,'C',1,'2026-08-02 15:06:00','2026-08-02 15:06:00'),
('elder_002','dev_004','heart_rate',100.0,'bpm',1,'2026-08-02 16:16:00','2026-08-02 16:16:00'),
('elder_002','dev_004','spo2',91.0,'%',1,'2026-08-02 16:16:00','2026-08-02 16:16:00'),
('elder_002','dev_004','blood_pressure_sys',148.0,'mmHg',1,'2026-08-02 16:16:00','2026-08-02 16:16:00'),
('elder_002','dev_004','blood_pressure_dia',95.0,'mmHg',1,'2026-08-02 16:16:00','2026-08-02 16:16:00'),
('elder_002','dev_004','temperature',36.7,'C',1,'2026-08-02 16:16:00','2026-08-02 16:16:00'),
('elder_002','dev_004','heart_rate',88.0,'bpm',0,'2026-08-02 17:04:00','2026-08-02 17:04:00'),
('elder_002','dev_004','spo2',94.0,'%',0,'2026-08-02 17:04:00','2026-08-02 17:04:00'),
('elder_002','dev_004','blood_pressure_sys',148.0,'mmHg',0,'2026-08-02 17:04:00','2026-08-02 17:04:00'),
('elder_002','dev_004','blood_pressure_dia',93.0,'mmHg',0,'2026-08-02 17:04:00','2026-08-02 17:04:00'),
('elder_002','dev_004','temperature',37.3,'C',0,'2026-08-02 17:04:00','2026-08-02 17:04:00'),
('elder_002','dev_004','heart_rate',107.0,'bpm',0,'2026-08-02 18:29:00','2026-08-02 18:29:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-08-02 18:29:00','2026-08-02 18:29:00'),
('elder_002','dev_004','blood_pressure_sys',145.0,'mmHg',0,'2026-08-02 18:29:00','2026-08-02 18:29:00'),
('elder_002','dev_004','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 18:29:00','2026-08-02 18:29:00'),
('elder_002','dev_004','temperature',36.8,'C',0,'2026-08-02 18:29:00','2026-08-02 18:29:00'),
('elder_002','dev_004','heart_rate',104.0,'bpm',0,'2026-08-02 19:15:00','2026-08-02 19:15:00'),
('elder_002','dev_004','spo2',94.0,'%',0,'2026-08-02 19:15:00','2026-08-02 19:15:00'),
('elder_002','dev_004','blood_pressure_sys',152.0,'mmHg',0,'2026-08-02 19:15:00','2026-08-02 19:15:00'),
('elder_002','dev_004','blood_pressure_dia',90.0,'mmHg',0,'2026-08-02 19:15:00','2026-08-02 19:15:00'),
('elder_002','dev_004','temperature',37.1,'C',0,'2026-08-02 19:15:00','2026-08-02 19:15:00'),
('elder_002','dev_004','heart_rate',91.0,'bpm',0,'2026-08-02 20:49:00','2026-08-02 20:49:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-08-02 20:49:00','2026-08-02 20:49:00'),
('elder_002','dev_004','blood_pressure_sys',143.0,'mmHg',0,'2026-08-02 20:49:00','2026-08-02 20:49:00'),
('elder_002','dev_004','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 20:49:00','2026-08-02 20:49:00'),
('elder_002','dev_004','temperature',37.6,'C',0,'2026-08-02 20:49:00','2026-08-02 20:49:00'),
('elder_002','dev_004','heart_rate',107.0,'bpm',1,'2026-08-02 21:27:00','2026-08-02 21:27:00'),
('elder_002','dev_004','spo2',88.0,'%',1,'2026-08-02 21:27:00','2026-08-02 21:27:00'),
('elder_002','dev_004','blood_pressure_sys',159.0,'mmHg',1,'2026-08-02 21:27:00','2026-08-02 21:27:00'),
('elder_002','dev_004','blood_pressure_dia',99.0,'mmHg',1,'2026-08-02 21:27:00','2026-08-02 21:27:00'),
('elder_002','dev_004','temperature',37.1,'C',1,'2026-08-02 21:27:00','2026-08-02 21:27:00'),
('elder_002','dev_004','heart_rate',106.0,'bpm',0,'2026-08-02 22:36:00','2026-08-02 22:36:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-08-02 22:36:00','2026-08-02 22:36:00'),
('elder_002','dev_004','blood_pressure_sys',166.0,'mmHg',0,'2026-08-02 22:36:00','2026-08-02 22:36:00'),
('elder_002','dev_004','blood_pressure_dia',102.0,'mmHg',0,'2026-08-02 22:36:00','2026-08-02 22:36:00'),
('elder_002','dev_004','temperature',37.1,'C',0,'2026-08-02 22:36:00','2026-08-02 22:36:00'),
('elder_002','dev_004','heart_rate',104.0,'bpm',1,'2026-08-02 23:04:00','2026-08-02 23:04:00'),
('elder_002','dev_004','spo2',91.0,'%',1,'2026-08-02 23:04:00','2026-08-02 23:04:00'),
('elder_002','dev_004','blood_pressure_sys',151.0,'mmHg',1,'2026-08-02 23:04:00','2026-08-02 23:04:00'),
('elder_002','dev_004','blood_pressure_dia',94.0,'mmHg',1,'2026-08-02 23:04:00','2026-08-02 23:04:00'),
('elder_002','dev_004','temperature',36.9,'C',1,'2026-08-02 23:04:00','2026-08-02 23:04:00'),
('elder_003','dev_007','heart_rate',98.0,'bpm',0,'2026-08-02 00:44:00','2026-08-02 00:44:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-08-02 00:44:00','2026-08-02 00:44:00'),
('elder_003','dev_007','blood_pressure_sys',128.0,'mmHg',0,'2026-08-02 00:44:00','2026-08-02 00:44:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',0,'2026-08-02 00:44:00','2026-08-02 00:44:00'),
('elder_003','dev_007','temperature',36.7,'C',0,'2026-08-02 00:44:00','2026-08-02 00:44:00'),
('elder_003','dev_007','heart_rate',107.0,'bpm',0,'2026-08-02 01:03:00','2026-08-02 01:03:00'),
('elder_003','dev_007','spo2',92.0,'%',0,'2026-08-02 01:03:00','2026-08-02 01:03:00'),
('elder_003','dev_007','blood_pressure_sys',150.0,'mmHg',0,'2026-08-02 01:03:00','2026-08-02 01:03:00'),
('elder_003','dev_007','blood_pressure_dia',102.0,'mmHg',0,'2026-08-02 01:03:00','2026-08-02 01:03:00'),
('elder_003','dev_007','temperature',37.1,'C',0,'2026-08-02 01:03:00','2026-08-02 01:03:00'),
('elder_003','dev_007','heart_rate',103.0,'bpm',1,'2026-08-02 02:26:00','2026-08-02 02:26:00'),
('elder_003','dev_007','spo2',91.0,'%',1,'2026-08-02 02:26:00','2026-08-02 02:26:00'),
('elder_003','dev_007','blood_pressure_sys',143.0,'mmHg',1,'2026-08-02 02:26:00','2026-08-02 02:26:00'),
('elder_003','dev_007','blood_pressure_dia',95.0,'mmHg',1,'2026-08-02 02:26:00','2026-08-02 02:26:00'),
('elder_003','dev_007','temperature',36.8,'C',1,'2026-08-02 02:26:00','2026-08-02 02:26:00'),
('elder_003','dev_007','heart_rate',94.0,'bpm',1,'2026-08-02 03:25:00','2026-08-02 03:25:00'),
('elder_003','dev_007','spo2',91.0,'%',1,'2026-08-02 03:25:00','2026-08-02 03:25:00'),
('elder_003','dev_007','blood_pressure_sys',150.0,'mmHg',1,'2026-08-02 03:25:00','2026-08-02 03:25:00'),
('elder_003','dev_007','blood_pressure_dia',91.0,'mmHg',1,'2026-08-02 03:25:00','2026-08-02 03:25:00'),
('elder_003','dev_007','temperature',37.0,'C',1,'2026-08-02 03:25:00','2026-08-02 03:25:00'),
('elder_003','dev_007','heart_rate',101.0,'bpm',0,'2026-08-02 04:33:00','2026-08-02 04:33:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-08-02 04:33:00','2026-08-02 04:33:00'),
('elder_003','dev_007','blood_pressure_sys',147.0,'mmHg',0,'2026-08-02 04:33:00','2026-08-02 04:33:00'),
('elder_003','dev_007','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 04:33:00','2026-08-02 04:33:00'),
('elder_003','dev_007','temperature',36.8,'C',0,'2026-08-02 04:33:00','2026-08-02 04:33:00'),
('elder_003','dev_007','heart_rate',99.0,'bpm',0,'2026-08-02 05:42:00','2026-08-02 05:42:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-08-02 05:42:00','2026-08-02 05:42:00'),
('elder_003','dev_007','blood_pressure_sys',150.0,'mmHg',0,'2026-08-02 05:42:00','2026-08-02 05:42:00'),
('elder_003','dev_007','blood_pressure_dia',101.0,'mmHg',0,'2026-08-02 05:42:00','2026-08-02 05:42:00'),
('elder_003','dev_007','temperature',37.0,'C',0,'2026-08-02 05:42:00','2026-08-02 05:42:00'),
('elder_003','dev_007','heart_rate',80.0,'bpm',1,'2026-08-02 06:43:00','2026-08-02 06:43:00'),
('elder_003','dev_007','spo2',91.0,'%',1,'2026-08-02 06:43:00','2026-08-02 06:43:00'),
('elder_003','dev_007','blood_pressure_sys',135.0,'mmHg',1,'2026-08-02 06:43:00','2026-08-02 06:43:00'),
('elder_003','dev_007','blood_pressure_dia',81.0,'mmHg',1,'2026-08-02 06:43:00','2026-08-02 06:43:00'),
('elder_003','dev_007','temperature',37.2,'C',1,'2026-08-02 06:43:00','2026-08-02 06:43:00'),
('elder_003','dev_007','heart_rate',92.0,'bpm',0,'2026-08-02 07:25:00','2026-08-02 07:25:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-08-02 07:25:00','2026-08-02 07:25:00'),
('elder_003','dev_007','blood_pressure_sys',142.0,'mmHg',0,'2026-08-02 07:25:00','2026-08-02 07:25:00'),
('elder_003','dev_007','blood_pressure_dia',84.0,'mmHg',0,'2026-08-02 07:25:00','2026-08-02 07:25:00'),
('elder_003','dev_007','temperature',36.7,'C',0,'2026-08-02 07:25:00','2026-08-02 07:25:00'),
('elder_003','dev_007','heart_rate',82.0,'bpm',0,'2026-08-02 08:43:00','2026-08-02 08:43:00'),
('elder_003','dev_007','spo2',96.0,'%',0,'2026-08-02 08:43:00','2026-08-02 08:43:00'),
('elder_003','dev_007','blood_pressure_sys',139.0,'mmHg',0,'2026-08-02 08:43:00','2026-08-02 08:43:00'),
('elder_003','dev_007','blood_pressure_dia',84.0,'mmHg',0,'2026-08-02 08:43:00','2026-08-02 08:43:00'),
('elder_003','dev_007','temperature',37.3,'C',0,'2026-08-02 08:43:00','2026-08-02 08:43:00'),
('elder_003','dev_007','heart_rate',98.0,'bpm',1,'2026-08-02 09:19:00','2026-08-02 09:19:00'),
('elder_003','dev_007','spo2',91.0,'%',1,'2026-08-02 09:19:00','2026-08-02 09:19:00'),
('elder_003','dev_007','blood_pressure_sys',128.0,'mmHg',1,'2026-08-02 09:19:00','2026-08-02 09:19:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',1,'2026-08-02 09:19:00','2026-08-02 09:19:00'),
('elder_003','dev_007','temperature',37.2,'C',1,'2026-08-02 09:19:00','2026-08-02 09:19:00'),
('elder_003','dev_007','heart_rate',88.0,'bpm',1,'2026-08-02 10:54:00','2026-08-02 10:54:00'),
('elder_003','dev_007','spo2',91.0,'%',1,'2026-08-02 10:54:00','2026-08-02 10:54:00'),
('elder_003','dev_007','blood_pressure_sys',127.0,'mmHg',1,'2026-08-02 10:54:00','2026-08-02 10:54:00'),
('elder_003','dev_007','blood_pressure_dia',86.0,'mmHg',1,'2026-08-02 10:54:00','2026-08-02 10:54:00'),
('elder_003','dev_007','temperature',37.0,'C',1,'2026-08-02 10:54:00','2026-08-02 10:54:00'),
('elder_003','dev_007','heart_rate',89.0,'bpm',0,'2026-08-02 11:12:00','2026-08-02 11:12:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-08-02 11:12:00','2026-08-02 11:12:00'),
('elder_003','dev_007','blood_pressure_sys',135.0,'mmHg',0,'2026-08-02 11:12:00','2026-08-02 11:12:00'),
('elder_003','dev_007','blood_pressure_dia',90.0,'mmHg',0,'2026-08-02 11:12:00','2026-08-02 11:12:00'),
('elder_003','dev_007','temperature',36.8,'C',0,'2026-08-02 11:12:00','2026-08-02 11:12:00'),
('elder_003','dev_007','heart_rate',89.0,'bpm',1,'2026-08-02 12:28:00','2026-08-02 12:28:00'),
('elder_003','dev_007','spo2',91.0,'%',1,'2026-08-02 12:28:00','2026-08-02 12:28:00'),
('elder_003','dev_007','blood_pressure_sys',124.0,'mmHg',1,'2026-08-02 12:28:00','2026-08-02 12:28:00'),
('elder_003','dev_007','blood_pressure_dia',89.0,'mmHg',1,'2026-08-02 12:28:00','2026-08-02 12:28:00'),
('elder_003','dev_007','temperature',37.0,'C',1,'2026-08-02 12:28:00','2026-08-02 12:28:00'),
('elder_003','dev_007','heart_rate',100.0,'bpm',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_003','dev_007','spo2',94.0,'%',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_003','dev_007','blood_pressure_sys',126.0,'mmHg',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_003','dev_007','blood_pressure_dia',83.0,'mmHg',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_003','dev_007','temperature',37.2,'C',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_003','dev_007','heart_rate',85.0,'bpm',0,'2026-08-02 14:54:00','2026-08-02 14:54:00'),
('elder_003','dev_007','spo2',94.0,'%',0,'2026-08-02 14:54:00','2026-08-02 14:54:00'),
('elder_003','dev_007','blood_pressure_sys',136.0,'mmHg',0,'2026-08-02 14:54:00','2026-08-02 14:54:00'),
('elder_003','dev_007','blood_pressure_dia',84.0,'mmHg',0,'2026-08-02 14:54:00','2026-08-02 14:54:00'),
('elder_003','dev_007','temperature',37.0,'C',0,'2026-08-02 14:54:00','2026-08-02 14:54:00'),
('elder_003','dev_007','heart_rate',89.0,'bpm',0,'2026-08-02 15:34:00','2026-08-02 15:34:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-08-02 15:34:00','2026-08-02 15:34:00'),
('elder_003','dev_007','blood_pressure_sys',127.0,'mmHg',0,'2026-08-02 15:34:00','2026-08-02 15:34:00'),
('elder_003','dev_007','blood_pressure_dia',86.0,'mmHg',0,'2026-08-02 15:34:00','2026-08-02 15:34:00'),
('elder_003','dev_007','temperature',36.6,'C',0,'2026-08-02 15:34:00','2026-08-02 15:34:00'),
('elder_003','dev_007','heart_rate',86.0,'bpm',0,'2026-08-02 16:15:00','2026-08-02 16:15:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-08-02 16:15:00','2026-08-02 16:15:00'),
('elder_003','dev_007','blood_pressure_sys',142.0,'mmHg',0,'2026-08-02 16:15:00','2026-08-02 16:15:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',0,'2026-08-02 16:15:00','2026-08-02 16:15:00'),
('elder_003','dev_007','temperature',36.6,'C',0,'2026-08-02 16:15:00','2026-08-02 16:15:00'),
('elder_003','dev_007','heart_rate',100.0,'bpm',0,'2026-08-02 17:12:00','2026-08-02 17:12:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-08-02 17:12:00','2026-08-02 17:12:00'),
('elder_003','dev_007','blood_pressure_sys',131.0,'mmHg',0,'2026-08-02 17:12:00','2026-08-02 17:12:00'),
('elder_003','dev_007','blood_pressure_dia',85.0,'mmHg',0,'2026-08-02 17:12:00','2026-08-02 17:12:00'),
('elder_003','dev_007','temperature',36.5,'C',0,'2026-08-02 17:12:00','2026-08-02 17:12:00'),
('elder_003','dev_007','heart_rate',82.0,'bpm',1,'2026-08-02 18:48:00','2026-08-02 18:48:00'),
('elder_003','dev_007','spo2',91.0,'%',1,'2026-08-02 18:48:00','2026-08-02 18:48:00'),
('elder_003','dev_007','blood_pressure_sys',137.0,'mmHg',1,'2026-08-02 18:48:00','2026-08-02 18:48:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',1,'2026-08-02 18:48:00','2026-08-02 18:48:00'),
('elder_003','dev_007','temperature',36.8,'C',1,'2026-08-02 18:48:00','2026-08-02 18:48:00'),
('elder_003','dev_007','heart_rate',85.0,'bpm',0,'2026-08-02 19:40:00','2026-08-02 19:40:00'),
('elder_003','dev_007','spo2',96.0,'%',0,'2026-08-02 19:40:00','2026-08-02 19:40:00'),
('elder_003','dev_007','blood_pressure_sys',125.0,'mmHg',0,'2026-08-02 19:40:00','2026-08-02 19:40:00'),
('elder_003','dev_007','blood_pressure_dia',89.0,'mmHg',0,'2026-08-02 19:40:00','2026-08-02 19:40:00'),
('elder_003','dev_007','temperature',37.0,'C',0,'2026-08-02 19:40:00','2026-08-02 19:40:00'),
('elder_003','dev_007','heart_rate',94.0,'bpm',1,'2026-08-02 20:00:00','2026-08-02 20:00:00'),
('elder_003','dev_007','spo2',91.0,'%',1,'2026-08-02 20:00:00','2026-08-02 20:00:00'),
('elder_003','dev_007','blood_pressure_sys',131.0,'mmHg',1,'2026-08-02 20:00:00','2026-08-02 20:00:00'),
('elder_003','dev_007','blood_pressure_dia',85.0,'mmHg',1,'2026-08-02 20:00:00','2026-08-02 20:00:00'),
('elder_003','dev_007','temperature',37.1,'C',1,'2026-08-02 20:00:00','2026-08-02 20:00:00'),
('elder_003','dev_007','heart_rate',106.0,'bpm',0,'2026-08-02 21:24:00','2026-08-02 21:24:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-08-02 21:24:00','2026-08-02 21:24:00'),
('elder_003','dev_007','blood_pressure_sys',138.0,'mmHg',0,'2026-08-02 21:24:00','2026-08-02 21:24:00'),
('elder_003','dev_007','blood_pressure_dia',94.0,'mmHg',0,'2026-08-02 21:24:00','2026-08-02 21:24:00'),
('elder_003','dev_007','temperature',37.1,'C',0,'2026-08-02 21:24:00','2026-08-02 21:24:00'),
('elder_003','dev_007','heart_rate',104.0,'bpm',0,'2026-08-02 22:41:00','2026-08-02 22:41:00'),
('elder_003','dev_007','spo2',94.0,'%',0,'2026-08-02 22:41:00','2026-08-02 22:41:00'),
('elder_003','dev_007','blood_pressure_sys',138.0,'mmHg',0,'2026-08-02 22:41:00','2026-08-02 22:41:00'),
('elder_003','dev_007','blood_pressure_dia',88.0,'mmHg',0,'2026-08-02 22:41:00','2026-08-02 22:41:00'),
('elder_003','dev_007','temperature',36.5,'C',0,'2026-08-02 22:41:00','2026-08-02 22:41:00'),
('elder_003','dev_007','heart_rate',104.0,'bpm',0,'2026-08-02 23:46:00','2026-08-02 23:46:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-08-02 23:46:00','2026-08-02 23:46:00'),
('elder_003','dev_007','blood_pressure_sys',135.0,'mmHg',0,'2026-08-02 23:46:00','2026-08-02 23:46:00'),
('elder_003','dev_007','blood_pressure_dia',94.0,'mmHg',0,'2026-08-02 23:46:00','2026-08-02 23:46:00'),
('elder_003','dev_007','temperature',36.4,'C',0,'2026-08-02 23:46:00','2026-08-02 23:46:00'),
('elder_004','dev_010','heart_rate',106.0,'bpm',1,'2026-08-02 00:56:00','2026-08-02 00:56:00'),
('elder_004','dev_010','spo2',89.0,'%',1,'2026-08-02 00:56:00','2026-08-02 00:56:00'),
('elder_004','dev_010','blood_pressure_sys',152.0,'mmHg',1,'2026-08-02 00:56:00','2026-08-02 00:56:00'),
('elder_004','dev_010','blood_pressure_dia',86.0,'mmHg',1,'2026-08-02 00:56:00','2026-08-02 00:56:00'),
('elder_004','dev_010','temperature',37.5,'C',1,'2026-08-02 00:56:00','2026-08-02 00:56:00'),
('elder_004','dev_010','heart_rate',107.0,'bpm',1,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_004','dev_010','spo2',90.0,'%',1,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_004','dev_010','blood_pressure_sys',169.0,'mmHg',1,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_004','dev_010','blood_pressure_dia',98.0,'mmHg',1,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_004','dev_010','temperature',37.5,'C',1,'2026-08-02 01:51:00','2026-08-02 01:51:00'),
('elder_004','dev_010','heart_rate',109.0,'bpm',1,'2026-08-02 02:21:00','2026-08-02 02:21:00'),
('elder_004','dev_010','spo2',90.0,'%',1,'2026-08-02 02:21:00','2026-08-02 02:21:00'),
('elder_004','dev_010','blood_pressure_sys',162.0,'mmHg',1,'2026-08-02 02:21:00','2026-08-02 02:21:00'),
('elder_004','dev_010','blood_pressure_dia',102.0,'mmHg',1,'2026-08-02 02:21:00','2026-08-02 02:21:00'),
('elder_004','dev_010','temperature',37.6,'C',1,'2026-08-02 02:21:00','2026-08-02 02:21:00'),
('elder_004','dev_010','heart_rate',102.0,'bpm',1,'2026-08-02 03:41:00','2026-08-02 03:41:00'),
('elder_004','dev_010','spo2',87.0,'%',1,'2026-08-02 03:41:00','2026-08-02 03:41:00'),
('elder_004','dev_010','blood_pressure_sys',162.0,'mmHg',1,'2026-08-02 03:41:00','2026-08-02 03:41:00'),
('elder_004','dev_010','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 03:41:00','2026-08-02 03:41:00'),
('elder_004','dev_010','temperature',36.8,'C',1,'2026-08-02 03:41:00','2026-08-02 03:41:00'),
('elder_004','dev_010','heart_rate',101.0,'bpm',0,'2026-08-02 04:27:00','2026-08-02 04:27:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-08-02 04:27:00','2026-08-02 04:27:00'),
('elder_004','dev_010','blood_pressure_sys',170.0,'mmHg',0,'2026-08-02 04:27:00','2026-08-02 04:27:00'),
('elder_004','dev_010','blood_pressure_dia',105.0,'mmHg',0,'2026-08-02 04:27:00','2026-08-02 04:27:00'),
('elder_004','dev_010','temperature',37.1,'C',0,'2026-08-02 04:27:00','2026-08-02 04:27:00'),
('elder_004','dev_010','heart_rate',103.0,'bpm',1,'2026-08-02 05:00:00','2026-08-02 05:00:00'),
('elder_004','dev_010','spo2',89.0,'%',1,'2026-08-02 05:00:00','2026-08-02 05:00:00'),
('elder_004','dev_010','blood_pressure_sys',166.0,'mmHg',1,'2026-08-02 05:00:00','2026-08-02 05:00:00'),
('elder_004','dev_010','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 05:00:00','2026-08-02 05:00:00'),
('elder_004','dev_010','temperature',37.0,'C',1,'2026-08-02 05:00:00','2026-08-02 05:00:00'),
('elder_004','dev_010','heart_rate',90.0,'bpm',1,'2026-08-02 06:15:00','2026-08-02 06:15:00'),
('elder_004','dev_010','spo2',91.0,'%',1,'2026-08-02 06:15:00','2026-08-02 06:15:00'),
('elder_004','dev_010','blood_pressure_sys',142.0,'mmHg',1,'2026-08-02 06:15:00','2026-08-02 06:15:00'),
('elder_004','dev_010','blood_pressure_dia',85.0,'mmHg',1,'2026-08-02 06:15:00','2026-08-02 06:15:00'),
('elder_004','dev_010','temperature',37.5,'C',1,'2026-08-02 06:15:00','2026-08-02 06:15:00'),
('elder_004','dev_010','heart_rate',105.0,'bpm',1,'2026-08-02 07:11:00','2026-08-02 07:11:00'),
('elder_004','dev_010','spo2',88.0,'%',1,'2026-08-02 07:11:00','2026-08-02 07:11:00'),
('elder_004','dev_010','blood_pressure_sys',147.0,'mmHg',1,'2026-08-02 07:11:00','2026-08-02 07:11:00'),
('elder_004','dev_010','blood_pressure_dia',89.0,'mmHg',1,'2026-08-02 07:11:00','2026-08-02 07:11:00'),
('elder_004','dev_010','temperature',37.4,'C',1,'2026-08-02 07:11:00','2026-08-02 07:11:00'),
('elder_004','dev_010','heart_rate',103.0,'bpm',0,'2026-08-02 08:29:00','2026-08-02 08:29:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-08-02 08:29:00','2026-08-02 08:29:00'),
('elder_004','dev_010','blood_pressure_sys',147.0,'mmHg',0,'2026-08-02 08:29:00','2026-08-02 08:29:00'),
('elder_004','dev_010','blood_pressure_dia',90.0,'mmHg',0,'2026-08-02 08:29:00','2026-08-02 08:29:00'),
('elder_004','dev_010','temperature',37.4,'C',0,'2026-08-02 08:29:00','2026-08-02 08:29:00'),
('elder_004','dev_010','heart_rate',98.0,'bpm',0,'2026-08-02 09:22:00','2026-08-02 09:22:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-08-02 09:22:00','2026-08-02 09:22:00'),
('elder_004','dev_010','blood_pressure_sys',150.0,'mmHg',0,'2026-08-02 09:22:00','2026-08-02 09:22:00'),
('elder_004','dev_010','blood_pressure_dia',86.0,'mmHg',0,'2026-08-02 09:22:00','2026-08-02 09:22:00'),
('elder_004','dev_010','temperature',37.1,'C',0,'2026-08-02 09:22:00','2026-08-02 09:22:00'),
('elder_004','dev_010','heart_rate',94.0,'bpm',0,'2026-08-02 10:16:00','2026-08-02 10:16:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-08-02 10:16:00','2026-08-02 10:16:00'),
('elder_004','dev_010','blood_pressure_sys',150.0,'mmHg',0,'2026-08-02 10:16:00','2026-08-02 10:16:00'),
('elder_004','dev_010','blood_pressure_dia',86.0,'mmHg',0,'2026-08-02 10:16:00','2026-08-02 10:16:00'),
('elder_004','dev_010','temperature',37.0,'C',0,'2026-08-02 10:16:00','2026-08-02 10:16:00'),
('elder_004','dev_010','heart_rate',102.0,'bpm',1,'2026-08-02 11:24:00','2026-08-02 11:24:00'),
('elder_004','dev_010','spo2',90.0,'%',1,'2026-08-02 11:24:00','2026-08-02 11:24:00'),
('elder_004','dev_010','blood_pressure_sys',145.0,'mmHg',1,'2026-08-02 11:24:00','2026-08-02 11:24:00'),
('elder_004','dev_010','blood_pressure_dia',94.0,'mmHg',1,'2026-08-02 11:24:00','2026-08-02 11:24:00'),
('elder_004','dev_010','temperature',37.0,'C',1,'2026-08-02 11:24:00','2026-08-02 11:24:00'),
('elder_004','dev_010','heart_rate',93.0,'bpm',0,'2026-08-02 12:35:00','2026-08-02 12:35:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-08-02 12:35:00','2026-08-02 12:35:00'),
('elder_004','dev_010','blood_pressure_sys',147.0,'mmHg',0,'2026-08-02 12:35:00','2026-08-02 12:35:00'),
('elder_004','dev_010','blood_pressure_dia',85.0,'mmHg',0,'2026-08-02 12:35:00','2026-08-02 12:35:00'),
('elder_004','dev_010','temperature',37.7,'C',0,'2026-08-02 12:35:00','2026-08-02 12:35:00'),
('elder_004','dev_010','heart_rate',92.0,'bpm',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_004','dev_010','blood_pressure_sys',140.0,'mmHg',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_004','dev_010','blood_pressure_dia',89.0,'mmHg',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_004','dev_010','temperature',37.1,'C',0,'2026-08-02 13:23:00','2026-08-02 13:23:00'),
('elder_004','dev_010','heart_rate',90.0,'bpm',0,'2026-08-02 14:48:00','2026-08-02 14:48:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-08-02 14:48:00','2026-08-02 14:48:00'),
('elder_004','dev_010','blood_pressure_sys',143.0,'mmHg',0,'2026-08-02 14:48:00','2026-08-02 14:48:00'),
('elder_004','dev_010','blood_pressure_dia',87.0,'mmHg',0,'2026-08-02 14:48:00','2026-08-02 14:48:00'),
('elder_004','dev_010','temperature',37.2,'C',0,'2026-08-02 14:48:00','2026-08-02 14:48:00'),
('elder_004','dev_010','heart_rate',88.0,'bpm',0,'2026-08-02 15:31:00','2026-08-02 15:31:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-08-02 15:31:00','2026-08-02 15:31:00'),
('elder_004','dev_010','blood_pressure_sys',142.0,'mmHg',0,'2026-08-02 15:31:00','2026-08-02 15:31:00'),
('elder_004','dev_010','blood_pressure_dia',96.0,'mmHg',0,'2026-08-02 15:31:00','2026-08-02 15:31:00'),
('elder_004','dev_010','temperature',37.0,'C',0,'2026-08-02 15:31:00','2026-08-02 15:31:00'),
('elder_004','dev_010','heart_rate',95.0,'bpm',1,'2026-08-02 16:59:00','2026-08-02 16:59:00'),
('elder_004','dev_010','spo2',90.0,'%',1,'2026-08-02 16:59:00','2026-08-02 16:59:00'),
('elder_004','dev_010','blood_pressure_sys',152.0,'mmHg',1,'2026-08-02 16:59:00','2026-08-02 16:59:00'),
('elder_004','dev_010','blood_pressure_dia',94.0,'mmHg',1,'2026-08-02 16:59:00','2026-08-02 16:59:00'),
('elder_004','dev_010','temperature',37.3,'C',1,'2026-08-02 16:59:00','2026-08-02 16:59:00'),
('elder_004','dev_010','heart_rate',89.0,'bpm',1,'2026-08-02 17:03:00','2026-08-02 17:03:00'),
('elder_004','dev_010','spo2',90.0,'%',1,'2026-08-02 17:03:00','2026-08-02 17:03:00'),
('elder_004','dev_010','blood_pressure_sys',134.0,'mmHg',1,'2026-08-02 17:03:00','2026-08-02 17:03:00'),
('elder_004','dev_010','blood_pressure_dia',85.0,'mmHg',1,'2026-08-02 17:03:00','2026-08-02 17:03:00'),
('elder_004','dev_010','temperature',37.2,'C',1,'2026-08-02 17:03:00','2026-08-02 17:03:00'),
('elder_004','dev_010','heart_rate',89.0,'bpm',0,'2026-08-02 18:45:00','2026-08-02 18:45:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-08-02 18:45:00','2026-08-02 18:45:00'),
('elder_004','dev_010','blood_pressure_sys',147.0,'mmHg',0,'2026-08-02 18:45:00','2026-08-02 18:45:00'),
('elder_004','dev_010','blood_pressure_dia',86.0,'mmHg',0,'2026-08-02 18:45:00','2026-08-02 18:45:00'),
('elder_004','dev_010','temperature',37.1,'C',0,'2026-08-02 18:45:00','2026-08-02 18:45:00'),
('elder_004','dev_010','heart_rate',93.0,'bpm',1,'2026-08-02 19:27:00','2026-08-02 19:27:00'),
('elder_004','dev_010','spo2',89.0,'%',1,'2026-08-02 19:27:00','2026-08-02 19:27:00'),
('elder_004','dev_010','blood_pressure_sys',140.0,'mmHg',1,'2026-08-02 19:27:00','2026-08-02 19:27:00'),
('elder_004','dev_010','blood_pressure_dia',89.0,'mmHg',1,'2026-08-02 19:27:00','2026-08-02 19:27:00'),
('elder_004','dev_010','temperature',37.5,'C',1,'2026-08-02 19:27:00','2026-08-02 19:27:00'),
('elder_004','dev_010','heart_rate',94.0,'bpm',0,'2026-08-02 20:02:00','2026-08-02 20:02:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-08-02 20:02:00','2026-08-02 20:02:00'),
('elder_004','dev_010','blood_pressure_sys',145.0,'mmHg',0,'2026-08-02 20:02:00','2026-08-02 20:02:00'),
('elder_004','dev_010','blood_pressure_dia',96.0,'mmHg',0,'2026-08-02 20:02:00','2026-08-02 20:02:00'),
('elder_004','dev_010','temperature',37.4,'C',0,'2026-08-02 20:02:00','2026-08-02 20:02:00'),
('elder_004','dev_010','heart_rate',104.0,'bpm',1,'2026-08-02 21:33:00','2026-08-02 21:33:00'),
('elder_004','dev_010','spo2',91.0,'%',1,'2026-08-02 21:33:00','2026-08-02 21:33:00'),
('elder_004','dev_010','blood_pressure_sys',156.0,'mmHg',1,'2026-08-02 21:33:00','2026-08-02 21:33:00'),
('elder_004','dev_010','blood_pressure_dia',97.0,'mmHg',1,'2026-08-02 21:33:00','2026-08-02 21:33:00'),
('elder_004','dev_010','temperature',37.6,'C',1,'2026-08-02 21:33:00','2026-08-02 21:33:00'),
('elder_004','dev_010','heart_rate',112.0,'bpm',1,'2026-08-02 22:47:00','2026-08-02 22:47:00'),
('elder_004','dev_010','spo2',92.0,'%',1,'2026-08-02 22:47:00','2026-08-02 22:47:00'),
('elder_004','dev_010','blood_pressure_sys',149.0,'mmHg',1,'2026-08-02 22:47:00','2026-08-02 22:47:00'),
('elder_004','dev_010','blood_pressure_dia',100.0,'mmHg',1,'2026-08-02 22:47:00','2026-08-02 22:47:00'),
('elder_004','dev_010','temperature',37.1,'C',1,'2026-08-02 22:47:00','2026-08-02 22:47:00'),
('elder_004','dev_010','heart_rate',99.0,'bpm',1,'2026-08-02 23:19:00','2026-08-02 23:19:00'),
('elder_004','dev_010','spo2',87.0,'%',1,'2026-08-02 23:19:00','2026-08-02 23:19:00'),
('elder_004','dev_010','blood_pressure_sys',151.0,'mmHg',1,'2026-08-02 23:19:00','2026-08-02 23:19:00'),
('elder_004','dev_010','blood_pressure_dia',98.0,'mmHg',1,'2026-08-02 23:19:00','2026-08-02 23:19:00'),
('elder_004','dev_010','temperature',37.6,'C',1,'2026-08-02 23:19:00','2026-08-02 23:19:00'),
('elder_005','dev_012','heart_rate',102.0,'bpm',1,'2026-08-02 00:43:00','2026-08-02 00:43:00'),
('elder_005','dev_012','spo2',87.0,'%',1,'2026-08-02 00:43:00','2026-08-02 00:43:00'),
('elder_005','dev_012','blood_pressure_sys',151.0,'mmHg',1,'2026-08-02 00:43:00','2026-08-02 00:43:00'),
('elder_005','dev_012','blood_pressure_dia',100.0,'mmHg',1,'2026-08-02 00:43:00','2026-08-02 00:43:00'),
('elder_005','dev_012','temperature',37.5,'C',1,'2026-08-02 00:43:00','2026-08-02 00:43:00'),
('elder_005','dev_012','heart_rate',122.0,'bpm',1,'2026-08-02 01:30:00','2026-08-02 01:30:00'),
('elder_005','dev_012','spo2',89.0,'%',1,'2026-08-02 01:30:00','2026-08-02 01:30:00'),
('elder_005','dev_012','blood_pressure_sys',165.0,'mmHg',1,'2026-08-02 01:30:00','2026-08-02 01:30:00'),
('elder_005','dev_012','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 01:30:00','2026-08-02 01:30:00'),
('elder_005','dev_012','temperature',37.0,'C',1,'2026-08-02 01:30:00','2026-08-02 01:30:00'),
('elder_005','dev_012','heart_rate',109.0,'bpm',1,'2026-08-02 02:37:00','2026-08-02 02:37:00'),
('elder_005','dev_012','spo2',90.0,'%',1,'2026-08-02 02:37:00','2026-08-02 02:37:00'),
('elder_005','dev_012','blood_pressure_sys',168.0,'mmHg',1,'2026-08-02 02:37:00','2026-08-02 02:37:00'),
('elder_005','dev_012','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 02:37:00','2026-08-02 02:37:00'),
('elder_005','dev_012','temperature',37.4,'C',1,'2026-08-02 02:37:00','2026-08-02 02:37:00'),
('elder_005','dev_012','heart_rate',116.0,'bpm',1,'2026-08-02 03:43:00','2026-08-02 03:43:00'),
('elder_005','dev_012','spo2',86.0,'%',1,'2026-08-02 03:43:00','2026-08-02 03:43:00'),
('elder_005','dev_012','blood_pressure_sys',169.0,'mmHg',1,'2026-08-02 03:43:00','2026-08-02 03:43:00'),
('elder_005','dev_012','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 03:43:00','2026-08-02 03:43:00'),
('elder_005','dev_012','temperature',37.8,'C',1,'2026-08-02 03:43:00','2026-08-02 03:43:00'),
('elder_005','dev_012','heart_rate',126.0,'bpm',1,'2026-08-02 04:14:00','2026-08-02 04:14:00'),
('elder_005','dev_012','spo2',86.0,'%',1,'2026-08-02 04:14:00','2026-08-02 04:14:00'),
('elder_005','dev_012','blood_pressure_sys',166.0,'mmHg',1,'2026-08-02 04:14:00','2026-08-02 04:14:00'),
('elder_005','dev_012','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 04:14:00','2026-08-02 04:14:00'),
('elder_005','dev_012','temperature',37.1,'C',1,'2026-08-02 04:14:00','2026-08-02 04:14:00'),
('elder_005','dev_012','heart_rate',107.0,'bpm',1,'2026-08-02 05:15:00','2026-08-02 05:15:00'),
('elder_005','dev_012','spo2',89.0,'%',1,'2026-08-02 05:15:00','2026-08-02 05:15:00'),
('elder_005','dev_012','blood_pressure_sys',170.0,'mmHg',1,'2026-08-02 05:15:00','2026-08-02 05:15:00'),
('elder_005','dev_012','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 05:15:00','2026-08-02 05:15:00'),
('elder_005','dev_012','temperature',37.4,'C',1,'2026-08-02 05:15:00','2026-08-02 05:15:00'),
('elder_005','dev_012','heart_rate',103.0,'bpm',1,'2026-08-02 06:00:00','2026-08-02 06:00:00'),
('elder_005','dev_012','spo2',87.0,'%',1,'2026-08-02 06:00:00','2026-08-02 06:00:00'),
('elder_005','dev_012','blood_pressure_sys',144.0,'mmHg',1,'2026-08-02 06:00:00','2026-08-02 06:00:00'),
('elder_005','dev_012','blood_pressure_dia',99.0,'mmHg',1,'2026-08-02 06:00:00','2026-08-02 06:00:00'),
('elder_005','dev_012','temperature',37.4,'C',1,'2026-08-02 06:00:00','2026-08-02 06:00:00'),
('elder_005','dev_012','heart_rate',97.0,'bpm',1,'2026-08-02 07:58:00','2026-08-02 07:58:00'),
('elder_005','dev_012','spo2',88.0,'%',1,'2026-08-02 07:58:00','2026-08-02 07:58:00'),
('elder_005','dev_012','blood_pressure_sys',150.0,'mmHg',1,'2026-08-02 07:58:00','2026-08-02 07:58:00'),
('elder_005','dev_012','blood_pressure_dia',99.0,'mmHg',1,'2026-08-02 07:58:00','2026-08-02 07:58:00'),
('elder_005','dev_012','temperature',37.1,'C',1,'2026-08-02 07:58:00','2026-08-02 07:58:00'),
('elder_005','dev_012','heart_rate',95.0,'bpm',1,'2026-08-02 08:22:00','2026-08-02 08:22:00'),
('elder_005','dev_012','spo2',91.0,'%',1,'2026-08-02 08:22:00','2026-08-02 08:22:00'),
('elder_005','dev_012','blood_pressure_sys',143.0,'mmHg',1,'2026-08-02 08:22:00','2026-08-02 08:22:00'),
('elder_005','dev_012','blood_pressure_dia',90.0,'mmHg',1,'2026-08-02 08:22:00','2026-08-02 08:22:00'),
('elder_005','dev_012','temperature',37.4,'C',1,'2026-08-02 08:22:00','2026-08-02 08:22:00'),
('elder_005','dev_012','heart_rate',92.0,'bpm',1,'2026-08-02 09:04:00','2026-08-02 09:04:00'),
('elder_005','dev_012','spo2',90.0,'%',1,'2026-08-02 09:04:00','2026-08-02 09:04:00'),
('elder_005','dev_012','blood_pressure_sys',146.0,'mmHg',1,'2026-08-02 09:04:00','2026-08-02 09:04:00'),
('elder_005','dev_012','blood_pressure_dia',91.0,'mmHg',1,'2026-08-02 09:04:00','2026-08-02 09:04:00'),
('elder_005','dev_012','temperature',37.7,'C',1,'2026-08-02 09:04:00','2026-08-02 09:04:00'),
('elder_005','dev_012','heart_rate',105.0,'bpm',1,'2026-08-02 10:21:00','2026-08-02 10:21:00'),
('elder_005','dev_012','spo2',87.0,'%',1,'2026-08-02 10:21:00','2026-08-02 10:21:00'),
('elder_005','dev_012','blood_pressure_sys',151.0,'mmHg',1,'2026-08-02 10:21:00','2026-08-02 10:21:00'),
('elder_005','dev_012','blood_pressure_dia',96.0,'mmHg',1,'2026-08-02 10:21:00','2026-08-02 10:21:00'),
('elder_005','dev_012','temperature',37.3,'C',1,'2026-08-02 10:21:00','2026-08-02 10:21:00'),
('elder_005','dev_012','heart_rate',111.0,'bpm',1,'2026-08-02 11:13:00','2026-08-02 11:13:00'),
('elder_005','dev_012','spo2',92.0,'%',1,'2026-08-02 11:13:00','2026-08-02 11:13:00'),
('elder_005','dev_012','blood_pressure_sys',145.0,'mmHg',1,'2026-08-02 11:13:00','2026-08-02 11:13:00'),
('elder_005','dev_012','blood_pressure_dia',98.0,'mmHg',1,'2026-08-02 11:13:00','2026-08-02 11:13:00'),
('elder_005','dev_012','temperature',37.5,'C',1,'2026-08-02 11:13:00','2026-08-02 11:13:00'),
('elder_005','dev_012','heart_rate',111.0,'bpm',1,'2026-08-02 12:12:00','2026-08-02 12:12:00'),
('elder_005','dev_012','spo2',87.0,'%',1,'2026-08-02 12:12:00','2026-08-02 12:12:00'),
('elder_005','dev_012','blood_pressure_sys',156.0,'mmHg',1,'2026-08-02 12:12:00','2026-08-02 12:12:00'),
('elder_005','dev_012','blood_pressure_dia',97.0,'mmHg',1,'2026-08-02 12:12:00','2026-08-02 12:12:00'),
('elder_005','dev_012','temperature',37.3,'C',1,'2026-08-02 12:12:00','2026-08-02 12:12:00'),
('elder_005','dev_012','heart_rate',106.0,'bpm',0,'2026-08-02 13:31:00','2026-08-02 13:31:00'),
('elder_005','dev_012','spo2',92.0,'%',0,'2026-08-02 13:31:00','2026-08-02 13:31:00'),
('elder_005','dev_012','blood_pressure_sys',143.0,'mmHg',0,'2026-08-02 13:31:00','2026-08-02 13:31:00'),
('elder_005','dev_012','blood_pressure_dia',91.0,'mmHg',0,'2026-08-02 13:31:00','2026-08-02 13:31:00'),
('elder_005','dev_012','temperature',37.7,'C',0,'2026-08-02 13:31:00','2026-08-02 13:31:00'),
('elder_005','dev_012','heart_rate',106.0,'bpm',0,'2026-08-02 14:21:00','2026-08-02 14:21:00'),
('elder_005','dev_012','spo2',92.0,'%',0,'2026-08-02 14:21:00','2026-08-02 14:21:00'),
('elder_005','dev_012','blood_pressure_sys',150.0,'mmHg',0,'2026-08-02 14:21:00','2026-08-02 14:21:00'),
('elder_005','dev_012','blood_pressure_dia',100.0,'mmHg',0,'2026-08-02 14:21:00','2026-08-02 14:21:00'),
('elder_005','dev_012','temperature',37.2,'C',0,'2026-08-02 14:21:00','2026-08-02 14:21:00'),
('elder_005','dev_012','heart_rate',106.0,'bpm',1,'2026-08-02 15:02:00','2026-08-02 15:02:00'),
('elder_005','dev_012','spo2',90.0,'%',1,'2026-08-02 15:02:00','2026-08-02 15:02:00'),
('elder_005','dev_012','blood_pressure_sys',150.0,'mmHg',1,'2026-08-02 15:02:00','2026-08-02 15:02:00'),
('elder_005','dev_012','blood_pressure_dia',99.0,'mmHg',1,'2026-08-02 15:02:00','2026-08-02 15:02:00'),
('elder_005','dev_012','temperature',37.3,'C',1,'2026-08-02 15:02:00','2026-08-02 15:02:00'),
('elder_005','dev_012','heart_rate',107.0,'bpm',1,'2026-08-02 16:33:00','2026-08-02 16:33:00'),
('elder_005','dev_012','spo2',89.0,'%',1,'2026-08-02 16:33:00','2026-08-02 16:33:00'),
('elder_005','dev_012','blood_pressure_sys',147.0,'mmHg',1,'2026-08-02 16:33:00','2026-08-02 16:33:00'),
('elder_005','dev_012','blood_pressure_dia',97.0,'mmHg',1,'2026-08-02 16:33:00','2026-08-02 16:33:00'),
('elder_005','dev_012','temperature',37.0,'C',1,'2026-08-02 16:33:00','2026-08-02 16:33:00'),
('elder_005','dev_012','heart_rate',98.0,'bpm',1,'2026-08-02 17:42:00','2026-08-02 17:42:00'),
('elder_005','dev_012','spo2',89.0,'%',1,'2026-08-02 17:42:00','2026-08-02 17:42:00'),
('elder_005','dev_012','blood_pressure_sys',150.0,'mmHg',1,'2026-08-02 17:42:00','2026-08-02 17:42:00'),
('elder_005','dev_012','blood_pressure_dia',96.0,'mmHg',1,'2026-08-02 17:42:00','2026-08-02 17:42:00'),
('elder_005','dev_012','temperature',37.5,'C',1,'2026-08-02 17:42:00','2026-08-02 17:42:00'),
('elder_005','dev_012','heart_rate',94.0,'bpm',1,'2026-08-02 18:52:00','2026-08-02 18:52:00'),
('elder_005','dev_012','spo2',90.0,'%',1,'2026-08-02 18:52:00','2026-08-02 18:52:00'),
('elder_005','dev_012','blood_pressure_sys',160.0,'mmHg',1,'2026-08-02 18:52:00','2026-08-02 18:52:00'),
('elder_005','dev_012','blood_pressure_dia',92.0,'mmHg',1,'2026-08-02 18:52:00','2026-08-02 18:52:00'),
('elder_005','dev_012','temperature',37.3,'C',1,'2026-08-02 18:52:00','2026-08-02 18:52:00'),
('elder_005','dev_012','heart_rate',92.0,'bpm',1,'2026-08-02 19:45:00','2026-08-02 19:45:00'),
('elder_005','dev_012','spo2',90.0,'%',1,'2026-08-02 19:45:00','2026-08-02 19:45:00'),
('elder_005','dev_012','blood_pressure_sys',158.0,'mmHg',1,'2026-08-02 19:45:00','2026-08-02 19:45:00'),
('elder_005','dev_012','blood_pressure_dia',90.0,'mmHg',1,'2026-08-02 19:45:00','2026-08-02 19:45:00'),
('elder_005','dev_012','temperature',37.8,'C',1,'2026-08-02 19:45:00','2026-08-02 19:45:00'),
('elder_005','dev_012','heart_rate',96.0,'bpm',1,'2026-08-02 20:23:00','2026-08-02 20:23:00'),
('elder_005','dev_012','spo2',91.0,'%',1,'2026-08-02 20:23:00','2026-08-02 20:23:00'),
('elder_005','dev_012','blood_pressure_sys',142.0,'mmHg',1,'2026-08-02 20:23:00','2026-08-02 20:23:00'),
('elder_005','dev_012','blood_pressure_dia',90.0,'mmHg',1,'2026-08-02 20:23:00','2026-08-02 20:23:00'),
('elder_005','dev_012','temperature',37.1,'C',1,'2026-08-02 20:23:00','2026-08-02 20:23:00'),
('elder_005','dev_012','heart_rate',105.0,'bpm',1,'2026-08-02 21:16:00','2026-08-02 21:16:00'),
('elder_005','dev_012','spo2',91.0,'%',1,'2026-08-02 21:16:00','2026-08-02 21:16:00'),
('elder_005','dev_012','blood_pressure_sys',157.0,'mmHg',1,'2026-08-02 21:16:00','2026-08-02 21:16:00'),
('elder_005','dev_012','blood_pressure_dia',103.0,'mmHg',1,'2026-08-02 21:16:00','2026-08-02 21:16:00'),
('elder_005','dev_012','temperature',37.4,'C',1,'2026-08-02 21:16:00','2026-08-02 21:16:00'),
('elder_005','dev_012','heart_rate',103.0,'bpm',1,'2026-08-02 22:48:00','2026-08-02 22:48:00'),
('elder_005','dev_012','spo2',90.0,'%',1,'2026-08-02 22:48:00','2026-08-02 22:48:00'),
('elder_005','dev_012','blood_pressure_sys',162.0,'mmHg',1,'2026-08-02 22:48:00','2026-08-02 22:48:00'),
('elder_005','dev_012','blood_pressure_dia',105.0,'mmHg',1,'2026-08-02 22:48:00','2026-08-02 22:48:00'),
('elder_005','dev_012','temperature',36.9,'C',1,'2026-08-02 22:48:00','2026-08-02 22:48:00'),
('elder_005','dev_012','heart_rate',111.0,'bpm',1,'2026-08-02 23:20:00','2026-08-02 23:20:00'),
('elder_005','dev_012','spo2',87.0,'%',1,'2026-08-02 23:20:00','2026-08-02 23:20:00'),
('elder_005','dev_012','blood_pressure_sys',164.0,'mmHg',1,'2026-08-02 23:20:00','2026-08-02 23:20:00'),
('elder_005','dev_012','blood_pressure_dia',99.0,'mmHg',1,'2026-08-02 23:20:00','2026-08-02 23:20:00'),
('elder_005','dev_012','temperature',37.1,'C',1,'2026-08-02 23:20:00','2026-08-02 23:20:00'),
('elder_001','dev_001','heart_rate',95.0,'bpm',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_001','dev_001','blood_pressure_sys',144.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_001','dev_001','blood_pressure_dia',90.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_001','dev_001','temperature',36.8,'C',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_001','dev_001','heart_rate',96.0,'bpm',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_001','dev_001','spo2',95.0,'%',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_001','dev_001','blood_pressure_sys',142.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_001','dev_001','temperature',37.2,'C',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_001','dev_001','heart_rate',97.0,'bpm',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_001','dev_001','spo2',96.0,'%',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_001','dev_001','blood_pressure_sys',135.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_001','dev_001','blood_pressure_dia',89.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_001','dev_001','temperature',36.9,'C',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_001','dev_001','heart_rate',96.0,'bpm',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_001','dev_001','spo2',96.0,'%',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_001','dev_001','blood_pressure_sys',135.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_001','dev_001','temperature',37.4,'C',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_001','dev_001','heart_rate',94.0,'bpm',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_001','dev_001','spo2',91.0,'%',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_001','dev_001','blood_pressure_sys',145.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_001','dev_001','blood_pressure_dia',92.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_001','dev_001','temperature',37.3,'C',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_001','dev_001','heart_rate',92.0,'bpm',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_001','dev_001','spo2',92.0,'%',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_001','dev_001','blood_pressure_sys',143.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_001','dev_001','blood_pressure_dia',92.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_001','dev_001','temperature',37.2,'C',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_001','dev_001','heart_rate',97.0,'bpm',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_001','dev_001','spo2',92.0,'%',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_001','dev_001','blood_pressure_sys',133.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_001','dev_001','blood_pressure_dia',90.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_001','dev_001','temperature',36.9,'C',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_002','dev_004','heart_rate',104.0,'bpm',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_002','dev_004','spo2',95.0,'%',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_002','dev_004','blood_pressure_sys',144.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_002','dev_004','blood_pressure_dia',89.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_002','dev_004','temperature',37.3,'C',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_002','dev_004','heart_rate',101.0,'bpm',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_002','dev_004','spo2',90.0,'%',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_002','dev_004','blood_pressure_sys',151.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_002','dev_004','blood_pressure_dia',90.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_002','dev_004','temperature',37.3,'C',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_002','dev_004','heart_rate',99.0,'bpm',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_002','dev_004','spo2',94.0,'%',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_002','dev_004','blood_pressure_sys',150.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_002','dev_004','blood_pressure_dia',90.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_002','dev_004','temperature',37.1,'C',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_002','dev_004','heart_rate',103.0,'bpm',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_002','dev_004','blood_pressure_sys',152.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_002','dev_004','blood_pressure_dia',94.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_002','dev_004','temperature',37.2,'C',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_002','dev_004','heart_rate',92.0,'bpm',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_002','dev_004','spo2',90.0,'%',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_002','dev_004','blood_pressure_sys',145.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_002','dev_004','blood_pressure_dia',92.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_002','dev_004','temperature',37.3,'C',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_002','dev_004','heart_rate',103.0,'bpm',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_002','dev_004','spo2',94.0,'%',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_002','dev_004','blood_pressure_sys',147.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_002','dev_004','blood_pressure_dia',93.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_002','dev_004','temperature',37.2,'C',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_002','dev_004','heart_rate',96.0,'bpm',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_002','dev_004','spo2',95.0,'%',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_002','dev_004','blood_pressure_sys',153.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_002','dev_004','blood_pressure_dia',94.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_002','dev_004','temperature',37.0,'C',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_003','dev_007','heart_rate',89.0,'bpm',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_003','dev_007','spo2',92.0,'%',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_003','dev_007','blood_pressure_sys',134.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_003','dev_007','blood_pressure_dia',81.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_003','dev_007','temperature',36.8,'C',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_003','dev_007','heart_rate',83.0,'bpm',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_003','dev_007','spo2',92.0,'%',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_003','dev_007','blood_pressure_sys',134.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_003','dev_007','blood_pressure_dia',84.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_003','dev_007','temperature',36.7,'C',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_003','dev_007','heart_rate',94.0,'bpm',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_003','dev_007','blood_pressure_sys',139.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_003','dev_007','blood_pressure_dia',84.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_003','dev_007','temperature',36.8,'C',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_003','dev_007','heart_rate',96.0,'bpm',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_003','dev_007','spo2',94.0,'%',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_003','dev_007','blood_pressure_sys',130.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_003','dev_007','blood_pressure_dia',86.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_003','dev_007','temperature',36.8,'C',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_003','dev_007','heart_rate',92.0,'bpm',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_003','dev_007','blood_pressure_sys',134.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_003','dev_007','blood_pressure_dia',85.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_003','dev_007','temperature',36.9,'C',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_003','dev_007','heart_rate',87.0,'bpm',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_003','dev_007','blood_pressure_sys',133.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_003','dev_007','blood_pressure_dia',85.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_003','dev_007','temperature',36.9,'C',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_003','dev_007','heart_rate',83.0,'bpm',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_003','dev_007','spo2',96.0,'%',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_003','dev_007','blood_pressure_sys',134.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_003','dev_007','blood_pressure_dia',83.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_003','dev_007','temperature',36.6,'C',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_004','dev_010','heart_rate',96.0,'bpm',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_004','dev_010','spo2',91.0,'%',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_004','dev_010','blood_pressure_sys',145.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_004','dev_010','blood_pressure_dia',90.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_004','dev_010','temperature',37.5,'C',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_004','dev_010','heart_rate',99.0,'bpm',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_004','dev_010','blood_pressure_sys',145.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_004','dev_010','blood_pressure_dia',90.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_004','dev_010','temperature',37.6,'C',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_004','dev_010','heart_rate',100.0,'bpm',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_004','dev_010','blood_pressure_sys',139.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_004','dev_010','blood_pressure_dia',94.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_004','dev_010','temperature',36.9,'C',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_004','dev_010','heart_rate',94.0,'bpm',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_004','dev_010','blood_pressure_sys',147.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_004','dev_010','blood_pressure_dia',90.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_004','dev_010','temperature',37.2,'C',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_004','dev_010','heart_rate',91.0,'bpm',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_004','dev_010','spo2',94.0,'%',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_004','dev_010','blood_pressure_sys',139.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_004','dev_010','blood_pressure_dia',93.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_004','dev_010','temperature',37.2,'C',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_004','dev_010','heart_rate',102.0,'bpm',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_004','dev_010','blood_pressure_sys',138.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_004','dev_010','blood_pressure_dia',93.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_004','dev_010','temperature',37.4,'C',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_004','dev_010','heart_rate',93.0,'bpm',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_004','dev_010','spo2',90.0,'%',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_004','dev_010','blood_pressure_sys',149.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_004','dev_010','blood_pressure_dia',92.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_004','dev_010','temperature',37.5,'C',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_005','dev_012','heart_rate',104.0,'bpm',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_005','dev_012','spo2',90.0,'%',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_005','dev_012','blood_pressure_sys',145.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_005','dev_012','blood_pressure_dia',95.0,'mmHg',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_005','dev_012','temperature',37.1,'C',0,'2026-07-27 08:00:00','2026-07-27 08:00:00'),
('elder_005','dev_012','heart_rate',101.0,'bpm',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_005','dev_012','spo2',91.0,'%',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_005','dev_012','blood_pressure_sys',145.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_005','dev_012','blood_pressure_dia',95.0,'mmHg',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_005','dev_012','temperature',37.4,'C',0,'2026-07-28 08:00:00','2026-07-28 08:00:00'),
('elder_005','dev_012','heart_rate',100.0,'bpm',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_005','dev_012','spo2',91.0,'%',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_005','dev_012','blood_pressure_sys',147.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_005','dev_012','blood_pressure_dia',95.0,'mmHg',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_005','dev_012','temperature',37.1,'C',0,'2026-07-29 08:00:00','2026-07-29 08:00:00'),
('elder_005','dev_012','heart_rate',97.0,'bpm',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_005','dev_012','spo2',92.0,'%',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_005','dev_012','blood_pressure_sys',156.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_005','dev_012','blood_pressure_dia',99.0,'mmHg',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_005','dev_012','temperature',37.4,'C',0,'2026-07-30 08:00:00','2026-07-30 08:00:00'),
('elder_005','dev_012','heart_rate',106.0,'bpm',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_005','dev_012','spo2',88.0,'%',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_005','dev_012','blood_pressure_sys',153.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_005','dev_012','blood_pressure_dia',95.0,'mmHg',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_005','dev_012','temperature',37.7,'C',0,'2026-07-31 08:00:00','2026-07-31 08:00:00'),
('elder_005','dev_012','heart_rate',105.0,'bpm',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_005','dev_012','spo2',90.0,'%',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_005','dev_012','blood_pressure_sys',146.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_005','dev_012','blood_pressure_dia',96.0,'mmHg',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_005','dev_012','temperature',37.3,'C',0,'2026-08-01 08:00:00','2026-08-01 08:00:00'),
('elder_005','dev_012','heart_rate',103.0,'bpm',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_005','dev_012','spo2',88.0,'%',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_005','dev_012','blood_pressure_sys',149.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_005','dev_012','blood_pressure_dia',93.0,'mmHg',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_005','dev_012','temperature',37.3,'C',0,'2026-08-02 08:00:00','2026-08-02 08:00:00'),
('elder_001','dev_001','heart_rate',97.0,'bpm',0,'2026-07-04 09:09:00','2026-07-04 09:09:00'),
('elder_001','dev_001','spo2',92.0,'%',0,'2026-07-04 09:09:00','2026-07-04 09:09:00'),
('elder_001','dev_001','blood_pressure_sys',146.0,'mmHg',0,'2026-07-04 09:09:00','2026-07-04 09:09:00'),
('elder_001','dev_001','blood_pressure_dia',92.0,'mmHg',0,'2026-07-04 09:09:00','2026-07-04 09:09:00'),
('elder_001','dev_001','temperature',37.1,'C',0,'2026-07-04 09:09:00','2026-07-04 09:09:00'),
('elder_001','dev_001','heart_rate',95.0,'bpm',0,'2026-07-05 09:22:00','2026-07-05 09:22:00'),
('elder_001','dev_001','spo2',90.0,'%',0,'2026-07-05 09:22:00','2026-07-05 09:22:00'),
('elder_001','dev_001','blood_pressure_sys',140.0,'mmHg',0,'2026-07-05 09:22:00','2026-07-05 09:22:00'),
('elder_001','dev_001','blood_pressure_dia',86.0,'mmHg',0,'2026-07-05 09:22:00','2026-07-05 09:22:00'),
('elder_001','dev_001','temperature',37.3,'C',0,'2026-07-05 09:22:00','2026-07-05 09:22:00'),
('elder_001','dev_001','heart_rate',90.0,'bpm',0,'2026-07-06 09:18:00','2026-07-06 09:18:00'),
('elder_001','dev_001','spo2',95.0,'%',0,'2026-07-06 09:18:00','2026-07-06 09:18:00'),
('elder_001','dev_001','blood_pressure_sys',140.0,'mmHg',0,'2026-07-06 09:18:00','2026-07-06 09:18:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-07-06 09:18:00','2026-07-06 09:18:00'),
('elder_001','dev_001','temperature',37.3,'C',0,'2026-07-06 09:18:00','2026-07-06 09:18:00'),
('elder_001','dev_001','heart_rate',92.0,'bpm',0,'2026-07-07 09:09:00','2026-07-07 09:09:00'),
('elder_001','dev_001','spo2',91.0,'%',0,'2026-07-07 09:09:00','2026-07-07 09:09:00'),
('elder_001','dev_001','blood_pressure_sys',140.0,'mmHg',0,'2026-07-07 09:09:00','2026-07-07 09:09:00'),
('elder_001','dev_001','blood_pressure_dia',84.0,'mmHg',0,'2026-07-07 09:09:00','2026-07-07 09:09:00'),
('elder_001','dev_001','temperature',36.7,'C',0,'2026-07-07 09:09:00','2026-07-07 09:09:00'),
('elder_001','dev_001','heart_rate',102.0,'bpm',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_001','dev_001','spo2',95.0,'%',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_001','dev_001','blood_pressure_sys',147.0,'mmHg',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_001','dev_001','blood_pressure_dia',95.0,'mmHg',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_001','dev_001','temperature',37.2,'C',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_001','dev_001','heart_rate',98.0,'bpm',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_001','dev_001','spo2',97.0,'%',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_001','dev_001','blood_pressure_sys',135.0,'mmHg',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_001','dev_001','blood_pressure_dia',94.0,'mmHg',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_001','dev_001','temperature',37.4,'C',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_001','dev_001','heart_rate',102.0,'bpm',0,'2026-07-10 09:13:00','2026-07-10 09:13:00'),
('elder_001','dev_001','spo2',93.0,'%',0,'2026-07-10 09:13:00','2026-07-10 09:13:00'),
('elder_001','dev_001','blood_pressure_sys',140.0,'mmHg',0,'2026-07-10 09:13:00','2026-07-10 09:13:00'),
('elder_001','dev_001','blood_pressure_dia',89.0,'mmHg',0,'2026-07-10 09:13:00','2026-07-10 09:13:00'),
('elder_001','dev_001','temperature',36.6,'C',0,'2026-07-10 09:13:00','2026-07-10 09:13:00'),
('elder_001','dev_001','heart_rate',92.0,'bpm',0,'2026-07-11 09:15:00','2026-07-11 09:15:00'),
('elder_001','dev_001','spo2',96.0,'%',0,'2026-07-11 09:15:00','2026-07-11 09:15:00'),
('elder_001','dev_001','blood_pressure_sys',138.0,'mmHg',0,'2026-07-11 09:15:00','2026-07-11 09:15:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-07-11 09:15:00','2026-07-11 09:15:00'),
('elder_001','dev_001','temperature',36.8,'C',0,'2026-07-11 09:15:00','2026-07-11 09:15:00'),
('elder_001','dev_001','heart_rate',99.0,'bpm',0,'2026-07-12 09:16:00','2026-07-12 09:16:00'),
('elder_001','dev_001','spo2',96.0,'%',0,'2026-07-12 09:16:00','2026-07-12 09:16:00'),
('elder_001','dev_001','blood_pressure_sys',139.0,'mmHg',0,'2026-07-12 09:16:00','2026-07-12 09:16:00'),
('elder_001','dev_001','blood_pressure_dia',86.0,'mmHg',0,'2026-07-12 09:16:00','2026-07-12 09:16:00'),
('elder_001','dev_001','temperature',37.5,'C',0,'2026-07-12 09:16:00','2026-07-12 09:16:00'),
('elder_001','dev_001','heart_rate',98.0,'bpm',0,'2026-07-13 09:02:00','2026-07-13 09:02:00'),
('elder_001','dev_001','spo2',92.0,'%',0,'2026-07-13 09:02:00','2026-07-13 09:02:00'),
('elder_001','dev_001','blood_pressure_sys',131.0,'mmHg',0,'2026-07-13 09:02:00','2026-07-13 09:02:00'),
('elder_001','dev_001','blood_pressure_dia',87.0,'mmHg',0,'2026-07-13 09:02:00','2026-07-13 09:02:00'),
('elder_001','dev_001','temperature',37.5,'C',0,'2026-07-13 09:02:00','2026-07-13 09:02:00'),
('elder_001','dev_001','heart_rate',93.0,'bpm',0,'2026-07-14 09:06:00','2026-07-14 09:06:00'),
('elder_001','dev_001','spo2',90.0,'%',0,'2026-07-14 09:06:00','2026-07-14 09:06:00'),
('elder_001','dev_001','blood_pressure_sys',143.0,'mmHg',0,'2026-07-14 09:06:00','2026-07-14 09:06:00'),
('elder_001','dev_001','blood_pressure_dia',95.0,'mmHg',0,'2026-07-14 09:06:00','2026-07-14 09:06:00'),
('elder_001','dev_001','temperature',36.6,'C',0,'2026-07-14 09:06:00','2026-07-14 09:06:00'),
('elder_001','dev_001','heart_rate',95.0,'bpm',0,'2026-07-15 09:15:00','2026-07-15 09:15:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-07-15 09:15:00','2026-07-15 09:15:00'),
('elder_001','dev_001','blood_pressure_sys',141.0,'mmHg',0,'2026-07-15 09:15:00','2026-07-15 09:15:00'),
('elder_001','dev_001','blood_pressure_dia',92.0,'mmHg',0,'2026-07-15 09:15:00','2026-07-15 09:15:00'),
('elder_001','dev_001','temperature',36.9,'C',0,'2026-07-15 09:15:00','2026-07-15 09:15:00'),
('elder_001','dev_001','heart_rate',98.0,'bpm',0,'2026-07-16 09:12:00','2026-07-16 09:12:00'),
('elder_001','dev_001','spo2',90.0,'%',0,'2026-07-16 09:12:00','2026-07-16 09:12:00'),
('elder_001','dev_001','blood_pressure_sys',147.0,'mmHg',0,'2026-07-16 09:12:00','2026-07-16 09:12:00'),
('elder_001','dev_001','blood_pressure_dia',95.0,'mmHg',0,'2026-07-16 09:12:00','2026-07-16 09:12:00'),
('elder_001','dev_001','temperature',37.4,'C',0,'2026-07-16 09:12:00','2026-07-16 09:12:00'),
('elder_001','dev_001','heart_rate',100.0,'bpm',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_001','dev_001','spo2',91.0,'%',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_001','dev_001','blood_pressure_sys',133.0,'mmHg',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_001','dev_001','blood_pressure_dia',88.0,'mmHg',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_001','dev_001','temperature',36.9,'C',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_001','dev_001','heart_rate',95.0,'bpm',0,'2026-07-18 09:29:00','2026-07-18 09:29:00'),
('elder_001','dev_001','spo2',90.0,'%',0,'2026-07-18 09:29:00','2026-07-18 09:29:00'),
('elder_001','dev_001','blood_pressure_sys',141.0,'mmHg',0,'2026-07-18 09:29:00','2026-07-18 09:29:00'),
('elder_001','dev_001','blood_pressure_dia',85.0,'mmHg',0,'2026-07-18 09:29:00','2026-07-18 09:29:00'),
('elder_001','dev_001','temperature',36.7,'C',0,'2026-07-18 09:29:00','2026-07-18 09:29:00'),
('elder_001','dev_001','heart_rate',99.0,'bpm',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_001','dev_001','spo2',93.0,'%',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_001','dev_001','blood_pressure_sys',132.0,'mmHg',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_001','dev_001','blood_pressure_dia',91.0,'mmHg',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_001','dev_001','temperature',36.6,'C',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_001','dev_001','heart_rate',98.0,'bpm',0,'2026-07-20 09:02:00','2026-07-20 09:02:00'),
('elder_001','dev_001','spo2',97.0,'%',0,'2026-07-20 09:02:00','2026-07-20 09:02:00'),
('elder_001','dev_001','blood_pressure_sys',136.0,'mmHg',0,'2026-07-20 09:02:00','2026-07-20 09:02:00'),
('elder_001','dev_001','blood_pressure_dia',94.0,'mmHg',0,'2026-07-20 09:02:00','2026-07-20 09:02:00'),
('elder_001','dev_001','temperature',37.2,'C',0,'2026-07-20 09:02:00','2026-07-20 09:02:00'),
('elder_001','dev_001','heart_rate',88.0,'bpm',0,'2026-07-21 09:07:00','2026-07-21 09:07:00'),
('elder_001','dev_001','spo2',90.0,'%',0,'2026-07-21 09:07:00','2026-07-21 09:07:00'),
('elder_001','dev_001','blood_pressure_sys',140.0,'mmHg',0,'2026-07-21 09:07:00','2026-07-21 09:07:00'),
('elder_001','dev_001','blood_pressure_dia',88.0,'mmHg',0,'2026-07-21 09:07:00','2026-07-21 09:07:00'),
('elder_001','dev_001','temperature',37.3,'C',0,'2026-07-21 09:07:00','2026-07-21 09:07:00'),
('elder_001','dev_001','heart_rate',100.0,'bpm',0,'2026-07-22 09:17:00','2026-07-22 09:17:00'),
('elder_001','dev_001','spo2',90.0,'%',0,'2026-07-22 09:17:00','2026-07-22 09:17:00'),
('elder_001','dev_001','blood_pressure_sys',146.0,'mmHg',0,'2026-07-22 09:17:00','2026-07-22 09:17:00'),
('elder_001','dev_001','blood_pressure_dia',89.0,'mmHg',0,'2026-07-22 09:17:00','2026-07-22 09:17:00'),
('elder_001','dev_001','temperature',36.7,'C',0,'2026-07-22 09:17:00','2026-07-22 09:17:00'),
('elder_001','dev_001','heart_rate',90.0,'bpm',0,'2026-07-23 09:27:00','2026-07-23 09:27:00'),
('elder_001','dev_001','spo2',93.0,'%',0,'2026-07-23 09:27:00','2026-07-23 09:27:00'),
('elder_001','dev_001','blood_pressure_sys',138.0,'mmHg',0,'2026-07-23 09:27:00','2026-07-23 09:27:00'),
('elder_001','dev_001','blood_pressure_dia',95.0,'mmHg',0,'2026-07-23 09:27:00','2026-07-23 09:27:00'),
('elder_001','dev_001','temperature',37.5,'C',0,'2026-07-23 09:27:00','2026-07-23 09:27:00'),
('elder_001','dev_001','heart_rate',97.0,'bpm',0,'2026-07-24 09:09:00','2026-07-24 09:09:00'),
('elder_001','dev_001','spo2',92.0,'%',0,'2026-07-24 09:09:00','2026-07-24 09:09:00'),
('elder_001','dev_001','blood_pressure_sys',143.0,'mmHg',0,'2026-07-24 09:09:00','2026-07-24 09:09:00'),
('elder_001','dev_001','blood_pressure_dia',90.0,'mmHg',0,'2026-07-24 09:09:00','2026-07-24 09:09:00'),
('elder_001','dev_001','temperature',36.7,'C',0,'2026-07-24 09:09:00','2026-07-24 09:09:00'),
('elder_001','dev_001','heart_rate',101.0,'bpm',0,'2026-07-25 09:28:00','2026-07-25 09:28:00'),
('elder_001','dev_001','spo2',92.0,'%',0,'2026-07-25 09:28:00','2026-07-25 09:28:00'),
('elder_001','dev_001','blood_pressure_sys',136.0,'mmHg',0,'2026-07-25 09:28:00','2026-07-25 09:28:00'),
('elder_001','dev_001','blood_pressure_dia',85.0,'mmHg',0,'2026-07-25 09:28:00','2026-07-25 09:28:00'),
('elder_001','dev_001','temperature',36.9,'C',0,'2026-07-25 09:28:00','2026-07-25 09:28:00'),
('elder_001','dev_001','heart_rate',85.0,'bpm',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_001','dev_001','spo2',91.0,'%',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_001','dev_001','blood_pressure_sys',146.0,'mmHg',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_001','dev_001','blood_pressure_dia',85.0,'mmHg',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_001','dev_001','temperature',37.3,'C',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_001','dev_001','heart_rate',101.0,'bpm',0,'2026-07-27 09:09:00','2026-07-27 09:09:00'),
('elder_001','dev_001','spo2',96.0,'%',0,'2026-07-27 09:09:00','2026-07-27 09:09:00'),
('elder_001','dev_001','blood_pressure_sys',142.0,'mmHg',0,'2026-07-27 09:09:00','2026-07-27 09:09:00'),
('elder_001','dev_001','blood_pressure_dia',85.0,'mmHg',0,'2026-07-27 09:09:00','2026-07-27 09:09:00'),
('elder_001','dev_001','temperature',37.4,'C',0,'2026-07-27 09:09:00','2026-07-27 09:09:00'),
('elder_001','dev_001','heart_rate',90.0,'bpm',0,'2026-07-28 09:29:00','2026-07-28 09:29:00'),
('elder_001','dev_001','spo2',96.0,'%',0,'2026-07-28 09:29:00','2026-07-28 09:29:00'),
('elder_001','dev_001','blood_pressure_sys',143.0,'mmHg',0,'2026-07-28 09:29:00','2026-07-28 09:29:00'),
('elder_001','dev_001','blood_pressure_dia',85.0,'mmHg',0,'2026-07-28 09:29:00','2026-07-28 09:29:00'),
('elder_001','dev_001','temperature',37.5,'C',0,'2026-07-28 09:29:00','2026-07-28 09:29:00'),
('elder_001','dev_001','heart_rate',97.0,'bpm',0,'2026-07-29 09:28:00','2026-07-29 09:28:00'),
('elder_001','dev_001','spo2',96.0,'%',0,'2026-07-29 09:28:00','2026-07-29 09:28:00'),
('elder_001','dev_001','blood_pressure_sys',141.0,'mmHg',0,'2026-07-29 09:28:00','2026-07-29 09:28:00'),
('elder_001','dev_001','blood_pressure_dia',85.0,'mmHg',0,'2026-07-29 09:28:00','2026-07-29 09:28:00'),
('elder_001','dev_001','temperature',37.1,'C',0,'2026-07-29 09:28:00','2026-07-29 09:28:00'),
('elder_001','dev_001','heart_rate',100.0,'bpm',0,'2026-07-30 09:11:00','2026-07-30 09:11:00'),
('elder_001','dev_001','spo2',96.0,'%',0,'2026-07-30 09:11:00','2026-07-30 09:11:00'),
('elder_001','dev_001','blood_pressure_sys',135.0,'mmHg',0,'2026-07-30 09:11:00','2026-07-30 09:11:00'),
('elder_001','dev_001','blood_pressure_dia',94.0,'mmHg',0,'2026-07-30 09:11:00','2026-07-30 09:11:00'),
('elder_001','dev_001','temperature',36.9,'C',0,'2026-07-30 09:11:00','2026-07-30 09:11:00'),
('elder_001','dev_001','heart_rate',94.0,'bpm',0,'2026-07-31 09:27:00','2026-07-31 09:27:00'),
('elder_001','dev_001','spo2',95.0,'%',0,'2026-07-31 09:27:00','2026-07-31 09:27:00'),
('elder_001','dev_001','blood_pressure_sys',142.0,'mmHg',0,'2026-07-31 09:27:00','2026-07-31 09:27:00'),
('elder_001','dev_001','blood_pressure_dia',93.0,'mmHg',0,'2026-07-31 09:27:00','2026-07-31 09:27:00'),
('elder_001','dev_001','temperature',37.1,'C',0,'2026-07-31 09:27:00','2026-07-31 09:27:00'),
('elder_001','dev_001','heart_rate',89.0,'bpm',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_001','dev_001','spo2',94.0,'%',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_001','dev_001','blood_pressure_sys',131.0,'mmHg',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_001','dev_001','blood_pressure_dia',94.0,'mmHg',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_001','dev_001','temperature',36.8,'C',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_001','dev_001','heart_rate',95.0,'bpm',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_001','dev_001','spo2',92.0,'%',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_001','dev_001','blood_pressure_sys',136.0,'mmHg',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_001','dev_001','blood_pressure_dia',94.0,'mmHg',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_001','dev_001','temperature',37.4,'C',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_002','dev_004','heart_rate',96.0,'bpm',0,'2026-07-04 09:20:00','2026-07-04 09:20:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-07-04 09:20:00','2026-07-04 09:20:00'),
('elder_002','dev_004','blood_pressure_sys',158.0,'mmHg',0,'2026-07-04 09:20:00','2026-07-04 09:20:00'),
('elder_002','dev_004','blood_pressure_dia',92.0,'mmHg',0,'2026-07-04 09:20:00','2026-07-04 09:20:00'),
('elder_002','dev_004','temperature',37.6,'C',0,'2026-07-04 09:20:00','2026-07-04 09:20:00'),
('elder_002','dev_004','heart_rate',105.0,'bpm',0,'2026-07-05 09:01:00','2026-07-05 09:01:00'),
('elder_002','dev_004','spo2',95.0,'%',0,'2026-07-05 09:01:00','2026-07-05 09:01:00'),
('elder_002','dev_004','blood_pressure_sys',158.0,'mmHg',0,'2026-07-05 09:01:00','2026-07-05 09:01:00'),
('elder_002','dev_004','blood_pressure_dia',91.0,'mmHg',0,'2026-07-05 09:01:00','2026-07-05 09:01:00'),
('elder_002','dev_004','temperature',36.7,'C',0,'2026-07-05 09:01:00','2026-07-05 09:01:00'),
('elder_002','dev_004','heart_rate',90.0,'bpm',0,'2026-07-06 09:07:00','2026-07-06 09:07:00'),
('elder_002','dev_004','spo2',95.0,'%',0,'2026-07-06 09:07:00','2026-07-06 09:07:00'),
('elder_002','dev_004','blood_pressure_sys',148.0,'mmHg',0,'2026-07-06 09:07:00','2026-07-06 09:07:00'),
('elder_002','dev_004','blood_pressure_dia',89.0,'mmHg',0,'2026-07-06 09:07:00','2026-07-06 09:07:00'),
('elder_002','dev_004','temperature',37.0,'C',0,'2026-07-06 09:07:00','2026-07-06 09:07:00'),
('elder_002','dev_004','heart_rate',93.0,'bpm',0,'2026-07-07 09:13:00','2026-07-07 09:13:00'),
('elder_002','dev_004','spo2',96.0,'%',0,'2026-07-07 09:13:00','2026-07-07 09:13:00'),
('elder_002','dev_004','blood_pressure_sys',148.0,'mmHg',0,'2026-07-07 09:13:00','2026-07-07 09:13:00'),
('elder_002','dev_004','blood_pressure_dia',94.0,'mmHg',0,'2026-07-07 09:13:00','2026-07-07 09:13:00'),
('elder_002','dev_004','temperature',37.4,'C',0,'2026-07-07 09:13:00','2026-07-07 09:13:00'),
('elder_002','dev_004','heart_rate',104.0,'bpm',0,'2026-07-08 09:00:00','2026-07-08 09:00:00'),
('elder_002','dev_004','spo2',94.0,'%',0,'2026-07-08 09:00:00','2026-07-08 09:00:00'),
('elder_002','dev_004','blood_pressure_sys',152.0,'mmHg',0,'2026-07-08 09:00:00','2026-07-08 09:00:00'),
('elder_002','dev_004','blood_pressure_dia',93.0,'mmHg',0,'2026-07-08 09:00:00','2026-07-08 09:00:00'),
('elder_002','dev_004','temperature',36.9,'C',0,'2026-07-08 09:00:00','2026-07-08 09:00:00'),
('elder_002','dev_004','heart_rate',96.0,'bpm',0,'2026-07-09 09:18:00','2026-07-09 09:18:00'),
('elder_002','dev_004','spo2',89.0,'%',0,'2026-07-09 09:18:00','2026-07-09 09:18:00'),
('elder_002','dev_004','blood_pressure_sys',141.0,'mmHg',0,'2026-07-09 09:18:00','2026-07-09 09:18:00'),
('elder_002','dev_004','blood_pressure_dia',94.0,'mmHg',0,'2026-07-09 09:18:00','2026-07-09 09:18:00'),
('elder_002','dev_004','temperature',36.6,'C',0,'2026-07-09 09:18:00','2026-07-09 09:18:00'),
('elder_002','dev_004','heart_rate',88.0,'bpm',0,'2026-07-10 09:17:00','2026-07-10 09:17:00'),
('elder_002','dev_004','spo2',94.0,'%',0,'2026-07-10 09:17:00','2026-07-10 09:17:00'),
('elder_002','dev_004','blood_pressure_sys',152.0,'mmHg',0,'2026-07-10 09:17:00','2026-07-10 09:17:00'),
('elder_002','dev_004','blood_pressure_dia',96.0,'mmHg',0,'2026-07-10 09:17:00','2026-07-10 09:17:00'),
('elder_002','dev_004','temperature',36.8,'C',0,'2026-07-10 09:17:00','2026-07-10 09:17:00'),
('elder_002','dev_004','heart_rate',91.0,'bpm',0,'2026-07-11 09:11:00','2026-07-11 09:11:00'),
('elder_002','dev_004','spo2',89.0,'%',0,'2026-07-11 09:11:00','2026-07-11 09:11:00'),
('elder_002','dev_004','blood_pressure_sys',157.0,'mmHg',0,'2026-07-11 09:11:00','2026-07-11 09:11:00'),
('elder_002','dev_004','blood_pressure_dia',94.0,'mmHg',0,'2026-07-11 09:11:00','2026-07-11 09:11:00'),
('elder_002','dev_004','temperature',37.7,'C',0,'2026-07-11 09:11:00','2026-07-11 09:11:00'),
('elder_002','dev_004','heart_rate',92.0,'bpm',0,'2026-07-12 09:30:00','2026-07-12 09:30:00'),
('elder_002','dev_004','spo2',89.0,'%',0,'2026-07-12 09:30:00','2026-07-12 09:30:00'),
('elder_002','dev_004','blood_pressure_sys',151.0,'mmHg',0,'2026-07-12 09:30:00','2026-07-12 09:30:00'),
('elder_002','dev_004','blood_pressure_dia',99.0,'mmHg',0,'2026-07-12 09:30:00','2026-07-12 09:30:00'),
('elder_002','dev_004','temperature',37.6,'C',0,'2026-07-12 09:30:00','2026-07-12 09:30:00'),
('elder_002','dev_004','heart_rate',98.0,'bpm',0,'2026-07-13 09:19:00','2026-07-13 09:19:00'),
('elder_002','dev_004','spo2',95.0,'%',0,'2026-07-13 09:19:00','2026-07-13 09:19:00'),
('elder_002','dev_004','blood_pressure_sys',154.0,'mmHg',0,'2026-07-13 09:19:00','2026-07-13 09:19:00'),
('elder_002','dev_004','blood_pressure_dia',93.0,'mmHg',0,'2026-07-13 09:19:00','2026-07-13 09:19:00'),
('elder_002','dev_004','temperature',37.5,'C',0,'2026-07-13 09:19:00','2026-07-13 09:19:00'),
('elder_002','dev_004','heart_rate',89.0,'bpm',0,'2026-07-14 09:15:00','2026-07-14 09:15:00'),
('elder_002','dev_004','spo2',93.0,'%',0,'2026-07-14 09:15:00','2026-07-14 09:15:00'),
('elder_002','dev_004','blood_pressure_sys',157.0,'mmHg',0,'2026-07-14 09:15:00','2026-07-14 09:15:00'),
('elder_002','dev_004','blood_pressure_dia',99.0,'mmHg',0,'2026-07-14 09:15:00','2026-07-14 09:15:00'),
('elder_002','dev_004','temperature',36.8,'C',0,'2026-07-14 09:15:00','2026-07-14 09:15:00'),
('elder_002','dev_004','heart_rate',92.0,'bpm',0,'2026-07-15 09:17:00','2026-07-15 09:17:00'),
('elder_002','dev_004','spo2',93.0,'%',0,'2026-07-15 09:17:00','2026-07-15 09:17:00'),
('elder_002','dev_004','blood_pressure_sys',152.0,'mmHg',0,'2026-07-15 09:17:00','2026-07-15 09:17:00'),
('elder_002','dev_004','blood_pressure_dia',91.0,'mmHg',0,'2026-07-15 09:17:00','2026-07-15 09:17:00'),
('elder_002','dev_004','temperature',37.3,'C',0,'2026-07-15 09:17:00','2026-07-15 09:17:00'),
('elder_002','dev_004','heart_rate',96.0,'bpm',0,'2026-07-16 09:26:00','2026-07-16 09:26:00'),
('elder_002','dev_004','spo2',91.0,'%',0,'2026-07-16 09:26:00','2026-07-16 09:26:00'),
('elder_002','dev_004','blood_pressure_sys',155.0,'mmHg',0,'2026-07-16 09:26:00','2026-07-16 09:26:00'),
('elder_002','dev_004','blood_pressure_dia',98.0,'mmHg',0,'2026-07-16 09:26:00','2026-07-16 09:26:00'),
('elder_002','dev_004','temperature',36.6,'C',0,'2026-07-16 09:26:00','2026-07-16 09:26:00'),
('elder_002','dev_004','heart_rate',106.0,'bpm',0,'2026-07-17 09:01:00','2026-07-17 09:01:00'),
('elder_002','dev_004','spo2',89.0,'%',0,'2026-07-17 09:01:00','2026-07-17 09:01:00'),
('elder_002','dev_004','blood_pressure_sys',146.0,'mmHg',0,'2026-07-17 09:01:00','2026-07-17 09:01:00'),
('elder_002','dev_004','blood_pressure_dia',97.0,'mmHg',0,'2026-07-17 09:01:00','2026-07-17 09:01:00'),
('elder_002','dev_004','temperature',36.6,'C',0,'2026-07-17 09:01:00','2026-07-17 09:01:00'),
('elder_002','dev_004','heart_rate',99.0,'bpm',0,'2026-07-18 09:17:00','2026-07-18 09:17:00'),
('elder_002','dev_004','spo2',95.0,'%',0,'2026-07-18 09:17:00','2026-07-18 09:17:00'),
('elder_002','dev_004','blood_pressure_sys',145.0,'mmHg',0,'2026-07-18 09:17:00','2026-07-18 09:17:00'),
('elder_002','dev_004','blood_pressure_dia',99.0,'mmHg',0,'2026-07-18 09:17:00','2026-07-18 09:17:00'),
('elder_002','dev_004','temperature',36.9,'C',0,'2026-07-18 09:17:00','2026-07-18 09:17:00'),
('elder_002','dev_004','heart_rate',94.0,'bpm',0,'2026-07-19 09:23:00','2026-07-19 09:23:00'),
('elder_002','dev_004','spo2',93.0,'%',0,'2026-07-19 09:23:00','2026-07-19 09:23:00'),
('elder_002','dev_004','blood_pressure_sys',151.0,'mmHg',0,'2026-07-19 09:23:00','2026-07-19 09:23:00'),
('elder_002','dev_004','blood_pressure_dia',89.0,'mmHg',0,'2026-07-19 09:23:00','2026-07-19 09:23:00'),
('elder_002','dev_004','temperature',37.3,'C',0,'2026-07-19 09:23:00','2026-07-19 09:23:00'),
('elder_002','dev_004','heart_rate',91.0,'bpm',0,'2026-07-20 09:10:00','2026-07-20 09:10:00'),
('elder_002','dev_004','spo2',94.0,'%',0,'2026-07-20 09:10:00','2026-07-20 09:10:00'),
('elder_002','dev_004','blood_pressure_sys',158.0,'mmHg',0,'2026-07-20 09:10:00','2026-07-20 09:10:00'),
('elder_002','dev_004','blood_pressure_dia',96.0,'mmHg',0,'2026-07-20 09:10:00','2026-07-20 09:10:00'),
('elder_002','dev_004','temperature',37.3,'C',0,'2026-07-20 09:10:00','2026-07-20 09:10:00'),
('elder_002','dev_004','heart_rate',97.0,'bpm',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_002','dev_004','spo2',90.0,'%',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_002','dev_004','blood_pressure_sys',140.0,'mmHg',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_002','dev_004','blood_pressure_dia',88.0,'mmHg',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_002','dev_004','temperature',37.2,'C',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_002','dev_004','heart_rate',104.0,'bpm',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_002','dev_004','spo2',95.0,'%',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_002','dev_004','blood_pressure_sys',154.0,'mmHg',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_002','dev_004','blood_pressure_dia',94.0,'mmHg',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_002','dev_004','temperature',37.0,'C',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_002','dev_004','heart_rate',94.0,'bpm',0,'2026-07-23 09:29:00','2026-07-23 09:29:00'),
('elder_002','dev_004','spo2',89.0,'%',0,'2026-07-23 09:29:00','2026-07-23 09:29:00'),
('elder_002','dev_004','blood_pressure_sys',144.0,'mmHg',0,'2026-07-23 09:29:00','2026-07-23 09:29:00'),
('elder_002','dev_004','blood_pressure_dia',96.0,'mmHg',0,'2026-07-23 09:29:00','2026-07-23 09:29:00'),
('elder_002','dev_004','temperature',37.2,'C',0,'2026-07-23 09:29:00','2026-07-23 09:29:00'),
('elder_002','dev_004','heart_rate',93.0,'bpm',0,'2026-07-24 09:20:00','2026-07-24 09:20:00'),
('elder_002','dev_004','spo2',93.0,'%',0,'2026-07-24 09:20:00','2026-07-24 09:20:00'),
('elder_002','dev_004','blood_pressure_sys',150.0,'mmHg',0,'2026-07-24 09:20:00','2026-07-24 09:20:00'),
('elder_002','dev_004','blood_pressure_dia',93.0,'mmHg',0,'2026-07-24 09:20:00','2026-07-24 09:20:00'),
('elder_002','dev_004','temperature',37.6,'C',0,'2026-07-24 09:20:00','2026-07-24 09:20:00'),
('elder_002','dev_004','heart_rate',92.0,'bpm',0,'2026-07-25 09:23:00','2026-07-25 09:23:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-07-25 09:23:00','2026-07-25 09:23:00'),
('elder_002','dev_004','blood_pressure_sys',144.0,'mmHg',0,'2026-07-25 09:23:00','2026-07-25 09:23:00'),
('elder_002','dev_004','blood_pressure_dia',99.0,'mmHg',0,'2026-07-25 09:23:00','2026-07-25 09:23:00'),
('elder_002','dev_004','temperature',37.4,'C',0,'2026-07-25 09:23:00','2026-07-25 09:23:00'),
('elder_002','dev_004','heart_rate',104.0,'bpm',0,'2026-07-26 09:19:00','2026-07-26 09:19:00'),
('elder_002','dev_004','spo2',95.0,'%',0,'2026-07-26 09:19:00','2026-07-26 09:19:00'),
('elder_002','dev_004','blood_pressure_sys',149.0,'mmHg',0,'2026-07-26 09:19:00','2026-07-26 09:19:00'),
('elder_002','dev_004','blood_pressure_dia',88.0,'mmHg',0,'2026-07-26 09:19:00','2026-07-26 09:19:00'),
('elder_002','dev_004','temperature',37.5,'C',0,'2026-07-26 09:19:00','2026-07-26 09:19:00'),
('elder_002','dev_004','heart_rate',91.0,'bpm',0,'2026-07-27 09:18:00','2026-07-27 09:18:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-07-27 09:18:00','2026-07-27 09:18:00'),
('elder_002','dev_004','blood_pressure_sys',152.0,'mmHg',0,'2026-07-27 09:18:00','2026-07-27 09:18:00'),
('elder_002','dev_004','blood_pressure_dia',89.0,'mmHg',0,'2026-07-27 09:18:00','2026-07-27 09:18:00'),
('elder_002','dev_004','temperature',36.9,'C',0,'2026-07-27 09:18:00','2026-07-27 09:18:00'),
('elder_002','dev_004','heart_rate',97.0,'bpm',0,'2026-07-28 09:13:00','2026-07-28 09:13:00'),
('elder_002','dev_004','spo2',89.0,'%',0,'2026-07-28 09:13:00','2026-07-28 09:13:00'),
('elder_002','dev_004','blood_pressure_sys',144.0,'mmHg',0,'2026-07-28 09:13:00','2026-07-28 09:13:00'),
('elder_002','dev_004','blood_pressure_dia',99.0,'mmHg',0,'2026-07-28 09:13:00','2026-07-28 09:13:00'),
('elder_002','dev_004','temperature',36.7,'C',0,'2026-07-28 09:13:00','2026-07-28 09:13:00'),
('elder_002','dev_004','heart_rate',104.0,'bpm',0,'2026-07-29 09:10:00','2026-07-29 09:10:00'),
('elder_002','dev_004','spo2',96.0,'%',0,'2026-07-29 09:10:00','2026-07-29 09:10:00'),
('elder_002','dev_004','blood_pressure_sys',149.0,'mmHg',0,'2026-07-29 09:10:00','2026-07-29 09:10:00'),
('elder_002','dev_004','blood_pressure_dia',90.0,'mmHg',0,'2026-07-29 09:10:00','2026-07-29 09:10:00'),
('elder_002','dev_004','temperature',36.8,'C',0,'2026-07-29 09:10:00','2026-07-29 09:10:00'),
('elder_002','dev_004','heart_rate',95.0,'bpm',0,'2026-07-30 09:09:00','2026-07-30 09:09:00'),
('elder_002','dev_004','spo2',92.0,'%',0,'2026-07-30 09:09:00','2026-07-30 09:09:00'),
('elder_002','dev_004','blood_pressure_sys',158.0,'mmHg',0,'2026-07-30 09:09:00','2026-07-30 09:09:00'),
('elder_002','dev_004','blood_pressure_dia',98.0,'mmHg',0,'2026-07-30 09:09:00','2026-07-30 09:09:00'),
('elder_002','dev_004','temperature',37.7,'C',0,'2026-07-30 09:09:00','2026-07-30 09:09:00'),
('elder_002','dev_004','heart_rate',88.0,'bpm',0,'2026-07-31 09:01:00','2026-07-31 09:01:00'),
('elder_002','dev_004','spo2',96.0,'%',0,'2026-07-31 09:01:00','2026-07-31 09:01:00'),
('elder_002','dev_004','blood_pressure_sys',157.0,'mmHg',0,'2026-07-31 09:01:00','2026-07-31 09:01:00'),
('elder_002','dev_004','blood_pressure_dia',91.0,'mmHg',0,'2026-07-31 09:01:00','2026-07-31 09:01:00'),
('elder_002','dev_004','temperature',37.3,'C',0,'2026-07-31 09:01:00','2026-07-31 09:01:00'),
('elder_002','dev_004','heart_rate',100.0,'bpm',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_002','dev_004','spo2',96.0,'%',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_002','dev_004','blood_pressure_sys',156.0,'mmHg',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_002','dev_004','blood_pressure_dia',96.0,'mmHg',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_002','dev_004','temperature',37.2,'C',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_002','dev_004','heart_rate',91.0,'bpm',0,'2026-08-02 09:24:00','2026-08-02 09:24:00'),
('elder_002','dev_004','spo2',96.0,'%',0,'2026-08-02 09:24:00','2026-08-02 09:24:00'),
('elder_002','dev_004','blood_pressure_sys',148.0,'mmHg',0,'2026-08-02 09:24:00','2026-08-02 09:24:00'),
('elder_002','dev_004','blood_pressure_dia',89.0,'mmHg',0,'2026-08-02 09:24:00','2026-08-02 09:24:00'),
('elder_002','dev_004','temperature',37.6,'C',0,'2026-08-02 09:24:00','2026-08-02 09:24:00'),
('elder_003','dev_007','heart_rate',93.0,'bpm',0,'2026-07-04 09:05:00','2026-07-04 09:05:00'),
('elder_003','dev_007','spo2',96.0,'%',0,'2026-07-04 09:05:00','2026-07-04 09:05:00'),
('elder_003','dev_007','blood_pressure_sys',139.0,'mmHg',0,'2026-07-04 09:05:00','2026-07-04 09:05:00'),
('elder_003','dev_007','blood_pressure_dia',80.0,'mmHg',0,'2026-07-04 09:05:00','2026-07-04 09:05:00'),
('elder_003','dev_007','temperature',37.1,'C',0,'2026-07-04 09:05:00','2026-07-04 09:05:00'),
('elder_003','dev_007','heart_rate',81.0,'bpm',0,'2026-07-05 09:17:00','2026-07-05 09:17:00'),
('elder_003','dev_007','spo2',97.0,'%',0,'2026-07-05 09:17:00','2026-07-05 09:17:00'),
('elder_003','dev_007','blood_pressure_sys',133.0,'mmHg',0,'2026-07-05 09:17:00','2026-07-05 09:17:00'),
('elder_003','dev_007','blood_pressure_dia',87.0,'mmHg',0,'2026-07-05 09:17:00','2026-07-05 09:17:00'),
('elder_003','dev_007','temperature',36.4,'C',0,'2026-07-05 09:17:00','2026-07-05 09:17:00'),
('elder_003','dev_007','heart_rate',91.0,'bpm',0,'2026-07-06 09:15:00','2026-07-06 09:15:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-07-06 09:15:00','2026-07-06 09:15:00'),
('elder_003','dev_007','blood_pressure_sys',139.0,'mmHg',0,'2026-07-06 09:15:00','2026-07-06 09:15:00'),
('elder_003','dev_007','blood_pressure_dia',87.0,'mmHg',0,'2026-07-06 09:15:00','2026-07-06 09:15:00'),
('elder_003','dev_007','temperature',36.5,'C',0,'2026-07-06 09:15:00','2026-07-06 09:15:00'),
('elder_003','dev_007','heart_rate',83.0,'bpm',0,'2026-07-07 09:10:00','2026-07-07 09:10:00'),
('elder_003','dev_007','spo2',91.0,'%',0,'2026-07-07 09:10:00','2026-07-07 09:10:00'),
('elder_003','dev_007','blood_pressure_sys',139.0,'mmHg',0,'2026-07-07 09:10:00','2026-07-07 09:10:00'),
('elder_003','dev_007','blood_pressure_dia',90.0,'mmHg',0,'2026-07-07 09:10:00','2026-07-07 09:10:00'),
('elder_003','dev_007','temperature',36.6,'C',0,'2026-07-07 09:10:00','2026-07-07 09:10:00'),
('elder_003','dev_007','heart_rate',88.0,'bpm',0,'2026-07-08 09:27:00','2026-07-08 09:27:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-07-08 09:27:00','2026-07-08 09:27:00'),
('elder_003','dev_007','blood_pressure_sys',124.0,'mmHg',0,'2026-07-08 09:27:00','2026-07-08 09:27:00'),
('elder_003','dev_007','blood_pressure_dia',89.0,'mmHg',0,'2026-07-08 09:27:00','2026-07-08 09:27:00'),
('elder_003','dev_007','temperature',36.5,'C',0,'2026-07-08 09:27:00','2026-07-08 09:27:00'),
('elder_003','dev_007','heart_rate',87.0,'bpm',0,'2026-07-09 09:08:00','2026-07-09 09:08:00'),
('elder_003','dev_007','spo2',91.0,'%',0,'2026-07-09 09:08:00','2026-07-09 09:08:00'),
('elder_003','dev_007','blood_pressure_sys',138.0,'mmHg',0,'2026-07-09 09:08:00','2026-07-09 09:08:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',0,'2026-07-09 09:08:00','2026-07-09 09:08:00'),
('elder_003','dev_007','temperature',37.0,'C',0,'2026-07-09 09:08:00','2026-07-09 09:08:00'),
('elder_003','dev_007','heart_rate',95.0,'bpm',0,'2026-07-10 09:28:00','2026-07-10 09:28:00'),
('elder_003','dev_007','spo2',97.0,'%',0,'2026-07-10 09:28:00','2026-07-10 09:28:00'),
('elder_003','dev_007','blood_pressure_sys',138.0,'mmHg',0,'2026-07-10 09:28:00','2026-07-10 09:28:00'),
('elder_003','dev_007','blood_pressure_dia',85.0,'mmHg',0,'2026-07-10 09:28:00','2026-07-10 09:28:00'),
('elder_003','dev_007','temperature',36.4,'C',0,'2026-07-10 09:28:00','2026-07-10 09:28:00'),
('elder_003','dev_007','heart_rate',91.0,'bpm',0,'2026-07-11 09:30:00','2026-07-11 09:30:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-07-11 09:30:00','2026-07-11 09:30:00'),
('elder_003','dev_007','blood_pressure_sys',130.0,'mmHg',0,'2026-07-11 09:30:00','2026-07-11 09:30:00'),
('elder_003','dev_007','blood_pressure_dia',79.0,'mmHg',0,'2026-07-11 09:30:00','2026-07-11 09:30:00'),
('elder_003','dev_007','temperature',37.2,'C',0,'2026-07-11 09:30:00','2026-07-11 09:30:00'),
('elder_003','dev_007','heart_rate',91.0,'bpm',0,'2026-07-12 09:29:00','2026-07-12 09:29:00'),
('elder_003','dev_007','spo2',97.0,'%',0,'2026-07-12 09:29:00','2026-07-12 09:29:00'),
('elder_003','dev_007','blood_pressure_sys',129.0,'mmHg',0,'2026-07-12 09:29:00','2026-07-12 09:29:00'),
('elder_003','dev_007','blood_pressure_dia',84.0,'mmHg',0,'2026-07-12 09:29:00','2026-07-12 09:29:00'),
('elder_003','dev_007','temperature',36.6,'C',0,'2026-07-12 09:29:00','2026-07-12 09:29:00'),
('elder_003','dev_007','heart_rate',89.0,'bpm',0,'2026-07-13 09:14:00','2026-07-13 09:14:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-07-13 09:14:00','2026-07-13 09:14:00'),
('elder_003','dev_007','blood_pressure_sys',139.0,'mmHg',0,'2026-07-13 09:14:00','2026-07-13 09:14:00'),
('elder_003','dev_007','blood_pressure_dia',81.0,'mmHg',0,'2026-07-13 09:14:00','2026-07-13 09:14:00'),
('elder_003','dev_007','temperature',36.6,'C',0,'2026-07-13 09:14:00','2026-07-13 09:14:00'),
('elder_003','dev_007','heart_rate',98.0,'bpm',0,'2026-07-14 09:00:00','2026-07-14 09:00:00'),
('elder_003','dev_007','spo2',96.0,'%',0,'2026-07-14 09:00:00','2026-07-14 09:00:00'),
('elder_003','dev_007','blood_pressure_sys',125.0,'mmHg',0,'2026-07-14 09:00:00','2026-07-14 09:00:00'),
('elder_003','dev_007','blood_pressure_dia',89.0,'mmHg',0,'2026-07-14 09:00:00','2026-07-14 09:00:00'),
('elder_003','dev_007','temperature',36.5,'C',0,'2026-07-14 09:00:00','2026-07-14 09:00:00'),
('elder_003','dev_007','heart_rate',80.0,'bpm',0,'2026-07-15 09:05:00','2026-07-15 09:05:00'),
('elder_003','dev_007','spo2',91.0,'%',0,'2026-07-15 09:05:00','2026-07-15 09:05:00'),
('elder_003','dev_007','blood_pressure_sys',134.0,'mmHg',0,'2026-07-15 09:05:00','2026-07-15 09:05:00'),
('elder_003','dev_007','blood_pressure_dia',79.0,'mmHg',0,'2026-07-15 09:05:00','2026-07-15 09:05:00'),
('elder_003','dev_007','temperature',37.3,'C',0,'2026-07-15 09:05:00','2026-07-15 09:05:00'),
('elder_003','dev_007','heart_rate',81.0,'bpm',0,'2026-07-16 09:11:00','2026-07-16 09:11:00'),
('elder_003','dev_007','spo2',96.0,'%',0,'2026-07-16 09:11:00','2026-07-16 09:11:00'),
('elder_003','dev_007','blood_pressure_sys',128.0,'mmHg',0,'2026-07-16 09:11:00','2026-07-16 09:11:00'),
('elder_003','dev_007','blood_pressure_dia',84.0,'mmHg',0,'2026-07-16 09:11:00','2026-07-16 09:11:00'),
('elder_003','dev_007','temperature',37.0,'C',0,'2026-07-16 09:11:00','2026-07-16 09:11:00'),
('elder_003','dev_007','heart_rate',89.0,'bpm',0,'2026-07-17 09:20:00','2026-07-17 09:20:00'),
('elder_003','dev_007','spo2',97.0,'%',0,'2026-07-17 09:20:00','2026-07-17 09:20:00'),
('elder_003','dev_007','blood_pressure_sys',129.0,'mmHg',0,'2026-07-17 09:20:00','2026-07-17 09:20:00'),
('elder_003','dev_007','blood_pressure_dia',87.0,'mmHg',0,'2026-07-17 09:20:00','2026-07-17 09:20:00'),
('elder_003','dev_007','temperature',37.2,'C',0,'2026-07-17 09:20:00','2026-07-17 09:20:00'),
('elder_003','dev_007','heart_rate',97.0,'bpm',0,'2026-07-18 09:04:00','2026-07-18 09:04:00'),
('elder_003','dev_007','spo2',96.0,'%',0,'2026-07-18 09:04:00','2026-07-18 09:04:00'),
('elder_003','dev_007','blood_pressure_sys',139.0,'mmHg',0,'2026-07-18 09:04:00','2026-07-18 09:04:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',0,'2026-07-18 09:04:00','2026-07-18 09:04:00'),
('elder_003','dev_007','temperature',37.3,'C',0,'2026-07-18 09:04:00','2026-07-18 09:04:00'),
('elder_003','dev_007','heart_rate',80.0,'bpm',0,'2026-07-19 09:03:00','2026-07-19 09:03:00'),
('elder_003','dev_007','spo2',94.0,'%',0,'2026-07-19 09:03:00','2026-07-19 09:03:00'),
('elder_003','dev_007','blood_pressure_sys',140.0,'mmHg',0,'2026-07-19 09:03:00','2026-07-19 09:03:00'),
('elder_003','dev_007','blood_pressure_dia',87.0,'mmHg',0,'2026-07-19 09:03:00','2026-07-19 09:03:00'),
('elder_003','dev_007','temperature',36.7,'C',0,'2026-07-19 09:03:00','2026-07-19 09:03:00'),
('elder_003','dev_007','heart_rate',88.0,'bpm',0,'2026-07-20 09:11:00','2026-07-20 09:11:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-07-20 09:11:00','2026-07-20 09:11:00'),
('elder_003','dev_007','blood_pressure_sys',129.0,'mmHg',0,'2026-07-20 09:11:00','2026-07-20 09:11:00'),
('elder_003','dev_007','blood_pressure_dia',90.0,'mmHg',0,'2026-07-20 09:11:00','2026-07-20 09:11:00'),
('elder_003','dev_007','temperature',37.2,'C',0,'2026-07-20 09:11:00','2026-07-20 09:11:00'),
('elder_003','dev_007','heart_rate',85.0,'bpm',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_003','dev_007','blood_pressure_sys',141.0,'mmHg',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_003','dev_007','blood_pressure_dia',79.0,'mmHg',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_003','dev_007','temperature',36.4,'C',0,'2026-07-21 09:11:00','2026-07-21 09:11:00'),
('elder_003','dev_007','heart_rate',80.0,'bpm',0,'2026-07-22 09:23:00','2026-07-22 09:23:00'),
('elder_003','dev_007','spo2',96.0,'%',0,'2026-07-22 09:23:00','2026-07-22 09:23:00'),
('elder_003','dev_007','blood_pressure_sys',134.0,'mmHg',0,'2026-07-22 09:23:00','2026-07-22 09:23:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',0,'2026-07-22 09:23:00','2026-07-22 09:23:00'),
('elder_003','dev_007','temperature',36.9,'C',0,'2026-07-22 09:23:00','2026-07-22 09:23:00'),
('elder_003','dev_007','heart_rate',98.0,'bpm',0,'2026-07-23 09:08:00','2026-07-23 09:08:00'),
('elder_003','dev_007','spo2',97.0,'%',0,'2026-07-23 09:08:00','2026-07-23 09:08:00'),
('elder_003','dev_007','blood_pressure_sys',137.0,'mmHg',0,'2026-07-23 09:08:00','2026-07-23 09:08:00'),
('elder_003','dev_007','blood_pressure_dia',86.0,'mmHg',0,'2026-07-23 09:08:00','2026-07-23 09:08:00'),
('elder_003','dev_007','temperature',36.8,'C',0,'2026-07-23 09:08:00','2026-07-23 09:08:00'),
('elder_003','dev_007','heart_rate',88.0,'bpm',0,'2026-07-24 09:27:00','2026-07-24 09:27:00'),
('elder_003','dev_007','spo2',92.0,'%',0,'2026-07-24 09:27:00','2026-07-24 09:27:00'),
('elder_003','dev_007','blood_pressure_sys',131.0,'mmHg',0,'2026-07-24 09:27:00','2026-07-24 09:27:00'),
('elder_003','dev_007','blood_pressure_dia',85.0,'mmHg',0,'2026-07-24 09:27:00','2026-07-24 09:27:00'),
('elder_003','dev_007','temperature',36.4,'C',0,'2026-07-24 09:27:00','2026-07-24 09:27:00'),
('elder_003','dev_007','heart_rate',92.0,'bpm',0,'2026-07-25 09:19:00','2026-07-25 09:19:00'),
('elder_003','dev_007','spo2',97.0,'%',0,'2026-07-25 09:19:00','2026-07-25 09:19:00'),
('elder_003','dev_007','blood_pressure_sys',140.0,'mmHg',0,'2026-07-25 09:19:00','2026-07-25 09:19:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',0,'2026-07-25 09:19:00','2026-07-25 09:19:00'),
('elder_003','dev_007','temperature',36.7,'C',0,'2026-07-25 09:19:00','2026-07-25 09:19:00'),
('elder_003','dev_007','heart_rate',90.0,'bpm',0,'2026-07-26 09:13:00','2026-07-26 09:13:00'),
('elder_003','dev_007','spo2',93.0,'%',0,'2026-07-26 09:13:00','2026-07-26 09:13:00'),
('elder_003','dev_007','blood_pressure_sys',126.0,'mmHg',0,'2026-07-26 09:13:00','2026-07-26 09:13:00'),
('elder_003','dev_007','blood_pressure_dia',84.0,'mmHg',0,'2026-07-26 09:13:00','2026-07-26 09:13:00'),
('elder_003','dev_007','temperature',36.6,'C',0,'2026-07-26 09:13:00','2026-07-26 09:13:00'),
('elder_003','dev_007','heart_rate',87.0,'bpm',0,'2026-07-27 09:02:00','2026-07-27 09:02:00'),
('elder_003','dev_007','spo2',98.0,'%',0,'2026-07-27 09:02:00','2026-07-27 09:02:00'),
('elder_003','dev_007','blood_pressure_sys',125.0,'mmHg',0,'2026-07-27 09:02:00','2026-07-27 09:02:00'),
('elder_003','dev_007','blood_pressure_dia',86.0,'mmHg',0,'2026-07-27 09:02:00','2026-07-27 09:02:00'),
('elder_003','dev_007','temperature',37.1,'C',0,'2026-07-27 09:02:00','2026-07-27 09:02:00'),
('elder_003','dev_007','heart_rate',88.0,'bpm',0,'2026-07-28 09:09:00','2026-07-28 09:09:00'),
('elder_003','dev_007','spo2',98.0,'%',0,'2026-07-28 09:09:00','2026-07-28 09:09:00'),
('elder_003','dev_007','blood_pressure_sys',127.0,'mmHg',0,'2026-07-28 09:09:00','2026-07-28 09:09:00'),
('elder_003','dev_007','blood_pressure_dia',91.0,'mmHg',0,'2026-07-28 09:09:00','2026-07-28 09:09:00'),
('elder_003','dev_007','temperature',36.9,'C',0,'2026-07-28 09:09:00','2026-07-28 09:09:00'),
('elder_003','dev_007','heart_rate',81.0,'bpm',0,'2026-07-29 09:12:00','2026-07-29 09:12:00'),
('elder_003','dev_007','spo2',94.0,'%',0,'2026-07-29 09:12:00','2026-07-29 09:12:00'),
('elder_003','dev_007','blood_pressure_sys',131.0,'mmHg',0,'2026-07-29 09:12:00','2026-07-29 09:12:00'),
('elder_003','dev_007','blood_pressure_dia',81.0,'mmHg',0,'2026-07-29 09:12:00','2026-07-29 09:12:00'),
('elder_003','dev_007','temperature',36.9,'C',0,'2026-07-29 09:12:00','2026-07-29 09:12:00'),
('elder_003','dev_007','heart_rate',91.0,'bpm',0,'2026-07-30 09:14:00','2026-07-30 09:14:00'),
('elder_003','dev_007','spo2',98.0,'%',0,'2026-07-30 09:14:00','2026-07-30 09:14:00'),
('elder_003','dev_007','blood_pressure_sys',140.0,'mmHg',0,'2026-07-30 09:14:00','2026-07-30 09:14:00'),
('elder_003','dev_007','blood_pressure_dia',87.0,'mmHg',0,'2026-07-30 09:14:00','2026-07-30 09:14:00'),
('elder_003','dev_007','temperature',36.6,'C',0,'2026-07-30 09:14:00','2026-07-30 09:14:00'),
('elder_003','dev_007','heart_rate',92.0,'bpm',0,'2026-07-31 09:02:00','2026-07-31 09:02:00'),
('elder_003','dev_007','spo2',97.0,'%',0,'2026-07-31 09:02:00','2026-07-31 09:02:00'),
('elder_003','dev_007','blood_pressure_sys',141.0,'mmHg',0,'2026-07-31 09:02:00','2026-07-31 09:02:00'),
('elder_003','dev_007','blood_pressure_dia',82.0,'mmHg',0,'2026-07-31 09:02:00','2026-07-31 09:02:00'),
('elder_003','dev_007','temperature',37.3,'C',0,'2026-07-31 09:02:00','2026-07-31 09:02:00'),
('elder_003','dev_007','heart_rate',92.0,'bpm',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_003','dev_007','spo2',97.0,'%',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_003','dev_007','blood_pressure_sys',139.0,'mmHg',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_003','dev_007','blood_pressure_dia',91.0,'mmHg',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_003','dev_007','temperature',36.5,'C',0,'2026-08-01 09:29:00','2026-08-01 09:29:00'),
('elder_003','dev_007','heart_rate',82.0,'bpm',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_003','dev_007','spo2',95.0,'%',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_003','dev_007','blood_pressure_sys',130.0,'mmHg',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_003','dev_007','blood_pressure_dia',84.0,'mmHg',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_003','dev_007','temperature',36.4,'C',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_004','dev_010','heart_rate',96.0,'bpm',0,'2026-07-04 09:06:00','2026-07-04 09:06:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-07-04 09:06:00','2026-07-04 09:06:00'),
('elder_004','dev_010','blood_pressure_sys',147.0,'mmHg',0,'2026-07-04 09:06:00','2026-07-04 09:06:00'),
('elder_004','dev_010','blood_pressure_dia',87.0,'mmHg',0,'2026-07-04 09:06:00','2026-07-04 09:06:00'),
('elder_004','dev_010','temperature',37.4,'C',0,'2026-07-04 09:06:00','2026-07-04 09:06:00'),
('elder_004','dev_010','heart_rate',100.0,'bpm',0,'2026-07-05 09:21:00','2026-07-05 09:21:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-05 09:21:00','2026-07-05 09:21:00'),
('elder_004','dev_010','blood_pressure_sys',152.0,'mmHg',0,'2026-07-05 09:21:00','2026-07-05 09:21:00'),
('elder_004','dev_010','blood_pressure_dia',93.0,'mmHg',0,'2026-07-05 09:21:00','2026-07-05 09:21:00'),
('elder_004','dev_010','temperature',37.1,'C',0,'2026-07-05 09:21:00','2026-07-05 09:21:00'),
('elder_004','dev_010','heart_rate',93.0,'bpm',0,'2026-07-06 09:13:00','2026-07-06 09:13:00'),
('elder_004','dev_010','spo2',88.0,'%',0,'2026-07-06 09:13:00','2026-07-06 09:13:00'),
('elder_004','dev_010','blood_pressure_sys',148.0,'mmHg',0,'2026-07-06 09:13:00','2026-07-06 09:13:00'),
('elder_004','dev_010','blood_pressure_dia',90.0,'mmHg',0,'2026-07-06 09:13:00','2026-07-06 09:13:00'),
('elder_004','dev_010','temperature',36.9,'C',0,'2026-07-06 09:13:00','2026-07-06 09:13:00'),
('elder_004','dev_010','heart_rate',86.0,'bpm',0,'2026-07-07 09:02:00','2026-07-07 09:02:00'),
('elder_004','dev_010','spo2',95.0,'%',0,'2026-07-07 09:02:00','2026-07-07 09:02:00'),
('elder_004','dev_010','blood_pressure_sys',148.0,'mmHg',0,'2026-07-07 09:02:00','2026-07-07 09:02:00'),
('elder_004','dev_010','blood_pressure_dia',88.0,'mmHg',0,'2026-07-07 09:02:00','2026-07-07 09:02:00'),
('elder_004','dev_010','temperature',37.4,'C',0,'2026-07-07 09:02:00','2026-07-07 09:02:00'),
('elder_004','dev_010','heart_rate',100.0,'bpm',0,'2026-07-08 09:11:00','2026-07-08 09:11:00'),
('elder_004','dev_010','spo2',89.0,'%',0,'2026-07-08 09:11:00','2026-07-08 09:11:00'),
('elder_004','dev_010','blood_pressure_sys',135.0,'mmHg',0,'2026-07-08 09:11:00','2026-07-08 09:11:00'),
('elder_004','dev_010','blood_pressure_dia',93.0,'mmHg',0,'2026-07-08 09:11:00','2026-07-08 09:11:00'),
('elder_004','dev_010','temperature',37.7,'C',0,'2026-07-08 09:11:00','2026-07-08 09:11:00'),
('elder_004','dev_010','heart_rate',100.0,'bpm',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_004','dev_010','blood_pressure_sys',148.0,'mmHg',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_004','dev_010','blood_pressure_dia',87.0,'mmHg',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_004','dev_010','temperature',37.6,'C',0,'2026-07-09 09:28:00','2026-07-09 09:28:00'),
('elder_004','dev_010','heart_rate',93.0,'bpm',0,'2026-07-10 09:22:00','2026-07-10 09:22:00'),
('elder_004','dev_010','spo2',91.0,'%',0,'2026-07-10 09:22:00','2026-07-10 09:22:00'),
('elder_004','dev_010','blood_pressure_sys',135.0,'mmHg',0,'2026-07-10 09:22:00','2026-07-10 09:22:00'),
('elder_004','dev_010','blood_pressure_dia',89.0,'mmHg',0,'2026-07-10 09:22:00','2026-07-10 09:22:00'),
('elder_004','dev_010','temperature',37.0,'C',0,'2026-07-10 09:22:00','2026-07-10 09:22:00'),
('elder_004','dev_010','heart_rate',89.0,'bpm',0,'2026-07-11 09:08:00','2026-07-11 09:08:00'),
('elder_004','dev_010','spo2',94.0,'%',0,'2026-07-11 09:08:00','2026-07-11 09:08:00'),
('elder_004','dev_010','blood_pressure_sys',139.0,'mmHg',0,'2026-07-11 09:08:00','2026-07-11 09:08:00'),
('elder_004','dev_010','blood_pressure_dia',93.0,'mmHg',0,'2026-07-11 09:08:00','2026-07-11 09:08:00'),
('elder_004','dev_010','temperature',36.8,'C',0,'2026-07-11 09:08:00','2026-07-11 09:08:00'),
('elder_004','dev_010','heart_rate',86.0,'bpm',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_004','dev_010','spo2',90.0,'%',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_004','dev_010','blood_pressure_sys',149.0,'mmHg',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_004','dev_010','blood_pressure_dia',94.0,'mmHg',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_004','dev_010','temperature',37.4,'C',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_004','dev_010','heart_rate',104.0,'bpm',0,'2026-07-13 09:26:00','2026-07-13 09:26:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-13 09:26:00','2026-07-13 09:26:00'),
('elder_004','dev_010','blood_pressure_sys',142.0,'mmHg',0,'2026-07-13 09:26:00','2026-07-13 09:26:00'),
('elder_004','dev_010','blood_pressure_dia',96.0,'mmHg',0,'2026-07-13 09:26:00','2026-07-13 09:26:00'),
('elder_004','dev_010','temperature',37.6,'C',0,'2026-07-13 09:26:00','2026-07-13 09:26:00'),
('elder_004','dev_010','heart_rate',102.0,'bpm',0,'2026-07-14 09:01:00','2026-07-14 09:01:00'),
('elder_004','dev_010','spo2',95.0,'%',0,'2026-07-14 09:01:00','2026-07-14 09:01:00'),
('elder_004','dev_010','blood_pressure_sys',149.0,'mmHg',0,'2026-07-14 09:01:00','2026-07-14 09:01:00'),
('elder_004','dev_010','blood_pressure_dia',93.0,'mmHg',0,'2026-07-14 09:01:00','2026-07-14 09:01:00'),
('elder_004','dev_010','temperature',37.5,'C',0,'2026-07-14 09:01:00','2026-07-14 09:01:00'),
('elder_004','dev_010','heart_rate',93.0,'bpm',0,'2026-07-15 09:20:00','2026-07-15 09:20:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-07-15 09:20:00','2026-07-15 09:20:00'),
('elder_004','dev_010','blood_pressure_sys',134.0,'mmHg',0,'2026-07-15 09:20:00','2026-07-15 09:20:00'),
('elder_004','dev_010','blood_pressure_dia',86.0,'mmHg',0,'2026-07-15 09:20:00','2026-07-15 09:20:00'),
('elder_004','dev_010','temperature',37.3,'C',0,'2026-07-15 09:20:00','2026-07-15 09:20:00'),
('elder_004','dev_010','heart_rate',100.0,'bpm',0,'2026-07-16 09:00:00','2026-07-16 09:00:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-16 09:00:00','2026-07-16 09:00:00'),
('elder_004','dev_010','blood_pressure_sys',150.0,'mmHg',0,'2026-07-16 09:00:00','2026-07-16 09:00:00'),
('elder_004','dev_010','blood_pressure_dia',95.0,'mmHg',0,'2026-07-16 09:00:00','2026-07-16 09:00:00'),
('elder_004','dev_010','temperature',37.3,'C',0,'2026-07-16 09:00:00','2026-07-16 09:00:00'),
('elder_004','dev_010','heart_rate',93.0,'bpm',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_004','dev_010','blood_pressure_sys',137.0,'mmHg',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_004','dev_010','blood_pressure_dia',95.0,'mmHg',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_004','dev_010','temperature',37.3,'C',0,'2026-07-17 09:05:00','2026-07-17 09:05:00'),
('elder_004','dev_010','heart_rate',94.0,'bpm',0,'2026-07-18 09:20:00','2026-07-18 09:20:00'),
('elder_004','dev_010','spo2',95.0,'%',0,'2026-07-18 09:20:00','2026-07-18 09:20:00'),
('elder_004','dev_010','blood_pressure_sys',135.0,'mmHg',0,'2026-07-18 09:20:00','2026-07-18 09:20:00'),
('elder_004','dev_010','blood_pressure_dia',90.0,'mmHg',0,'2026-07-18 09:20:00','2026-07-18 09:20:00'),
('elder_004','dev_010','temperature',36.8,'C',0,'2026-07-18 09:20:00','2026-07-18 09:20:00'),
('elder_004','dev_010','heart_rate',91.0,'bpm',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_004','dev_010','spo2',89.0,'%',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_004','dev_010','blood_pressure_sys',141.0,'mmHg',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_004','dev_010','blood_pressure_dia',86.0,'mmHg',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_004','dev_010','temperature',37.6,'C',0,'2026-07-19 09:29:00','2026-07-19 09:29:00'),
('elder_004','dev_010','heart_rate',97.0,'bpm',0,'2026-07-20 09:08:00','2026-07-20 09:08:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-20 09:08:00','2026-07-20 09:08:00'),
('elder_004','dev_010','blood_pressure_sys',147.0,'mmHg',0,'2026-07-20 09:08:00','2026-07-20 09:08:00'),
('elder_004','dev_010','blood_pressure_dia',91.0,'mmHg',0,'2026-07-20 09:08:00','2026-07-20 09:08:00'),
('elder_004','dev_010','temperature',37.3,'C',0,'2026-07-20 09:08:00','2026-07-20 09:08:00'),
('elder_004','dev_010','heart_rate',91.0,'bpm',0,'2026-07-21 09:24:00','2026-07-21 09:24:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-07-21 09:24:00','2026-07-21 09:24:00'),
('elder_004','dev_010','blood_pressure_sys',141.0,'mmHg',0,'2026-07-21 09:24:00','2026-07-21 09:24:00'),
('elder_004','dev_010','blood_pressure_dia',89.0,'mmHg',0,'2026-07-21 09:24:00','2026-07-21 09:24:00'),
('elder_004','dev_010','temperature',36.8,'C',0,'2026-07-21 09:24:00','2026-07-21 09:24:00'),
('elder_004','dev_010','heart_rate',91.0,'bpm',0,'2026-07-22 09:12:00','2026-07-22 09:12:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-22 09:12:00','2026-07-22 09:12:00'),
('elder_004','dev_010','blood_pressure_sys',140.0,'mmHg',0,'2026-07-22 09:12:00','2026-07-22 09:12:00'),
('elder_004','dev_010','blood_pressure_dia',95.0,'mmHg',0,'2026-07-22 09:12:00','2026-07-22 09:12:00'),
('elder_004','dev_010','temperature',37.3,'C',0,'2026-07-22 09:12:00','2026-07-22 09:12:00'),
('elder_004','dev_010','heart_rate',98.0,'bpm',0,'2026-07-23 09:09:00','2026-07-23 09:09:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-07-23 09:09:00','2026-07-23 09:09:00'),
('elder_004','dev_010','blood_pressure_sys',145.0,'mmHg',0,'2026-07-23 09:09:00','2026-07-23 09:09:00'),
('elder_004','dev_010','blood_pressure_dia',93.0,'mmHg',0,'2026-07-23 09:09:00','2026-07-23 09:09:00'),
('elder_004','dev_010','temperature',36.7,'C',0,'2026-07-23 09:09:00','2026-07-23 09:09:00'),
('elder_004','dev_010','heart_rate',86.0,'bpm',0,'2026-07-24 09:05:00','2026-07-24 09:05:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-07-24 09:05:00','2026-07-24 09:05:00'),
('elder_004','dev_010','blood_pressure_sys',140.0,'mmHg',0,'2026-07-24 09:05:00','2026-07-24 09:05:00'),
('elder_004','dev_010','blood_pressure_dia',90.0,'mmHg',0,'2026-07-24 09:05:00','2026-07-24 09:05:00'),
('elder_004','dev_010','temperature',37.0,'C',0,'2026-07-24 09:05:00','2026-07-24 09:05:00'),
('elder_004','dev_010','heart_rate',101.0,'bpm',0,'2026-07-25 09:12:00','2026-07-25 09:12:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-25 09:12:00','2026-07-25 09:12:00'),
('elder_004','dev_010','blood_pressure_sys',151.0,'mmHg',0,'2026-07-25 09:12:00','2026-07-25 09:12:00'),
('elder_004','dev_010','blood_pressure_dia',94.0,'mmHg',0,'2026-07-25 09:12:00','2026-07-25 09:12:00'),
('elder_004','dev_010','temperature',37.3,'C',0,'2026-07-25 09:12:00','2026-07-25 09:12:00'),
('elder_004','dev_010','heart_rate',101.0,'bpm',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_004','dev_010','blood_pressure_sys',140.0,'mmHg',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_004','dev_010','blood_pressure_dia',97.0,'mmHg',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_004','dev_010','temperature',37.2,'C',0,'2026-07-26 09:08:00','2026-07-26 09:08:00'),
('elder_004','dev_010','heart_rate',96.0,'bpm',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_004','dev_010','spo2',88.0,'%',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_004','dev_010','blood_pressure_sys',152.0,'mmHg',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_004','dev_010','blood_pressure_dia',87.0,'mmHg',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_004','dev_010','temperature',36.7,'C',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_004','dev_010','heart_rate',91.0,'bpm',0,'2026-07-28 09:08:00','2026-07-28 09:08:00'),
('elder_004','dev_010','spo2',89.0,'%',0,'2026-07-28 09:08:00','2026-07-28 09:08:00'),
('elder_004','dev_010','blood_pressure_sys',140.0,'mmHg',0,'2026-07-28 09:08:00','2026-07-28 09:08:00'),
('elder_004','dev_010','blood_pressure_dia',88.0,'mmHg',0,'2026-07-28 09:08:00','2026-07-28 09:08:00'),
('elder_004','dev_010','temperature',36.9,'C',0,'2026-07-28 09:08:00','2026-07-28 09:08:00'),
('elder_004','dev_010','heart_rate',90.0,'bpm',0,'2026-07-29 09:30:00','2026-07-29 09:30:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-29 09:30:00','2026-07-29 09:30:00'),
('elder_004','dev_010','blood_pressure_sys',151.0,'mmHg',0,'2026-07-29 09:30:00','2026-07-29 09:30:00'),
('elder_004','dev_010','blood_pressure_dia',92.0,'mmHg',0,'2026-07-29 09:30:00','2026-07-29 09:30:00'),
('elder_004','dev_010','temperature',37.5,'C',0,'2026-07-29 09:30:00','2026-07-29 09:30:00'),
('elder_004','dev_010','heart_rate',90.0,'bpm',0,'2026-07-30 09:04:00','2026-07-30 09:04:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-07-30 09:04:00','2026-07-30 09:04:00'),
('elder_004','dev_010','blood_pressure_sys',138.0,'mmHg',0,'2026-07-30 09:04:00','2026-07-30 09:04:00'),
('elder_004','dev_010','blood_pressure_dia',85.0,'mmHg',0,'2026-07-30 09:04:00','2026-07-30 09:04:00'),
('elder_004','dev_010','temperature',37.6,'C',0,'2026-07-30 09:04:00','2026-07-30 09:04:00'),
('elder_004','dev_010','heart_rate',90.0,'bpm',0,'2026-07-31 09:04:00','2026-07-31 09:04:00'),
('elder_004','dev_010','spo2',88.0,'%',0,'2026-07-31 09:04:00','2026-07-31 09:04:00'),
('elder_004','dev_010','blood_pressure_sys',134.0,'mmHg',0,'2026-07-31 09:04:00','2026-07-31 09:04:00'),
('elder_004','dev_010','blood_pressure_dia',88.0,'mmHg',0,'2026-07-31 09:04:00','2026-07-31 09:04:00'),
('elder_004','dev_010','temperature',37.7,'C',0,'2026-07-31 09:04:00','2026-07-31 09:04:00'),
('elder_004','dev_010','heart_rate',91.0,'bpm',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_004','dev_010','spo2',92.0,'%',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_004','dev_010','blood_pressure_sys',149.0,'mmHg',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_004','dev_010','blood_pressure_dia',95.0,'mmHg',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_004','dev_010','temperature',36.8,'C',0,'2026-08-01 09:01:00','2026-08-01 09:01:00'),
('elder_004','dev_010','heart_rate',99.0,'bpm',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_004','dev_010','spo2',93.0,'%',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_004','dev_010','blood_pressure_sys',143.0,'mmHg',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_004','dev_010','blood_pressure_dia',93.0,'mmHg',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_004','dev_010','temperature',37.4,'C',0,'2026-08-02 09:23:00','2026-08-02 09:23:00'),
('elder_005','dev_012','heart_rate',93.0,'bpm',0,'2026-07-04 09:15:00','2026-07-04 09:15:00'),
('elder_005','dev_012','spo2',89.0,'%',0,'2026-07-04 09:15:00','2026-07-04 09:15:00'),
('elder_005','dev_012','blood_pressure_sys',143.0,'mmHg',0,'2026-07-04 09:15:00','2026-07-04 09:15:00'),
('elder_005','dev_012','blood_pressure_dia',92.0,'mmHg',0,'2026-07-04 09:15:00','2026-07-04 09:15:00'),
('elder_005','dev_012','temperature',36.8,'C',0,'2026-07-04 09:15:00','2026-07-04 09:15:00'),
('elder_005','dev_012','heart_rate',109.0,'bpm',0,'2026-07-05 09:02:00','2026-07-05 09:02:00'),
('elder_005','dev_012','spo2',92.0,'%',0,'2026-07-05 09:02:00','2026-07-05 09:02:00'),
('elder_005','dev_012','blood_pressure_sys',156.0,'mmHg',0,'2026-07-05 09:02:00','2026-07-05 09:02:00'),
('elder_005','dev_012','blood_pressure_dia',95.0,'mmHg',0,'2026-07-05 09:02:00','2026-07-05 09:02:00'),
('elder_005','dev_012','temperature',37.8,'C',0,'2026-07-05 09:02:00','2026-07-05 09:02:00'),
('elder_005','dev_012','heart_rate',104.0,'bpm',0,'2026-07-06 09:20:00','2026-07-06 09:20:00'),
('elder_005','dev_012','spo2',94.0,'%',0,'2026-07-06 09:20:00','2026-07-06 09:20:00'),
('elder_005','dev_012','blood_pressure_sys',147.0,'mmHg',0,'2026-07-06 09:20:00','2026-07-06 09:20:00'),
('elder_005','dev_012','blood_pressure_dia',94.0,'mmHg',0,'2026-07-06 09:20:00','2026-07-06 09:20:00'),
('elder_005','dev_012','temperature',37.1,'C',0,'2026-07-06 09:20:00','2026-07-06 09:20:00'),
('elder_005','dev_012','heart_rate',110.0,'bpm',0,'2026-07-07 09:18:00','2026-07-07 09:18:00'),
('elder_005','dev_012','spo2',90.0,'%',0,'2026-07-07 09:18:00','2026-07-07 09:18:00'),
('elder_005','dev_012','blood_pressure_sys',160.0,'mmHg',0,'2026-07-07 09:18:00','2026-07-07 09:18:00'),
('elder_005','dev_012','blood_pressure_dia',100.0,'mmHg',0,'2026-07-07 09:18:00','2026-07-07 09:18:00'),
('elder_005','dev_012','temperature',37.0,'C',0,'2026-07-07 09:18:00','2026-07-07 09:18:00'),
('elder_005','dev_012','heart_rate',107.0,'bpm',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_005','dev_012','spo2',87.0,'%',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_005','dev_012','blood_pressure_sys',146.0,'mmHg',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_005','dev_012','blood_pressure_dia',92.0,'mmHg',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_005','dev_012','temperature',37.1,'C',0,'2026-07-08 09:07:00','2026-07-08 09:07:00'),
('elder_005','dev_012','heart_rate',93.0,'bpm',0,'2026-07-09 09:01:00','2026-07-09 09:01:00'),
('elder_005','dev_012','spo2',87.0,'%',0,'2026-07-09 09:01:00','2026-07-09 09:01:00'),
('elder_005','dev_012','blood_pressure_sys',147.0,'mmHg',0,'2026-07-09 09:01:00','2026-07-09 09:01:00'),
('elder_005','dev_012','blood_pressure_dia',98.0,'mmHg',0,'2026-07-09 09:01:00','2026-07-09 09:01:00'),
('elder_005','dev_012','temperature',37.1,'C',0,'2026-07-09 09:01:00','2026-07-09 09:01:00'),
('elder_005','dev_012','heart_rate',102.0,'bpm',0,'2026-07-10 09:02:00','2026-07-10 09:02:00'),
('elder_005','dev_012','spo2',91.0,'%',0,'2026-07-10 09:02:00','2026-07-10 09:02:00'),
('elder_005','dev_012','blood_pressure_sys',159.0,'mmHg',0,'2026-07-10 09:02:00','2026-07-10 09:02:00'),
('elder_005','dev_012','blood_pressure_dia',93.0,'mmHg',0,'2026-07-10 09:02:00','2026-07-10 09:02:00'),
('elder_005','dev_012','temperature',37.2,'C',0,'2026-07-10 09:02:00','2026-07-10 09:02:00'),
('elder_005','dev_012','heart_rate',97.0,'bpm',0,'2026-07-11 09:10:00','2026-07-11 09:10:00'),
('elder_005','dev_012','spo2',94.0,'%',0,'2026-07-11 09:10:00','2026-07-11 09:10:00'),
('elder_005','dev_012','blood_pressure_sys',154.0,'mmHg',0,'2026-07-11 09:10:00','2026-07-11 09:10:00'),
('elder_005','dev_012','blood_pressure_dia',90.0,'mmHg',0,'2026-07-11 09:10:00','2026-07-11 09:10:00'),
('elder_005','dev_012','temperature',37.4,'C',0,'2026-07-11 09:10:00','2026-07-11 09:10:00'),
('elder_005','dev_012','heart_rate',97.0,'bpm',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_005','dev_012','spo2',88.0,'%',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_005','dev_012','blood_pressure_sys',156.0,'mmHg',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_005','dev_012','blood_pressure_dia',99.0,'mmHg',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_005','dev_012','temperature',37.2,'C',0,'2026-07-12 09:09:00','2026-07-12 09:09:00'),
('elder_005','dev_012','heart_rate',108.0,'bpm',0,'2026-07-13 09:10:00','2026-07-13 09:10:00'),
('elder_005','dev_012','spo2',88.0,'%',0,'2026-07-13 09:10:00','2026-07-13 09:10:00'),
('elder_005','dev_012','blood_pressure_sys',157.0,'mmHg',0,'2026-07-13 09:10:00','2026-07-13 09:10:00'),
('elder_005','dev_012','blood_pressure_dia',92.0,'mmHg',0,'2026-07-13 09:10:00','2026-07-13 09:10:00'),
('elder_005','dev_012','temperature',37.4,'C',0,'2026-07-13 09:10:00','2026-07-13 09:10:00'),
('elder_005','dev_012','heart_rate',100.0,'bpm',0,'2026-07-14 09:30:00','2026-07-14 09:30:00'),
('elder_005','dev_012','spo2',91.0,'%',0,'2026-07-14 09:30:00','2026-07-14 09:30:00'),
('elder_005','dev_012','blood_pressure_sys',152.0,'mmHg',0,'2026-07-14 09:30:00','2026-07-14 09:30:00'),
('elder_005','dev_012','blood_pressure_dia',102.0,'mmHg',0,'2026-07-14 09:30:00','2026-07-14 09:30:00'),
('elder_005','dev_012','temperature',37.0,'C',0,'2026-07-14 09:30:00','2026-07-14 09:30:00'),
('elder_005','dev_012','heart_rate',95.0,'bpm',0,'2026-07-15 09:00:00','2026-07-15 09:00:00'),
('elder_005','dev_012','spo2',89.0,'%',0,'2026-07-15 09:00:00','2026-07-15 09:00:00'),
('elder_005','dev_012','blood_pressure_sys',158.0,'mmHg',0,'2026-07-15 09:00:00','2026-07-15 09:00:00'),
('elder_005','dev_012','blood_pressure_dia',102.0,'mmHg',0,'2026-07-15 09:00:00','2026-07-15 09:00:00'),
('elder_005','dev_012','temperature',37.8,'C',0,'2026-07-15 09:00:00','2026-07-15 09:00:00'),
('elder_005','dev_012','heart_rate',95.0,'bpm',0,'2026-07-16 09:29:00','2026-07-16 09:29:00'),
('elder_005','dev_012','spo2',88.0,'%',0,'2026-07-16 09:29:00','2026-07-16 09:29:00'),
('elder_005','dev_012','blood_pressure_sys',156.0,'mmHg',0,'2026-07-16 09:29:00','2026-07-16 09:29:00'),
('elder_005','dev_012','blood_pressure_dia',96.0,'mmHg',0,'2026-07-16 09:29:00','2026-07-16 09:29:00'),
('elder_005','dev_012','temperature',37.5,'C',0,'2026-07-16 09:29:00','2026-07-16 09:29:00'),
('elder_005','dev_012','heart_rate',93.0,'bpm',0,'2026-07-17 09:17:00','2026-07-17 09:17:00'),
('elder_005','dev_012','spo2',92.0,'%',0,'2026-07-17 09:17:00','2026-07-17 09:17:00'),
('elder_005','dev_012','blood_pressure_sys',143.0,'mmHg',0,'2026-07-17 09:17:00','2026-07-17 09:17:00'),
('elder_005','dev_012','blood_pressure_dia',100.0,'mmHg',0,'2026-07-17 09:17:00','2026-07-17 09:17:00'),
('elder_005','dev_012','temperature',37.2,'C',0,'2026-07-17 09:17:00','2026-07-17 09:17:00'),
('elder_005','dev_012','heart_rate',105.0,'bpm',0,'2026-07-18 09:19:00','2026-07-18 09:19:00'),
('elder_005','dev_012','spo2',89.0,'%',0,'2026-07-18 09:19:00','2026-07-18 09:19:00'),
('elder_005','dev_012','blood_pressure_sys',149.0,'mmHg',0,'2026-07-18 09:19:00','2026-07-18 09:19:00'),
('elder_005','dev_012','blood_pressure_dia',98.0,'mmHg',0,'2026-07-18 09:19:00','2026-07-18 09:19:00'),
('elder_005','dev_012','temperature',37.8,'C',0,'2026-07-18 09:19:00','2026-07-18 09:19:00'),
('elder_005','dev_012','heart_rate',99.0,'bpm',0,'2026-07-19 09:13:00','2026-07-19 09:13:00'),
('elder_005','dev_012','spo2',87.0,'%',0,'2026-07-19 09:13:00','2026-07-19 09:13:00'),
('elder_005','dev_012','blood_pressure_sys',158.0,'mmHg',0,'2026-07-19 09:13:00','2026-07-19 09:13:00'),
('elder_005','dev_012','blood_pressure_dia',92.0,'mmHg',0,'2026-07-19 09:13:00','2026-07-19 09:13:00'),
('elder_005','dev_012','temperature',37.7,'C',0,'2026-07-19 09:13:00','2026-07-19 09:13:00'),
('elder_005','dev_012','heart_rate',104.0,'bpm',0,'2026-07-20 09:06:00','2026-07-20 09:06:00'),
('elder_005','dev_012','spo2',94.0,'%',0,'2026-07-20 09:06:00','2026-07-20 09:06:00'),
('elder_005','dev_012','blood_pressure_sys',154.0,'mmHg',0,'2026-07-20 09:06:00','2026-07-20 09:06:00'),
('elder_005','dev_012','blood_pressure_dia',90.0,'mmHg',0,'2026-07-20 09:06:00','2026-07-20 09:06:00'),
('elder_005','dev_012','temperature',37.3,'C',0,'2026-07-20 09:06:00','2026-07-20 09:06:00'),
('elder_005','dev_012','heart_rate',95.0,'bpm',0,'2026-07-21 09:06:00','2026-07-21 09:06:00'),
('elder_005','dev_012','spo2',89.0,'%',0,'2026-07-21 09:06:00','2026-07-21 09:06:00'),
('elder_005','dev_012','blood_pressure_sys',153.0,'mmHg',0,'2026-07-21 09:06:00','2026-07-21 09:06:00'),
('elder_005','dev_012','blood_pressure_dia',91.0,'mmHg',0,'2026-07-21 09:06:00','2026-07-21 09:06:00'),
('elder_005','dev_012','temperature',36.9,'C',0,'2026-07-21 09:06:00','2026-07-21 09:06:00'),
('elder_005','dev_012','heart_rate',101.0,'bpm',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_005','dev_012','spo2',88.0,'%',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_005','dev_012','blood_pressure_sys',153.0,'mmHg',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_005','dev_012','blood_pressure_dia',91.0,'mmHg',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_005','dev_012','temperature',37.5,'C',0,'2026-07-22 09:29:00','2026-07-22 09:29:00'),
('elder_005','dev_012','heart_rate',92.0,'bpm',0,'2026-07-23 09:03:00','2026-07-23 09:03:00'),
('elder_005','dev_012','spo2',92.0,'%',0,'2026-07-23 09:03:00','2026-07-23 09:03:00'),
('elder_005','dev_012','blood_pressure_sys',145.0,'mmHg',0,'2026-07-23 09:03:00','2026-07-23 09:03:00'),
('elder_005','dev_012','blood_pressure_dia',98.0,'mmHg',0,'2026-07-23 09:03:00','2026-07-23 09:03:00'),
('elder_005','dev_012','temperature',37.6,'C',0,'2026-07-23 09:03:00','2026-07-23 09:03:00'),
('elder_005','dev_012','heart_rate',94.0,'bpm',0,'2026-07-24 09:06:00','2026-07-24 09:06:00'),
('elder_005','dev_012','spo2',90.0,'%',0,'2026-07-24 09:06:00','2026-07-24 09:06:00'),
('elder_005','dev_012','blood_pressure_sys',158.0,'mmHg',0,'2026-07-24 09:06:00','2026-07-24 09:06:00'),
('elder_005','dev_012','blood_pressure_dia',99.0,'mmHg',0,'2026-07-24 09:06:00','2026-07-24 09:06:00'),
('elder_005','dev_012','temperature',37.7,'C',0,'2026-07-24 09:06:00','2026-07-24 09:06:00'),
('elder_005','dev_012','heart_rate',104.0,'bpm',0,'2026-07-25 09:04:00','2026-07-25 09:04:00'),
('elder_005','dev_012','spo2',94.0,'%',0,'2026-07-25 09:04:00','2026-07-25 09:04:00'),
('elder_005','dev_012','blood_pressure_sys',159.0,'mmHg',0,'2026-07-25 09:04:00','2026-07-25 09:04:00'),
('elder_005','dev_012','blood_pressure_dia',99.0,'mmHg',0,'2026-07-25 09:04:00','2026-07-25 09:04:00'),
('elder_005','dev_012','temperature',37.1,'C',0,'2026-07-25 09:04:00','2026-07-25 09:04:00'),
('elder_005','dev_012','heart_rate',92.0,'bpm',0,'2026-07-26 09:16:00','2026-07-26 09:16:00'),
('elder_005','dev_012','spo2',94.0,'%',0,'2026-07-26 09:16:00','2026-07-26 09:16:00'),
('elder_005','dev_012','blood_pressure_sys',142.0,'mmHg',0,'2026-07-26 09:16:00','2026-07-26 09:16:00'),
('elder_005','dev_012','blood_pressure_dia',91.0,'mmHg',0,'2026-07-26 09:16:00','2026-07-26 09:16:00'),
('elder_005','dev_012','temperature',37.7,'C',0,'2026-07-26 09:16:00','2026-07-26 09:16:00'),
('elder_005','dev_012','heart_rate',109.0,'bpm',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_005','dev_012','spo2',93.0,'%',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_005','dev_012','blood_pressure_sys',154.0,'mmHg',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_005','dev_012','blood_pressure_dia',90.0,'mmHg',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_005','dev_012','temperature',37.4,'C',0,'2026-07-27 09:21:00','2026-07-27 09:21:00'),
('elder_005','dev_012','heart_rate',95.0,'bpm',0,'2026-07-28 09:03:00','2026-07-28 09:03:00'),
('elder_005','dev_012','spo2',90.0,'%',0,'2026-07-28 09:03:00','2026-07-28 09:03:00'),
('elder_005','dev_012','blood_pressure_sys',158.0,'mmHg',0,'2026-07-28 09:03:00','2026-07-28 09:03:00'),
('elder_005','dev_012','blood_pressure_dia',95.0,'mmHg',0,'2026-07-28 09:03:00','2026-07-28 09:03:00'),
('elder_005','dev_012','temperature',36.9,'C',0,'2026-07-28 09:03:00','2026-07-28 09:03:00'),
('elder_005','dev_012','heart_rate',102.0,'bpm',0,'2026-07-29 09:26:00','2026-07-29 09:26:00'),
('elder_005','dev_012','spo2',93.0,'%',0,'2026-07-29 09:26:00','2026-07-29 09:26:00'),
('elder_005','dev_012','blood_pressure_sys',156.0,'mmHg',0,'2026-07-29 09:26:00','2026-07-29 09:26:00'),
('elder_005','dev_012','blood_pressure_dia',100.0,'mmHg',0,'2026-07-29 09:26:00','2026-07-29 09:26:00'),
('elder_005','dev_012','temperature',37.1,'C',0,'2026-07-29 09:26:00','2026-07-29 09:26:00'),
('elder_005','dev_012','heart_rate',92.0,'bpm',0,'2026-07-30 09:25:00','2026-07-30 09:25:00'),
('elder_005','dev_012','spo2',90.0,'%',0,'2026-07-30 09:25:00','2026-07-30 09:25:00'),
('elder_005','dev_012','blood_pressure_sys',157.0,'mmHg',0,'2026-07-30 09:25:00','2026-07-30 09:25:00'),
('elder_005','dev_012','blood_pressure_dia',90.0,'mmHg',0,'2026-07-30 09:25:00','2026-07-30 09:25:00'),
('elder_005','dev_012','temperature',36.9,'C',0,'2026-07-30 09:25:00','2026-07-30 09:25:00'),
('elder_005','dev_012','heart_rate',109.0,'bpm',0,'2026-07-31 09:13:00','2026-07-31 09:13:00'),
('elder_005','dev_012','spo2',87.0,'%',0,'2026-07-31 09:13:00','2026-07-31 09:13:00'),
('elder_005','dev_012','blood_pressure_sys',159.0,'mmHg',0,'2026-07-31 09:13:00','2026-07-31 09:13:00'),
('elder_005','dev_012','blood_pressure_dia',95.0,'mmHg',0,'2026-07-31 09:13:00','2026-07-31 09:13:00'),
('elder_005','dev_012','temperature',37.0,'C',0,'2026-07-31 09:13:00','2026-07-31 09:13:00'),
('elder_005','dev_012','heart_rate',95.0,'bpm',0,'2026-08-01 09:16:00','2026-08-01 09:16:00'),
('elder_005','dev_012','spo2',87.0,'%',0,'2026-08-01 09:16:00','2026-08-01 09:16:00'),
('elder_005','dev_012','blood_pressure_sys',160.0,'mmHg',0,'2026-08-01 09:16:00','2026-08-01 09:16:00'),
('elder_005','dev_012','blood_pressure_dia',90.0,'mmHg',0,'2026-08-01 09:16:00','2026-08-01 09:16:00'),
('elder_005','dev_012','temperature',37.1,'C',0,'2026-08-01 09:16:00','2026-08-01 09:16:00'),
('elder_005','dev_012','heart_rate',93.0,'bpm',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_005','dev_012','spo2',94.0,'%',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_005','dev_012','blood_pressure_sys',150.0,'mmHg',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_005','dev_012','blood_pressure_dia',98.0,'mmHg',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_005','dev_012','temperature',37.5,'C',0,'2026-08-02 09:28:00','2026-08-02 09:28:00'),
('elder_003','dev_007','activity_status',3.0,'',1,'2026-06-27 19:43:13','2026-06-27 19:43:13'),
('elder_003','dev_007','fall_status',1.0,'',0,'2026-07-20 22:55:57','2026-07-20 22:55:57'),
('elder_004','dev_010','activity_status',2.0,'',0,'2026-07-20 08:10:01','2026-07-20 08:10:01'),
('elder_005','dev_012','activity_status',1.0,'',0,'2026-07-28 01:34:29','2026-07-28 01:34:29'),
('elder_005','dev_012','activity_status',1.0,'',0,'2026-07-30 12:43:08','2026-07-30 12:43:08'),
('elder_005','dev_012','activity_status',1.0,'',0,'2026-07-05 04:01:44','2026-07-05 04:01:44'),
('elder_004','dev_010','activity_status',3.0,'',1,'2026-07-31 12:45:20','2026-07-31 12:45:20'),
('elder_003','dev_007','activity_status',1.0,'',0,'2026-07-01 21:34:19','2026-07-01 21:34:19'),
('elder_005','dev_012','activity_status',3.0,'',1,'2026-07-04 20:53:10','2026-07-04 20:53:10'),
('elder_002','dev_004','activity_status',1.0,'',0,'2026-07-07 00:34:09','2026-07-07 00:34:09'),
('elder_005','dev_012','activity_status',2.0,'',0,'2026-06-23 04:10:50','2026-06-23 04:10:50'),
('elder_003','dev_007','fall_status',1.0,'',0,'2026-06-28 05:43:20','2026-06-28 05:43:20'),
('elder_002','dev_004','fall_status',0.0,'',0,'2026-08-04 01:12:04','2026-08-04 01:12:04'),
('elder_003','dev_007','fall_status',1.0,'',0,'2026-06-25 02:02:13','2026-06-25 02:02:13'),
('elder_003','dev_007','fall_status',0.0,'',0,'2026-06-28 14:02:38','2026-06-28 14:02:38'),
('elder_005','dev_012','fall_status',0.0,'',0,'2026-07-17 17:21:08','2026-07-17 17:21:08'),
('elder_001','dev_001','activity_status',2.0,'',0,'2026-07-16 11:17:07','2026-07-16 11:17:07'),
('elder_002','dev_004','activity_status',3.0,'',1,'2026-07-30 09:30:52','2026-07-30 09:30:52'),
('elder_004','dev_010','fall_status',0.0,'',0,'2026-06-22 21:40:30','2026-06-22 21:40:30'),
('elder_001','dev_001','activity_status',1.0,'',0,'2026-07-17 13:57:14','2026-07-17 13:57:14'),
('elder_001','dev_001','activity_status',3.0,'',1,'2026-07-17 04:27:18','2026-07-17 04:27:18'),
('elder_005','dev_012','activity_status',2.0,'',0,'2026-07-25 02:24:05','2026-07-25 02:24:05'),
('elder_004','dev_010','activity_status',3.0,'',1,'2026-06-23 17:36:23','2026-06-23 17:36:23'),
('elder_002','dev_004','fall_status',2.0,'',1,'2026-07-26 14:38:38','2026-07-26 14:38:38'),
('elder_003','dev_007','activity_status',3.0,'',1,'2026-07-21 01:58:37','2026-07-21 01:58:37'),
('elder_005','dev_012','activity_status',1.0,'',0,'2026-06-28 08:29:19','2026-06-28 08:29:19'),
('elder_001','dev_001','activity_status',2.0,'',0,'2026-06-29 05:14:35','2026-06-29 05:14:35'),
('elder_002','dev_004','fall_status',0.0,'',0,'2026-07-26 02:01:26','2026-07-26 02:01:26'),
('elder_005','dev_012','activity_status',1.0,'',0,'2026-07-26 21:39:38','2026-07-26 21:39:38'),
('elder_004','dev_010','activity_status',1.0,'',0,'2026-07-13 23:18:59','2026-07-13 23:18:59'),
('elder_001','dev_001','insomnia',3.0,'',0,'2026-06-24 17:25:58','2026-06-24 17:25:58'),
('elder_001','dev_001','sleep_time',21.07,'',0,'2026-06-24 17:25:58','2026-06-24 17:25:58'),
('elder_001','dev_001','insomnia',0.0,'',0,'2026-08-04 19:19:02','2026-08-04 19:19:02'),
('elder_001','dev_001','sleep_time',22.99,'',0,'2026-08-04 19:19:02','2026-08-04 19:19:02'),
('elder_001','dev_001','insomnia',0.0,'',0,'2026-06-26 15:56:11','2026-06-26 15:56:11'),
('elder_001','dev_001','sleep_time',22.51,'',0,'2026-06-26 15:56:11','2026-06-26 15:56:11'),
('elder_001','dev_001','insomnia',1.0,'',0,'2026-07-17 00:16:06','2026-07-17 00:16:06'),
('elder_001','dev_001','sleep_time',21.64,'',0,'2026-07-17 00:16:06','2026-07-17 00:16:06'),
('elder_002','dev_004','insomnia',3.0,'',0,'2026-07-04 11:59:32','2026-07-04 11:59:32'),
('elder_002','dev_004','sleep_time',23.05,'',0,'2026-07-04 11:59:32','2026-07-04 11:59:32'),
('elder_002','dev_004','insomnia',2.0,'',0,'2026-08-01 10:42:06','2026-08-01 10:42:06'),
('elder_002','dev_004','sleep_time',23.59,'',0,'2026-08-01 10:42:06','2026-08-01 10:42:06'),
('elder_002','dev_004','insomnia',0.0,'',0,'2026-06-21 23:52:36','2026-06-21 23:52:36'),
('elder_002','dev_004','sleep_time',21.3,'',0,'2026-06-21 23:52:36','2026-06-21 23:52:36'),
('elder_002','dev_004','insomnia',3.0,'',0,'2026-08-02 00:41:23','2026-08-02 00:41:23'),
('elder_002','dev_004','sleep_time',21.95,'',0,'2026-08-02 00:41:23','2026-08-02 00:41:23'),
('elder_003','dev_007','insomnia',2.0,'',0,'2026-07-09 05:23:52','2026-07-09 05:23:52'),
('elder_003','dev_007','sleep_time',22.78,'',0,'2026-07-09 05:23:52','2026-07-09 05:23:52'),
('elder_003','dev_007','insomnia',3.0,'',0,'2026-06-23 16:22:58','2026-06-23 16:22:58'),
('elder_003','dev_007','sleep_time',22.64,'',0,'2026-06-23 16:22:58','2026-06-23 16:22:58'),
('elder_003','dev_007','insomnia',3.0,'',0,'2026-07-17 11:34:54','2026-07-17 11:34:54'),
('elder_003','dev_007','sleep_time',22.54,'',0,'2026-07-17 11:34:54','2026-07-17 11:34:54'),
('elder_003','dev_007','insomnia',0.0,'',0,'2026-07-05 08:57:00','2026-07-05 08:57:00'),
('elder_003','dev_007','sleep_time',21.48,'',0,'2026-07-05 08:57:00','2026-07-05 08:57:00'),
('elder_004','dev_010','insomnia',1.0,'',0,'2026-06-22 09:17:44','2026-06-22 09:17:44'),
('elder_004','dev_010','sleep_time',21.6,'',0,'2026-06-22 09:17:44','2026-06-22 09:17:44'),
('elder_004','dev_010','insomnia',1.0,'',0,'2026-07-15 17:02:51','2026-07-15 17:02:51'),
('elder_004','dev_010','sleep_time',22.81,'',0,'2026-07-15 17:02:51','2026-07-15 17:02:51'),
('elder_004','dev_010','insomnia',2.0,'',0,'2026-08-02 01:40:37','2026-08-02 01:40:37'),
('elder_004','dev_010','sleep_time',23.42,'',0,'2026-08-02 01:40:37','2026-08-02 01:40:37'),
('elder_004','dev_010','insomnia',3.0,'',0,'2026-07-14 00:52:34','2026-07-14 00:52:34'),
('elder_004','dev_010','sleep_time',21.11,'',0,'2026-07-14 00:52:34','2026-07-14 00:52:34'),
('elder_005','dev_012','insomnia',1.0,'',0,'2026-08-02 10:49:22','2026-08-02 10:49:22'),
('elder_005','dev_012','sleep_time',22.59,'',0,'2026-08-02 10:49:22','2026-08-02 10:49:22'),
('elder_005','dev_012','insomnia',2.0,'',0,'2026-07-25 21:43:05','2026-07-25 21:43:05'),
('elder_005','dev_012','sleep_time',21.28,'',0,'2026-07-25 21:43:05','2026-07-25 21:43:05'),
('elder_005','dev_012','insomnia',2.0,'',0,'2026-08-03 22:31:29','2026-08-03 22:31:29'),
('elder_005','dev_012','sleep_time',21.74,'',0,'2026-08-03 22:31:29','2026-08-03 22:31:29'),
('elder_005','dev_012','insomnia',3.0,'',0,'2026-06-27 23:30:03','2026-06-27 23:30:03'),
('elder_005','dev_012','sleep_time',23.43,'',0,'2026-06-27 23:30:03','2026-06-27 23:30:03'),
('elder_001','dev_001','heart_rate',72,'bpm',0,'2025-05-17 08:00:00','2025-05-17 08:00:00'),
('elder_001','dev_001','heart_rate',85,'bpm',0,'2025-05-17 09:00:00','2025-05-17 09:00:00'),
('elder_001','dev_001','heart_rate',95,'bpm',1,'2025-05-17 09:30:00','2025-05-17 09:30:00'),
('elder_001','dev_001','temperature',36.5,'C',0,'2025-05-17 09:00:00','2025-05-17 09:00:00'),
('elder_001','dev_001','spo2',98,'%',0,'2025-05-17 09:00:00','2025-05-17 09:00:00'),
('elder_002','dev_004','heart_rate',68,'bpm',0,'2025-05-17 07:30:00','2025-05-17 07:30:00'),
('elder_002','dev_004','heart_rate',92,'bpm',1,'2025-05-17 09:20:00','2025-05-17 09:20:00'),
('elder_002','dev_004','temperature',36.8,'C',0,'2025-05-17 09:20:00','2025-05-17 09:20:00'),
('elder_002','dev_004','spo2',96,'%',0,'2025-05-17 09:20:00','2025-05-17 09:20:00'),
('elder_003','dev_007','heart_rate',70,'bpm',0,'2025-05-17 08:00:00','2025-05-17 08:00:00'),
('elder_003','dev_007','temperature',36.4,'C',0,'2025-05-17 08:00:00','2025-05-17 08:00:00'),
('elder_003','dev_007','spo2',97,'%',0,'2025-05-17 08:00:00','2025-05-17 08:00:00'),
('elder_004','dev_010','heart_rate',65,'bpm',0,'2025-05-17 08:00:00','2025-05-17 08:00:00'),
('elder_004','dev_010','spo2',99,'%',0,'2025-05-17 08:00:00','2025-05-17 08:00:00'),
('elder_005','dev_012','heart_rate',110,'bpm',1,'2025-05-16 23:00:00','2025-05-16 23:00:00'),
('elder_005','dev_012','spo2',94,'%',1,'2025-05-16 23:00:00','2025-05-16 23:00:00');

-- agent_conversation: 25
INSERT INTO `agent_conversation` (`conversation_id`,`elder_id`,`agent_type`,`user_text`,`intent`,`agent_reply`,`risk_level`,`created_at`) VALUES
('conv_bbf43454','elder_005','local_agent','我好像摔倒了...','fall_alert','已接收跌倒警报，工作人员正在赶来。','high','2026-07-28 16:48:05'),

('conv_5b207f0a','elder_005','cloud_agent','我有点胸闷，喘不上气。','report_discomfort','已为您联系医护人员，请保持冷静。','critical','2026-07-08 03:49:36'),

('conv_fe19b2be','elder_005','local_agent','把窗帘拉开一点。','curtain-control','好的，已为您调整窗帘。','high','2026-06-10 13:58:04'),

('conv_0c853c7a','elder_002','cloud_agent','我好像摔倒了...','fall_alert','已接收跌倒警报，工作人员正在赶来。','critical','2026-07-15 03:52:53'),

('conv_4b9a6082','elder_002','cloud_agent','给我放点音乐吧。','control_music','正在为您播放轻音乐。','low','2026-06-09 09:02:45'),

('conv_6d177537','elder_001','local_agent','明天会下雨吗？','weather_query','明天多云，气温22-28度。','high','2026-07-20 14:12:06'),

('conv_c334049b','elder_005','cloud_agent','今天天气怎么样？','chat','今天天气不错，适合出门散步。','low','2026-07-09 13:03:02'),

('conv_445d26bc','elder_003','cloud_agent','救命！我不舒服！','emergency','已收到紧急呼叫，正在通知家属！','low','2026-07-23 03:09:04'),

('conv_b318439f','elder_003','local_agent','今天血压怎么样？','query_health','您今天血压偏高，记得按时服药。','high','2026-06-14 03:07:21'),

('conv_158453fc','elder_002','cloud_agent','把窗帘拉开一点。','curtain-control','好的，已为您调整窗帘。','low','2026-06-12 05:38:12'),

('conv_ed1e0b2e','elder_001','cloud_agent','我的老花镜找不到了。','find-item','已启动摄像头帮您查找。','low','2026-07-08 14:33:55'),

('conv_bc03c560','elder_005','local_agent','今天天气怎么样？','chat','今天天气不错，适合出门散步。','low','2026-06-14 13:34:27'),

('conv_9801041d','elder_004','cloud_agent','把窗帘拉开一点。','curtain-control','好的，已为您调整窗帘。','high','2026-07-13 03:19:33'),

('conv_c4be17b5','elder_005','local_agent','我有点胸闷，喘不上气。','report_discomfort','已为您联系医护人员，请保持冷静。','high','2026-07-17 23:44:31'),

('conv_2ce53f70','elder_001','local_agent','救命！我不舒服！','emergency','已收到紧急呼叫，正在通知家属！','medium','2026-06-12 11:22:25'),

('conv_97e17135','elder_005','cloud_agent','我的老花镜找不到了。','find-item','已启动摄像头帮您查找。','medium','2026-06-25 01:44:08'),

('conv_ddacc403','elder_004','cloud_agent','我好像摔倒了...','fall_alert','已接收跌倒警报，工作人员正在赶来。','low','2026-06-26 03:32:56'),

('conv_1caafff5','elder_003','cloud_agent','我的老花镜找不到了。','find-item','已启动摄像头帮您查找。','critical','2026-07-03 10:22:05'),

('conv_a558e55b','elder_005','cloud_agent','把窗帘拉开一点。','curtain-control','好的，已为您调整窗帘。','medium','2026-07-03 07:50:10'),

('conv_0bdc647e','elder_001','local_agent','给我放点音乐吧。','control_music','正在为您播放轻音乐。','medium','2026-06-17 20:58:33'),

('conv_02145bf5','elder_003','cloud_agent','明天会下雨吗？','weather_query','明天多云，气温22-28度。','low','2026-07-23 02:30:34'),

('conv_83eb367b','elder_005','cloud_agent','救命！我不舒服！','emergency','已收到紧急呼叫，正在通知家属！','high','2026-06-13 06:17:15'),

('conv_c0c634ea','elder_003','cloud_agent','给我放点音乐吧。','control_music','正在为您播放轻音乐。','low','2026-07-03 18:28:34'),

('conv_e9462dfc','elder_002','local_agent','明天会下雨吗？','weather_query','明天多云，气温22-28度。','critical','2026-07-14 09:38:07'),

('conv_aa6bf025','elder_002','local_agent','救命！我不舒服！','emergency','已收到紧急呼叫，正在通知家属！','low','2026-08-03 03:23:53');

-- agent_intent_log: 25
INSERT INTO `agent_intent_log` (`intent_id`,`elder_id`,`source`,`user_text`,`intent`,`confidence`,`handled_by`,`created_at`) VALUES
('intent_ce1ab6c8','elder_001','voice','帮我把灯打开。','light-control',0.98,'local_agent','2026-07-23 14:59:28'),

('intent_240acb9c','elder_001','voice','明天会下雨吗？','weather_query',0.83,'local_agent','2026-07-05 13:39:32'),

('intent_26dcd0af','elder_003','voice','给我放点音乐吧。','control_music',0.81,'local_agent','2026-07-28 04:15:58'),

('intent_1ff39f57','elder_005','voice','我有点胸闷，喘不上气。','report_discomfort',0.85,'cloud_agent','2026-08-01 22:41:31'),

('intent_42345064','elder_004','voice','今天血压怎么样？','query_health',0.85,'cloud_agent','2026-07-22 13:22:13'),

('intent_e4fb8c70','elder_001','voice','帮我把灯打开。','light-control',0.79,'local_agent','2026-07-04 08:54:18'),

('intent_803bc568','elder_002','voice','救命！我不舒服！','emergency',0.95,'local_agent','2026-07-04 03:30:58'),

('intent_a940ee95','elder_001','app','帮我把灯打开。','light-control',0.81,'cloud_agent','2026-07-06 00:50:00'),

('intent_0c434461','elder_004','app','把窗帘拉开一点。','curtain-control',0.82,'local_agent','2026-08-01 07:22:03'),

('intent_543dabc9','elder_005','app','我好像摔倒了...','fall_alert',0.92,'cloud_agent','2026-06-15 07:20:53'),

('intent_49ace1d3','elder_001','voice','救命！我不舒服！','emergency',0.78,'cloud_agent','2026-07-18 00:44:23'),

('intent_96a63110','elder_001','voice','我有点胸闷，喘不上气。','report_discomfort',0.77,'local_agent','2026-06-21 01:43:38'),

('intent_015a05b2','elder_004','voice','今天天气怎么样？','chat',0.79,'cloud_agent','2026-07-30 11:33:21'),

('intent_3f423804','elder_001','app','我有点胸闷，喘不上气。','report_discomfort',0.86,'cloud_agent','2026-07-21 05:25:51'),

('intent_259579fc','elder_002','voice','把窗帘拉开一点。','curtain-control',0.77,'cloud_agent','2026-07-05 16:41:11'),

('intent_7723ab5b','elder_003','app','给我放点音乐吧。','control_music',0.86,'local_agent','2026-07-02 07:45:59'),

('intent_d6e97b6e','elder_002','voice','今天天气怎么样？','chat',0.79,'local_agent','2026-06-26 03:00:36'),

('intent_1ba3d0ba','elder_004','voice','我有点胸闷，喘不上气。','report_discomfort',0.86,'cloud_agent','2026-07-05 03:52:41'),

('intent_4b3b844e','elder_001','voice','把窗帘拉开一点。','curtain-control',0.95,'cloud_agent','2026-08-03 04:59:55'),

('intent_d4ffb561','elder_004','voice','我的老花镜找不到了。','find-item',0.77,'cloud_agent','2026-07-06 23:28:02'),

('intent_da78a103','elder_004','voice','明天会下雨吗？','weather_query',0.93,'cloud_agent','2026-07-04 14:56:10'),

('intent_78e215ec','elder_005','app','我的老花镜找不到了。','find-item',0.91,'cloud_agent','2026-07-17 02:15:54'),

('intent_344e5526','elder_003','voice','救命！我不舒服！','emergency',0.82,'local_agent','2026-07-31 07:31:12'),

('intent_d8446388','elder_005','voice','明天会下雨吗？','weather_query',0.81,'local_agent','2026-06-13 15:28:28'),

('intent_13ee59a5','elder_001','voice','我的老花镜找不到了。','find-item',0.82,'local_agent','2026-07-17 22:09:20');

-- ai_advice: 20
INSERT INTO `ai_advice` (`advice_id`,`elder_id`,`advice_type`,`input_summary`,`advice_content`,`created_at`) VALUES
('adv_3440f2eb','elder_003','cognitive','记忆力减退，轻度认知障碍','建议进行认知训练，保持社交活动；规律作息；定期复查认知功能。','2026-07-30 09:07:55'),

('adv_0319ff6a','elder_003','rehabilitation','脑梗后遗症，行动不便','建议坚持每日康复训练；家属协助进行肢体活动；定期复查脑部影像。','2026-06-08 22:47:17'),

('adv_6346d477','elder_002','health','近期血压偏高，血糖波动较大','建议增加血压监测频率，每日早晚各一次；调整饮食结构，减少盐分摄入。','2026-08-01 07:31:41'),

('adv_fab6b52b','elder_001','rehabilitation','脑梗后遗症，行动不便','建议坚持每日康复训练；家属协助进行肢体活动；定期复查脑部影像。','2026-08-03 20:15:13'),

('adv_eacc43f9','elder_004','health','近期血压偏高，血糖波动较大','建议增加血压监测频率，每日早晚各一次；调整饮食结构，减少盐分摄入。','2026-06-13 10:32:22'),

('adv_afe41f41','elder_003','hypertension','血压持续偏高，需长期管理','建议低盐低脂饮食，每日限盐；监测早晚血压并记录；避免情绪激动。','2026-06-05 12:45:02'),

('adv_94a7c377','elder_004','fall_prevention','近期有跌倒记录，骨质疏松','建议在卫生间和卧室安装防滑垫；夜间保持走廊照明；穿戴防滑鞋。','2026-05-20 06:59:08'),

('adv_1f53f20a','elder_003','diabetes','糖尿病恢复期，血糖控制','建议每日检查足部皮肤状况；保持足部清洁干燥；定期测量血糖。','2026-05-12 05:11:12'),

('adv_43ab1f98','elder_001','insomnia','近期睡眠质量差，入睡困难','建议睡前避免使用电子设备；保持卧室安静；尝试热水泡脚、听轻音乐。','2026-05-20 14:01:03'),

('adv_5a24a692','elder_005','anxiety','情绪焦虑，心率偏快','建议家属增加陪伴沟通频次；社区可安排心理疏导；适当听舒缓音乐。','2026-07-02 18:27:38'),

('adv_51e7c7d7','elder_005','diabetes','糖尿病恢复期，血糖控制','建议每日检查足部皮肤状况；保持足部清洁干燥；定期测量血糖。','2026-05-26 03:41:00'),

('adv_7966f223','elder_001','diabetes','糖尿病恢复期，血糖控制','建议每日检查足部皮肤状况；保持足部清洁干燥；定期测量血糖。','2026-06-11 07:05:35'),

('adv_a611d45b','elder_004','health','近期血压偏高，血糖波动较大','建议增加血压监测频率，每日早晚各一次；调整饮食结构，减少盐分摄入。','2026-05-31 03:27:31'),

('adv_4039fafc','elder_004','fall_prevention','近期有跌倒记录，骨质疏松','建议在卫生间和卧室安装防滑垫；夜间保持走廊照明；穿戴防滑鞋。','2026-07-04 00:01:24'),

('adv_371dc784','elder_005','hypertension','血压持续偏高，需长期管理','建议低盐低脂饮食，每日限盐；监测早晚血压并记录；避免情绪激动。','2026-06-08 07:14:18'),

('adv_875c2b1b','elder_001','rehabilitation','脑梗后遗症，行动不便','建议坚持每日康复训练；家属协助进行肢体活动；定期复查脑部影像。','2026-05-26 08:57:07'),

('adv_212ee1a9','elder_002','diabetes','糖尿病恢复期，血糖控制','建议每日检查足部皮肤状况；保持足部清洁干燥；定期测量血糖。','2026-06-27 10:55:16'),

('adv_e7d9424a','elder_005','health','近期血压偏高，血糖波动较大','建议增加血压监测频率，每日早晚各一次；调整饮食结构，减少盐分摄入。','2026-06-17 07:49:11'),

('adv_c7f402bb','elder_004','health','近期血压偏高，血糖波动较大','建议增加血压监测频率，每日早晚各一次；调整饮食结构，减少盐分摄入。','2026-07-04 10:09:56'),

('adv_947fa3c3','elder_004','hypertension','血压持续偏高，需长期管理','建议低盐低脂饮食，每日限盐；监测早晚血压并记录；避免情绪激动。','2026-06-02 00:40:40');

-- ai_service_record: 25
INSERT INTO `ai_service_record` (`record_id`,`elder_id`,`service_type`,`user_text`,`ai_reply`,`emotion`,`emotion_color`,`item`,`location`,`result`,`summary`,`music_type`,`interaction_time`,`created_at`) VALUES
('aisr_c17d00ae','elder_002','companion_chat',NULL,NULL,低落,#607D8B,NULL,NULL,NULL,老人与AI聊天，情绪低落,NULL,'2026-06-17 03:36:51','2026-06-17 03:36:51'),

('aisr_5104ef57','elder_004','find_item',我的钥匙在哪？,钥匙在玄关,NULL,NULL,钥匙,玄关,found,NULL,NULL,'2026-07-23 02:20:20','2026-07-23 02:20:20'),

('aisr_5b22f73b','elder_002','music_control',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,轻音乐,'2026-07-13 02:23:36','2026-07-13 02:23:36'),

('aisr_ed5989da','elder_002','music_control',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,民谣,'2026-07-18 18:17:17','2026-07-18 18:17:17'),

('aisr_2cbc0bde','elder_003','find_item',我的钥匙在哪？,钥匙在玄关,NULL,NULL,钥匙,玄关,found,NULL,NULL,'2026-08-01 22:27:49','2026-08-01 22:27:49'),

('aisr_a749d127','elder_005','find_item',我的手机在哪？,手机在枕头下,NULL,NULL,手机,枕头下,found,NULL,NULL,'2026-06-15 16:41:58','2026-06-15 16:41:58'),

('aisr_ef7b8b1b','elder_001','companion_chat',NULL,NULL,思念,#795548,NULL,NULL,NULL,老人与AI聊天，情绪思念,NULL,'2026-06-28 03:38:19','2026-06-28 03:38:19'),

('aisr_5e0f0b4c','elder_005','companion_chat',NULL,NULL,担心,#FF5722,NULL,NULL,NULL,老人与AI聊天，情绪担心,NULL,'2026-06-20 04:57:02','2026-06-20 04:57:02'),

('aisr_98dcf318','elder_002','companion_chat',NULL,NULL,开心,#4CAF50,NULL,NULL,NULL,老人与AI聊天，情绪开心,NULL,'2026-08-04 01:03:20','2026-08-04 01:03:20'),

('aisr_6c152006','elder_005','find_item',我的眼镜在哪？,眼镜在客厅茶几,NULL,NULL,眼镜,客厅茶几,found,NULL,NULL,'2026-06-10 14:27:38','2026-06-10 14:27:38'),

('aisr_5a88eb9b','elder_001','companion_chat',NULL,NULL,疲惫,#795548,NULL,NULL,NULL,老人与AI聊天，情绪疲惫,NULL,'2026-07-31 19:30:27','2026-07-31 19:30:27'),

('aisr_7bcc3989','elder_001','companion_chat',NULL,NULL,烦躁,#FF5722,NULL,NULL,NULL,老人与AI聊天，情绪烦躁,NULL,'2026-08-04 16:35:15','2026-08-04 16:35:15'),

('aisr_26e3ef00','elder_004','music_control',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,戏曲,'2026-07-18 17:07:41','2026-07-18 17:07:41'),

('aisr_c7ffa64a','elder_004','music_control',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,民谣,'2026-07-08 13:56:36','2026-07-08 13:56:36'),

('aisr_06d0e760','elder_001','find_item',我的老花镜在哪？,老花镜在电视柜,NULL,NULL,老花镜,电视柜,found,NULL,NULL,'2026-06-29 20:48:44','2026-06-29 20:48:44'),

('aisr_fd9f6de8','elder_005','find_item',我的手机在哪？,手机在枕头下,NULL,NULL,手机,枕头下,found,NULL,NULL,'2026-07-26 09:05:43','2026-07-26 09:05:43'),

('aisr_cc76080f','elder_005','find_item',我的钥匙在哪？,钥匙在玄关,NULL,NULL,钥匙,玄关,found,NULL,NULL,'2026-06-21 13:17:39','2026-06-21 13:17:39'),

('aisr_cfc72403','elder_001','find_item',我的钥匙在哪？,钥匙在玄关,NULL,NULL,钥匙,玄关,found,NULL,NULL,'2026-06-27 10:20:49','2026-06-27 10:20:49'),

('aisr_3862afb2','elder_004','find_item',我的药盒在哪？,抱歉，暂未找到药盒,NULL,NULL,药盒,,not_found,NULL,NULL,'2026-06-25 02:39:21','2026-06-25 02:39:21'),

('aisr_2b2211ac','elder_002','companion_chat',NULL,NULL,焦虑,#FF9800,NULL,NULL,NULL,老人与AI聊天，情绪焦虑,NULL,'2026-06-27 00:33:10','2026-06-27 00:33:10'),

('aisr_60ac5e5b','elder_002','music_control',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,轻音乐,'2026-06-18 16:32:18','2026-06-18 16:32:18'),

('aisr_c050bc8a','elder_002','companion_chat',NULL,NULL,开心,#4CAF50,NULL,NULL,NULL,老人与AI聊天，情绪开心,NULL,'2026-07-10 01:24:55','2026-07-10 01:24:55'),

('aisr_c130f56b','elder_003','music_control',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,戏曲,'2026-08-02 12:10:24','2026-08-02 12:10:24'),

('aisr_7eb05db8','elder_003','music_control',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,戏曲,'2026-06-13 16:58:50','2026-06-13 16:58:50'),

('aisr_15930778','elder_001','music_control',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,红歌,'2026-06-16 15:13:03','2026-06-16 15:13:03');

-- alarm_event: 40
INSERT INTO `alarm_event` (`alarm_id`,`elder_id`,`device_id`,`alarm_type`,`alarm_level`,`alarm_status`,`description`,`building`,`room_number`,`unit`,`snapshot_url`,`handler`,`handler_name`,`handle_remark`,`is_read`,`created_at`,`resolved_at`,`update_time`,`location`) VALUES
('alarm_10946089','elder_003','dev_007','intrusion','low','handled','客厅检测到陌生人闯入','3号楼','3-401','3单元','','','','',1,'2026-07-24 02:00:21','1970-01-01 00:00:00','2026-07-24 02:00:21','3-401'),

('alarm_fb2c7ec6','elder_004','dev_010','sos','medium','handled','老人触发紧急呼救按钮','4号楼','4-102','4单元','','','','',1,'2026-07-19 04:50:11','1970-01-01 00:00:00','2026-07-19 04:50:11','4-102'),

('alarm_f3973113','elder_001','dev_001','sos','low','handled','老人触发紧急呼救按钮','1号楼','1-201','1单元','','staff_001','张建国','已处理',1,'2026-05-09 05:41:56','1970-01-01 00:00:00','2026-05-09 05:41:56','1-201'),

('alarm_0640bde2','elder_002','dev_004','fingerprint-fail','critical','handled','指纹连续验证失败3次','2号楼','2-301','2单元','','staff_003','王强','已处理',1,'2026-05-08 08:31:09','1970-01-01 00:00:00','2026-05-08 08:31:09','2-301'),

('alarm_e52bb55f','elder_001','dev_001','inactive','medium','handled','老人超过6小时未检测到活动','1号楼','1-201','1单元','','staff_003','王强','已处理',1,'2026-07-27 02:23:57','1970-01-01 00:00:00','2026-07-27 02:23:57','1-201'),

('alarm_b95b67d1','elder_005','dev_012','intrusion','high','pending','客厅检测到陌生人闯入','5号楼','5-502','5单元','','','','',0,'2026-06-11 21:35:40','1970-01-01 00:00:00','2026-06-11 21:35:40','5-502'),

('alarm_2c253041','elder_004','dev_010','inactive','medium','handled','老人超过6小时未检测到活动','4号楼','4-102','4单元','','staff_001','张建国','已处理',1,'2026-06-01 07:22:56','1970-01-01 00:00:00','2026-06-01 07:22:56','4-102'),

('alarm_fc27ecc9','elder_005','dev_012','door_lock','medium','handled','门锁检测到异常开锁尝试','5号楼','5-502','5单元','','staff_003','王强','已处理',1,'2026-07-15 14:35:27','1970-01-01 00:00:00','2026-07-15 14:35:27','5-502'),

('alarm_d8db10c2','elder_005','dev_012','health_abnormal','critical','handled','心率持续偏高，超出正常范围','5号楼','5-502','5单元','','','','',1,'2026-06-12 22:42:53','1970-01-01 00:00:00','2026-06-12 22:42:53','5-502'),

('alarm_38bfd67f','elder_003','dev_007','intrusion','high','handled','客厅检测到陌生人闯入','3号楼','3-401','3单元','','staff_002','李秀英','已处理',1,'2026-07-13 15:39:25','1970-01-01 00:00:00','2026-07-13 15:39:25','3-401'),

('alarm_537e6326','elder_003','dev_007','emergency-call','medium','pending','智能手表自动检测跌倒并SOS','3号楼','3-401','3单元','','','','',0,'2026-05-15 15:26:00','1970-01-01 00:00:00','2026-05-15 15:26:00','3-401'),

('alarm_bbc7b649','elder_003','dev_007','emergency-call','critical','pending','智能手表自动检测跌倒并SOS','3号楼','3-401','3单元','','','','',0,'2026-07-29 14:43:56','1970-01-01 00:00:00','2026-07-29 14:43:56','3-401'),

('alarm_aa650c4d','elder_002','dev_004','health_abnormal','low','handled','心率持续偏高，超出正常范围','2号楼','2-301','2单元','','staff_001','张建国','已处理',1,'2026-06-28 11:34:35','1970-01-01 00:00:00','2026-06-28 11:34:35','2-301'),

('alarm_a74de0be','elder_003','dev_007','inactive','low','handled','老人超过6小时未检测到活动','3号楼','3-401','3单元','','staff_002','李秀英','已处理',1,'2026-07-28 05:01:48','1970-01-01 00:00:00','2026-07-28 05:01:48','3-401'),

('alarm_adb033b4','elder_001','dev_001','fall','high','handled','传感器检测到疑似跌倒行为','1号楼','1-201','1单元','','staff_003','王强','已处理',1,'2026-07-01 18:28:11','1970-01-01 00:00:00','2026-07-01 18:28:11','1-201'),

('alarm_baaeaf19','elder_004','dev_010','smoke','high','handled','厨房烟雾浓度超标','4号楼','4-102','4单元','','','','',1,'2026-07-10 04:42:04','1970-01-01 00:00:00','2026-07-10 04:42:04','4-102'),

('alarm_a72d1166','elder_001','dev_001','inactive','low','handled','老人超过6小时未检测到活动','1号楼','1-201','1单元','','staff_002','李秀英','已处理',1,'2026-05-21 05:13:24','1970-01-01 00:00:00','2026-05-21 05:13:24','1-201'),

('alarm_2925c698','elder_005','dev_012','emergency-call','high','handled','智能手表自动检测跌倒并SOS','5号楼','5-502','5单元','','staff_001','张建国','已处理',1,'2026-06-01 05:17:31','1970-01-01 00:00:00','2026-06-01 05:17:31','5-502'),

('alarm_5c163bb5','elder_001','dev_001','sos','medium','handled','老人触发紧急呼救按钮','1号楼','1-201','1单元','','staff_002','李秀英','已处理',1,'2026-05-27 07:56:30','1970-01-01 00:00:00','2026-05-27 07:56:30','1-201'),

('alarm_af280184','elder_003','dev_007','fingerprint-fail','critical','handled','指纹连续验证失败3次','3号楼','3-401','3单元','','staff_001','张建国','已处理',1,'2026-07-25 16:18:21','1970-01-01 00:00:00','2026-07-25 16:18:21','3-401'),

('alarm_2e13eed8','elder_003','dev_007','health_abnormal','low','handled','心率持续偏高，超出正常范围','3号楼','3-401','3单元','','staff_001','张建国','已处理',1,'2026-06-11 18:02:04','1970-01-01 00:00:00','2026-06-11 18:02:04','3-401'),

('alarm_c19b41b0','elder_003','dev_007','fingerprint-fail','high','handled','指纹连续验证失败3次','3号楼','3-401','3单元','','staff_002','李秀英','已处理',1,'2026-07-20 02:09:58','1970-01-01 00:00:00','2026-07-20 02:09:58','3-401'),

('alarm_e23bd272','elder_002','dev_004','fall','medium','handled','传感器检测到疑似跌倒行为','2号楼','2-301','2单元','','staff_001','张建国','已处理',1,'2026-07-29 02:48:57','1970-01-01 00:00:00','2026-07-29 02:48:57','2-301'),

('alarm_a06ef100','elder_002','dev_004','fingerprint-fail','low','handled','指纹连续验证失败3次','2号楼','2-301','2单元','','staff_001','张建国','已处理',1,'2026-07-19 18:55:51','1970-01-01 00:00:00','2026-07-19 18:55:51','2-301'),

('alarm_5745d393','elder_003','dev_007','intrusion','critical','pending','客厅检测到陌生人闯入','3号楼','3-401','3单元','','','','',0,'2026-05-27 20:46:15','1970-01-01 00:00:00','2026-05-27 20:46:15','3-401'),

('alarm_d514c60b','elder_005','dev_012','intrusion','high','handled','客厅检测到陌生人闯入','5号楼','5-502','5单元','','staff_003','王强','已处理',1,'2026-07-21 23:34:06','1970-01-01 00:00:00','2026-07-21 23:34:06','5-502'),

('alarm_0c22afc3','elder_004','dev_010','smoke','high','handled','厨房烟雾浓度超标','4号楼','4-102','4单元','','staff_002','李秀英','已处理',1,'2026-07-19 19:04:08','1970-01-01 00:00:00','2026-07-19 19:04:08','4-102'),

('alarm_a9fbbfd6','elder_003','dev_007','fall','critical','pending','传感器检测到疑似跌倒行为','3号楼','3-401','3单元','','','','',0,'2026-06-05 19:55:34','1970-01-01 00:00:00','2026-06-05 19:55:34','3-401'),

('alarm_0c054316','elder_002','dev_004','emergency-call','critical','handled','智能手表自动检测跌倒并SOS','2号楼','2-301','2单元','','staff_003','王强','已处理',1,'2026-07-20 13:58:58','1970-01-01 00:00:00','2026-07-20 13:58:58','2-301'),

('alarm_18aafc76','elder_004','dev_010','emergency-call','low','pending','智能手表自动检测跌倒并SOS','4号楼','4-102','4单元','','','','',0,'2026-05-14 15:06:12','1970-01-01 00:00:00','2026-05-14 15:06:12','4-102'),

('alarm_3831df0b','elder_004','dev_010','health_abnormal','medium','handled','心率持续偏高，超出正常范围','4号楼','4-102','4单元','','staff_002','李秀英','已处理',1,'2026-05-21 22:45:13','1970-01-01 00:00:00','2026-05-21 22:45:13','4-102'),

('alarm_58723ff0','elder_004','dev_010','door_lock','low','handled','门锁检测到异常开锁尝试','4号楼','4-102','4单元','','staff_001','张建国','已处理',1,'2026-07-10 15:36:56','1970-01-01 00:00:00','2026-07-10 15:36:56','4-102'),

('alarm_7c2164e1','elder_002','dev_004','health_abnormal','low','handled','心率持续偏高，超出正常范围','2号楼','2-301','2单元','','','','',1,'2026-05-17 13:01:47','1970-01-01 00:00:00','2026-05-17 13:01:47','2-301'),

('alarm_0ca3b6f6','elder_005','dev_012','door_lock','low','handled','门锁检测到异常开锁尝试','5号楼','5-502','5单元','','staff_002','李秀英','已处理',1,'2026-07-27 04:43:56','1970-01-01 00:00:00','2026-07-27 04:43:56','5-502'),

('alarm_8952d46c','elder_004','dev_010','intrusion','critical','handled','客厅检测到陌生人闯入','4号楼','4-102','4单元','','staff_001','张建国','已处理',1,'2026-07-27 03:25:39','1970-01-01 00:00:00','2026-07-27 03:25:39','4-102'),

('alarm_12e0d058','elder_002','dev_004','fingerprint-fail','high','handled','指纹连续验证失败3次','2号楼','2-301','2单元','','staff_002','李秀英','已处理',1,'2026-07-18 10:08:12','1970-01-01 00:00:00','2026-07-18 10:08:12','2-301'),

('alarm_0ede537f','elder_004','dev_010','door_lock','medium','handled','门锁检测到异常开锁尝试','4号楼','4-102','4单元','','staff_002','李秀英','已处理',1,'2026-07-14 17:03:20','1970-01-01 00:00:00','2026-07-14 17:03:20','4-102'),

('alarm_c6897ed0','elder_002','dev_004','door_lock','medium','handled','门锁检测到异常开锁尝试','2号楼','2-301','2单元','','staff_001','张建国','已处理',1,'2026-07-16 03:19:46','1970-01-01 00:00:00','2026-07-16 03:19:46','2-301'),

('alarm_b2601d4d','elder_005','dev_012','inactive','critical','handled','老人超过6小时未检测到活动','5号楼','5-502','5单元','','','','',1,'2026-07-12 13:05:41','1970-01-01 00:00:00','2026-07-12 13:05:41','5-502'),

('alarm_ab3b2913','elder_003','dev_007','intrusion','high','handled','客厅检测到陌生人闯入','3号楼','3-401','3单元','','staff_002','李秀英','已处理',1,'2026-05-13 07:33:23','1970-01-01 00:00:00','2026-05-13 07:33:23','3-401');

-- chat_records: 25
INSERT INTO `chat_records` (`user_id`,`date`,`message`,`emotion`,`created_at`) VALUES
('elder_003','2026-07-15','今天做了康复训练','开心','2026-07-02 04:02:14'),

('elder_005','2026-07-24','今天和邻居聊天很开心','开心','2026-07-06 00:07:31'),

('elder_003','2026-06-21','想吃家乡的菜了','思念','2026-07-04 09:47:25'),

('elder_001','2026-07-03','感觉心跳有点快','焦虑','2026-06-29 03:40:19'),

('elder_003','2026-07-08','昨晚没睡好，翻来覆去','焦虑','2026-07-24 11:25:34'),

('elder_003','2026-06-30','昨晚没睡好，翻来覆去','焦虑','2026-06-28 06:57:59'),

('elder_004','2026-07-27','有点喘不上气','焦虑','2026-07-27 01:22:17'),

('elder_002','2026-07-08','昨晚没睡好，翻来覆去','焦虑','2026-07-11 20:01:50'),

('elder_003','2026-07-21','今天天气不错，心情蛮好的','开心','2026-08-02 21:13:13'),

('elder_005','2026-06-20','有点想孩子们了','思念','2026-06-28 06:59:48'),

('elder_002','2026-07-12','有点想孩子们了','思念','2026-07-06 14:36:28'),

('elder_003','2026-07-11','孙子给我打电话了','开心','2026-06-20 22:37:04'),

('elder_005','2026-06-25','今天天气不错，心情蛮好的','开心','2026-07-06 09:36:26'),

('elder_003','2026-07-16','有点喘不上气','焦虑','2026-07-01 14:32:33'),

('elder_002','2026-08-03','下雨天腿疼','低落','2026-06-29 01:53:02'),

('elder_005','2026-07-11','药按时吃了','平静','2026-07-18 04:52:08'),

('elder_002','2026-07-17','孙子给我打电话了','开心','2026-07-17 18:44:06'),

('elder_001','2026-07-15','身体不太舒服，有点担心','焦虑','2026-07-02 16:27:34'),

('elder_002','2026-06-29','孙子给我打电话了','开心','2026-07-23 03:47:14'),

('elder_002','2026-07-12','有点想孩子们了','思念','2026-07-04 01:46:23'),

('elder_002','2026-07-05','孙子给我打电话了','开心','2026-08-01 23:32:49'),

('elder_003','2026-06-27','今天和邻居聊天很开心','开心','2026-07-24 23:22:45'),

('elder_003','2026-07-26','今天和邻居聊天很开心','开心','2026-08-01 02:22:42'),

('elder_001','2026-06-26','想吃家乡的菜了','思念','2026-06-24 11:09:47'),

('elder_005','2026-06-29','今天血压有点高','担心','2026-07-21 07:20:21');

-- music_logs: 25
INSERT INTO `music_logs` (`user_id`,`date`,`song_name`,`duration_minutes`,`scene`,`created_at`) VALUES
('elder_001','2026-07-15','二泉映月',28,'安抚','2026-07-27 10:44:12'),

('elder_004','2026-06-19','月光',41,'娱乐','2026-07-27 14:05:50'),

('elder_002','2026-07-31','渔舟唱晚',21,'助眠','2026-07-01 09:07:23'),

('elder_003','2026-06-16','甜蜜蜜',10,'助眠','2026-07-01 06:38:45'),

('elder_004','2026-06-26','渔舟唱晚',30,'助眠','2026-07-21 11:47:52'),

('elder_004','2026-07-09','春江花月夜',34,'娱乐','2026-06-11 08:42:33'),

('elder_005','2026-07-25','渔舟唱晚',40,'康复','2026-07-17 03:52:27'),

('elder_003','2026-06-20','春江花月夜',35,'娱乐','2026-06-19 19:59:48'),

('elder_005','2026-06-06','平湖秋月',14,'助眠','2026-07-08 06:02:49'),

('elder_003','2026-07-24','平湖秋月',13,'助眠','2026-07-22 01:19:21'),

('elder_005','2026-06-11','茉莉花',22,'放松','2026-06-20 06:19:40'),

('elder_003','2026-06-08','梅花三弄',26,'放松','2026-07-16 06:30:55'),

('elder_002','2026-06-13','梅花三弄',37,'康复','2026-08-04 16:09:21'),

('elder_001','2026-06-29','茉莉花',12,'放松','2026-06-29 13:32:59'),

('elder_005','2026-06-19','梅花三弄',39,'娱乐','2026-07-27 06:53:38'),

('elder_004','2026-07-27','春江花月夜',31,'娱乐','2026-07-05 10:27:24'),

('elder_002','2026-06-06','京剧选段',27,'安抚','2026-07-03 00:28:34'),

('elder_003','2026-07-13','甜蜜蜜',21,'放松','2026-06-18 00:53:42'),

('elder_001','2026-07-27','渔舟唱晚',36,'安抚','2026-07-30 20:06:30'),

('elder_002','2026-07-16','高山流水',19,'助眠','2026-06-19 02:43:55'),

('elder_005','2026-06-14','京剧选段',15,'助眠','2026-07-03 18:28:20'),

('elder_005','2026-08-01','梁祝',42,'康复','2026-07-21 02:37:37'),

('elder_004','2026-07-25','甜蜜蜜',35,'助眠','2026-07-10 20:19:51'),

('elder_004','2026-07-25','京剧选段',40,'娱乐','2026-07-30 17:45:54'),

('elder_005','2026-07-25','春江花月夜',14,'放松','2026-07-13 18:42:37');

-- item_find_logs: 25
INSERT INTO `item_find_logs` (`user_id`,`date`,`item_name`,`duration_seconds`,`found`,`position`,`created_at`) VALUES
('elder_004','2026-07-12','药盒',90,0,NULL,'2026-07-01 04:38:59'),

('elder_004','2026-06-23','保温杯',19,1,'厨房台面','2026-07-08 08:44:54'),

('elder_001','2026-07-15','钥匙',9,1,'玄关鞋柜','2026-07-21 22:25:59'),

('elder_002','2026-07-03','拐杖',34,1,'床旁边','2026-07-03 04:42:53'),

('elder_005','2026-07-26','保温杯',14,1,'厨房台面','2026-07-08 10:43:38'),

('elder_005','2026-07-25','钥匙',10,1,'玄关鞋柜','2026-07-16 18:09:55'),

('elder_005','2026-07-20','拐杖',23,1,'床旁边','2026-07-16 19:17:13'),

('elder_004','2026-07-22','保温杯',44,1,'厨房台面','2026-07-13 08:39:08'),

('elder_002','2026-07-18','钥匙',55,0,NULL,'2026-07-12 16:01:24'),

('elder_003','2026-07-15','拖鞋',45,1,'床底下','2026-06-25 16:25:11'),

('elder_003','2026-07-12','钥匙',39,1,'玄关鞋柜','2026-08-02 08:15:08'),

('elder_005','2026-07-26','遥控器',8,1,'沙发缝隙','2026-06-26 01:15:02'),

('elder_004','2026-07-11','钥匙',40,1,'玄关鞋柜','2026-07-28 14:37:52'),

('elder_003','2026-06-23','拐杖',16,1,'床旁边','2026-08-01 09:27:19'),

('elder_003','2026-08-02','拖鞋',9,1,'床底下','2026-06-24 04:13:29'),

('elder_002','2026-07-14','保温杯',40,1,'厨房台面','2026-07-20 23:50:12'),

('elder_003','2026-07-30','帽子',30,1,'衣柜顶层','2026-07-02 13:09:10'),

('elder_005','2026-06-29','遥控器',24,1,'沙发缝隙','2026-07-15 12:21:48'),

('elder_001','2026-07-27','拖鞋',36,1,'床底下','2026-07-12 08:49:00'),

('elder_005','2026-07-30','拖鞋',44,1,'床底下','2026-06-28 02:26:06'),

('elder_003','2026-07-30','药盒',27,1,'床头柜','2026-07-05 04:38:43'),

('elder_002','2026-08-01','老花镜',18,1,'电视柜','2026-07-11 18:36:19'),

('elder_001','2026-06-30','手机',40,1,'枕头下面','2026-07-26 13:18:42'),

('elder_002','2026-07-15','手机',27,1,'枕头下面','2026-07-26 16:30:54'),

('elder_002','2026-07-04','帽子',22,1,'衣柜顶层','2026-07-05 00:27:43');

-- sleep_record: 25
INSERT INTO `sleep_record` (`elder_id`,`bed_time`,`in_bed`,`quality_score`,`deep_sleep_percent`,`wake_count`,`recorded_at`,`created_at`) VALUES
('elder_004','22:15',1,44,12,1,'2026-07-20 12:01:12.000000','2026-07-20 12:01:12.000000'),

('elder_002','21:15',1,66,22,0,'2026-08-02 22:26:00.000000','2026-08-02 22:26:00.000000'),

('elder_004','21:45',1,55,31,4,'2026-07-17 00:12:48.000000','2026-07-17 00:12:48.000000'),

('elder_003','22:45',1,72,33,0,'2026-07-10 20:10:02.000000','2026-07-10 20:10:02.000000'),

('elder_001','22:30',1,50,15,2,'2026-07-25 07:30:49.000000','2026-07-25 07:30:49.000000'),

('elder_002','22:30',1,69,18,1,'2026-07-10 11:53:38.000000','2026-07-10 11:53:38.000000'),

('elder_005','21:45',1,66,12,2,'2026-07-08 21:17:13.000000','2026-07-08 21:17:13.000000'),

('elder_003','23:30',1,61,21,3,'2026-07-05 11:03:46.000000','2026-07-05 11:03:46.000000'),

('elder_001','21:00',1,46,34,3,'2026-07-12 14:28:35.000000','2026-07-12 14:28:35.000000'),

('elder_001','21:45',1,63,35,3,'2026-07-27 04:28:17.000000','2026-07-27 04:28:17.000000'),

('elder_004','22:00',1,78,12,2,'2026-06-29 02:13:43.000000','2026-06-29 02:13:43.000000'),

('elder_003','23:45',1,47,16,3,'2026-07-11 20:16:20.000000','2026-07-11 20:16:20.000000'),

('elder_001','23:45',1,81,38,1,'2026-07-03 01:37:18.000000','2026-07-03 01:37:18.000000'),

('elder_003','23:45',1,82,24,1,'2026-07-01 00:38:45.000000','2026-07-01 00:38:45.000000'),

('elder_003','22:00',1,78,21,3,'2026-07-04 22:42:30.000000','2026-07-04 22:42:30.000000'),

('elder_001','22:30',1,76,34,4,'2026-07-13 14:41:57.000000','2026-07-13 14:41:57.000000'),

('elder_002','22:15',1,49,14,4,'2026-07-18 02:43:51.000000','2026-07-18 02:43:51.000000'),

('elder_001','23:30',1,55,30,1,'2026-07-25 06:16:30.000000','2026-07-25 06:16:30.000000'),

('elder_004','21:30',1,49,37,0,'2026-07-15 12:57:50.000000','2026-07-15 12:57:50.000000'),

('elder_002','21:30',1,55,19,3,'2026-07-21 18:55:20.000000','2026-07-21 18:55:20.000000'),

('elder_002','21:00',1,82,26,1,'2026-07-06 20:39:17.000000','2026-07-06 20:39:17.000000'),

('elder_003','22:45',1,43,30,4,'2026-07-02 15:40:13.000000','2026-07-02 15:40:13.000000'),

('elder_003','22:30',1,66,26,1,'2026-06-26 13:06:40.000000','2026-06-26 13:06:40.000000'),

('elder_003','23:45',1,50,19,0,'2026-07-05 15:59:54.000000','2026-07-05 15:59:54.000000'),

('elder_002','23:45',1,67,16,4,'2026-07-22 09:16:00.000000','2026-07-22 09:16:00.000000');

-- camera_request: 18
INSERT INTO `camera_request` (`request_id`,`elder_id`,`staff_id`,`staff_name`,`staff_phone`,`reason`,`status`,`approved_at`,`expired_at`,`camera_type`,`created_at`,`updated_at`) VALUES
('mr_44399cba','elder_001','staff_002','李秀英','13800138002','紧急呼叫后需查看现场','approved',1785843825460,'2026-08-05 19:43:45','door','2026-07-24 16:40:17.000000','2026-07-24 16:40:17.000000'),

('mr_a43f583a','elder_004','staff_003','王强','13800138003','紧急呼叫后需查看现场','none',0,'1970-01-01 00:00:00','living','2026-08-03 09:52:53.000000','2026-08-03 09:52:53.000000'),

('mr_13f5855f','elder_005','staff_001','张建国','13800138001','日常巡查需要查看老人状态','rejected',0,'1970-01-01 00:00:00','bedroom','2026-07-03 17:58:41.000000','2026-07-03 17:58:41.000000'),

('mr_ab099fba','elder_005','staff_001','张建国','13800138001','设备维护期间需查看监控','rejected',0,'1970-01-01 00:00:00','bedroom','2026-06-10 10:26:44.000000','2026-06-10 10:26:44.000000'),

('mr_d3fe4497','elder_002','staff_003','王强','13800138003','设备维护期间需查看监控','approved',1785843825460,'2026-08-05 19:43:45','bedroom','2026-05-23 07:16:58.000000','2026-05-23 07:16:58.000000'),

('mr_fadfcf33','elder_003','staff_002','李秀英','13800138002','老人摔倒后需持续观察','pending',0,'1970-01-01 00:00:00','living','2026-07-13 01:51:09.000000','2026-07-13 01:51:09.000000'),

('mr_97ec1f45','elder_003','staff_001','张建国','13800138001','老人情绪异常需要关注','pending',0,'1970-01-01 00:00:00','living','2026-06-17 20:43:28.000000','2026-06-17 20:43:28.000000'),

('mr_2076255f','elder_005','staff_003','王强','13800138003','新入住老人需了解情况','approved',1785843825460,'2026-08-05 19:43:45','bedroom','2026-06-22 23:04:34.000000','2026-06-22 23:04:34.000000'),

('mr_28b405e2','elder_002','staff_003','王强','13800138003','老人情绪异常需要关注','approved',1785843825460,'2026-08-05 19:43:45','living','2026-07-01 16:01:52.000000','2026-07-01 16:01:52.000000'),

('mr_a1fcf034','elder_001','staff_001','张建国','13800138001','紧急呼叫后需查看现场','pending',0,'1970-01-01 00:00:00','living','2026-07-11 18:22:30.000000','2026-07-11 18:22:30.000000'),

('mr_862c448b','elder_005','staff_003','王强','13800138003','日常巡查需要查看老人状态','none',0,'1970-01-01 00:00:00','bedroom','2026-05-22 02:08:36.000000','2026-05-22 02:08:36.000000'),

('mr_3d1405ca','elder_005','staff_001','张建国','13800138001','日常巡查需要查看老人状态','approved',1785843825460,'2026-08-05 19:43:45','living','2026-05-13 20:36:05.000000','2026-05-13 20:36:05.000000'),

('mr_40f18386','elder_004','staff_002','李秀英','13800138002','老人摔倒后需持续观察','pending',0,'1970-01-01 00:00:00','living','2026-07-05 05:43:59.000000','2026-07-05 05:43:59.000000'),

('mr_abc301ea','elder_005','staff_003','王强','13800138003','日常巡查需要查看老人状态','none',0,'1970-01-01 00:00:00','bedroom','2026-07-19 16:54:18.000000','2026-07-19 16:54:18.000000'),

('mr_6648a95a','elder_004','staff_001','张建国','13800138001','设备维护期间需查看监控','approved',1785843825460,'2026-08-05 19:43:45','bedroom','2026-07-16 00:11:26.000000','2026-07-16 00:11:26.000000'),

('mr_0e3c564b','elder_002','staff_001','张建国','13800138001','紧急呼叫后需查看现场','none',0,'1970-01-01 00:00:00','bedroom','2026-05-15 21:45:07.000000','2026-05-15 21:45:07.000000'),

('mr_63fb2efd','elder_005','staff_002','李秀英','13800138002','老人摔倒后需持续观察','approved',1785843825460,'2026-08-05 19:43:45','bedroom','2026-07-02 06:39:53.000000','2026-07-02 06:39:53.000000'),

('mr_260fe1d5','elder_002','staff_001','张建国','13800138001','设备维护期间需查看监控','approved',1785843825460,'2026-08-05 19:43:45','living','2026-05-15 09:48:04.000000','2026-05-15 09:48:04.000000');

-- camera_view_record: 18
INSERT INTO `camera_view_record` (`camera_request_id`,`camera_type`,`staff_id`,`duration`,`view_time`,`created_at`) VALUES
('cr_6cc8ff3b','卧室摄像头','staff_002',466,'2026-07-31 00:58:45.000000','2026-07-31 00:58:45.000000'),

('cr_d51676e8','客厅摄像头','staff_003',314,'2026-06-25 02:09:23.000000','2026-06-25 02:09:23.000000'),

('cr_45e25bff','门口摄像头','staff_003',500,'2026-08-03 15:24:36.000000','2026-08-03 15:24:36.000000'),

('cr_d69bea9a','门口摄像头','staff_003',554,'2026-07-15 07:44:14.000000','2026-07-15 07:44:14.000000'),

('cr_fc2c0513','门口摄像头','staff_002',478,'2026-06-19 04:01:47.000000','2026-06-19 04:01:47.000000'),

('cr_0dcbc97f','门口摄像头','staff_001',53,'2026-06-19 07:00:22.000000','2026-06-19 07:00:22.000000'),

('cr_f4b87e75','卧室摄像头','staff_001',169,'2026-07-24 03:01:48.000000','2026-07-24 03:01:48.000000'),

('cr_81740ae3','客厅摄像头','staff_002',44,'2026-06-29 20:18:14.000000','2026-06-29 20:18:14.000000'),

('cr_6b5a12c7','客厅摄像头','staff_002',282,'2026-07-22 06:43:30.000000','2026-07-22 06:43:30.000000'),

('cr_0eef256f','门口摄像头','staff_003',328,'2026-07-22 17:30:49.000000','2026-07-22 17:30:49.000000'),

('cr_c73e7ec5','门口摄像头','staff_001',486,'2026-06-27 03:59:10.000000','2026-06-27 03:59:10.000000'),

('cr_4ac187f8','门口摄像头','staff_002',508,'2026-07-20 05:49:07.000000','2026-07-20 05:49:07.000000'),

('cr_dc9e4f63','卧室摄像头','staff_001',284,'2026-07-08 12:52:49.000000','2026-07-08 12:52:49.000000'),

('cr_2cd6cbd3','卧室摄像头','staff_001',396,'2026-07-27 14:14:43.000000','2026-07-27 14:14:43.000000'),

('cr_e4fa488a','卧室摄像头','staff_003',57,'2026-07-22 01:00:57.000000','2026-07-22 01:00:57.000000'),

('cr_a4ab2923','卧室摄像头','staff_002',475,'2026-08-04 14:56:12.000000','2026-08-04 14:56:12.000000'),

('cr_2288fb2a','卧室摄像头','staff_001',133,'2026-07-08 03:38:55.000000','2026-07-08 03:38:55.000000'),

('cr_8374db63','客厅摄像头','staff_001',415,'2026-06-09 11:39:24.000000','2026-06-09 11:39:24.000000');

-- music_intervention: 20
INSERT INTO `music_intervention` (`intervention_id`,`elder_id`,`trigger_reason`,`music_type`,`start_time`,`duration_minutes`,`before_state`,`after_state`,`result`,`created_at`,`update_time`) VALUES
('int_338165f1','elder_004','久坐提醒','古典','2026-07-14 03:29:54',23,'久坐不动超过2小时','起身活动、舒展身体','completed','2026-07-14 03:29:54','2026-07-14 03:13:00'),

('int_4a2f0dcf','elder_004','情绪安抚','红歌','2026-06-10 09:37:51',22,'情绪低落、不愿交流','心情好转、愿意配合','completed','2026-06-10 09:37:51','2026-06-10 17:40:00'),

('int_1a1903b2','elder_003','久坐提醒','轻音乐','2026-06-07 23:17:30',16,'久坐不动超过2小时','起身活动、舒展身体','completed','2026-06-07 23:17:30','2026-06-07 23:02:00'),

('int_51ceb49b','elder_001','血压偏高提醒','红歌','2026-07-31 22:31:01',5,'血压偏高','情绪稳定','completed','2026-07-31 22:31:01','2026-07-31 23:50:00'),

('int_0cf8406d','elder_003','情绪识别-焦虑','红歌','2026-06-06 06:24:28',6,'焦虑不安、心率加快','平静放松、心率恢复正常','completed','2026-06-06 06:24:28','2026-06-06 19:51:00'),

('int_0dd20469','elder_004','跌倒后安抚','评弹','2026-07-09 19:47:26',21,'受到惊吓','感觉安心','pending','2026-07-09 19:47:26','2026-07-09 13:54:00'),

('int_5634f907','elder_003','跌倒后安抚','钢琴曲','2026-06-28 11:48:27',17,'受到惊吓','感觉安心','completed','2026-06-28 11:48:27','2026-06-28 19:57:00'),

('int_b1dc27b7','elder_005','久坐提醒','轻音乐','2026-07-07 15:33:34',7,'久坐不动超过2小时','起身活动、舒展身体','pending','2026-07-07 15:33:34','2026-07-07 16:16:00'),

('int_ac15de39','elder_004','情绪识别-焦虑','古典','2026-07-15 15:04:30',18,'焦虑不安、心率加快','平静放松、心率恢复正常','completed','2026-07-15 15:04:30','2026-07-15 12:05:00'),

('int_642d0689','elder_004','久坐提醒','轻音乐','2026-06-12 16:55:10',12,'久坐不动超过2小时','起身活动、舒展身体','completed','2026-06-12 16:55:10','2026-06-12 19:14:00'),

('int_ac07b9e9','elder_003','血压偏高提醒','钢琴曲','2026-07-14 21:39:15',30,'血压偏高','情绪稳定','pending','2026-07-14 21:39:15','2026-07-14 14:46:00'),

('int_130ef426','elder_001','血压偏高提醒','评弹','2026-06-14 21:58:24',16,'血压偏高','情绪稳定','completed','2026-06-14 21:58:24','2026-06-14 12:11:00'),

('int_8a3a55b1','elder_003','情绪识别-焦虑','红歌','2026-07-07 08:04:02',29,'焦虑不安、心率加快','平静放松、心率恢复正常','completed','2026-07-07 08:04:02','2026-07-07 03:20:00'),

('int_6e35f656','elder_004','跌倒后安抚','轻音乐','2026-06-30 20:33:44',16,'受到惊吓','感觉安心','completed','2026-06-30 20:33:44','2026-06-30 09:22:00'),

('int_50dd1d92','elder_002','失眠干预','轻音乐','2026-07-14 19:30:06',15,'难以入睡','感到舒适','completed','2026-07-14 19:30:06','2026-07-14 14:18:00'),

('int_7face5fa','elder_002','跌倒后安抚','戏曲','2026-06-29 22:23:49',23,'受到惊吓','感觉安心','completed','2026-06-29 22:23:49','2026-06-29 07:08:00'),

('int_b2fc83f1','elder_001','夜间唤醒','戏曲','2026-07-11 11:24:51',6,'睡眠中断、辗转反侧','重新入睡、呼吸平稳','completed','2026-07-11 11:24:51','2026-07-11 03:32:00'),

('int_d7c3cfd2','elder_005','心率异常告警联动','评弹','2026-06-18 10:15:46',26,'心率偏快','心率平稳','pending','2026-06-18 10:15:46','2026-06-18 05:11:00'),

('int_50897b88','elder_004','心率异常告警联动','红歌','2026-07-20 00:16:47',28,'心率偏快','心率平稳','completed','2026-07-20 00:16:47','2026-07-20 21:06:00'),

('int_fedac177','elder_005','血压偏高提醒','戏曲','2026-07-25 17:27:40',20,'血压偏高','情绪稳定','completed','2026-07-25 17:27:40','2026-07-25 15:07:00');

-- notification: 20
INSERT INTO `notification` (`notification_id`,`user_id`,`user_type`,`notification_type`,`title`,`content`,`is_read`,`building`,`room`,`order_id`,`request_id`,`elder_id`,`related_id`,`created_at`) VALUES
('notif_a8d81b21','staff_003','staff','alarm','新告警通知','1号楼1-201elder_001-告警',1,'1号楼','1-201','','','','elder_001',''),

('notif_f8feca14','staff_002','staff','service','服务请求通知','4号楼4-102elder_004-告警',1,'4号楼','4-102','','','','elder_004',''),

('notif_db21dca0','staff_003','staff','service','服务请求通知','3号楼3-401elder_003-告警',1,'3号楼','3-401','','','','elder_003',''),

('notif_ffe4ab98','staff_003','staff','alarm','新告警通知','3号楼3-401elder_003-告警',1,'3号楼','3-401','','','','elder_003',''),

('notif_bff096fc','staff_001','staff','service','服务请求通知','5号楼5-502elder_005-告警',1,'5号楼','5-502','','','','elder_005',''),

('notif_a109179c','staff_002','staff','service','服务请求通知','1号楼1-201elder_001-告警',1,'1号楼','1-201','','','','elder_001',''),

('notif_f827451a','staff_002','staff','emergency','紧急通知','3号楼3-401elder_003-告警',1,'3号楼','3-401','','','','elder_003',''),

('notif_a5b9d4cd','staff_002','staff','emergency','紧急通知','1号楼1-201elder_001-告警',1,'1号楼','1-201','','','','elder_001',''),

('notif_7ea0725d','staff_002','staff','emergency','紧急通知','5号楼5-502elder_005-告警',1,'5号楼','5-502','','','','elder_005',''),

('notif_a55a8cc8','staff_002','staff','monitor','监控授权通知','4号楼4-102elder_004-告警',1,'4号楼','4-102','','','','elder_004',''),

('notif_67245596','staff_001','staff','emergency','紧急通知','3号楼3-401elder_003-告警',0,'3号楼','3-401','','','','elder_003',''),

('notif_8e102b84','staff_003','staff','monitor','监控授权通知','2号楼2-301elder_002-告警',1,'2号楼','2-301','','','','elder_002',''),

('notif_96742d2b','staff_001','staff','service','服务请求通知','2号楼2-301elder_002-告警',1,'2号楼','2-301','','','','elder_002',''),

('notif_ab5a1a9a','staff_001','staff','emergency','紧急通知','3号楼3-401elder_003-告警',1,'3号楼','3-401','','','','elder_003',''),

('notif_b61ebe75','staff_001','staff','monitor','监控授权通知','1号楼1-201elder_001-告警',1,'1号楼','1-201','','','','elder_001',''),

('notif_e5b35c59','staff_002','staff','service','服务请求通知','3号楼3-401elder_003-告警',1,'3号楼','3-401','','','','elder_003',''),

('notif_94d387ce','staff_002','staff','alarm','新告警通知','3号楼3-401elder_003-告警',0,'3号楼','3-401','','','','elder_003',''),

('notif_150817f1','staff_001','staff','device','设备状态通知','1号楼1-201elder_001-告警',1,'1号楼','1-201','','','','elder_001',''),

('notif_4653a9f2','staff_001','staff','service','服务请求通知','5号楼5-502elder_005-告警',1,'5号楼','5-502','','','','elder_005',''),

('notif_8fd658d5','staff_003','staff','emergency','紧急通知','5号楼5-502elder_005-告警',0,'5号楼','5-502','','','','elder_005','');

-- emergency_contact: 15
INSERT INTO `emergency_contact` (`contact_id`,`elder_id`,`name`,`phone`,`relation`,`is_primary`,`sort_order`,`created_at`,`update_time`) VALUES
('contact_67e12eb8','elder_001','侄子','135008398','侄子',1,0,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_33119e08','elder_001','外甥1','135003024','外甥',0,1,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_fb110a56','elder_001','外甥2','132008420','外甥',0,2,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_53b1f3ac','elder_002','侄子','136004702','侄子',1,0,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_d66e4bc2','elder_002','儿子1','139002585','儿子',0,1,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_162310f1','elder_002','侄子2','137001459','侄子',0,2,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_8f770c7f','elder_003','儿子','134008372','儿子',1,0,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_6ed4fefb','elder_003','孙子1','139009487','孙子',0,1,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_39291d6d','elder_003','侄子2','139002349','侄子',0,2,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_74d25fea','elder_004','侄子','133001172','侄子',1,0,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_17076bbb','elder_004','妹妹1','132007952','妹妹',0,1,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_81958052','elder_004','外甥2','139004780','外甥',0,2,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_498af3f0','elder_005','妹妹','132007825','妹妹',1,0,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_84cbbfbc','elder_005','孙子1','133002664','孙子',0,1,'2025-01-10 09:00:00','2026-07-01 00:00:00'),

('contact_e67cffb3','elder_005','孙子2','139003986','孙子',0,2,'2025-01-10 09:00:00','2026-07-01 00:00:00');

-- sos_record: 20
INSERT INTO `sos_record` (`sos_id`,`elder_id`,`trigger_time`,`status`,`location`,`handler_id`,`handled_time`,`created_at`) VALUES
('sos_cf0abf5c','elder_004','2026-07-26 12:31:43','handled','4号楼4-102客厅','staff_002','2026-07-01 06:08:48','2026-07-26 12:31:43'),

('sos_ee3931c0','elder_002','2026-05-14 21:51:55','triggered','2号楼2-301客厅','','1970-01-01 00:00:00','2026-05-14 21:51:55'),

('sos_42976028','elder_003','2026-07-31 08:53:02','pending','3号楼3-401客厅','','1970-01-01 00:00:00','2026-07-31 08:53:02'),

('sos_793d2c21','elder_004','2026-06-02 17:41:48','handled','4号楼4-102客厅','','1970-01-01 00:00:00','2026-06-02 17:41:48'),

('sos_3dc8d994','elder_003','2026-06-12 09:56:36','triggered','3号楼3-401客厅','','1970-01-01 00:00:00','2026-06-12 09:56:36'),

('sos_ddc707e8','elder_003','2026-07-10 20:52:49','pending','3号楼3-401客厅','','1970-01-01 00:00:00','2026-07-10 20:52:49'),

('sos_2f68fbd4','elder_003','2026-05-16 17:22:22','triggered','3号楼3-401客厅','','1970-01-01 00:00:00','2026-05-16 17:22:22'),

('sos_a0fb58bd','elder_003','2026-05-26 07:53:22','triggered','3号楼3-401客厅','','1970-01-01 00:00:00','2026-05-26 07:53:22'),

('sos_88dd56f5','elder_004','2026-06-24 16:34:37','triggered','4号楼4-102客厅','','1970-01-01 00:00:00','2026-06-24 16:34:37'),

('sos_c169cefc','elder_005','2026-07-01 10:45:41','handled','5号楼5-502客厅','staff_003','2026-07-11 17:05:26','2026-07-01 10:45:41'),

('sos_cf784e77','elder_001','2026-05-12 22:14:21','pending','1号楼1-201客厅','','1970-01-01 00:00:00','2026-05-12 22:14:21'),

('sos_787b60a0','elder_001','2026-07-05 13:59:41','triggered','1号楼1-201客厅','','1970-01-01 00:00:00','2026-07-05 13:59:41'),

('sos_2be5ac3f','elder_004','2026-06-23 18:09:47','pending','4号楼4-102客厅','','1970-01-01 00:00:00','2026-06-23 18:09:47'),

('sos_7d1ee04c','elder_003','2026-06-26 16:03:26','handled','3号楼3-401客厅','staff_001','2026-07-31 18:03:24','2026-06-26 16:03:26'),

('sos_97c815aa','elder_001','2026-05-28 13:08:06','handled','1号楼1-201客厅','staff_002','2026-07-03 15:41:28','2026-05-28 13:08:06'),

('sos_cde893ab','elder_004','2026-05-29 08:33:12','handled','4号楼4-102客厅','','1970-01-01 00:00:00','2026-05-29 08:33:12'),

('sos_db4bfd90','elder_002','2026-06-19 07:48:35','handled','2号楼2-301客厅','staff_002','2026-06-24 15:20:59','2026-06-19 07:48:35'),

('sos_a7a9e30d','elder_004','2026-06-11 13:35:43','handled','4号楼4-102客厅','staff_001','2026-07-02 03:30:24','2026-06-11 13:35:43'),

('sos_37b70274','elder_004','2026-07-09 21:24:45','triggered','4号楼4-102客厅','','1970-01-01 00:00:00','2026-07-09 21:24:45'),

('sos_b4139595','elder_002','2026-05-28 17:21:07','pending','2号楼2-301客厅','','1970-01-01 00:00:00','2026-05-28 17:21:07');

-- service_request: 15
INSERT INTO `service_request` (`request_id`,`family_id`,`elder_id`,`request_type`,`content`,`status`,`related_order_id`,`reject_reason`,`created_at`,`update_time`) VALUES
('sr_9b2b7aba','family_002','elder_002','日常关怀','老人情绪低落，请求工作人员关怀','approved','wo_40b4eef2','','2026-05-26 07:40:57','2026-05-26 07:40:57'),

('sr_a811384c','family_001','elder_001','紧急协助','老人情况紧急，请求立即上门','approved','wo_e1e4d59d','','2026-07-16 04:50:34','2026-07-16 04:50:34'),

('sr_4af5c751','family_005','elder_005','紧急协助','老人情况紧急，请求立即上门','approved','wo_59c0754f','','2026-05-19 00:02:07','2026-05-19 00:02:07'),

('sr_153a94cb','family_004','elder_004','健康咨询','需要咨询老人用药问题','approved','wo_d71abaac','','2026-06-15 02:03:39','2026-06-15 02:03:39'),

('sr_543c0a9d','family_003','elder_003','紧急协助','老人情况紧急，请求立即上门','pending','','','2026-06-22 14:52:37','2026-06-22 14:52:37'),

('sr_f970a872','family_004','elder_004','日常关怀','老人情绪低落，请求工作人员关怀','rejected','','已自行处理','2026-07-05 03:30:33','2026-07-05 03:30:33'),

('sr_21deae06','family_001','elder_001','健康咨询','需要咨询老人用药问题','rejected','','不在服务范围内','2026-05-12 06:07:58','2026-05-12 06:07:58'),

('sr_e8e28916','family_001','elder_001','设备维修','请求安排人员上门检查设备','rejected','','不在服务范围内','2026-06-02 12:17:25','2026-06-02 12:17:25'),

('sr_8bfe1594','family_005','elder_005','健康咨询','需要咨询老人用药问题','approved','wo_7e60d8f0','','2026-06-06 13:34:24','2026-06-06 13:34:24'),

('sr_2ffe6f3b','family_004','elder_004','设备维修','请求安排人员上门检查设备','pending','','','2026-07-21 16:53:23','2026-07-21 16:53:23'),

('sr_34e71d84','family_004','elder_004','生活物资代购','需要代购生活用品','approved','wo_a0513c23','','2026-05-08 11:42:09','2026-05-08 11:42:09'),

('sr_b3c6b010','family_005','elder_005','紧急协助','老人情况紧急，请求立即上门','approved','wo_16361291','','2026-07-19 13:58:45','2026-07-19 13:58:45'),

('sr_b0b3d310','family_004','elder_004','生活物资代购','需要代购生活用品','rejected','','暂不需要','2026-06-26 14:40:02','2026-06-26 14:40:02'),

('sr_1195f105','family_004','elder_004','健康咨询','需要咨询老人用药问题','pending','','','2026-06-08 06:36:44','2026-06-08 06:36:44'),

('sr_83bd4bda','family_002','elder_002','设备维修','请求安排人员上门检查设备','pending','','','2026-06-23 22:56:33','2026-06-23 22:56:33');

-- work_order: 15
INSERT INTO `work_order` (`order_id`,`elder_id`,`order_type`,`description`,`status`,`creator_id`,`handler_id`,`handler_name`,`handler_phone`,`complete_time`,`service_request_id`,`created_at`,`update_time`) VALUES
('wo_21b6dd9e','elder_004','紧急巡检','高风险告警触发，需立即上门确认老人安全','待处理','system','staff_001','张建国','','1970-01-01 00:00:00','','2026-06-27 01:11:03','2026-06-27 01:11:03'),

('wo_206d8491','elder_005','上门护理','老人需上门护理服务','待处理','system','','','','1970-01-01 00:00:00','','2026-06-10 09:09:24','2026-06-10 09:09:24'),

('wo_40f02f89','elder_003','日常关怀','老人情绪异常，需心理疏导','处理中','system','staff_001','张建国','','1970-01-01 00:00:00','','2026-07-18 01:35:12','2026-07-18 01:35:12'),

('wo_4653af0c','elder_005','日常关怀','老人情绪异常，需心理疏导','待处理','system','staff_001','张建国','','1970-01-01 00:00:00','','2026-07-06 06:06:27','2026-07-06 06:06:27'),

('wo_b740cf89','elder_004','健康关注','健康指标异常，24小时内完成随访','已完成','system','staff_003','王强','','2026-07-09 21:13:09','','2026-07-18 04:58:13','2026-07-18 04:58:13'),

('wo_01b563c4','elder_001','设备检查','设备异常，需检查运行状态','已完成','system','staff_003','王强','','2026-08-02 06:05:07','','2026-06-11 16:10:00','2026-06-11 16:10:00'),

('wo_ded27d60','elder_001','日常关怀','老人情绪异常，需心理疏导','已完成','system','staff_001','张建国','','2026-06-22 04:25:00','','2026-07-05 02:27:25','2026-07-05 02:27:25'),

('wo_8677fcf7','elder_003','紧急巡检','高风险告警触发，需立即上门确认老人安全','待处理','system','','','','1970-01-01 00:00:00','','2026-06-18 18:27:38','2026-06-18 18:27:38'),

('wo_c6bcfaf8','elder_005','设备检查','设备异常，需检查运行状态','处理中','system','staff_002','李秀英','','1970-01-01 00:00:00','','2026-06-21 03:33:35','2026-06-21 03:33:35'),

('wo_c613c4fd','elder_001','日常关怀','老人情绪异常，需心理疏导','处理中','system','staff_003','王强','','1970-01-01 00:00:00','','2026-06-11 17:48:05','2026-06-11 17:48:05'),

('wo_cabfc53b','elder_001','日常关怀','老人情绪异常，需心理疏导','待处理','system','','','','1970-01-01 00:00:00','','2026-07-30 08:01:26','2026-07-30 08:01:26'),

('wo_4159bccc','elder_001','紧急巡检','高风险告警触发，需立即上门确认老人安全','已完成','system','','','','2026-07-18 03:37:24','','2026-08-02 16:03:31','2026-08-02 16:03:31'),

('wo_d4dccf05','elder_004','紧急巡检','高风险告警触发，需立即上门确认老人安全','已完成','system','','','','2026-06-24 00:14:59','','2026-06-15 00:52:58','2026-06-15 00:52:58'),

('wo_daaaf27d','elder_004','设备维修','设备故障，需上门维修','待处理','system','staff_002','李秀英','','1970-01-01 00:00:00','','2026-06-17 03:36:03','2026-06-17 03:36:03'),

('wo_3a23730f','elder_004','上门护理','老人需上门护理服务','已完成','system','staff_002','李秀英','','2026-07-01 07:09:04','','2026-07-11 03:03:36','2026-07-11 03:03:36');

-- home_control_log: 15
INSERT INTO `home_control_log` (`control_id`,`elder_id`,`device_id`,`command`,`source_agent`,`result`,`created_at`) VALUES
('ctrl_f5ba4431','elder_001','dev_001','beep','CLOUD-01','success','2026-06-22 06:13:54'),

('ctrl_d6c64e45','elder_005','dev_012','beep','CLOUD-01','failed','2026-07-19 21:04:11'),

('ctrl_6e619d24','elder_002','dev_004','beep','LOCAL-1002','failed','2026-07-30 08:42:01'),

('ctrl_43e8cdac','elder_004','dev_010','alarmMute','LOCAL-1001','success','2026-07-20 22:06:43'),

('ctrl_128bce4b','elder_003','dev_007','enableMotionDetect','CLOUD-01','success','2026-06-12 11:50:42'),

('ctrl_a8070770','elder_002','dev_004','setVolume','LOCAL-1001','failed','2026-07-09 22:42:05'),

('ctrl_194ae8b4','elder_001','dev_001','beep','LOCAL-1002','success','2026-07-11 05:20:16'),

('ctrl_8f800548','elder_002','dev_004','enableMotionDetect','CLOUD-01','success','2026-08-02 06:34:42'),

('ctrl_8687e078','elder_003','dev_007','beep','CLOUD-01','failed','2026-06-14 21:54:36'),

('ctrl_5e9f267f','elder_003','dev_007','startRecord','LOCAL-1002','failed','2026-07-07 05:07:23'),

('ctrl_d79e33b9','elder_005','dev_012','alarmMute','CLOUD-01','failed','2026-06-10 15:14:49'),

('ctrl_450a0a1c','elder_001','dev_001','close','LOCAL-1001','failed','2026-06-06 05:53:04'),

('ctrl_c9975aa6','elder_003','dev_007','startRecord','CLOUD-01','success','2026-08-03 16:34:46'),

('ctrl_b252f02c','elder_004','dev_010','setVolume','LOCAL-1001','success','2026-06-10 04:36:07'),

('ctrl_c1e00556','elder_004','dev_010','close','LOCAL-1002','success','2026-06-28 12:42:44');

-- local_agent: 6
INSERT INTO `local_agent` (`agent_id`,`agent_type`,`status`,`last_heartbeat`,`ip`,`device_count`,`connected_devices`,`created_at`,`update_time`) VALUES
('LOCAL-1001','local_gateway','online','2026-08-04 19:43:45','192.168.1.101',4,4,'2025-01-05 09:00:00','2026-08-04 19:43:45'),

('LOCAL-1002','local_gateway','online','2026-08-04 19:43:45','192.168.1.102',5,3,'2025-02-05 09:00:00','2026-08-04 19:43:45'),

('LOCAL-1003','local_gateway','online','2026-08-04 19:43:45','192.168.1.103',3,5,'2025-03-05 09:00:00','2026-08-04 19:43:45'),

('LOCAL-1004','local_gateway','online','2026-08-04 19:43:45','192.168.1.104',4,5,'2025-04-05 09:00:00','2026-08-04 19:43:45'),

('LOCAL-1005','local_gateway','online','2026-08-04 19:43:45','192.168.1.105',4,1,'2025-05-05 09:00:00','2026-08-04 19:43:45'),

('LOCAL-1006','local_gateway','online','2026-08-04 19:43:45','192.168.1.106',5,4,'2025-06-05 09:00:00','2026-08-04 19:43:45');

-- cloud_agent: 4
INSERT INTO `cloud_agent` (`agent_id`,`agent_type`,`status`,`last_heartbeat`,`ip`,`device_count`,`connected_devices`,`created_at`,`update_time`) VALUES
('CLOUD-01','cloud_agent','online','2026-08-03 10:00:00','10.0.0.1',15,17,'2025-01-01 08:00:00','2026-08-03 10:00:00'),

('CLOUD-02','cloud_agent','online','2026-08-03 10:00:00','10.0.0.2',19,19,'2025-02-01 08:00:00','2026-08-03 10:00:00'),

('CLOUD-03','cloud_agent','online','2026-08-03 10:00:00','10.0.0.3',29,15,'2025-03-01 08:00:00','2026-08-03 10:00:00'),

('CLOUD-04','cloud_agent','online','2026-08-03 10:00:00','10.0.0.4',30,17,'2025-04-01 08:00:00','2026-08-03 10:00:00');
-- ai_analysis_record: 30 rows
INSERT INTO `ai_analysis_record` (`record_id`,`elder_id`,`risk_level`,`risk_reason`,`suggestion`,`elder_reply`,`family_notice`,`community_suggestion`,`need_work_order`,`work_order_type`,`source`,`need_alarm`,`generated_alarm_id`,`created_at`) VALUES
('ar_74bfc465','elder_002','中风险','心率偏快(111次/分)','建议立即联系家属并安排社区人员确认老人状态','请保持平静，社区工作人员很快会到。','老人当前心率偏快(111次/分)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'健康关注','python_ai_service',1,'alarm_517e5161','2026-07-14 03:02:03'),

('ar_875acfe2','elder_003','正常','各项指标均在正常范围内','各项指标正常，维持日常活动即可','今天数据还不错，记得按时吃药哦。','','',0,'无需工单','python_ai_service',0,'','2026-07-19 06:47:26'),

('ar_93846aaa','elder_005','高风险','血氧偏低(86%)、心率偏快(114次/分)','建议立即联系家属并安排社区人员确认老人状态','您的数据有些波动，不用担心，已经通知了家人。','老人当前血氧偏低(86%)、心率偏快(114次/分)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_0b3c1ac2','2026-05-11 09:16:31'),

('ar_ec76ce5a','elder_001','中风险','血氧偏低(88%)','建议立即联系家属并安排社区人员确认老人状态','您的数据有些波动，不用担心，已经通知了家人。','老人当前血氧偏低(88%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'健康关注','python_ai_service',1,'alarm_7d7ddb2c','2026-06-09 08:14:21'),

('ar_044c3b42','elder_005','高风险','血氧偏低(86%)','建议立即联系家属并安排社区人员确认老人状态','您的数据有些波动，不用担心，已经通知了家人。','老人当前血氧偏低(86%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','java_rule_fallback',1,'alarm_519ddda5','2026-05-23 06:07:41'),

('ar_6f24b8db','elder_001','中风险','血压偏高(149/91mmHg)','各项指标正常，维持日常活动即可','今天数据还不错，记得按时吃药哦。','','',0,'无需工单','python_ai_service',0,'','2026-06-29 08:33:51'),

('ar_d8ec1b58','elder_005','高风险','血氧偏低(85%)','建议立即联系家属并安排社区人员确认老人状态','您的数据有些波动，不用担心，已经通知了家人。','老人当前血氧偏低(85%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_780486b7','2026-07-09 08:06:50'),

('ar_fc301dda','elder_003','中风险','血压偏高(147/90mmHg)','各项指标正常，维持日常活动即可','您的健康指标整体平稳，请继续保持。','','',0,'无需工单','python_ai_service',0,'','2026-07-29 00:59:10'),

('ar_01f9406a','elder_001','正常','各项指标均在正常范围内','建议继续保持目前的生活和监测习惯','今天数据还不错，记得按时吃药哦。','','',0,'无需工单','python_ai_service',0,'','2026-05-13 19:55:13'),

('ar_1086eff4','elder_005','中风险','血氧偏低(90%)','建议立即联系家属并安排社区人员确认老人状态','请保持平静，社区工作人员很快会到。','老人当前血氧偏低(90%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'健康关注','python_ai_service',1,'alarm_de7b6f07','2026-06-18 07:08:06'),

('ar_db714d50','elder_005','高风险','血氧偏低(88%)、血压偏高(149/91mmHg)','建议立即联系家属并安排社区人员确认老人状态','您的数据有些波动，不用担心，已经通知了家人。','老人当前血氧偏低(88%)、血压偏高(149/91mmHg)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_112cb3d8','2026-05-18 14:21:07'),

('ar_8ad6d43e','elder_005','高风险','血氧偏低(85%)、心率偏快(112次/分)、血压偏高(146/76mmHg)','建议立即联系家属并安排社区人员确认老人状态','深呼吸，放松，我们正在联系您的家人。','老人当前血氧偏低(85%)、心率偏快(112次/分)、血压偏高(146/76mmHg)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_95c9b5aa','2026-06-25 14:36:55'),

('ar_4a96157e','elder_001','中风险','血氧偏低(89%)','建议立即联系家属并安排社区人员确认老人状态','深呼吸，放松，我们正在联系您的家人。','老人当前血氧偏低(89%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'健康关注','java_rule_fallback',1,'alarm_fc3b74e3','2026-06-07 11:01:44'),

('ar_142e3148','elder_004','中风险','血氧偏低(89%)','建议立即联系家属并安排社区人员确认老人状态','请您先坐下休息，我已经帮您通知家属。','老人当前血氧偏低(89%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'健康关注','python_ai_service',1,'alarm_67a1a756','2026-07-19 17:18:22'),

('ar_b4e27beb','elder_003','正常','各项指标均在正常范围内','建议继续保持目前的生活和监测习惯','各项指标正常，保持好心态最重要。','','',0,'无需工单','python_ai_service',0,'','2026-05-11 07:00:13'),

('ar_f84f36a4','elder_003','中风险','血压偏高(148/92mmHg)','建议继续保持目前的生活和监测习惯','您的健康指标整体平稳，请继续保持。','','',0,'无需工单','python_ai_service',0,'','2026-07-25 14:03:26'),

('ar_4a4a0c3e','elder_001','正常','各项指标均在正常范围内','各项指标正常，维持日常活动即可','您的健康指标整体平稳，请继续保持。','','',0,'无需工单','python_ai_service',0,'','2026-07-29 23:58:45'),

('ar_3cb99724','elder_005','高风险','血氧偏低(86%)、心率偏快(111次/分)、血压偏高(146/82mmHg)','建议立即联系家属并安排社区人员确认老人状态','请您先坐下休息，我已经帮您通知家属。','老人当前血氧偏低(86%)、心率偏快(111次/分)、血压偏高(146/82mmHg)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_f6ed5f37','2026-05-13 05:00:18'),

('ar_157af03b','elder_005','高风险','血氧偏低(85%)','建议立即联系家属并安排社区人员确认老人状态','请您先坐下休息，我已经帮您通知家属。','老人当前血氧偏低(85%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_cc4fea8a','2026-06-07 16:22:56'),

('ar_32623180','elder_005','高风险','血氧偏低(86%)','建议立即联系家属并安排社区人员确认老人状态','深呼吸，放松，我们正在联系您的家人。','老人当前血氧偏低(86%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_e92ebb0e','2026-06-13 16:57:47'),

('ar_70010267','elder_004','正常','各项指标均在正常范围内','各项指标正常，维持日常活动即可','各项指标正常，保持好心态最重要。','','',0,'无需工单','java_rule_fallback',0,'','2026-05-08 18:49:19'),

('ar_f7ca451b','elder_004','正常','各项指标均在正常范围内','建议增加监测频率，密切关注指标变化','各项指标正常，保持好心态最重要。','','',0,'无需工单','java_rule_fallback',0,'','2026-05-19 09:43:38'),

('ar_81d287be','elder_003','高风险','血氧偏低(91%)、血压偏高(147/87mmHg)','建议立即联系家属并安排社区人员确认老人状态','请您先坐下休息，我已经帮您通知家属。','老人当前血氧偏低(91%)、血压偏高(147/87mmHg)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_eedeb2c7','2026-07-04 05:01:15'),

('ar_6cf981c7','elder_001','中风险','血氧偏低(91%)','建议立即联系家属并安排社区人员确认老人状态','您的数据有些波动，不用担心，已经通知了家人。','老人当前血氧偏低(91%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'健康关注','python_ai_service',1,'alarm_d362fbf0','2026-07-29 20:58:49'),

('ar_d311d227','elder_004','高风险','血氧偏低(86%)','建议立即联系家属并安排社区人员确认老人状态','深呼吸，放松，我们正在联系您的家人。','老人当前血氧偏低(86%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','java_rule_fallback',1,'alarm_30875d9b','2026-07-12 21:48:37'),

('ar_4dd5adf7','elder_005','高风险','血氧偏低(86%)','建议立即联系家属并安排社区人员确认老人状态','深呼吸，放松，我们正在联系您的家人。','老人当前血氧偏低(86%)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_91154ff7','2026-06-28 15:14:16'),

('ar_646a1c2d','elder_004','正常','各项指标均在正常范围内','建议增加监测频率，密切关注指标变化','各项指标正常，保持好心态最重要。','','',0,'无需工单','python_ai_service',0,'','2026-07-21 14:25:36'),

('ar_38e54ed5','elder_003','中风险','血压偏高(146/90mmHg)','各项指标正常，维持日常活动即可','各项指标正常，保持好心态最重要。','','',0,'无需工单','python_ai_service',0,'','2026-07-04 21:32:46'),

('ar_3aaad2b3','elder_003','高风险','血氧偏低(89%)、血压偏高(147/93mmHg)','建议立即联系家属并安排社区人员确认老人状态','您的数据有些波动，不用担心，已经通知了家人。','老人当前血氧偏低(89%)、血压偏高(147/93mmHg)，建议尽快电话确认或前往查看。','建议生成紧急巡检工单，安排社区人员上门确认。',1,'紧急巡检','python_ai_service',1,'alarm_660b08f2','2026-06-21 18:04:24'),

('ar_ce0cb843','elder_003','正常','各项指标均在正常范围内','各项指标正常，维持日常活动即可','您的健康指标整体平稳，请继续保持。','','',0,'无需工单','java_rule_fallback',0,'','2026-05-27 13:56:14');


-- Family notifications (15 rows)
INSERT INTO `notification` (`notification_id`,`user_id`,`user_type`,`notification_type`,`title`,`content`,`is_read`,`elder_id`,`created_at`) VALUES
('nfam_3b9f6ef8','family_002','family','camera','监控授权','监控查看授权已过期，如需继续请重新授权',0,'elder_002','2026-06-11 10:06:00'),
('nfam_ca7c6625','family_001','family','alarm','告警通知','张三智能手环触发SOS紧急呼叫',1,'elder_001','2026-07-08 07:02:00'),
('nfam_7f6f9f78','family_001','family','health','健康提醒','张三血氧偏低，请注意观察',0,'elder_001','2026-07-26 09:30:00'),
('nfam_3a2f43cb','family_004','family','camera','监控授权','工作人员申请查看赵六的监控画面',1,'elder_004','2026-08-10 20:17:00'),
('nfam_ce5dd2af','family_002','family','service','服务通知','您的服务申请已受理，工单号wo_7915afc3',0,'elder_002','2026-06-25 11:03:00'),
('nfam_9eb92937','family_001','family','health','健康提醒','张三今日血压偏高，建议关注',0,'elder_001','2026-06-04 09:44:00'),
('nfam_3677ef3f','family_003','family','alarm','告警通知','王五家中检测到疑似跌倒，请尽快确认',1,'elder_003','2026-07-22 11:06:00'),
('nfam_bb9abf47','family_005','family','camera','监控授权','工作人员申请查看孙七的监控画面',1,'elder_005','2026-08-10 07:09:00'),
('nfam_59df7289','family_005','family','camera','监控授权','监控查看授权已过期，如需继续请重新授权',1,'elder_005','2026-06-09 19:53:00'),
('nfam_027c9cff','family_003','family','service','服务通知','服务申请处理完成，请查看详情',1,'elder_003','2026-06-13 10:59:00'),
('nfam_c33c10a0','family_005','family','camera','监控授权','监控查看授权已过期，如需继续请重新授权',1,'elder_005','2026-08-02 19:22:00'),
('nfam_b67b0b33','family_002','family','service','服务通知','服务申请处理完成，请查看详情',0,'elder_002','2026-07-16 11:15:00'),
('nfam_89746237','family_002','family','service','服务通知','服务申请处理完成，请查看详情',0,'elder_002','2026-07-01 12:42:00'),
('nfam_a26ec4cf','family_002','family','health','健康提醒','李四今日血压偏高，建议关注',0,'elder_002','2026-07-03 16:04:00'),
('nfam_530415c6','family_003','family','camera','监控授权','监控查看授权已过期，如需继续请重新授权',1,'elder_003','2026-08-04 16:00:00');
