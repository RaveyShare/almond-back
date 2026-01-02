package com.ravey.almond.service.ai;

import com.ravey.almond.api.enums.AlmondMaturityStatus;
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
            return;
        }
        if (!AlmondMaturityStatus.RAW.name().equals(item.getAlmondStatus())) {
            return;
        }

        AiCenterUnderstandingReq req = new AiCenterUnderstandingReq();
        req.setText(content);
        req.setTaskId(almondId);
        req.setUserId(userId);

        AiCenterUnderstandingResp aiResp = aiCenterSdk.understanding(req);

        AlmondAiSnapshot snapshot = new AlmondAiSnapshot();
        snapshot.setAlmondId(almondId);
        snapshot.setUserId(userId);
        snapshot.setAnalysisType("understanding");
        snapshot.setAiModel(aiResp.getModel());
        
        Map<String, Object> promptContent = new HashMap<>();
        promptContent.put("user_input", content);
        snapshot.setPromptContent(JsonUtil.bean2Json(promptContent));
        
        snapshot.setAnalysisResult(StringUtils.hasText(aiResp.getRawJson()) ? aiResp.getRawJson() : JsonUtil.bean2Json(buildSnapshotFallback(aiResp)));
        snapshot.setStatus(aiResp.isSuccess() ? "success" : "failed");
        snapshot.setCostTime(aiResp.getCostTime());
        almondAiSnapshotMapper.insert(snapshot);

        if (!aiResp.isSuccess()) {
            log.warn("ai-center understanding 失败: almondId={}, userId={}, err={}", almondId, userId, aiResp.getErrorMessage());
            return;
        }

        double confidence = aiResp.getConfidence();
        String title = StringUtils.hasText(aiResp.getTitle()) ? aiResp.getTitle().trim() : null;
        String clarifiedText = StringUtils.hasText(aiResp.getClarifiedText()) ? aiResp.getClarifiedText().trim() : null;

        if (confidence >= 0.7) {
            // confidence >= 0.7：流转到 UNDERSTOOD
            if (StringUtils.hasText(title) && StringUtils.hasText(clarifiedText)) {
                almondItemMapper.updateUnderstanding(
                        almondId,
                        title,
                        clarifiedText,
                        AlmondMaturityStatus.UNDERSTOOD.name()
                );

                saveTags(almondId, aiResp.getTags());

                AlmondStateLog stateLog = new AlmondStateLog();
                stateLog.setAlmondId(almondId);
                stateLog.setUserId(userId);
                stateLog.setFromStatus(AlmondMaturityStatus.RAW.name());
                stateLog.setToStatus(AlmondMaturityStatus.UNDERSTOOD.name());
                stateLog.setTriggerType("AI");

                Map<String, Object> contextData = new HashMap<>();
                contextData.put("title", title);
                contextData.put("clarified_text", clarifiedText);
                contextData.put("tags", aiResp.getTags());
                contextData.put("confidence", aiResp.getConfidence());
                stateLog.setContextData(JsonUtil.bean2Json(contextData));

                almondStateLogMapper.insert(stateLog);
            }
        } else if (confidence >= 0.5) {
            // 0.5 <= confidence < 0.7：保持 RAW，建议人工review
            // 更新 title 和 clarified_content 以供用户 review，但状态保持 RAW
            if (StringUtils.hasText(title) && StringUtils.hasText(clarifiedText)) {
                almondItemMapper.updateUnderstanding(
                        almondId,
                        title,
                        clarifiedText,
                        AlmondMaturityStatus.RAW.name()
                );
                saveTags(almondId, aiResp.getTags());
            }
        }
        // confidence < 0.5：保持 RAW，提示用户补充信息 (不更新 Item，依靠 Snapshot 记录)
    }

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

