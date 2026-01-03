# 杏仁库列表查询功能实现

## 功能概述
本PR实现了杏仁库的列表查询功能，包括分页、筛选、排序和统计信息展示。

## 主要改动

### 1. API层 (almond-back-api)

#### 新增请求对象
- `AlmondListReq.java` - 杏仁库列表查询请求
  - 支持分页参数 (pageNum, pageSize)
  - 支持筛选条件 (almondStatus, finalType, starred, keyword)
  - 支持排序 (sortBy, sortOrder)

#### 新增响应对象
- `AlmondListResp.java` - 杏仁库列表响应（包含列表、总数和统计信息）
- `AlmondListItemResp.java` - 杏仁列表项详细信息
  - 包含基础信息
  - 包含标签信息
  - 包含行动执行信息（action类型）
  - 包含最新状态日志
  - 包含最新AI分析
- `AlmondStatistics.java` - 统计信息（按状态、类型统计）

#### 接口扩展
- `AlmondService.java` - 新增 `listAlmonds()` 方法

### 2. Service层 (almond-back-service)

#### Mapper扩展
- `AlmondItemMapper.java` - 新增查询方法
  - `selectAlmondList()` - 分页查询列表
  - `countAlmondList()` - 统计总数
  - `countByStatus()` - 按状态统计
  - `countByFinalType()` - 按类型统计
  - `countStarred()` - 统计星标数量

#### Mapper XML
- `AlmondItemMapper.xml` - 实现对应的SQL查询
  - 支持动态条件查询
  - 支持多字段排序
  - 使用索引优化查询性能

#### Service实现
- `AlmondServiceImpl.java` - 实现列表查询逻辑
  - 分页查询
  - 批量查询关联数据（标签、状态日志、AI分析）
  - 统计信息汇总

### 3. Web层 (almond-back-web)

#### Controller扩展
- `AlmondController.java` - 新增 `/front/almonds/list` 接口
  - POST 请求
  - 返回分页列表和统计信息

## 技术要点

### 1. 性能优化
- 使用数据库索引优化查询（idx_user_status_time, idx_user_type_time）
- 批量查询关联数据，减少数据库访问次数
- 分页查询，避免一次性加载大量数据

### 2. 扩展性
- 预留了批量查询方法的实现空间
- 支持灵活的筛选和排序条件
- 统计信息可按需扩展

### 3. 代码规范
- 所有类添加了 @Schema 注解用于API文档
- 遵循项目命名规范（Req/Resp后缀）
- 使用 Lombok 减少样板代码
- 统一使用 MyBatis XML 编写SQL

## 测试建议

### 单元测试
1. 测试不同筛选条件的组合
2. 测试排序功能
3. 测试分页边界情况
4. 测试统计信息准确性

### 集成测试
1. 测试完整的查询流程
2. 测试大数据量下的性能
3. 测试并发查询

## API示例

### 请求示例
```json
POST /front/almonds/list
{
  "pageNum": 1,
  "pageSize": 20,
  "almondStatus": "converged",
  "finalType": "goal",
  "starred": 1,
  "keyword": "学习",
  "sortBy": "update_time",
  "sortOrder": "desc"
}
```

### 响应示例
```json
{
  "code": 200,
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "title": "学习 Rust 的所有权机制",
        "almondStatus": "converged",
        "finalType": "goal",
        "maturityScore": 85,
        "starred": 1,
        "tags": [
          {"name": "编程", "tagType": "cognitive"},
          {"name": "学习", "tagType": "cognitive"}
        ],
        "createTime": "2024-01-02T10:30:00",
        "updateTime": "2024-01-02T14:20:00"
      }
    ],
    "statistics": {
      "totalCount": 100,
      "statusCount": {
        "raw": 5,
        "understood": 8,
        "converged": 60
      },
      "typeCount": {
        "memory": 20,
        "action": 30,
        "goal": 25
      },
      "starredCount": 12
    }
  }
}
```

## 后续优化建议

1. 实现批量查询状态日志和AI分析的SQL
2. 添加缓存机制提升查询性能
3. 支持更复杂的筛选条件（时间范围、标签筛选等）
4. 添加导出功能

## 相关文档
- 数据库设计文档: `doc/sql/init.sql`
- 状态流转文档: `doc/小杏仁状态流转与落地文档.md`
