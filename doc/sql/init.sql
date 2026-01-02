create table almond_item
(
    id                bigint auto_increment primary key comment '杏仁ID',
    parent_id         bigint null comment '父杏仁ID（拆解/派生）',
    user_id           bigint not null comment '用户ID',

    title             varchar(500) null comment 'AI生成原始输入摘要',
    content           text null comment '原始内容',
    clarified_content text null comment 'AI澄清后的内容，用户可修改',
    almond_status     varchar(30) default 'raw' not null comment
        '成熟度状态: raw/understood/evolving/converged/archived',
    final_type        varchar(30) null comment
        '终态类型: memory/action/goal/decision/review',
    maturity_score    int default 0 not null comment '成熟度评分(0-100)',
    evolution_stage   int default 0 not null comment '演化阶段',
    user_feedback     varchar(30) null comment 'accept/modify/reject',

    priority          tinyint default 0,
    starred           tinyint default 0,

    creator           varchar(50),
    creator_id        varchar(50),
    create_time       datetime default CURRENT_TIMESTAMP,
    updater           varchar(50),
    updater_id        varchar(50),
    update_time       datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)
    comment '杏仁核心表' charset=utf8mb4;

create index idx_user_status on almond_item(user_id, almond_status);
create index idx_user_final on almond_item(user_id, final_type);

create table almond_ai_snapshot
(
    id              bigint auto_increment primary key,
    almond_id       bigint not null,
    user_id         bigint not null,

    analysis_type   varchar(50) not null comment
        'understanding/evolution_check/convergence_check',

    ai_model        varchar(100),
    prompt_content  text,

    analysis_result json not null comment
        '{ maturity, possible_final_types, confidence, reasoning }',

    status          varchar(20) default 'success',
    cost_time       int,

    creator         varchar(50),
    creator_id      varchar(50),
    create_time     datetime default CURRENT_TIMESTAMP,
    updater         varchar(50),
    updater_id      varchar(50),
    update_time     datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)
    comment '杏仁AI分析快照表';

create index idx_snapshot_almond on almond_ai_snapshot(almond_id);
create index idx_snapshot_type on almond_ai_snapshot(analysis_type);

create table almond_state_log
(
    id             bigint auto_increment primary key,
    almond_id      bigint not null,
    user_id        bigint not null,

    from_status    varchar(30)  null,
    to_status      varchar(30) not null,

    trigger_type   varchar(30) not null comment 'ai/user/system/time',
    trigger_event  varchar(100),
    context_data   json,
    description    text,

    creator        varchar(50),
    creator_id     varchar(50),
    create_time    datetime default CURRENT_TIMESTAMP,
    updater         varchar(50),
    updater_id      varchar(50),
    update_time     datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)
    comment '杏仁状态演化日志';

create index idx_state_log on almond_state_log(almond_id, create_time);

create table almond_tag
(
    id          bigint auto_increment primary key,
    name        varchar(100) not null,
    tag_type    varchar(30) default 'cognitive' comment
        'topic/emotion/context/cognitive',

    creator     varchar(50),
    creator_id  varchar(50),
    create_time datetime default CURRENT_TIMESTAMP,
    updater     varchar(50),
    updater_id  varchar(50),
    update_time datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)
    comment '杏仁标签表';

create table almond_tag_relation
(
    almond_id  bigint not null,
    tag_id     bigint not null,

    creator     varchar(50),
    creator_id  varchar(50),
    create_time datetime default CURRENT_TIMESTAMP,
    updater     varchar(50),
    updater_id  varchar(50),
    update_time datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,

    primary key (almond_id, tag_id)
)
    comment '杏仁-标签关联表';

create table action_execution
(
    id            bigint auto_increment primary key,
    almond_id     bigint not null,

    actual_start  datetime,
    actual_end    datetime,

    creator       varchar(50),
    creator_id    varchar(50),
    create_time   datetime default CURRENT_TIMESTAMP,
    updater       varchar(50),
    updater_id    varchar(50),
    update_time   datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)
    comment '行动执行表';

create table memory_aids
(
    id              bigint auto_increment primary key,
    almond_id       bigint not null,

    mind_map_data   json,
    mnemonics_data  json,
    sensory_data    json,

    creator         varchar(50),
    creator_id      varchar(50),
    create_time     datetime default CURRENT_TIMESTAMP,
    updater         varchar(50),
    updater_id      varchar(50),
    update_time     datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)
    comment '记忆辅助表';

create table review
(
    id            bigint auto_increment primary key,
    almond_id     bigint not null,
    user_id       bigint not null,

    level         varchar(30) not null comment 'week/month/quarter/year',
    period_start  date not null,
    period_end    date not null,

    rating        int,
    content       longtext,
    ai_summary    longtext,

    creator       varchar(50),
    creator_id    varchar(50),
    create_time   datetime default CURRENT_TIMESTAMP,
    updater       varchar(50),
    updater_id    varchar(50),
    update_time   datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)
    comment '复盘表';

create table review_schedule
(
    id            bigint auto_increment primary key,
    almond_id     bigint not null,
    user_id       bigint not null,

    review_date   datetime not null,
    completed     tinyint default 0,

    interval_days int default 0,
    repetition    int default 0,
    easiness      double default 2.5,

    creator       varchar(50),
    creator_id    varchar(50),
    create_time   datetime default CURRENT_TIMESTAMP,
    updater       varchar(50),
    updater_id    varchar(50),
    update_time   datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)
    comment '复习计划表';

create table automation_log
(
    id          bigint auto_increment primary key,
    user_id     bigint,
    almond_id   bigint,

    action      varchar(255),
    result_json json,

    creator     varchar(50),
    creator_id  varchar(50),
    create_time datetime default CURRENT_TIMESTAMP,
    updater     varchar(50),
    updater_id  varchar(50),
    update_time datetime default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
)
    comment '自动化日志表';
