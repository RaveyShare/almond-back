CREATE TABLE IF NOT EXISTS task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    parent_id BIGINT DEFAULT NULL COMMENT '父任务ID，用于任务拆分',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    title VARCHAR(500) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述/记忆项正文',
    category VARCHAR(100) DEFAULT '其他' COMMENT '分类',
    task_type VARCHAR(50) DEFAULT 'task' COMMENT '类型(task/memory/goal)',
    item_type VARCHAR(50) DEFAULT 'general' COMMENT '详细类型(例如 general/qa/flashcard 等)',
    level VARCHAR(20) DEFAULT 'inbox' COMMENT '任务层级(year/quarter/month/week/day/inbox)',
    tags JSON COMMENT '标签列表',
    difficulty VARCHAR(20) DEFAULT 'medium' COMMENT '难度(easy/medium/hard)',
    mastery INT NOT NULL DEFAULT 0 COMMENT '掌握度',
    review_count INT NOT NULL DEFAULT 0 COMMENT '复习次数',
    review_date DATETIME COMMENT '最近复习时间',
    next_review_date DATETIME COMMENT '下次复习时间',
    starred TINYINT DEFAULT 0 COMMENT '是否加星(0否/1是)',
    start_date DATETIME COMMENT '计划开始时间',
    end_date DATETIME COMMENT '计划结束时间',
    actual_start DATETIME COMMENT '实际开始时间',
    actual_end DATETIME COMMENT '实际结束时间',
    status VARCHAR(20) DEFAULT 'todo' COMMENT '状态(todo/doing/done/archived)',
    priority TINYINT DEFAULT 0 COMMENT '优先级',
    order_index INT DEFAULT 0 COMMENT '排序',
    creator VARCHAR(50) COMMENT '创建人',
    creator_id VARCHAR(50) COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(50) COMMENT '更新人',
    updater_id VARCHAR(50) COMMENT '更新人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_user_id (user_id),
    INDEX idx_task_parent_id (parent_id),
    INDEX idx_task_type (task_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='统一任务/记忆表';

CREATE TABLE IF NOT EXISTS review_schedules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '关联的任务/记忆项ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    review_date DATETIME NOT NULL COMMENT '计划复习时间',
    interval_days INT DEFAULT 0 COMMENT '复习间隔天数',
    repetition INT DEFAULT 0 COMMENT '复习次数',
    easiness_factor DOUBLE DEFAULT 2.5 COMMENT '简易系数',
    completed TINYINT NOT NULL DEFAULT 0 COMMENT '是否完成(0否/1是)',
    creator VARCHAR(50) COMMENT '创建人',
    creator_id VARCHAR(50) COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(50) COMMENT '更新人',
    updater_id VARCHAR(50) COMMENT '更新人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_review_schedules_user_id (user_id),
    INDEX idx_review_schedules_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复习计划表';

CREATE TABLE IF NOT EXISTS memory_aids (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT COMMENT '关联的任务/记忆项ID',
    user_id BIGINT COMMENT '所属用户ID',
    mind_map_data JSON COMMENT '思维导图数据(JSON)',
    mnemonics_data JSON COMMENT '助记术数据(JSON)',
    sensory_associations_data JSON COMMENT '多感官联想数据(JSON)',
    creator VARCHAR(50) COMMENT '创建人',
    creator_id VARCHAR(50) COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(50) COMMENT '更新人',
    updater_id VARCHAR(50) COMMENT '更新人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_memory_aids_user_id (user_id),
    INDEX idx_memory_aids_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='记忆辅助信息表';

CREATE TABLE IF NOT EXISTS shares (
    id VARCHAR(255) PRIMARY KEY COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '关联的任务/记忆项ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    share_type VARCHAR(50) NOT NULL COMMENT '分享类型',
    content_id VARCHAR(255) COMMENT '关联内容ID',
    share_content JSON NOT NULL COMMENT '分享内容(JSON)',
    expires_at DATETIME COMMENT '过期时间',
    creator VARCHAR(50) COMMENT '创建人',
    creator_id VARCHAR(50) COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(50) COMMENT '更新人',
    updater_id VARCHAR(50) COMMENT '更新人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_shares_user_id (user_id),
    INDEX idx_shares_task_id (task_id),
    INDEX idx_shares_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分享记录表';

CREATE TABLE IF NOT EXISTS task_tag (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '标签名称',
    tag_type VARCHAR(20) DEFAULT 'topic' COMMENT '标签类型(topic/skill/emotion/context)',
    creator VARCHAR(50) COMMENT '创建人',
    creator_id VARCHAR(50) COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(50) COMMENT '更新人',
    updater_id VARCHAR(50) COMMENT '更新人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务标签表';

CREATE TABLE IF NOT EXISTS task_tag_relation (
    task_id BIGINT NOT NULL COMMENT '任务ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    creator VARCHAR(50) COMMENT '创建人',
    creator_id VARCHAR(50) COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(50) COMMENT '更新人',
    updater_id VARCHAR(50) COMMENT '更新人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (task_id, tag_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务标签关联表';

CREATE TABLE IF NOT EXISTS review (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    level VARCHAR(50) NOT NULL COMMENT '复盘层级(week/month/quarter/year)',
    period_start DATE NOT NULL COMMENT '周期开始日期',
    period_end DATE NOT NULL COMMENT '周期结束日期',
    rating INT DEFAULT NULL COMMENT '评分(1-5)',
    content LONGTEXT COMMENT '主复盘内容',
    fields_json JSON COMMENT '自定义字段(JSON)',
    attachments JSON COMMENT '附件(JSON)',
    ai_summary LONGTEXT COMMENT 'AI总结',
    creator VARCHAR(50) COMMENT '创建人',
    creator_id VARCHAR(50) COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(50) COMMENT '更新人',
    updater_id VARCHAR(50) COMMENT '更新人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_review_user_id (user_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复盘表';

CREATE TABLE IF NOT EXISTS automation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT COMMENT '所属用户ID',
    task_id BIGINT COMMENT '关联任务ID',
    action VARCHAR(255) COMMENT '执行动作',
    result_json JSON COMMENT '执行结果(JSON)',
    creator VARCHAR(50) COMMENT '创建人',
    creator_id VARCHAR(50) COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(50) COMMENT '更新人',
    updater_id VARCHAR(50) COMMENT '更新人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_automation_log_user_id (user_id),
    INDEX idx_automation_log_task_id (task_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自动化日志表';

CREATE TABLE IF NOT EXISTS task_execution (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    task_id BIGINT NOT NULL COMMENT '关联任务ID',
    actual_start DATETIME COMMENT '实际开始时间',
    actual_end DATETIME COMMENT '实际结束时间',
    creator VARCHAR(50) COMMENT '创建人',
    creator_id VARCHAR(50) COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(50) COMMENT '更新人',
    updater_id VARCHAR(50) COMMENT '更新人ID',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_task_execution_task_id (task_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行记录表';


ALTER TABLE review_schedules
    ADD COLUMN interval_days INT DEFAULT 0 COMMENT '复习间隔天数',
    ADD COLUMN repetition INT DEFAULT 0 COMMENT '复习次数',
    ADD COLUMN easiness_factor DOUBLE DEFAULT 2.5 COMMENT '简易系数';