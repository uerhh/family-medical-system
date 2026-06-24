-- 家庭医疗系统数据库初始化脚本
CREATE DATABASE IF NOT EXISTS family_medical DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_general_ci;
USE family_medical;

-- 用户表（通用：管理员、医生、用户共用）
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `gender` TINYINT DEFAULT 0 COMMENT '性别：0未知 1男 2女',
    `age` INT DEFAULT NULL COMMENT '年龄',
    `id_card` VARCHAR(18) DEFAULT NULL COMMENT '身份证号',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '地址',
    `role` TINYINT NOT NULL DEFAULT 3 COMMENT '角色：1管理员 2医生 3用户',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删 1已删',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 医生详情表
CREATE TABLE `doctor_info` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` BIGINT NOT NULL COMMENT '关联用户ID',
    `department` VARCHAR(50) DEFAULT NULL COMMENT '科室',
    `title` VARCHAR(50) DEFAULT NULL COMMENT '职称（主任医师/副主任/主治/住院医师）',
    `specialty` VARCHAR(200) DEFAULT NULL COMMENT '擅长领域',
    `introduction` TEXT DEFAULT NULL COMMENT '个人简介',
    `consult_fee` DECIMAL(10,2) DEFAULT 0 COMMENT '问诊费用',
    `work_years` INT DEFAULT 0 COMMENT '工作年限',
    `deleted` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生信息表';

-- 健康档案表
CREATE TABLE `health_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '所属用户ID',
    `blood_type` VARCHAR(10) DEFAULT NULL COMMENT '血型',
    `height` DECIMAL(5,1) DEFAULT NULL COMMENT '身高(cm)',
    `weight` DECIMAL(5,1) DEFAULT NULL COMMENT '体重(kg)',
    `allergy` TEXT DEFAULT NULL COMMENT '过敏史',
    `medical_history` TEXT DEFAULT NULL COMMENT '既往病史',
    `family_history` TEXT DEFAULT NULL COMMENT '家族病史',
    `deleted` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康档案表';

-- 预约挂号表
CREATE TABLE `appointment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `patient_id` BIGINT NOT NULL COMMENT '患者用户ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生用户ID',
    `appointment_date` DATE NOT NULL COMMENT '预约日期',
    `time_slot` VARCHAR(20) NOT NULL COMMENT '时间段（上午/下午）',
    `symptoms` TEXT DEFAULT NULL COMMENT '症状描述',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0待确认 1已确认 2已完成 3已取消',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `deleted` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_patient` (`patient_id`),
    KEY `idx_doctor` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约挂号表';

-- 诊断记录表
CREATE TABLE `diagnosis` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `appointment_id` BIGINT DEFAULT NULL COMMENT '关联预约ID',
    `patient_id` BIGINT NOT NULL COMMENT '患者ID',
    `doctor_id` BIGINT NOT NULL COMMENT '医生ID',
    `diagnosis` TEXT DEFAULT NULL COMMENT '诊断结果',
    `prescription` TEXT DEFAULT NULL COMMENT '处方',
    `advice` TEXT DEFAULT NULL COMMENT '医嘱',
    `follow_up_date` DATE DEFAULT NULL COMMENT '复诊日期',
    `deleted` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_patient` (`patient_id`),
    KEY `idx_doctor` (`doctor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='诊断记录表';

-- 系统通知表
CREATE TABLE `notification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '接收用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT DEFAULT NULL COMMENT '内容',
    `type` TINYINT DEFAULT 1 COMMENT '类型：1系统通知 2预约提醒 3诊断结果',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读：0未读 1已读',
    `deleted` TINYINT NOT NULL DEFAULT 0,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_read` (`user_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统通知表';

-- 初始管理员账号（密码: admin123，BCrypt加密）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `role`, `status`)
VALUES ('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PSRSKnWQhjz1CHlQVsmKq', '系统管理员', 1, 1);
