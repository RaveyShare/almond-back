#!/bin/bash

# 杏仁库列表功能 - Git提交和PR创建脚本
# 使用方法: ./git_commit_and_pr.sh

set -e  # 遇到错误立即退出

echo "================================"
echo "杏仁库列表功能 - 代码提交脚本"
echo "================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查是否在git仓库中
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo -e "${RED}错误: 当前目录不是git仓库${NC}"
    exit 1
fi

# 检查工作区是否干净
if [[ -n $(git status -s) ]]; then
    echo -e "${YELLOW}警告: 工作区有未提交的更改${NC}"
    git status -s
    echo ""
    read -p "是否继续? (y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 创建新分支
BRANCH_NAME="feature/almond-list-query"
echo "步骤1: 创建并切换到新分支: $BRANCH_NAME"
git checkout -b $BRANCH_NAME 2>/dev/null || git checkout $BRANCH_NAME
echo -e "${GREEN}✓ 分支创建/切换成功${NC}"
echo ""

# 添加文件
echo "步骤2: 添加修改的文件"
echo "添加API模块文件..."
git add almond-back-api/src/main/java/com/ravey/almond/api/model/req/AlmondListReq.java
git add almond-back-api/src/main/java/com/ravey/almond/api/model/res/AlmondListResp.java
git add almond-back-api/src/main/java/com/ravey/almond/api/model/res/AlmondListItemResp.java
git add almond-back-api/src/main/java/com/ravey/almond/api/model/res/AlmondStatistics.java
git add almond-back-api/src/main/java/com/ravey/almond/api/service/AlmondService.java

echo "添加Service模块文件..."
git add almond-back-service/src/main/java/com/ravey/almond/service/dao/mapper/AlmondItemMapper.java
git add almond-back-service/src/main/java/com/ravey/almond/service/impl/AlmondServiceImpl.java
git add almond-back-service/src/main/resources/mappings/AlmondItemMapper.xml

echo "添加Web模块文件..."
git add almond-back-web/src/main/java/com/ravey/almond/web/controller/front/AlmondController.java

echo "添加文档文件..."
git add doc/sql/add_list_indexes.sql

echo -e "${GREEN}✓ 文件添加成功${NC}"
echo ""

# 查看将要提交的更改
echo "步骤3: 查看将要提交的更改"
git status
echo ""

# 提交更改
echo "步骤4: 提交更改"
COMMIT_MESSAGE="feat: 实现杏仁库列表查询功能

## 主要功能
- 支持分页查询杏仁列表
- 支持多条件筛选（状态、类型、星标、关键词）
- 支持多字段排序（创建时间、更新时间、成熟度）
- 提供统计信息（总数、状态分布、类型分布）
- 支持关联数据查询（标签、状态日志、AI分析）

## 技术实现
- 使用MyBatis XML编写SQL查询
- 添加数据库索引优化查询性能
- 实现批量查询减少数据库访问
- 遵循项目代码规范和命名约定

## API接口
- POST /front/almonds/list

## 相关文档
- CHANGELOG.md: 详细变更记录
- PR_DESCRIPTION.md: PR说明文档
- DEPLOYMENT_GUIDE.md: 部署指南
- doc/sql/add_list_indexes.sql: 索引优化SQL

Co-authored-by: Ravey"

git commit -m "$COMMIT_MESSAGE"
echo -e "${GREEN}✓ 提交成功${NC}"
echo ""

# 推送到远程
echo "步骤5: 推送到远程仓库"
echo "执行: git push origin $BRANCH_NAME"
git push origin $BRANCH_NAME

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 推送成功${NC}"
else
    echo -e "${RED}✗ 推送失败${NC}"
    exit 1
fi
echo ""

# 创建PR的说明
echo "================================"
echo "步骤6: 创建Pull Request"
echo "================================"
echo ""
echo -e "${YELLOW}请访问以下链接创建PR:${NC}"
echo "https://github.com/RaveyShare/almond-back/compare/main...$BRANCH_NAME"
echo ""
echo -e "${YELLOW}PR标题建议:${NC}"
echo "feat: 实现杏仁库列表查询功能"
echo ""
echo -e "${YELLOW}PR描述请使用:${NC}"
echo "请复制 PR_DESCRIPTION.md 的内容"
echo ""
echo -e "${GREEN}✓ 所有步骤完成！${NC}"
echo ""
echo "================================"
echo "下一步操作"
echo "================================"
echo "1. 访问GitHub创建Pull Request"
echo "2. 等待CI/CD检查通过"
echo "3. 请求代码审查"
echo "4. 合并到main分支"
echo ""
