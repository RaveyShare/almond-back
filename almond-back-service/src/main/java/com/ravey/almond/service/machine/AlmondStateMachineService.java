package com.ravey.almond.service.machine;

import com.ravey.almond.api.enums.AlmondMaturityStatus;
import com.ravey.almond.api.enums.EvolutionTriggerType;
import com.ravey.almond.service.dao.entity.AlmondItem;
import com.ravey.almond.service.dao.entity.AlmondStateLog;
import com.ravey.almond.service.dao.mapper.AlmondItemMapper;
import com.ravey.almond.service.dao.mapper.AlmondStateLogMapper;
import com.ravey.common.utils.json.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 杏仁状态机服务
 * 管理杏仁的状态流转
 *
 * @author Ravey
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlmondStateMachineService {

    private final AlmondItemMapper almondItemMapper;
    private final AlmondStateLogMapper almondStateLogMapper;

    /**
     * 状态流转
     */
    @Transactional(rollbackFor = Exception.class)
    public void transition(Long almondId, 
                          AlmondMaturityStatus targetStatus,
                          EvolutionTriggerType triggerType, 
                          String description,
                          Map<String, Object> contextData) {
        
        log.info("状态流转开始，almondId: {}, target: {}, trigger: {}", 
            almondId, targetStatus, triggerType);
        
        AlmondItem item = almondItemMapper.selectById(almondId);
        if (item == null) {
            throw new IllegalArgumentException("杏仁不存在: " + almondId);
        }
        
        AlmondMaturityStatus currentStatus = AlmondMaturityStatus.valueOf(
            item.getAlmondStatus().toUpperCase()
        );
        
        // 1. 验证状态流转是否合法
        if (!canTransition(currentStatus, targetStatus, triggerType)) {
            throw new IllegalStateException(
                String.format("不允许从 %s 流转到 %s", currentStatus, targetStatus)
            );
        }
        
        // 2. 更新状态
        item.setAlmondStatus(targetStatus.name().toLowerCase());
        item.setUpdateTime(LocalDateTime.now());
        almondItemMapper.updateById(item);
        
        // 3. 记录状态日志
        recordStateLog(almondId, item.getUserId(), currentStatus, targetStatus, 
            triggerType, description, contextData);
        
        // 4. 后置处理
        handlePostTransition(almondId, item.getUserId(), targetStatus);
        
        log.info("状态流转完成，almondId: {}, from: {} -> to: {}", 
            almondId, currentStatus, targetStatus);
    }

    /**
     * 验证状态流转是否合法
     */
    private boolean canTransition(AlmondMaturityStatus from, 
                                  AlmondMaturityStatus to,
                                  EvolutionTriggerType triggerType) {
        
        // 定义允许的状态流转
        Map<AlmondMaturityStatus, List<AlmondMaturityStatus>> allowedTransitions = Map.of(
            AlmondMaturityStatus.RAW, List.of(AlmondMaturityStatus.UNDERSTOOD),
            AlmondMaturityStatus.UNDERSTOOD, List.of(
                AlmondMaturityStatus.EVOLVING, 
                AlmondMaturityStatus.CONVERGED
            ),
            AlmondMaturityStatus.EVOLVING, List.of(
                AlmondMaturityStatus.CONVERGED,
                AlmondMaturityStatus.UNDERSTOOD
            ),
            AlmondMaturityStatus.CONVERGED, List.of(
                AlmondMaturityStatus.ARCHIVED,
                AlmondMaturityStatus.EVOLVING
            ),
            AlmondMaturityStatus.ARCHIVED, List.of()
        );
        
        List<AlmondMaturityStatus> allowed = allowedTransitions.get(from);
        if (allowed == null) {
            return false;
        }
        
        return allowed.contains(to);
    }

    /**
     * 记录状态日志
     */
    private void recordStateLog(Long almondId, Long userId,
                                AlmondMaturityStatus from,
                                AlmondMaturityStatus to,
                                EvolutionTriggerType triggerType,
                                String description,
                                Map<String, Object> contextData) {
        
        AlmondStateLog log = new AlmondStateLog();
        log.setAlmondId(almondId);
        log.setUserId(userId);
        log.setFromStatus(from.name().toLowerCase());
        log.setToStatus(to.name().toLowerCase());
        log.setTriggerType(triggerType.getCode());
        log.setDescription(description);
        
        if (contextData != null && !contextData.isEmpty()) {
            log.setContextData(JsonUtil.bean2Json(contextData));
        }
        
        log.setCreateTime(LocalDateTime.now());
        
        almondStateLogMapper.insert(log);
    }

    /**
     * 状态流转后的后置处理
     */
    private void handlePostTransition(Long almondId, Long userId, AlmondMaturityStatus status) {
        switch (status) {
            case CONVERGED:
                handleConvergedPost(almondId, userId);
                break;
            case ARCHIVED:
                handleArchivedPost(almondId, userId);
                break;
            default:
                // 其他状态暂无后置处理
                break;
        }
    }

    /**
     * CONVERGED状态的后置处理
     */
    private void handleConvergedPost(Long almondId, Long userId) {
        // Phase 3会在这里实现类型特定处理
        AlmondItem item = almondItemMapper.selectById(almondId);
        
        if (item.getFinalType() == null) {
            log.warn("杏仁已收敛但未设置finalType，almondId: {}", almondId);
            return;
        }
        
        log.info("触发类型特定处理，almondId: {}, finalType: {}", almondId, item.getFinalType());
        
        // TODO: Phase 3 - 根据finalType调用相应的处理服务
        // switch (item.getFinalType()) {
        //     case "memory":
        //         memoryService.handleMemoryConverged(almondId, userId);
        //         break;
        //     case "action":
        //         actionService.handleActionConverged(almondId, userId);
        //         break;
        //     case "goal":
        //         goalService.handleGoalConverged(almondId, userId);
        //         break;
        // }
    }

    /**
     * ARCHIVED状态的后置处理
     */
    private void handleArchivedPost(Long almondId, Long userId) {
        log.info("杏仁已归档，almondId: {}", almondId);
        
        // TODO: 归档后的处理
        // 1. 统计更新
        // 2. 通知发送
        // 3. 清理临时数据等
    }

    /**
     * 手动流转状态（用户操作）
     */
    @Transactional(rollbackFor = Exception.class)
    public void manualTransition(Long almondId, Long userId, 
                                 AlmondMaturityStatus targetStatus,
                                 String reason) {
        
        Map<String, Object> context = Map.of("manual_reason", reason);
        
        transition(
            almondId,
            targetStatus,
            EvolutionTriggerType.USER,
            "用户手动操作: " + reason,
            context
        );
    }

    /**
     * 检查是否可以流转到目标状态
     */
    public boolean checkCanTransition(Long almondId, AlmondMaturityStatus targetStatus) {
        AlmondItem item = almondItemMapper.selectById(almondId);
        if (item == null) {
            return false;
        }
        
        AlmondMaturityStatus currentStatus = AlmondMaturityStatus.valueOf(
            item.getAlmondStatus().toUpperCase()
        );
        
        return canTransition(currentStatus, targetStatus, EvolutionTriggerType.USER);
    }
}
