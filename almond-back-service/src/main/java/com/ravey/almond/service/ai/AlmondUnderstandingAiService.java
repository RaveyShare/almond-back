package com.ravey.almond.service.ai;

import com.ravey.almond.api.enums.AlmondMaturityStatus;
import com.ravey.almond.api.enums.EvolutionTriggerType;
import com.ravey.almond.service.dao.entity.AlmondAiSnapshot;
import com.ravey.almond.service.dao.entity.AlmondItem;
import com.ravey.almond.service.dao.entity.AlmondTag;
import com.ravey.almond.service.dao.mapper.AlmondAiSnapshotMapper;
import com.ravey.almond.service.dao.mapper.AlmondItemMapper;
import com.ravey.almond.service.dao.mapper.AlmondTagMapper;
import com.ravey.almond.service.dao.mapper.AlmondTagRelationMapper;
import com.ravey.almond.service.machine.AlmondStateMachineService;
import com.ravey.almond.service.sdk.aicenter.AiCenterSdk;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterUnderstandingReq;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterUnderstandingResp;
import com.ravey.common.utils.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 杏仁理解AI服务
 *
 * @author Ravey
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlmondUnderstandingAiService {

    private final AlmondItemMapper almondItemMapper;
    private final AlmondAiSnapshotMapper almondAiSnapshotMapper;
    private final AlmondTagMapper almondTagMapper;
    private final AlmondTagRelationMapper almondTagRelationMapper;
    private final AlmondStateMachineService stateMachineService;
    private final AlmondClassificationService classificationService;
    private final AiCenterSdk aiCenterSdk;

    /**
     * 创建后自动理解
     * 异步执行，不阻塞用户操作
     */
    @Async("aiExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void understandAfterCreate(Long almondId, Long userId, String content) {
        log.info("开始AI理解，almondId: {}, userId: {}", almondId, userId);
        
        try {
            // 1. 调用AI理解接口
            AiCenterUnderstandingResp aiResp = callAiUnderstanding(almondId, userId, content);
            
            // 2. 记录AI快照
            saveAiSnapshot(almondId, userId, "understanding", aiResp);
            
            // 3. 根据置信度决策
            double confidence = aiResp.getConfidence();
            
            if (confidence >= 0.7) {
                // 置信度高：自动流转到UNDERSTOOD
                handleHighConfidence(almondId, userId, aiResp);
                
            } else if (confidence >= 0.5) {
                // 置信度中等：保持RAW，给出建议
                handleMediumConfidence(almondId, userId, aiResp);
                
            } else {
                // 置信度低：保持RAW，强烈建议补充
                handleLowConfidence(almondId, userId, aiResp);
            }
            
            log.info("AI理解完成，almondId: {}, confidence: {}", almondId, confidence);
            
        } catch (Exception e) {
            log.error("AI理解失败，almondId: " + almondId, e);
            // 降级处理：保持RAW状态，不影响用户使用
            handleAiFailure(almondId, userId, e);
        }
    }

    /**
     * 调用AI理解接口
     */
    private AiCenterUnderstandingResp callAiUnderstanding(Long almondId, Long userId, String content) {
        AiCenterUnderstandingReq req = new AiCenterUnderstandingReq();
        req.setTaskId(almondId);
        req.setUserId(userId);
        req.setContent(content);
        req.setMaxTokens(1000);
        req.setTemperature(0.7);
        
        return aiCenterSdk.understanding(req);
    }

    /**
     * 处理高置信度情况（>=0.7）
     */
    private void handleHighConfidence(Long almondId, Long userId, AiCenterUnderstandingResp aiResp) {
        AlmondItem item = almondItemMapper.selectById(almondId);
        
        // 1. 更新标题和澄清内容
        if (aiResp.getTitle() != null) {
            item.setTitle(aiResp.getTitle());
        }
        if (aiResp.getClarifiedText() != null) {
            item.setClarifiedContent(aiResp.getClarifiedText());
        }
        almondItemMapper.updateById(item);
        
        // 2. 处理标签
        if (!CollectionUtils.isEmpty(aiResp.getTags())) {
            saveTags(almondId, userId, aiResp.getTags());
        }
        
        // 3. 流转状态到UNDERSTOOD
        Map<String, Object> context = new HashMap<>();
        context.put("confidence", aiResp.getConfidence());
        context.put("ai_model", aiResp.getModel());
        
        stateMachineService.transition(
            almondId,
            AlmondMaturityStatus.UNDERSTOOD,
            EvolutionTriggerType.AI,
            "AI理解完成，置信度: " + String.format("%.2f", aiResp.getConfidence()),
            context
        );
        
        // 4. 延迟触发分类分析（30秒后）
        classificationService.scheduleClassification(almondId, userId, 30);
    }

    /**
     * 处理中等置信度情况（0.5-0.7）
     */
    private void handleMediumConfidence(Long almondId, Long userId, AiCenterUnderstandingResp aiResp) {
        AlmondItem item = almondItemMapper.selectById(almondId);
        
        // 1. 更新澄清内容（如果有）
        if (aiResp.getClarifiedText() != null) {
            item.setClarifiedContent(aiResp.getClarifiedText());
            almondItemMapper.updateById(item);
        }
        
        // 2. 添加建议
        String suggestion = "AI理解程度：中等（" + 
            String.format("%.0f", aiResp.getConfidence() * 100) + "%）\n" +
            "建议补充更多信息以提高分类准确度";
        
        // TODO: 发送建议通知给用户
        log.info("添加建议，almondId: {}, suggestion: {}", almondId, suggestion);
    }

    /**
     * 处理低置信度情况（<0.5）
     */
    private void handleLowConfidence(Long almondId, Long userId, AiCenterUnderstandingResp aiResp) {
        String suggestion = "AI理解程度：较低（" + 
            String.format("%.0f", aiResp.getConfidence() * 100) + "%）\n" +
            "请补充详细描述，帮助系统更好地理解你的想法";
        
        // TODO: 发送建议通知给用户
        log.info("添加强烈建议，almondId: {}, suggestion: {}", almondId, suggestion);
    }

    /**
     * 处理AI调用失败
     */
    private void handleAiFailure(Long almondId, Long userId, Exception e) {
        // 记录失败的AI快照
        AlmondAiSnapshot snapshot = new AlmondAiSnapshot();
        snapshot.setAlmondId(almondId);
        snapshot.setUserId(userId);
        snapshot.setAnalysisType("understanding");
        snapshot.setStatus("failed");
        snapshot.setAnalysisResult(JsonUtil.bean2Json(Map.of(
            "error", e.getMessage(),
            "error_type", e.getClass().getSimpleName()
        )));
        snapshot.setCreateTime(LocalDateTime.now());
        
        almondAiSnapshotMapper.insert(snapshot);
        
        // 保持RAW状态，用户可以手动操作
        log.warn("AI理解失败，杏仁保持RAW状态，almondId: {}", almondId);
    }

    /**
     * 保存AI快照
     */
    private void saveAiSnapshot(Long almondId, Long userId, String analysisType, 
                                AiCenterUnderstandingResp aiResp) {
        AlmondAiSnapshot snapshot = new AlmondAiSnapshot();
        snapshot.setAlmondId(almondId);
        snapshot.setUserId(userId);
        snapshot.setAnalysisType(analysisType);
        snapshot.setAiModel(aiResp.getModel());
        snapshot.setPromptContent(aiResp.getPrompt());
        snapshot.setAnalysisResult(JsonUtil.bean2Json(aiResp));
        snapshot.setStatus("success");
        snapshot.setCostTime(aiResp.getCostTime());
        snapshot.setCreateTime(LocalDateTime.now());
        
        almondAiSnapshotMapper.insert(snapshot);
    }

    /**
     * 保存标签
     */
    private void saveTags(Long almondId, Long userId, List<String> tagNames) {
        for (String tagName : tagNames) {
            // 1. 查找或创建标签
            Long tagId = almondTagMapper.selectIdByName(tagName);
            
            if (tagId == null) {
                // 标签不存在，创建新标签
                AlmondTag tag = new AlmondTag();
                tag.setName(tagName);
                tag.setTagType("ai_generated");  // AI生成的标签
                almondTagMapper.insert(tag);
                tagId = tag.getId();
            }
            
            // 2. 创建关联（忽略重复）
            almondTagRelationMapper.insertIgnore(almondId, tagId);
        }
    }
}
