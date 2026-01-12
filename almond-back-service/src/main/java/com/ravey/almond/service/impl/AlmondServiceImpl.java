package com.ravey.almond.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ravey.almond.api.enums.AlmondMaturityStatus;
import com.ravey.almond.api.model.req.AlmondListReq;
import com.ravey.almond.api.model.req.CreateAlmondReq;
import com.ravey.almond.api.model.res.AlmondDetailResp;
import com.ravey.almond.api.model.res.AlmondItemResp;
import com.ravey.almond.api.model.res.AlmondListItemResp;
import com.ravey.almond.api.model.res.AlmondListResp;
import com.ravey.almond.api.model.res.AlmondStatistics;
import com.ravey.almond.api.service.AlmondService;
import com.ravey.almond.service.ai.AlmondUnderstandingAiService;
import com.ravey.almond.service.dao.entity.ActionExecution;
import com.ravey.almond.service.dao.entity.AlmondAiSnapshot;
import com.ravey.almond.service.dao.entity.AlmondItem;
import com.ravey.almond.service.dao.entity.AlmondStateLog;
import com.ravey.almond.service.dao.entity.AlmondTag;
import com.ravey.almond.service.dao.entity.MemoryAids;
import com.ravey.almond.service.dao.entity.ReviewSchedule;
import com.ravey.almond.service.dao.mapper.ActionExecutionMapper;
import com.ravey.almond.service.dao.mapper.AlmondAiSnapshotMapper;
import com.ravey.almond.service.dao.mapper.AlmondItemMapper;
import com.ravey.almond.service.dao.mapper.AlmondStateLogMapper;
import com.ravey.almond.service.dao.mapper.AlmondTagMapper;
import com.ravey.almond.service.dao.mapper.MemoryAidsMapper;
import com.ravey.almond.service.dao.mapper.ReviewScheduleMapper;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.utils.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 杏仁服务实现
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlmondServiceImpl implements AlmondService {

    private final AlmondItemMapper almondItemMapper;
    private final AlmondStateLogMapper almondStateLogMapper;
    private final AlmondTagMapper almondTagMapper;
    private final AlmondAiSnapshotMapper almondAiSnapshotMapper;
    private final AlmondUnderstandingAiService almondUnderstandingAiService;
    private final ActionExecutionMapper actionExecutionMapper;
    private final MemoryAidsMapper memoryAidsMapper;
    private final ReviewScheduleMapper reviewScheduleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlmondItemResp createAlmond(CreateAlmondReq req) {
        // 从上下文中获取用户ID
        Long userId = UserCache.getUserId();

        // 1. 创建杏仁项
        AlmondItem item = new AlmondItem();
        item.setUserId(userId);
        item.setContent(req.getContent());
        // 初始状态为 RAW
        item.setAlmondStatus(AlmondMaturityStatus.RAW.getCode());
        item.setMaturityScore(0);
        item.setEvolutionStage(0);
        item.setPriority(0);
        item.setStarred(0);
        
        almondItemMapper.insert(item);

        // 2. 记录状态日志
        AlmondStateLog log = new AlmondStateLog();
        log.setAlmondId(item.getId());
        log.setUserId(userId);
        log.setFromStatus(null);
        log.setToStatus(AlmondMaturityStatus.RAW.getCode());
        log.setTriggerType("USER");
        // 简单记录内容
        log.setContextData("{\"content\":\"" + escapeJson(req.getContent()) + "\"}");
        
        almondStateLogMapper.insert(log);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Long almondId = item.getId();
            String content = req.getContent();
            // 只有 enableAutoClassify=true 时才触发 AI 分析
            if (Boolean.TRUE.equals(req.getEnableAutoClassify())) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        almondUnderstandingAiService.understandAfterCreate(almondId, userId, content);
                    }
                });
            }
        }

        // 3. 转换为响应对象
        AlmondItemResp resp = new AlmondItemResp();
        BeanUtils.copyProperties(item, resp);
        return resp;
    }

    @Override
    public AlmondItemResp getAlmond(Long id) {
        AlmondItem item = almondItemMapper.selectById(id);
        if (item == null) {
            throw new RuntimeException("杏仁不存在");
        }

        AlmondItemResp resp = new AlmondItemResp();
        BeanUtils.copyProperties(item, resp);

        // 字段映射
        resp.setDescription(item.getContent());
        resp.setAiClassification(item.getFinalType());
        if (item.getMaturityScore() != null) {
            resp.setClassificationConfidence(item.getMaturityScore() / 100.0);
        }
        // status 映射为 almondStatus
        resp.setStatus(item.getAlmondStatus());
        resp.setTaskType(item.getFinalType());
        resp.setPriority(item.getPriority());

        // 查询标签
        List<String> tags = almondTagMapper.selectTagNamesByAlmondId(id);
        resp.setTags(tags);

        return resp;
    }

    @Override
    public AlmondListResp listAlmonds(AlmondListReq req) {
        // 从上下文中获取用户ID
        Long userId = UserCache.getUserId();

        // 计算分页参数
        int offset = (req.getPageNum() - 1) * req.getPageSize();

        String almondStatus = AlmondMaturityStatus.normalizeCode(req.getAlmondStatus());
        if (almondStatus != null) {
            almondStatus = almondStatus.toLowerCase(Locale.ROOT);
        }

        // 查询列表
        List<AlmondItem> items = almondItemMapper.selectAlmondList(
                userId,
                almondStatus,
                req.getFinalType(),
                req.getStarred(),
                req.getKeyword(),
                req.getSortBy(),
                req.getSortOrder(),
                offset,
                req.getPageSize()
        );

        // 查询总数
        Long total = almondItemMapper.countAlmondList(
                userId,
                almondStatus,
                req.getFinalType(),
                req.getStarred(),
                req.getKeyword()
        );

        // 转换为响应对象
        List<AlmondListItemResp> list = new ArrayList<>();
        if (!CollectionUtils.isEmpty(items)) {
            // 批量查询标签
            List<Long> almondIds = items.stream().map(AlmondItem::getId).collect(Collectors.toList());
            Map<Long, List<AlmondListItemResp.TagInfo>> tagsMap = batchQueryTags(almondIds);

            // 批量查询最新状态日志
            Map<Long, AlmondListItemResp.StateLogInfo> stateLogMap = batchQueryLatestStateLogs(almondIds);

            // 批量查询最新AI分析
            Map<Long, AlmondListItemResp.AiAnalysisInfo> aiAnalysisMap = batchQueryLatestAiAnalysis(almondIds);

            for (AlmondItem item : items) {
                AlmondListItemResp itemResp = new AlmondListItemResp();
                BeanUtils.copyProperties(item, itemResp);

                // 设置标签
                itemResp.setTags(tagsMap.get(item.getId()));

                // 设置状态日志
                itemResp.setLatestStateLog(stateLogMap.get(item.getId()));

                // 设置AI分析
                itemResp.setLatestAiAnalysis(aiAnalysisMap.get(item.getId()));

                list.add(itemResp);
            }
        }

        // 查询统计信息
        AlmondStatistics statistics = queryStatistics(userId);

        // 组装响应
        AlmondListResp resp = new AlmondListResp();
        resp.setTotal(total);
        resp.setList(list);
        resp.setStatistics(statistics);

        return resp;
    }

    /**
     * 批量查询标签
     */
    private Map<Long, List<AlmondListItemResp.TagInfo>> batchQueryTags(List<Long> almondIds) {
        // 这里简化处理，实际可以写一个批量查询SQL
        Map<Long, List<AlmondListItemResp.TagInfo>> result = new HashMap<>();
        for (Long almondId : almondIds) {
            List<String> tagNames = almondTagMapper.selectTagNamesByAlmondId(almondId);
            if (!CollectionUtils.isEmpty(tagNames)) {
                List<AlmondListItemResp.TagInfo> tags = tagNames.stream()
                        .map(name -> {
                            AlmondListItemResp.TagInfo tag = new AlmondListItemResp.TagInfo();
                            tag.setName(name);
                            tag.setTagType("cognitive");
                            return tag;
                        })
                        .collect(Collectors.toList());
                result.put(almondId, tags);
            }
        }
        return result;
    }

    /**
     * 批量查询最新状态日志
     */
    private Map<Long, AlmondListItemResp.StateLogInfo> batchQueryLatestStateLogs(List<Long> almondIds) {
        // 这里简化处理，实际应该写批量查询SQL
        Map<Long, AlmondListItemResp.StateLogInfo> result = new HashMap<>();
        // TODO: 实现批量查询逻辑
        return result;
    }

    /**
     * 批量查询最新AI分析
     */
    private Map<Long, AlmondListItemResp.AiAnalysisInfo> batchQueryLatestAiAnalysis(List<Long> almondIds) {
        // 这里简化处理，实际应该写批量查询SQL
        Map<Long, AlmondListItemResp.AiAnalysisInfo> result = new HashMap<>();
        // TODO: 实现批量查询逻辑
        return result;
    }

    /**
     * 查询统计信息
     */
    private AlmondStatistics queryStatistics(Long userId) {
        AlmondStatistics statistics = new AlmondStatistics();

        // 按状态统计
        List<Map<String, Object>> statusCounts = almondItemMapper.countByStatus(userId);
        Map<String, Long> statusMap = new HashMap<>();
        for (Map<String, Object> map : statusCounts) {
            String status = (String) map.get("status");
            Long count = ((Number) map.get("count")).longValue();
            statusMap.put(status == null ? null : status.toLowerCase(Locale.ROOT), count);
        }
        statistics.setStatusCount(statusMap);

        // 按类型统计
        List<Map<String, Object>> typeCounts = almondItemMapper.countByFinalType(userId);
        Map<String, Long> typeMap = new HashMap<>();
        for (Map<String, Object> map : typeCounts) {
            String type = (String) map.get("type");
            Long count = ((Number) map.get("count")).longValue();
            typeMap.put(type, count);
        }
        statistics.setTypeCount(typeMap);

        // 星标数量
        Long starredCount = almondItemMapper.countStarred(userId);
        statistics.setStarredCount(starredCount);

        // 总数
        Long totalCount = almondItemMapper.countAlmondList(userId, null, null, null, null);
        statistics.setTotalCount(totalCount);

        return statistics;
    }

    @Override
    public AlmondDetailResp getAlmondDetail(Long id) {
        // 从上下文中获取用户ID
        Long userId = UserCache.getUserId();

        // 1. 查询基本信息
        AlmondItem item = almondItemMapper.selectById(id);
        if (item == null) {
            throw new RuntimeException("杏仁不存在");
        }

        // 检查权限
        if (!item.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该杏仁");
        }

        AlmondDetailResp resp = new AlmondDetailResp();
        BeanUtils.copyProperties(item, resp);

        // 2. 查询标签详情
        List<AlmondTag> tags = almondTagMapper.selectTagsByAlmondId(id);
        if (!CollectionUtils.isEmpty(tags)) {
            List<AlmondDetailResp.TagInfo> tagInfos = tags.stream()
                    .map(tag -> {
                        AlmondDetailResp.TagInfo tagInfo = new AlmondDetailResp.TagInfo();
                        tagInfo.setId(tag.getId());
                        tagInfo.setName(tag.getName());
                        tagInfo.setTagType(tag.getTagType());
                        return tagInfo;
                    })
                    .collect(Collectors.toList());
            resp.setTags(tagInfos);
        }

        // 3. 查询状态日志
        List<AlmondStateLog> stateLogs = almondStateLogMapper.selectByAlmondId(id);
        if (!CollectionUtils.isEmpty(stateLogs)) {
            List<AlmondDetailResp.StateLogInfo> stateLogInfos = stateLogs.stream()
                    .map(log -> {
                        AlmondDetailResp.StateLogInfo logInfo = new AlmondDetailResp.StateLogInfo();
                        BeanUtils.copyProperties(log, logInfo);
                        return logInfo;
                    })
                    .collect(Collectors.toList());
            resp.setStateLogs(stateLogInfos);
        }

        // 4. 查询AI分析历史
        List<AlmondAiSnapshot> aiSnapshots = almondAiSnapshotMapper.selectByAlmondId(id);
        if (!CollectionUtils.isEmpty(aiSnapshots)) {
            List<AlmondDetailResp.AiSnapshotInfo> snapshotInfos = aiSnapshots.stream()
                    .map(snapshot -> {
                        AlmondDetailResp.AiSnapshotInfo snapshotInfo = new AlmondDetailResp.AiSnapshotInfo();
                        BeanUtils.copyProperties(snapshot, snapshotInfo);
                        return snapshotInfo;
                    })
                    .collect(Collectors.toList());
            resp.setAiSnapshots(snapshotInfos);
        }

        // 5. 根据类型查询特定信息
        if ("action".equals(item.getFinalType())) {
            // 查询行动执行信息
            ActionExecution actionExecution = actionExecutionMapper.selectByAlmondId(id);
            if (actionExecution != null) {
                AlmondDetailResp.ActionExecutionInfo executionInfo = new AlmondDetailResp.ActionExecutionInfo();
                executionInfo.setActualStart(actionExecution.getActualStart());
                executionInfo.setActualEnd(actionExecution.getActualEnd());
                resp.setActionExecution(executionInfo);
            }
        } else if ("memory".equals(item.getFinalType())) {
            // 查询记忆辅助信息
            MemoryAids memoryAids = memoryAidsMapper.selectByAlmondId(id);
            if (memoryAids != null) {
                AlmondDetailResp.MemoryAidsInfo aidsInfo = new AlmondDetailResp.MemoryAidsInfo();
                aidsInfo.setMindMapData(memoryAids.getMindMapData());
                aidsInfo.setMnemonicsData(memoryAids.getMnemonicsData());
                aidsInfo.setSensoryData(memoryAids.getSensoryData());
                resp.setMemoryAids(aidsInfo);
            }

            // 查询复习计划
            List<ReviewSchedule> reviewSchedules = reviewScheduleMapper.selectByAlmondId(id);
            if (!CollectionUtils.isEmpty(reviewSchedules)) {
                List<AlmondDetailResp.ReviewScheduleInfo> scheduleInfos = reviewSchedules.stream()
                        .map(schedule -> {
                            AlmondDetailResp.ReviewScheduleInfo scheduleInfo = new AlmondDetailResp.ReviewScheduleInfo();
                            BeanUtils.copyProperties(schedule, scheduleInfo);
                            return scheduleInfo;
                        })
                        .collect(Collectors.toList());
                resp.setReviewSchedules(scheduleInfos);
            }
        }

        return resp;
    }

    private String escapeJson(String content) {
        if (content == null) return "";
        return content.replace("\"", "\\\"").replace("\\", "\\\\");
    }
}
