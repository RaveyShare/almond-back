package com.ravey.almond.service.ai;

import com.ravey.almond.api.enums.AlmondMaturityStatus;
import com.ravey.almond.api.enums.EvolutionStageType;
import com.ravey.almond.service.dao.entity.AlmondAiSnapshot;
import com.ravey.almond.service.dao.entity.AlmondItem;
import com.ravey.almond.service.dao.entity.AlmondStateLog;
import com.ravey.almond.service.dao.entity.AlmondTag;
import com.ravey.almond.service.dao.mapper.AlmondAiSnapshotMapper;
import com.ravey.almond.service.dao.mapper.AlmondItemMapper;
import com.ravey.almond.service.dao.mapper.AlmondStateLogMapper;
import com.ravey.almond.service.dao.mapper.AlmondTagMapper;
import com.ravey.almond.service.dao.mapper.AlmondTagRelationMapper;
import com.ravey.almond.service.sdk.aicenter.AiCenterSdk;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterUnderstandingReq;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterUnderstandingResp;
import com.ravey.common.utils.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 杏仁理解AI服务
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlmondUnderstandingAiService {

    private final AiCenterSdk aiCenterSdk;
    private final AlmondAiSnapshotMapper almondAiSnapshotMapper;
    private final AlmondItemMapper almondItemMapper;
    private final AlmondStateLogMapper almondStateLogMapper;
    private final AlmondTagMapper almondTagMapper;
    private final AlmondTagRelationMapper almondTagRelationMapper;

    @Value("${almond.ai.understanding.threshold:0.9}")
    private double understandingThreshold;

    @Async("aiExecutor")
    @Transactional(rollbackFor = Exception.class)
    public void understandAfterCreate(Long almondId, Long userId, String content) {
        AlmondItem item = almondItemMapper.selectById(almondId);
        if (item == null) {
            log.warn("杏仁不存在，almondId: {}", almondId);
            return;
        }

        // 检查状态：必须是RAW
        if (!AlmondMaturityStatus.RAW.getCode().equals(item.getAlmondStatus())) {
            log.warn("杏仁不在RAW状态，跳过理解，almondId: {}, currentStatus: {}",
                    almondId, item.getAlmondStatus());
            return;
        }

        // 检查阶段：只有CREATED阶段才执行理解
        if (item.getEvolutionStage() != null && item.getEvolutionStage() != EvolutionStageType.CREATED.getCode()) {
            log.warn("杏仁不在CREATED阶段，跳过理解，almondId: {}, currentStage: {}",
                    almondId, item.getEvolutionStage());
            return;
        }

        try {
            // 1. 更新为理解中
            updateStage(almondId, EvolutionStageType.UNDERSTANDING);

            // 2. 调用AI理解
            AiCenterUnderstandingReq req = new AiCenterUnderstandingReq();
            req.setText(content);
            req.setTaskId(almondId);
            req.setUserId(userId);

            AiCenterUnderstandingResp aiResp = aiCenterSdk.understanding(req);

            // 3. 记录AI快照
            saveAiSnapshot(almondId, userId, content, aiResp);

            if (!aiResp.isSuccess()) {
                log.warn("AI理解失败，almondId: {}, err: {}", almondId, aiResp.getErrorMessage());
                // 回退到CREATED阶段
                updateStage(almondId, EvolutionStageType.CREATED);
                return;
            }

            // 4. 根据置信度决策
            double confidence = aiResp.getConfidence();
            String title = StringUtils.hasText(aiResp.getTitle()) ? aiResp.getTitle().trim() : null;
            String clarifiedText = StringUtils.hasText(aiResp.getClarifiedText()) ? aiResp.getClarifiedText().trim() : null;

            if (confidence >= 0.7) {
                // 置信度高：流转到UNDERSTOOD
                handleHighConfidence(almondId, userId, title, clarifiedText, aiResp);

            } else if (confidence >= 0.5) {
                // 置信度中等：更新内容但保持RAW
                handleMediumConfidence(almondId, title, clarifiedText, aiResp);

            } else {
                // 置信度低：回退到CREATED
                log.info("置信度过低，回退到CREATED，almondId: {}, confidence: {}", almondId, confidence);
                updateStage(almondId, EvolutionStageType.CREATED);
            }

        } catch (Exception e) {
            log.error("AI理解失败，almondId: " + almondId, e);
            // 回退到CREATED阶段
            updateStage(almondId, EvolutionStageType.CREATED);
        }
    }

    /**
     * 处理高置信度（>=0.7）
     */
    private void handleHighConfidence(Long almondId, Long userId, String title,
                                      String clarifiedText, AiCenterUnderstandingResp aiResp) {
        if (StringUtils.hasText(title) && StringUtils.hasText(clarifiedText)) {
            // 更新内容和状态
            almondItemMapper.updateUnderstanding(
                    almondId,
                    title,
                    clarifiedText,
                    AlmondMaturityStatus.UNDERSTOOD.getCode()
            );

            // 更新为UNDERSTOOD阶段
            updateStage(almondId, EvolutionStageType.UNDERSTOOD);

            // 保存标签
            saveTags(almondId, aiResp.getTags());

            // 记录状态日志
            AlmondStateLog stateLog = new AlmondStateLog();
            stateLog.setAlmondId(almondId);
            stateLog.setUserId(userId);
            stateLog.setFromStatus(AlmondMaturityStatus.RAW.getCode());
            stateLog.setToStatus(AlmondMaturityStatus.UNDERSTOOD.getCode());
            stateLog.setTriggerType("AI");

            Map<String, Object> contextData = new HashMap<>();
            contextData.put("title", title);
            contextData.put("clarified_text", clarifiedText);
            contextData.put("tags", aiResp.getTags());
            contextData.put("confidence", aiResp.getConfidence());
            contextData.put("evolution_stage", EvolutionStageType.UNDERSTOOD.getCode());
            stateLog.setContextData(JsonUtil.bean2Json(contextData));

            almondStateLogMapper.insert(stateLog);

            log.info("AI理解完成，流转到UNDERSTOOD，almondId: {}, stage: {}",
                    almondId, EvolutionStageType.UNDERSTOOD.getCode());
        }
    }

    /**
     * 处理中等置信度（0.5-0.7）
     */
    private void handleMediumConfidence(Long almondId, String title,
                                        String clarifiedText, AiCenterUnderstandingResp aiResp) {
        if (StringUtils.hasText(title) && StringUtils.hasText(clarifiedText)) {
            // 更新内容但保持RAW状态
            almondItemMapper.updateUnderstanding(
                    almondId,
                    title,
                    clarifiedText,
                    AlmondMaturityStatus.RAW.getCode()
            );

            // 回退到CREATED阶段（等待用户review后再触发）
            updateStage(almondId, EvolutionStageType.CREATED);

            saveTags(almondId, aiResp.getTags());

            log.info("置信度中等，保持RAW状态，almondId: {}, confidence: {}, stage: {}",
                    almondId, aiResp.getConfidence(), EvolutionStageType.CREATED.getCode());
        }
    }

    /**
     * 仅更新演化阶段
     */
    private void updateStage(Long almondId, EvolutionStageType stage) {
        AlmondItem item = new AlmondItem();
        item.setId(almondId);
        item.setEvolutionStage(stage.getCode());
        almondItemMapper.updateById(item);

        log.debug("更新演化阶段，almondId: {}, stage: {} ({})",
                almondId, stage.getCode(), stage.getName());
    }

    /**
     * 保存AI快照
     */
    private void saveAiSnapshot(Long almondId, Long userId, String content, AiCenterUnderstandingResp aiResp) {
        AlmondAiSnapshot snapshot = new AlmondAiSnapshot();
        snapshot.setAlmondId(almondId);
        snapshot.setUserId(userId);
        snapshot.setAnalysisType("understanding");
        snapshot.setAiModel(aiResp.getModel());

        Map<String, Object> promptContent = new HashMap<>();
        promptContent.put("user_input", content);
        snapshot.setPromptContent(JsonUtil.bean2Json(promptContent));

        snapshot.setAnalysisResult(
                StringUtils.hasText(aiResp.getRawJson()) ?
                        aiResp.getRawJson() :
                        JsonUtil.bean2Json(buildSnapshotFallback(aiResp))
        );
        snapshot.setStatus(aiResp.isSuccess() ? "success" : "failed");
        snapshot.setCostTime(aiResp.getCostTime());
        almondAiSnapshotMapper.insert(snapshot);
    }

    /**
     * 保存标签
     */
    private void saveTags(Long almondId, List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        for (String name : tags) {
            if (!StringUtils.hasText(name)) {
                continue;
            }
            String tagName = name.trim();
            if (!StringUtils.hasText(tagName)) {
                continue;
            }
            Long tagId = almondTagMapper.selectIdByName(tagName);
            if (tagId == null) {
                AlmondTag tag = new AlmondTag();
                tag.setName(tagName);
                tag.setTagType("cognitive");
                almondTagMapper.insert(tag);
                tagId = tag.getId();
                if (tagId == null) {
                    tagId = almondTagMapper.selectIdByName(tagName);
                }
            }
            if (tagId != null) {
                almondTagRelationMapper.insertIgnore(almondId, tagId);
            }
        }
    }

    private Map<String, Object> buildSnapshotFallback(AiCenterUnderstandingResp aiResp) {
        Map<String, Object> map = new HashMap<>();
        map.put("title", aiResp.getTitle());
        map.put("clarified_text", aiResp.getClarifiedText());
        map.put("tags", aiResp.getTags());
        map.put("core", aiResp.getCore());
        map.put("confidence", aiResp.getConfidence());
        map.put("reasoning", aiResp.getReasoning());
        map.put("success", aiResp.isSuccess());
        map.put("error_message", aiResp.getErrorMessage());
        return map;
    }
}
