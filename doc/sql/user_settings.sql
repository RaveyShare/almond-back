CREATE TABLE user_settings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL UNIQUE COMMENT '用户ID',
    auto_classify_permission VARCHAR(20) DEFAULT 'not_asked' COMMENT '自动分类权限: always/ask/never/not_asked',
    creator         varchar(50) COMMENT '创建人',
    creator_id      varchar(50) COMMENT '创建人ID',
    create_time     datetime default CURRENT_TIMESTAMP COMMENT '创建时间',
    updater         varchar(50) COMMENT '更新人',
    updater_id      varchar(50) COMMENT '更新人ID',
    update_time     datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户设置表';

-- Foreign key is documented but not enforced if users table is missing in this DB
-- FOREIGN KEY (user_id) REFERENCES users(id)
