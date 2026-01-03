# 杏仁库列表功能 - 文件变更清单

## 新增文件

### API模块 (almond-back-api)
1. `src/main/java/com/ravey/almond/api/model/req/AlmondListReq.java`
   - 杏仁库列表查询请求对象

2. `src/main/java/com/ravey/almond/api/model/res/AlmondListResp.java`
   - 杏仁库列表响应对象

3. `src/main/java/com/ravey/almond/api/model/res/AlmondListItemResp.java`
   - 杏仁列表项响应对象（包含内嵌类：TagInfo, ActionInfo, StateLogInfo, AiAnalysisInfo）

4. `src/main/java/com/ravey/almond/api/model/res/AlmondStatistics.java`
   - 统计信息响应对象

### Service模块 (almond-back-service)
5. `src/main/java/com/ravey/almond/service/dao/mapper/AlmondItemMapper.java`
   - 扩展的Mapper接口（新增列表查询和统计方法）

6. `src/main/resources/mappings/AlmondItemMapper.xml`
   - 完整的Mapper XML配置（包含新增和原有的SQL）

7. `src/main/java/com/ravey/almond/service/impl/AlmondServiceImpl.java`
   - 扩展的Service实现类（新增listAlmonds方法）

### Web模块 (almond-back-web)
8. `src/main/java/com/ravey/almond/web/controller/front/AlmondController.java`
   - 扩展的Controller（新增list接口）

## 修改文件

### API模块
1. `src/main/java/com/ravey/almond/api/service/AlmondService.java`
   - 新增 `listAlmonds(AlmondListReq req)` 方法声明

## 数据库相关

### 已有索引（需确保存在）
```sql
-- 这些索引在init.sql中已定义，需确保已创建
CREATE INDEX idx_user_status ON almond_item(user_id, almond_status);
CREATE INDEX idx_user_final ON almond_item(user_id, final_type);
```

### 建议新增索引（可选，用于性能优化）
```sql
-- 用户+状态+更新时间的复合索引（用于列表查询排序）
CREATE INDEX idx_user_status_time ON almond_item(user_id, almond_status, update_time DESC);

-- 用户+类型+更新时间的复合索引
CREATE INDEX idx_user_type_time ON almond_item(user_id, final_type, update_time DESC);

-- 用户+星标+更新时间的复合索引
CREATE INDEX idx_user_starred_time ON almond_item(user_id, starred, update_time DESC);
```

## 依赖关系

```
AlmondController (Web层)
    ↓
AlmondService (API接口层)
    ↓
AlmondServiceImpl (Service实现层)
    ↓
AlmondItemMapper (Mapper层)
    ↓
almond_item 表 (数据库层)
```

## 接口路径

```
POST /front/almonds/list
```

## 主要功能点

1. ✅ 分页查询
2. ✅ 多条件筛选（状态、类型、星标、关键词）
3. ✅ 多字段排序（创建时间、更新时间、成熟度）
4. ✅ 统计信息（总数、状态分布、类型分布、星标数）
5. ✅ 关联数据查询（标签、状态日志、AI分析）
6. ⚠️ 批量查询优化（部分TODO，可后续优化）

## 注意事项

1. 所有新增代码的author都使用"Ravey"
2. 遵循项目现有的代码规范和目录结构
3. 使用MyBatis XML方式编写SQL，不使用lambda表达式
4. 所有异常需要友好提示并记录日志
5. API接口需添加@Operation注解用于文档生成
6. 请求和响应对象需添加@Schema注解

## 测试清单

- [ ] 基础列表查询测试
- [ ] 分页功能测试
- [ ] 各种筛选条件组合测试
- [ ] 排序功能测试
- [ ] 统计信息准确性测试
- [ ] 大数据量性能测试
- [ ] 并发查询测试
- [ ] 异常情况测试

## 下一步工作

1. 实现批量查询状态日志的SQL优化
2. 实现批量查询AI分析的SQL优化
3. 考虑添加Redis缓存提升查询性能
4. 添加更多筛选条件（时间范围、标签筛选等）
5. 前端集成测试
