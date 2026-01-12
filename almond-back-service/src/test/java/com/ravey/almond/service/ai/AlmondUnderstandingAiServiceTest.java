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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AlmondUnderstandingAiService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AlmondUnderstandingAiServiceTest {

    @Mock
    private AiCenterSdk aiCenterSdk;
    @Mock
    private AlmondAiSnapshotMapper almondAiSnapshotMapper;
    @Mock
    private AlmondItemMapper almondItemMapper;
    @Mock
    private AlmondStateLogMapper almondStateLogMapper;
    @Mock
    private AlmondTagMapper almondTagMapper;
    @Mock
    private AlmondTagRelationMapper almondTagRelationMapper;

    @InjectMocks
    private AlmondUnderstandingAiService service;

    /**
     * 条目不存在时直接返回，不调用 AI 也不落快照
     */
    @Test
    void understandAfterCreate_shouldReturnWhenItemNotFound() {
        when(almondItemMapper.selectById(1L)).thenReturn(null);

        service.understandAfterCreate(1L, 10L, "content");

        verify(aiCenterSdk, never()).understanding(any());
        verify(almondAiSnapshotMapper, never()).insert(any());
    }

    /**
     * Almond 状态不是 RAW 时直接返回，不触发理解流程
     */
    @Test
    void understandAfterCreate_shouldReturnWhenStatusNotRaw() {
        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setAlmondStatus(AlmondMaturityStatus.UNDERSTOOD.getCode());
        item.setEvolutionStage(EvolutionStageType.CREATED.getCode());
        when(almondItemMapper.selectById(1L)).thenReturn(item);

        service.understandAfterCreate(1L, 10L, "content");

        verify(aiCenterSdk, never()).understanding(any());
        verify(almondItemMapper, never()).updateById(any());
    }

    /**
     * 演化阶段不是 CREATED 时直接返回，不触发理解流程
     */
    @Test
    void understandAfterCreate_shouldReturnWhenStageNotCreated() {
        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setAlmondStatus(AlmondMaturityStatus.RAW.getCode());
        item.setEvolutionStage(EvolutionStageType.UNDERSTANDING.getCode());
        when(almondItemMapper.selectById(1L)).thenReturn(item);

        service.understandAfterCreate(1L, 10L, "content");

        verify(aiCenterSdk, never()).understanding(any());
        verify(almondItemMapper, never()).updateById(any());
    }

    /**
     * AI 理解失败时回滚演化阶段到 CREATED，并落快照
     */
    @Test
    void understandAfterCreate_shouldRollbackToCreatedWhenAiFailed() {
        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setAlmondStatus(AlmondMaturityStatus.RAW.getCode());
        item.setEvolutionStage(EvolutionStageType.CREATED.getCode());
        when(almondItemMapper.selectById(1L)).thenReturn(item);

        AiCenterUnderstandingResp aiResp = new AiCenterUnderstandingResp();
        aiResp.setSuccess(false);
        aiResp.setErrorMessage("failed");
        aiResp.setConfidence(0.9);
        when(aiCenterSdk.understanding(any(AiCenterUnderstandingReq.class))).thenReturn(aiResp);

        service.understandAfterCreate(1L, 10L, "content");

        ArgumentCaptor<AlmondItem> updateCaptor = ArgumentCaptor.forClass(AlmondItem.class);
        verify(almondItemMapper, times(2)).updateById(updateCaptor.capture());
        assertThat(updateCaptor.getAllValues().get(0).getEvolutionStage()).isEqualTo(EvolutionStageType.UNDERSTANDING.getCode());
        assertThat(updateCaptor.getAllValues().get(1).getEvolutionStage()).isEqualTo(EvolutionStageType.CREATED.getCode());

        verify(almondAiSnapshotMapper).insert(any(AlmondAiSnapshot.class));
        verify(almondItemMapper, never()).updateUnderstanding(any(), any(), any(), any());
    }

    /**
     * 高置信度：更新理解结果、演化阶段到 UNDERSTOOD，并写状态日志与标签关联
     */
    @Test
    void understandAfterCreate_shouldHandleHighConfidence() {
        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setAlmondStatus(AlmondMaturityStatus.RAW.getCode());
        item.setEvolutionStage(EvolutionStageType.CREATED.getCode());
        when(almondItemMapper.selectById(1L)).thenReturn(item);

        AiCenterUnderstandingResp aiResp = new AiCenterUnderstandingResp();
        aiResp.setSuccess(true);
        aiResp.setConfidence(0.8);
        aiResp.setTitle("title");
        aiResp.setClarifiedText("clarified");
        aiResp.setTags(Arrays.asList("tag1", "tag2"));
        aiResp.setModel("model");
        aiResp.setCostTime(12);
        when(aiCenterSdk.understanding(any(AiCenterUnderstandingReq.class))).thenReturn(aiResp);

        when(almondTagMapper.selectIdByName("tag1")).thenReturn(101L);
        when(almondTagMapper.selectIdByName("tag2")).thenReturn(null);
        when(almondTagMapper.insert(any(AlmondTag.class))).thenAnswer(invocation -> {
            AlmondTag tag = invocation.getArgument(0);
            tag.setId(102L);
            return 1;
        });

        service.understandAfterCreate(1L, 10L, "content");

        verify(almondItemMapper).updateUnderstanding(1L, "title", "clarified", AlmondMaturityStatus.UNDERSTOOD.getCode());

        ArgumentCaptor<AlmondItem> updateCaptor = ArgumentCaptor.forClass(AlmondItem.class);
        verify(almondItemMapper, times(2)).updateById(updateCaptor.capture());
        assertThat(updateCaptor.getAllValues().get(0).getEvolutionStage()).isEqualTo(EvolutionStageType.UNDERSTANDING.getCode());
        assertThat(updateCaptor.getAllValues().get(1).getEvolutionStage()).isEqualTo(EvolutionStageType.UNDERSTOOD.getCode());

        verify(almondTagRelationMapper).insertIgnore(1L, 101L);
        verify(almondTagRelationMapper).insertIgnore(1L, 102L);

        ArgumentCaptor<AlmondStateLog> logCaptor = ArgumentCaptor.forClass(AlmondStateLog.class);
        verify(almondStateLogMapper).insert(logCaptor.capture());
        assertThat(logCaptor.getValue().getFromStatus()).isEqualTo(AlmondMaturityStatus.RAW.getCode());
        assertThat(logCaptor.getValue().getToStatus()).isEqualTo(AlmondMaturityStatus.UNDERSTOOD.getCode());
        assertThat(logCaptor.getValue().getTriggerType()).isEqualTo("AI");
        assertThat(logCaptor.getValue().getContextData()).contains("confidence");
    }

    /**
     * 中等置信度：更新理解结果但保持 RAW，演化阶段回到 CREATED，不写状态日志
     */
    @Test
    void understandAfterCreate_shouldHandleMediumConfidence() {
        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setAlmondStatus(AlmondMaturityStatus.RAW.getCode());
        item.setEvolutionStage(EvolutionStageType.CREATED.getCode());
        when(almondItemMapper.selectById(1L)).thenReturn(item);

        AiCenterUnderstandingResp aiResp = new AiCenterUnderstandingResp();
        aiResp.setSuccess(true);
        aiResp.setConfidence(0.6);
        aiResp.setTitle("title");
        aiResp.setClarifiedText("clarified");
        aiResp.setTags(Collections.singletonList("tag1"));
        when(aiCenterSdk.understanding(any(AiCenterUnderstandingReq.class))).thenReturn(aiResp);

        when(almondTagMapper.selectIdByName("tag1")).thenReturn(101L);

        service.understandAfterCreate(1L, 10L, "content");

        verify(almondItemMapper).updateUnderstanding(1L, "title", "clarified", AlmondMaturityStatus.RAW.getCode());

        ArgumentCaptor<AlmondItem> updateCaptor = ArgumentCaptor.forClass(AlmondItem.class);
        verify(almondItemMapper, times(2)).updateById(updateCaptor.capture());
        assertThat(updateCaptor.getAllValues().get(0).getEvolutionStage()).isEqualTo(EvolutionStageType.UNDERSTANDING.getCode());
        assertThat(updateCaptor.getAllValues().get(1).getEvolutionStage()).isEqualTo(EvolutionStageType.CREATED.getCode());

        verify(almondStateLogMapper, never()).insert(any());
        verify(almondTagRelationMapper).insertIgnore(1L, 101L);
    }

    /**
     * 低置信度：不更新理解结果，演化阶段回到 CREATED
     */
    @Test
    void understandAfterCreate_shouldHandleLowConfidence() {
        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setAlmondStatus(AlmondMaturityStatus.RAW.getCode());
        item.setEvolutionStage(EvolutionStageType.CREATED.getCode());
        when(almondItemMapper.selectById(1L)).thenReturn(item);

        AiCenterUnderstandingResp aiResp = new AiCenterUnderstandingResp();
        aiResp.setSuccess(true);
        aiResp.setConfidence(0.4);
        when(aiCenterSdk.understanding(any(AiCenterUnderstandingReq.class))).thenReturn(aiResp);

        service.understandAfterCreate(1L, 10L, "content");

        verify(almondItemMapper, never()).updateUnderstanding(any(), any(), any(), any());

        ArgumentCaptor<AlmondItem> updateCaptor = ArgumentCaptor.forClass(AlmondItem.class);
        verify(almondItemMapper, times(2)).updateById(updateCaptor.capture());
        assertThat(updateCaptor.getAllValues().get(0).getEvolutionStage()).isEqualTo(EvolutionStageType.UNDERSTANDING.getCode());
        assertThat(updateCaptor.getAllValues().get(1).getEvolutionStage()).isEqualTo(EvolutionStageType.CREATED.getCode());
    }
}
