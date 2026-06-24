-- 家庭医疗系统 - 新功能表结构
USE family_medical;

-- 健康指标记录表
CREATE TABLE `health_indicator` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `indicator_type` VARCHAR(50) NOT NULL COMMENT '指标类型',
  `indicator_value` DECIMAL(10,2) NOT NULL COMMENT '指标值',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位',
  `measure_time` DATETIME NOT NULL COMMENT '测量时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `deleted` INT NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_type` (`user_id`, `indicator_type`),
  KEY `idx_user_time` (`user_id`, `measure_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康指标记录';

-- 图文问诊表
CREATE TABLE `consultation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `patient_id` BIGINT NOT NULL COMMENT '患者ID',
  `doctor_id` BIGINT DEFAULT NULL COMMENT '医生ID',
  `title` VARCHAR(200) NOT NULL COMMENT '咨询标题',
  `symptoms` TEXT NOT NULL COMMENT '症状描述',
  `images` VARCHAR(2000) DEFAULT NULL COMMENT '图片URL(JSON数组)',
  `status` INT NOT NULL DEFAULT 0 COMMENT '0=待回复 1=已回复 2=已关闭',
  `deleted` INT NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_patient` (`patient_id`),
  KEY `idx_doctor` (`doctor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图文问诊';

-- 问诊回复表
CREATE TABLE `consultation_reply` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `consultation_id` BIGINT NOT NULL COMMENT '问诊ID',
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
  `content` TEXT NOT NULL COMMENT '回复内容',
  `images` VARCHAR(2000) DEFAULT NULL COMMENT '图片URL(JSON数组)',
  `deleted` INT NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_consultation` (`consultation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊回复';

-- 聊天消息表
CREATE TABLE `chat_message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
  `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `msg_type` INT NOT NULL DEFAULT 0 COMMENT '0=文本 1=图片',
  `is_read` INT NOT NULL DEFAULT 0 COMMENT '0=未读 1=已读',
  `deleted` INT NOT NULL DEFAULT 0,
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_sender` (`sender_id`),
  KEY `idx_receiver` (`receiver_id`),
  KEY `idx_pair` (`sender_id`, `receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息';
