package com.ravey.almond.service.ai;

import com.ravey.almond.api.enums.AlmondMaturityStatus;
import com.ravey.almond.api.enums.EvolutionTriggerType;
import com.ravey.almond.service.dao.entity.AlmondAiSnapshot;
import com.ravey.almond.service.dao.entity.AlmondItem;
import com.ravey.almond.service.dao.mapper.AlmondAiSnapshotMapper;
import com.ravey.almond.service.dao.mapper.AlmondItemMapper;
import com.ravey.almond.service.machine.AlmondStateMachineService;
import com.ravey.almond.service.profile.UserProfile;
import com.ravey.almond.service.profile.UserProfileService;
import com.ravey.almond.service.sdk.aicenter.AiCenterSdk;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterClassificationReq;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterClassificationResp;
import com.ravey.common.utils.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 杏仁分类AI服务
 *
 * @author Ravey
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlmondClassificationService {

    private final AlmondItemMapper almondItemMapper;
    private final AlmondAiSnapshotMapper almondAiSnapshotMapper;
    private final UserProfileService userProfileService;
    private final AlmondStateMachineService stateMachineService;
    private final AiCenterSdk aiCenterSdk;

    /**
     * 延迟触发分类分析
     */
    @Async("aiExecutor")
    public void scheduleClassification(Long almondId, Long userId, int delaySeconds) {
        try {
            log.info("延迟{}秒后执行分类分析，almondId: {}", delaySeconds, almondId);
            
            // 延迟执行
            Thread.sleep(delaySeconds * 1000L);
            
            // 检查状态是否仍为UNDERSTOOD
            AlmondItem item = almondItemMapper.selectById(almondId);
            if (item == null) {
                log.warn("杏仁不存在，almondId: {}", almondId);
                return;
            }
            
            if (!"understood".equals(item.getAlmondStatus())) {
                log.info("杏仁状态已变更，不再执行分类，almondId: {}, status: {}", 
                    almondId, item.getAlmondStatus());
                return;
            }
            
            // 执行分类
            classifyAndConverge(almondId, userId);
            
        } catch (InterruptedException e) {
            log.error("分类任务被中断，almondId: " + almondId, e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("分类任务执行失败，almondId: " + almondId, e);
        }
    }

    /**
     * AI分类分析
     */
    @Transactional(rollbackFor = Exception.class)
    public void classifyAndConverge(Long almondId, Long userId) {
        log.info("开始AI分类分析，almondId: {}", almondId);
        
        try {
            AlmondItem item = almondItemMapper.selectById(almondId);
            
            // 1. 构建用户画像
            UserProfile profile = userProfileService.buildUserProfile(userId);
            
            // 2. 调用AI分类接口
            AiCenterClassificationResp aiResp = callAiClassification(item, profile, userId);
            
            // 3. 记录AI快照
            saveAiSnapshot(almondId, userId, "classification", aiResp);
            
            // 4. 根据置信度和用户画像决策
            double confidence = aiResp.getConfidence();
            boolean isFirstTimeUser = userProfileService.isFirstTimeUser(userId);
            
            if (confidence >= 0.85) {
                // 置信度高：自动收敛（Phase 2会加入用户偏好判断）
                handleHighConfidence(almondId, userId, item, aiResp, isFirstTimeUser);
                
            } else if (confidence >= 0.70) {
                // 置信度中等：流转到EVOLVING
                handleMediumConfidence(almondId, userId, aiResp);
                
            } else {
                // 置信度低：保持UNDERSTOOD
                handleLowConfidence(almondId, userId, aiResp);
            }
            
            log.info("AI分类完成，almondId: {}, suggestedType: {}, confidence: {}", 
                almondId, aiResp.getSuggestedType(), confidence);
            
        } catch (Exception e) {
            log.error("AI分类失败，almondId: " + almondId, e);
            handleAiFailure(almondId, userId, e);
        }
    }

    /**
     * 调用AI分类接口
     */
    private AiCenterClassificationResp callAiClassification(AlmondItem item, UserProfile profile, Long userId) {
        AiCenterClassificationReq req = new AiCenterClassificationReq();
        req.setTaskId(item.getId());
        req.setUserId(userId);
        req.setTitle(item.getTitle());
        req.setClarifiedContent(item.getClarifiedContent());
        req.setUserProfile(convertProfile(profile));
        
        return aiCenterSdk.classify(req);
    }

    /**
     * 转换用户画像为AI接口需要的格式
     */
    private Map<String, Object> convertProfile(UserProfile profile) {
        Map<String, Object> map = new HashMap<>();
        map.put("total_count", profile.getTotalCount());
        map.put("type_distribution", profile.getTypeDistribution());
        map.put("frequent_tags", profile.getFrequentTags());
        map.put("focus_areas", profile.getFocusAreas());
        map.put("accept_rate", profile.getAcceptRate());
        return map;
    }

    /**
     * 处理高置信度（>=0.85）
     */
    private void handleHighConfidence(Long almondId, Long userId, AlmondItem item, 
                                      AiCenterClassificationResp aiResp, 
                                      boolean isFirstTimeUser) {
        // Phase 1: 直接自动收敛（不考虑用户偏好）
        // Phase 2会在这里加入用户偏好判断
        
        log.info("高置信度，自动收敛，almondId: {}, type: {}", almondId, aiResp.getSuggestedType());
        
        // 1. 流转状态到CONVERGED
        Map<String, Object> context = new HashMap<>();
        context.put("suggested_type", aiResp.getSuggestedType());
        context.put("confidence", aiResp.getConfidence());
        context.put("reasoning", aiResp.getReasoning());
        context.put("is_first_time_user", isFirstTimeUser);
        
        stateMachineService.transition(
            almondId,
            AlmondMaturityStatus.CONVERGED,
            EvolutionTriggerType.AI,
            "AI分类完成，自动收敛",
            context
        );
        
        // 2. 设置finalType
        item.setFinalType(aiResp.getSuggestedType());
        item.setMaturityScore(calculateMaturityScore(aiResp.getConfidence()));
        almondItemMapper.updateById(item);
        
        // 3. 类型特定处理会在状态机的后置处理中触发（Phase 3实现）
    }

    /**
     * 处理中等置信度（0.70-0.85）
     */
    private void handleMediumConfidence(Long almondId, Long userId, AiCenterClassificationResp aiResp) {
        log.info("中等置信度，流转到EVOLVING，almondId: {}", almondId);
        
        Map<String, Object> context = new HashMap<>();
        context.put("suggested_type", aiResp.getSuggestedType());
        context.put("confidence", aiResp.getConfidence());
        context.put("reasoning", aiResp.getReasoning());
        
        stateMachineService.transition(
            almondId,
            AlmondMaturityStatus.EVOLVING,
            EvolutionTriggerType.AI,
            "AI置信度中等，需要更多信息",
            context
        );
        
        // TODO: 发送建议通知
    }

    /**
     * 处理低置信度（<0.70）
     */
    private void handleLowConfidence(Long almondId, Long userId, AiCenterClassificationResp aiResp) {
        log.info("低置信度，保持UNDERSTOOD，almondId: {}", almondId);
        
        // 保持UNDERSTOOD状态，建议用户补充信息
        String suggestion = "AI无法确定准确的分类（置信度: " + 
            String.format("%.0f", aiResp.getConfidence() * 100) + "%）\n" +
            "建议补充更多信息或手动设置类型";
        
        // TODO: 发送建议通知
        log.info("添加建议，almondId: {}, suggestion: {}", almondId, suggestion);
    }

    /**
     * 处理AI调用失败
     */
    private void handleAiFailure(Long almondId, Long userId, Exception e) {
        // 记录失败的AI快照
        AlmondAiSnapshot snapshot = new AlmondAiSnapshot();
        snapshot.setAlmondId(almondId);
        snapshot.setUserId(userId);
        snapshot.setAnalysisType("classification");
        snapshot.setStatus("failed");
        snapshot.setAnalysisResult(JsonUtil.bean2Json(Map.of(
            "error", e.getMessage(),
            "error_type", e.getClass().getSimpleName()
        )));
        snapshot.setCreateTime(LocalDateTime.now());
        
        almondAiSnapshotMapper.insert(snapshot);
        
        log.warn("AI分类失败，杏仁保持UNDERSTOOD状态，almondId: {}", almondId);
    }

    /**
     * 保存AI快照
     */
    private void saveAiSnapshot(Long almondId, Long userId, String analysisType, 
                                AiCenterClassificationResp aiResp) {
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
     * 根据置信度计算成熟度评分
     */
    private Integer calculateMaturityScore(double confidence) {
        // 简单映射：置信度 * 100
        return (int) (confidence * 100);
    }
}
