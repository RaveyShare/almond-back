package com.ravey.almond.service.impl;

import com.ravey.almond.api.enums.AlmondMaturityStatus;
import com.ravey.almond.api.model.req.AlmondListReq;
import com.ravey.almond.api.model.req.CreateAlmondReq;
import com.ravey.almond.api.model.res.AlmondDetailResp;
import com.ravey.almond.api.model.res.AlmondItemResp;
import com.ravey.almond.api.model.res.AlmondListResp;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AlmondServiceImpl 单元测试
 *
 * @author Ravey
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class AlmondServiceImplTest {

    @Mock
    private AlmondItemMapper almondItemMapper;
    @Mock
    private AlmondStateLogMapper almondStateLogMapper;
    @Mock
    private AlmondTagMapper almondTagMapper;
    @Mock
    private AlmondAiSnapshotMapper almondAiSnapshotMapper;
    @Mock
    private AlmondUnderstandingAiService almondUnderstandingAiService;
    @Mock
    private ActionExecutionMapper actionExecutionMapper;
    @Mock
    private MemoryAidsMapper memoryAidsMapper;
    @Mock
    private ReviewScheduleMapper reviewScheduleMapper;

    @InjectMocks
    private AlmondServiceImpl almondService;

    /**
     * 每个用例执行前清理事务同步上下文，避免测试间互相影响
     */
    @BeforeEach
    void setUp() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    /**
     * 每个用例执行后清理事务同步上下文，避免测试间互相影响
     */
    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    /**
     * 创建 Almond：持久化条目与状态日志，并返回响应
     */
    @Test
    void createAlmond_shouldPersistItemAndStateLogAndReturnResp() {
        Long userId = 1001L;

        CreateAlmondReq req = new CreateAlmondReq();
        req.setContent("test content");

        AlmondItem persisted = new AlmondItem();
        persisted.setId(1L);
        when(almondItemMapper.insert(any(AlmondItem.class))).thenAnswer(invocation -> {
            AlmondItem arg = invocation.getArgument(0);
            arg.setId(persisted.getId());
            return 1;
        });

        try (MockedStatic<UserCache> userCacheMock = Mockito.mockStatic(UserCache.class)) {
            userCacheMock.when(UserCache::getUserId).thenReturn(userId);
            AlmondItemResp resp = almondService.createAlmond(req);

            ArgumentCaptor<AlmondItem> itemCaptor = ArgumentCaptor.forClass(AlmondItem.class);
            verify(almondItemMapper).insert(itemCaptor.capture());
            AlmondItem savedItem = itemCaptor.getValue();
            assertThat(savedItem.getUserId()).isEqualTo(userId);
            assertThat(savedItem.getContent()).isEqualTo("test content");
            assertThat(savedItem.getAlmondStatus()).isEqualTo(AlmondMaturityStatus.RAW.getCode());
            assertThat(savedItem.getMaturityScore()).isEqualTo(0);
            assertThat(savedItem.getEvolutionStage()).isEqualTo(0);
            assertThat(savedItem.getPriority()).isEqualTo(0);
            assertThat(savedItem.getStarred()).isEqualTo(0);

            ArgumentCaptor<AlmondStateLog> logCaptor = ArgumentCaptor.forClass(AlmondStateLog.class);
            verify(almondStateLogMapper).insert(logCaptor.capture());
            AlmondStateLog savedLog = logCaptor.getValue();
            assertThat(savedLog.getAlmondId()).isEqualTo(persisted.getId());
            assertThat(savedLog.getUserId()).isEqualTo(userId);
            assertThat(savedLog.getFromStatus()).isNull();
            assertThat(savedLog.getToStatus()).isEqualTo(AlmondMaturityStatus.RAW.getCode());
            assertThat(savedLog.getTriggerType()).isEqualTo("USER");
            assertThat(savedLog.getContextData()).contains("test content");

            assertThat(resp.getId()).isEqualTo(persisted.getId());
            assertThat(resp.getContent()).isEqualTo("test content");
            assertThat(resp.getAlmondStatus()).isEqualTo(AlmondMaturityStatus.RAW.getCode());
        }
    }

    /**
     * 创建 Almond：事务开启时，提交后触发 AI 异步理解
     */
    @Test
    void createAlmond_shouldRegisterAsyncUnderstandingAfterCommitWhenTxActive() {
        Long userId = 1002L;
        TransactionSynchronizationManager.initSynchronization();

        CreateAlmondReq req = new CreateAlmondReq();
        req.setContent("async content");

        AlmondItem persisted = new AlmondItem();
        persisted.setId(2L);
        when(almondItemMapper.insert(any(AlmondItem.class))).thenAnswer(invocation -> {
            AlmondItem arg = invocation.getArgument(0);
            arg.setId(persisted.getId());
            return 1;
        });

        try (MockedStatic<UserCache> userCacheMock = Mockito.mockStatic(UserCache.class)) {
            userCacheMock.when(UserCache::getUserId).thenReturn(userId);
            almondService.createAlmond(req);
        }

        for (TransactionSynchronization synchronization : TransactionSynchronizationManager.getSynchronizations()) {
            synchronization.afterCommit();
        }

        verify(almondUnderstandingAiService)
                .understandAfterCreate(persisted.getId(), userId, "async content");
    }

    /**
     * 创建 Almond：事务未开启时，不触发 AI 理解
     */
    @Test
    void createAlmond_shouldNotCallAiWhenTxNotActive() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }

        Long userId = 1003L;

        CreateAlmondReq req = new CreateAlmondReq();
        req.setContent("no tx");

        AlmondItem persisted = new AlmondItem();
        persisted.setId(3L);
        when(almondItemMapper.insert(any(AlmondItem.class))).thenAnswer(invocation -> {
            AlmondItem arg = invocation.getArgument(0);
            arg.setId(persisted.getId());
            return 1;
        });

        try (MockedStatic<UserCache> userCacheMock = Mockito.mockStatic(UserCache.class)) {
            userCacheMock.when(UserCache::getUserId).thenReturn(userId);
            almondService.createAlmond(req);
        }

        verify(almondUnderstandingAiService, never())
                .understandAfterCreate(any(), any(), any());
    }

    @Test
    void getAlmond_shouldThrowWhenNotFound() {
        when(almondItemMapper.selectById(1L)).thenReturn(null);
        assertThatThrownBy(() -> almondService.getAlmond(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("杏仁不存在");
    }

    @Test
    void getAlmond_shouldMapFieldsAndTags() {
        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setUserId(10L);
        item.setContent("hello");
        item.setAlmondStatus(AlmondMaturityStatus.RAW.getCode());
        item.setFinalType("memory");
        item.setMaturityScore(80);
        item.setEvolutionStage(2);
        item.setPriority(3);

        when(almondItemMapper.selectById(1L)).thenReturn(item);
        when(almondTagMapper.selectTagNamesByAlmondId(1L)).thenReturn(List.of("tag1", "tag2"));

        AlmondItemResp resp = almondService.getAlmond(1L);

        assertThat(resp.getId()).isEqualTo(1L);
        assertThat(resp.getUserId()).isEqualTo(10L);
        assertThat(resp.getContent()).isEqualTo("hello");
        assertThat(resp.getDescription()).isEqualTo("hello");
        assertThat(resp.getAlmondStatus()).isEqualTo(AlmondMaturityStatus.RAW.getCode());
        assertThat(resp.getStatus()).isEqualTo(AlmondMaturityStatus.RAW.getCode());
        assertThat(resp.getAiClassification()).isEqualTo("memory");
        assertThat(resp.getTaskType()).isEqualTo("memory");
        assertThat(resp.getClassificationConfidence()).isEqualTo(0.8);
        assertThat(resp.getPriority()).isEqualTo(3);
        assertThat(resp.getTags()).containsExactly("tag1", "tag2");
    }

    @Test
    void listAlmonds_shouldReturnListAndStatistics() {
        Long userId = 2001L;
        AlmondListReq req = new AlmondListReq();
        req.setPageNum(1);
        req.setPageSize(2);
        req.setAlmondStatus("RAW");

        AlmondItem item1 = new AlmondItem();
        item1.setId(1L);
        item1.setUserId(userId);
        item1.setAlmondStatus("raw");
        AlmondItem item2 = new AlmondItem();
        item2.setId(2L);
        item2.setUserId(userId);
        item2.setAlmondStatus("raw");

        try (MockedStatic<UserCache> userCacheMock = Mockito.mockStatic(UserCache.class)) {
            userCacheMock.when(UserCache::getUserId).thenReturn(userId);

            when(almondItemMapper.selectAlmondList(
                    eq(userId),
                    eq("raw"),
                    isNull(),
                    isNull(),
                    isNull(),
                    eq("update_time"),
                    eq("desc"),
                    anyInt(),
                    eq(2)
            )).thenReturn(List.of(item1, item2));

            when(almondItemMapper.countAlmondList(eq(userId), eq("raw"), isNull(), isNull(), isNull()))
                    .thenReturn(2L);

            when(almondTagMapper.selectTagNamesByAlmondId(1L)).thenReturn(List.of("tag1"));
            when(almondTagMapper.selectTagNamesByAlmondId(2L)).thenReturn(List.of());

            when(almondItemMapper.countByStatus(userId)).thenReturn(List.of(
                    Map.of("status", "raw", "count", 2)
            ));
            when(almondItemMapper.countByFinalType(userId)).thenReturn(List.of(
                    Map.of("type", "memory", "count", 1),
                    Map.of("type", "action", "count", 1)
            ));
            when(almondItemMapper.countStarred(userId)).thenReturn(1L);
            when(almondItemMapper.countAlmondList(eq(userId), isNull(), isNull(), isNull(), isNull()))
                    .thenReturn(2L);

            AlmondListResp resp = almondService.listAlmonds(req);

            assertThat(resp.getTotal()).isEqualTo(2L);
            assertThat(resp.getList()).hasSize(2);
            assertThat(resp.getList().get(0).getTags()).hasSize(1);
            assertThat(resp.getStatistics().getTotalCount()).isEqualTo(2L);
            assertThat(resp.getStatistics().getStarredCount()).isEqualTo(1L);
            assertThat(resp.getStatistics().getStatusCount()).containsEntry("raw", 2L);
            assertThat(resp.getStatistics().getTypeCount()).containsEntry("memory", 1L).containsEntry("action", 1L);
        }
    }

    @Test
    void getAlmondDetail_shouldThrowWhenNotFound() {
        when(almondItemMapper.selectById(1L)).thenReturn(null);
        try (MockedStatic<UserCache> userCacheMock = Mockito.mockStatic(UserCache.class)) {
            userCacheMock.when(UserCache::getUserId).thenReturn(10L);
            assertThatThrownBy(() -> almondService.getAlmondDetail(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("杏仁不存在");
        }
    }

    @Test
    void getAlmondDetail_shouldThrowWhenNoPermission() {
        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setUserId(11L);
        when(almondItemMapper.selectById(1L)).thenReturn(item);

        try (MockedStatic<UserCache> userCacheMock = Mockito.mockStatic(UserCache.class)) {
            userCacheMock.when(UserCache::getUserId).thenReturn(10L);
            assertThatThrownBy(() -> almondService.getAlmondDetail(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("无权访问");
        }
    }

    @Test
    void getAlmondDetail_shouldBuildActionDetail() {
        Long userId = 10L;
        LocalDateTime start = LocalDateTime.now().minusHours(1);
        LocalDateTime end = LocalDateTime.now();

        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setUserId(userId);
        item.setFinalType("action");
        when(almondItemMapper.selectById(1L)).thenReturn(item);

        AlmondTag tag = new AlmondTag();
        tag.setId(101L);
        tag.setName("tag1");
        tag.setTagType("cognitive");
        when(almondTagMapper.selectTagsByAlmondId(1L)).thenReturn(List.of(tag));

        AlmondStateLog stateLog = new AlmondStateLog();
        stateLog.setId(1001L);
        stateLog.setFromStatus("raw");
        stateLog.setToStatus("understood");
        when(almondStateLogMapper.selectByAlmondId(1L)).thenReturn(List.of(stateLog));

        AlmondAiSnapshot snapshot = new AlmondAiSnapshot();
        snapshot.setId(2001L);
        snapshot.setAnalysisType("understanding");
        when(almondAiSnapshotMapper.selectByAlmondId(1L)).thenReturn(List.of(snapshot));

        ActionExecution execution = new ActionExecution();
        execution.setActualStart(start);
        execution.setActualEnd(end);
        when(actionExecutionMapper.selectByAlmondId(1L)).thenReturn(execution);

        try (MockedStatic<UserCache> userCacheMock = Mockito.mockStatic(UserCache.class)) {
            userCacheMock.when(UserCache::getUserId).thenReturn(userId);

            AlmondDetailResp resp = almondService.getAlmondDetail(1L);

            assertThat(resp.getId()).isEqualTo(1L);
            assertThat(resp.getTags()).hasSize(1);
            assertThat(resp.getStateLogs()).hasSize(1);
            assertThat(resp.getAiSnapshots()).hasSize(1);
            assertThat(resp.getActionExecution()).isNotNull();
            assertThat(resp.getActionExecution().getActualStart()).isEqualTo(start);
            assertThat(resp.getActionExecution().getActualEnd()).isEqualTo(end);
        }
    }

    @Test
    void getAlmondDetail_shouldBuildMemoryDetail() {
        Long userId = 10L;
        LocalDateTime reviewDate = LocalDateTime.now().plusDays(1);

        AlmondItem item = new AlmondItem();
        item.setId(1L);
        item.setUserId(userId);
        item.setFinalType("memory");
        when(almondItemMapper.selectById(1L)).thenReturn(item);

        MemoryAids aids = new MemoryAids();
        aids.setMindMapData("mind");
        aids.setMnemonicsData("mnem");
        aids.setSensoryData("sense");
        when(memoryAidsMapper.selectByAlmondId(1L)).thenReturn(aids);

        ReviewSchedule schedule = new ReviewSchedule();
        schedule.setId(3001L);
        schedule.setReviewDate(reviewDate);
        schedule.setCompleted(0);
        schedule.setIntervalDays(1);
        schedule.setRepetition(0);
        schedule.setEasiness(2.5);
        when(reviewScheduleMapper.selectByAlmondId(1L)).thenReturn(List.of(schedule));

        try (MockedStatic<UserCache> userCacheMock = Mockito.mockStatic(UserCache.class)) {
            userCacheMock.when(UserCache::getUserId).thenReturn(userId);

            AlmondDetailResp resp = almondService.getAlmondDetail(1L);

            assertThat(resp.getMemoryAids()).isNotNull();
            assertThat(resp.getMemoryAids().getMindMapData()).isEqualTo("mind");
            assertThat(resp.getReviewSchedules()).hasSize(1);
            assertThat(resp.getReviewSchedules().get(0).getId()).isEqualTo(3001L);
            assertThat(resp.getReviewSchedules().get(0).getReviewDate()).isEqualTo(reviewDate);
        }
    }
}
