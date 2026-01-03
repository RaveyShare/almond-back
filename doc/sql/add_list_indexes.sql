-- 杏仁库列表查询索引优化SQL
-- 执行日期：需根据实际情况执行
-- 说明：这些索引用于优化杏仁库列表查询性能

-- 检查是否已存在基础索引（init.sql中已定义）
-- SELECT * FROM information_schema.statistics 
-- WHERE table_name = 'almond_item' AND table_schema = 'almond';

-- 用户+状态+更新时间的复合索引（用于列表查询）
-- 场景：按状态筛选并按更新时间排序
ALTER TABLE almond_item 
ADD INDEX idx_user_status_time (user_id, almond_status, update_time DESC);

-- 用户+类型+更新时间的复合索引
-- 场景：按终态类型筛选并按更新时间排序
ALTER TABLE almond_item 
ADD INDEX idx_user_type_time (user_id, final_type, update_time DESC);

-- 用户+星标+更新时间的复合索引
-- 场景：查询星标杏仁并按更新时间排序
ALTER TABLE almond_item 
ADD INDEX idx_user_starred_time (user_id, starred, update_time DESC);

-- 用户+创建时间索引
-- 场景：按创建时间排序
ALTER TABLE almond_item 
ADD INDEX idx_user_create_time (user_id, create_time DESC);

-- 用户+成熟度评分索引
-- 场景：按成熟度评分排序
ALTER TABLE almond_item 
ADD INDEX idx_user_maturity (user_id, maturity_score DESC);

-- 验证索引创建
SELECT 
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    CARDINALITY,
    INDEX_TYPE
FROM information_schema.statistics 
WHERE table_name = 'almond_item' 
  AND table_schema = DATABASE()
ORDER BY INDEX_NAME, SEQ_IN_INDEX;

-- 索引使用情况说明：
-- 1. idx_user_status_time: 最常用，适用于按状态筛选+时间排序的场景
-- 2. idx_user_type_time: 适用于按类型筛选+时间排序的场景
-- 3. idx_user_starred_time: 适用于查看星标列表的场景
-- 4. idx_user_create_time: 适用于按创建时间排序的场景
-- 5. idx_user_maturity: 适用于按成熟度排序的场景

-- 注意事项：
-- 1. 索引会占用额外的存储空间
-- 2. 索引会影响写入性能（INSERT/UPDATE/DELETE）
-- 3. 定期分析表统计信息：ANALYZE TABLE almond_item;
-- 4. 根据实际查询情况决定是否需要所有索引
-- 5. 可以先创建最常用的索引，观察效果后再决定是否创建其他索引
