package com.ravey.almond.service.statemachine;

import com.ravey.almond.api.enums.AlmondMaturityStatus;
import com.ravey.almond.api.enums.AlmondFinalType;
import com.ravey.almond.api.enums.EvolutionTriggerType;

import java.util.*;

/**
 * 小杏仁认知状态机
 * 管理 maturity_status 流转，同时约束 final_type
 * AI 自动流转为默认，用户可手动 override
 */
public class AlmondStateMachine {

    /**
     * 状态转换规则图：maturity_status -> 允许的下一个状态
     */
    private static final Map<AlmondMaturityStatus, AlmondMaturityStatus> TRANSITION_RULES;
    static {
        Map<AlmondMaturityStatus, AlmondMaturityStatus> map = new EnumMap<>(AlmondMaturityStatus.class);
        map.put(AlmondMaturityStatus.RAW, AlmondMaturityStatus.UNDERSTOOD);
        map.put(AlmondMaturityStatus.UNDERSTOOD, AlmondMaturityStatus.EVOLVING);
        map.put(AlmondMaturityStatus.EVOLVING, AlmondMaturityStatus.CONVERGED);
        map.put(AlmondMaturityStatus.CONVERGED, AlmondMaturityStatus.ARCHIVED);
        map.put(AlmondMaturityStatus.ARCHIVED, null); // 终态
        TRANSITION_RULES = Collections.unmodifiableMap(map);
    }

    /**
     * 校验是否允许状态流转
     *
     * @param current 当前 maturity_status
     * @param target  目标 maturity_status
     * @param trigger 触发类型（AI / USER / SYSTEM）
     * @return true 如果允许流转
     */
    public static boolean canTransition(AlmondMaturityStatus current, AlmondMaturityStatus target, EvolutionTriggerType trigger) {
        AlmondMaturityStatus allowed = TRANSITION_RULES.get(current);
        if (allowed == null) {
            return false;
        }
        if (target != allowed) {
            // 只有用户手动 override 才允许跳跃（AI / SYSTEM 不允许跳跃）
            return trigger == EvolutionTriggerType.USER;
        }
        return true;
    }

    /**
     * 执行状态流转
     *
     * @param current 当前 maturity_status
     * @param trigger 触发类型
     * @return 下一个状态（null 表示终态或非法）
     */
    public static AlmondMaturityStatus nextState(AlmondMaturityStatus current, EvolutionTriggerType trigger) {
        AlmondMaturityStatus allowed = TRANSITION_RULES.get(current);
        if (allowed == null) return null;
        if (trigger == EvolutionTriggerType.AI || trigger == EvolutionTriggerType.SYSTEM) {
            return allowed;
        }
        // USER 可手动 override
        return allowed;
    }

    /**
     * 判断是否为终态
     *
     * @param status 当前状态
     * @return true 如果是 ARCHIVED
     */
    public static boolean isFinalState(AlmondMaturityStatus status) {
        return status == AlmondMaturityStatus.ARCHIVED;
    }

    /**
     * 判断是否可以赋值 final_type
     *
     * @param status 当前 maturity_status
     * @return true 如果状态允许 final_type
     */
    public static boolean canAssignFinalType(AlmondMaturityStatus status) {
        return status == AlmondMaturityStatus.CONVERGED;
    }

    /**
     * 获取下一个可能状态（用于 UI / AI 决策）
     */
    public static AlmondMaturityStatus getAllowedNextState(AlmondMaturityStatus current, EvolutionTriggerType trigger) {
        if (TRANSITION_RULES.get(current) == null) return null;
        if (trigger == EvolutionTriggerType.AI || trigger == EvolutionTriggerType.SYSTEM) {
            return TRANSITION_RULES.get(current);
        }
        return TRANSITION_RULES.get(current); // USER 可以 override
    }

    /**
     * 示例 AI 自动流转执行
     */
    public static AlmondMaturityStatus autoAdvanceByAI(AlmondMaturityStatus current) {
        return getAllowedNextState(current, EvolutionTriggerType.AI);
    }

    /**
     * 示例用户手动流转执行
     */
    public static AlmondMaturityStatus userOverride(AlmondMaturityStatus current, AlmondMaturityStatus target) {
        if (canTransition(current, target, EvolutionTriggerType.USER)) {
            return target;
        }
        throw new IllegalStateException("用户无法跳转到目标状态：" + target);
    }

    /**
     * 获取整个状态流列表（可用于 UI 显示或日志）
     */
    public static List<AlmondMaturityStatus> getAllStates() {
        return List.of(AlmondMaturityStatus.RAW, AlmondMaturityStatus.UNDERSTOOD,
                AlmondMaturityStatus.EVOLVING, AlmondMaturityStatus.CONVERGED,
                AlmondMaturityStatus.ARCHIVED);
    }

    /**
     * AI 自动决定 final_type 示例（仅 CONVERGED 状态允许）
     */
    public static AlmondFinalType autoDetermineFinalType(AlmondMaturityStatus status, Set<AlmondFinalType> candidates, double confidence) {
        if (!canAssignFinalType(status)) return null;
        if (confidence < 0.75) return null; // 阈值
        // 简单示例：选最高优先级类型
        return candidates.stream().findFirst().orElse(null);
    }
}
