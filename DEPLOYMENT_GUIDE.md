# 杏仁库列表功能 - 部署和集成指南

## 一、代码集成步骤

### 1. 复制文件到项目

将以下文件复制到你的项目对应目录：

#### API模块文件
```bash
# 复制到 almond-back-api 模块
almond-back-api/src/main/java/com/ravey/almond/api/model/req/AlmondListReq.java
almond-back-api/src/main/java/com/ravey/almond/api/model/res/AlmondListResp.java
almond-back-api/src/main/java/com/ravey/almond/api/model/res/AlmondListItemResp.java
almond-back-api/src/main/java/com/ravey/almond/api/model/res/AlmondStatistics.java
almond-back-api/src/main/java/com/ravey/almond/api/service/AlmondService.java  # 替换原文件
```

#### Service模块文件
```bash
# 复制到 almond-back-service 模块
almond-back-service/src/main/java/com/ravey/almond/service/dao/mapper/AlmondItemMapper.java  # 替换原文件
almond-back-service/src/main/java/com/ravey/almond/service/impl/AlmondServiceImpl.java  # 替换原文件
almond-back-service/src/main/resources/mappings/AlmondItemMapper.xml  # 替换原文件
```

#### Web模块文件
```bash
# 复制到 almond-back-web 模块
almond-back-web/src/main/java/com/ravey/almond/web/controller/front/AlmondController.java  # 替换原文件
```

### 2. 数据库索引优化（可选但推荐）

```bash
# 执行索引优化SQL
mysql -u your_username -p your_database < doc/sql/add_list_indexes.sql
```

或者手动执行关键索引：
```sql
-- 最重要的索引
ALTER TABLE almond_item ADD INDEX idx_user_status_time (user_id, almond_status, update_time DESC);
```

### 3. 编译项目

```bash
# 在项目根目录执行
mvn clean compile -s ~/Documents/soft/apache-maven-3.9.9-private/conf/settings.xml
```

### 4. 运行测试（可选）

```bash
mvn test -s ~/Documents/soft/apache-maven-3.9.9-private/conf/settings.xml
```

## 二、验证步骤

### 1. 启动应用

```bash
# 启动应用
cd almond-back-start
mvn spring-boot:run -s ~/Documents/soft/apache-maven-3.9.9-private/conf/settings.xml
```

### 2. 测试接口

使用以下curl命令测试接口：

```bash
# 获取Token（根据你的认证方式）
TOKEN="your_jwt_token_here"

# 测试列表查询 - 基础查询
curl -X POST "http://localhost:8082/front/almonds/list" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pageNum": 1,
    "pageSize": 20
  }'

# 测试列表查询 - 带筛选条件
curl -X POST "http://localhost:8082/front/almonds/list" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pageNum": 1,
    "pageSize": 20,
    "almondStatus": "converged",
    "finalType": "goal",
    "starred": 1,
    "sortBy": "update_time",
    "sortOrder": "desc"
  }'

# 测试列表查询 - 关键词搜索
curl -X POST "http://localhost:8082/front/almonds/list" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pageNum": 1,
    "pageSize": 20,
    "keyword": "学习"
  }'
```

### 3. 检查返回数据

正常返回应该包含：
- `total`: 总数
- `list`: 杏仁列表数组
- `statistics`: 统计信息对象

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 100,
    "list": [...],
    "statistics": {
      "totalCount": 100,
      "statusCount": {...},
      "typeCount": {...},
      "starredCount": 12
    }
  }
}
```

## 三、Swagger文档访问

如果启用了Swagger：

```
# Knife4j文档地址
http://localhost:8082/doc.html
```

在Swagger中可以找到新增的接口：
- 接口路径：`/front/almonds/list`
- 接口名称：查询杏仁库列表

## 四、性能监控

### 1. 检查慢查询日志

```sql
-- 检查慢查询
SELECT * FROM mysql.slow_log WHERE sql_text LIKE '%almond_item%' ORDER BY start_time DESC LIMIT 10;
```

### 2. 分析执行计划

```sql
-- 分析列表查询的执行计划
EXPLAIN SELECT * FROM almond_item 
WHERE user_id = 1 AND almond_status = 'converged' 
ORDER BY update_time DESC LIMIT 20;
```

### 3. 监控指标

关注以下指标：
- 接口响应时间（建议 < 200ms）
- 数据库查询时间（建议 < 50ms）
- 并发请求处理能力

## 五、常见问题排查

### 问题1：接口404

**排查步骤：**
1. 检查Controller是否被Spring扫描到
2. 检查请求路径是否正确：`POST /front/almonds/list`
3. 查看启动日志中的mapping信息

### 问题2：查询很慢

**排查步骤：**
1. 检查是否创建了索引
2. 使用EXPLAIN分析SQL执行计划
3. 检查是否有大量数据但未使用分页

**解决方案：**
```sql
-- 确保关键索引存在
SHOW INDEX FROM almond_item;

-- 分析表统计信息
ANALYZE TABLE almond_item;
```

### 问题3：统计数据不准确

**排查步骤：**
1. 检查数据库中的实际数据
2. 检查SQL的GROUP BY逻辑
3. 检查是否有数据类型转换问题

### 问题4：Token认证失败

**排查步骤：**
1. 检查TokenInterceptor配置
2. 检查Token是否正确传递
3. 查看拦截器日志

## 六、后续优化建议

### 短期优化（1-2周）
1. 实现批量查询状态日志和AI分析的优化
2. 添加Redis缓存统计信息
3. 添加接口访问日志

### 中期优化（1个月）
1. 实现更复杂的筛选条件（时间范围、多标签）
2. 添加全文搜索功能（Elasticsearch）
3. 实现导出功能

### 长期优化（3个月）
1. 实现实时统计（WebSocket推送）
2. 添加个性化推荐
3. 实现数据归档和冷热分离

## 七、回滚方案

如果出现问题需要回滚：

### 1. 代码回滚
```bash
# 使用git回滚到上一个版本
git revert <commit_hash>
```

### 2. 数据库回滚
```sql
-- 删除新增的索引
DROP INDEX idx_user_status_time ON almond_item;
DROP INDEX idx_user_type_time ON almond_item;
DROP INDEX idx_user_starred_time ON almond_item;
DROP INDEX idx_user_create_time ON almond_item;
DROP INDEX idx_user_maturity ON almond_item;
```

### 3. 接口降级
如果接口有问题但不能立即修复，可以：
1. 临时关闭新接口
2. 使用原有的简单查询接口
3. 添加熔断机制

## 八、联系方式

如有问题，请联系：
- 开发者：Ravey
- 文档维护：查看 CHANGELOG.md 和 PR_DESCRIPTION.md
