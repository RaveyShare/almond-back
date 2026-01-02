package com.ravey.almond.service.impl;

import com.ravey.almond.api.enums.AlmondMaturityStatus;
import com.ravey.almond.api.model.req.CreateAlmondReq;
import com.ravey.almond.api.model.res.AlmondItemResp;
import com.ravey.almond.api.service.AlmondService;
import com.ravey.almond.service.ai.AlmondUnderstandingAiService;
import com.ravey.almond.service.dao.entity.AlmondItem;
import com.ravey.almond.service.dao.entity.AlmondStateLog;
import com.ravey.almond.service.dao.mapper.AlmondItemMapper;
import com.ravey.almond.service.dao.mapper.AlmondStateLogMapper;
import com.ravey.almond.service.dao.mapper.AlmondTagMapper;
import com.ravey.common.core.user.UserCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

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
    private final AlmondUnderstandingAiService almondUnderstandingAiService;

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
        item.setAlmondStatus(AlmondMaturityStatus.RAW.name());
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
        log.setToStatus(AlmondMaturityStatus.RAW.name());
        log.setTriggerType("USER");
        // 简单记录内容
        log.setContextData("{\"content\":\"" + escapeJson(req.getContent()) + "\"}");
        
        almondStateLogMapper.insert(log);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Long almondId = item.getId();
            String content = req.getContent();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    almondUnderstandingAiService.understandAfterCreate(almondId, userId, content);
                }
            });
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

    private String escapeJson(String content) {
        if (content == null) return "";
        return content.replace("\"", "\\\"").replace("\\", "\\\\");
    }
}
