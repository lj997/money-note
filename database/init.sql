-- 创建数据库
CREATE DATABASE IF NOT EXISTS money_note DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE money_note;

-- 用户表
CREATE TABLE IF NOT EXISTS t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(MD5加密)',
    nickname VARCHAR(50) COMMENT '昵称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS t_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    type TINYINT NOT NULL COMMENT '类型: 0-支出, 1-收入',
    icon VARCHAR(10) DEFAULT '📝' COMMENT '图标',
    sort INT DEFAULT 99 COMMENT '排序',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认分类: 0-否, 1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_user_id (user_id),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 记账记录表
CREATE TABLE IF NOT EXISTS t_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type TINYINT NOT NULL COMMENT '类型: 0-支出, 1-收入',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    remark VARCHAR(200) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_user_id (user_id),
    INDEX idx_record_date (record_date),
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记账记录表';

-- 预算表
CREATE TABLE IF NOT EXISTS t_budget (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    year INT NOT NULL COMMENT '年份',
    month INT NOT NULL COMMENT '月份',
    budget_amount DECIMAL(10,2) NOT NULL COMMENT '预算金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
    INDEX idx_user_id (user_id),
    INDEX idx_year_month (year, month),
    UNIQUE KEY uk_user_category_year_month (user_id, category_id, year, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预算表';
