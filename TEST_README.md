# 杏仁服务测试体系文档

本文档描述了杏仁服务（Almond Service）的单元测试体系、测试范围、覆盖率要求及运行方式。

## 1. 测试框架与工具

- **测试框架**: JUnit 5
- **Mock工具**: Mockito (Core + Inline)
- **断言工具**: AssertJ / Hamcrest
- **覆盖率工具**: JaCoCo
- **Web测试**: Spring MockMvc (Standalone Setup)

## 2. 测试范围

### 2.1 核心业务服务 (AlmondServiceImpl)

文件: `almond-back-service/.../impl/AlmondServiceImplTest.java`

覆盖场景:
- **创建杏仁 (`createAlmond`)**:
    - 正常流程: 持久化Item、StateLog，注册事务后异步AI理解。
    - 事务同步: 验证仅在事务提交后触发AI理解。
    - 异常处理: 验证输入验证和数据完整性。
- **查询杏仁 (`getAlmond`, `getAlmondDetail`, `listAlmonds`)**:
    - 验证DTO转换逻辑。
    - 验证权限校验（如非本人无法访问）。
    - 验证关联数据聚合（标签、日志、AI快照等）。

### 2.2 AI理解服务 (AlmondUnderstandingAiService)

文件: `almond-back-service/.../ai/AlmondUnderstandingAiServiceTest.java`

覆盖场景:
- **正常理解流程**:
    - 状态流转: RAW -> UNDERSTANDING -> UNDERSTOOD (高置信度)。
    - 标签保存: 验证AI返回的标签被正确持久化。
    - 日志记录: 验证AI触发的状态变更日志。
- **边界与异常**:
    - **低置信度**: 回退到 CREATED 阶段。
    - **中等置信度**: 更新内容但保持 RAW 状态。
    - **状态检查**: 非RAW状态或非CREATED阶段应跳过处理。
    - **调用失败**: AI接口异常时的容错处理。

### 2.3 API接口 (AlmondController)

文件: `almond-back-web/.../front/AlmondControllerTest.java`

覆盖场景:
- **输入验证**: `@Validated` 注解生效，如 content 不能为空。
- **服务委托**: 验证Controller正确调用Service并返回结果。
- **HTTP状态码**: 成功返回200，参数错误返回400等。

## 3. 覆盖率要求

通过 JaCoCo Maven 插件强制执行以下覆盖率门禁：

- **目标类**:
    - `com.ravey.almond.service.impl.AlmondServiceImpl`
    - `com.ravey.almond.service.ai.AlmondUnderstandingAiService`
    - `com.ravey.almond.web.controller.front.AlmondController`
- **指标**: 行覆盖率 (Line Coverage) >= **80%**

## 4. 运行测试

### 4.1 本地运行

执行所有测试并生成报告：

```bash
mvn verify
```

仅运行测试：

```bash
mvn test
```

查看覆盖率报告：
- Service层: `almond-back-service/target/site/jacoco/index.html`
- Web层: `almond-back-web/target/site/jacoco/index.html`

### 4.2 CI/CD 集成

项目配置了 GitHub Actions (`.github/workflows/test.yml`)，在每次 Push 或 PR 到 `main`/`develop` 分支时自动触发：
1. 设置 JDK 17 环境。
2. 配置 Maven Settings (GitHub Packages)。
3. 执行 `mvn verify`。
4. 检查覆盖率门禁，未达标将导致构建失败。
5. 上传 JaCoCo 覆盖率报告。

## 5. 维护指南

- 新增业务逻辑时，必须同步添加单元测试。
- 尽量使用 `Mockito.when(...).thenReturn(...)` 模拟外部依赖（如 Mapper、AI SDK），避免依赖真实数据库或外部服务。
- 对于静态方法（如 `UserCache.getUserId()`），使用 `Mockito.mockStatic` 进行模拟。
- 保持测试代码简洁，每个测试方法只验证一个核心逻辑。
