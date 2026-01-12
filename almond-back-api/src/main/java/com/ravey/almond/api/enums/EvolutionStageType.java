package com.ravey.almond.api.enums;

import lombok.Getter;

/**
 * 演化阶段枚举（系统内部精确控制）
 * 
 * 与 almond_status 的关系：
 * - status 是用户视角的宏观状态（5种）
 * - stage 是系统内部的精确阶段（9种），用于驱动AI流程和自动化
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Getter
public enum EvolutionStageType {

    /**
     * 阶段0: 已创建，等待AI理解
     * status: RAW
     */
    CREATED(0, "已创建", "等待AI理解", AlmondMaturityStatus.RAW),

    /**
     * 阶段1: AI理解中
     * status: RAW
     */
    UNDERSTANDING(1, "AI理解中", "正在调用AI理解接口", AlmondMaturityStatus.RAW),

    /**
     * 阶段2: AI理解完成，等待分类
     * status: UNDERSTOOD
     */
    UNDERSTOOD(2, "AI理解完成", "等待分类分析", AlmondMaturityStatus.UNDERSTOOD),

    /**
     * 阶段3: AI分类中
     * status: EVOLVING
     */
    CLASSIFYING(3, "AI分类中", "正在调用AI分类接口", AlmondMaturityStatus.EVOLVING),

    /**
     * 阶段4: AI分类完成，等待用户确认（仅MANUAL模式）
     * status: EVOLVING
     */
    CLASSIFIED(4, "AI分类完成", "等待用户确认", AlmondMaturityStatus.EVOLVING),

    /**
     * 阶段5: 已收敛，类型确定
     * status: CONVERGED
     */
    CONVERGED(5, "已收敛", "类型已确定", AlmondMaturityStatus.CONVERGED),

    /**
     * 阶段6: 类型特定处理中（创建复习计划、执行记录等）
     * status: CONVERGED
     */
    TYPE_PROCESSING(6, "类型处理中", "正在创建类型特定数据", AlmondMaturityStatus.CONVERGED),

    /**
     * 阶段7: 完全完成（所有处理完成）
     * status: CONVERGED
     */
    COMPLETED(7, "完全完成", "所有处理已完成", AlmondMaturityStatus.CONVERGED),

    /**
     * 阶段8: 已归档
     * status: ARCHIVED
     */
    ARCHIVED(8, "已归档", "已归档冻结", AlmondMaturityStatus.ARCHIVED);

    private final int code;
    private final String name;
    private final String description;
    private final AlmondMaturityStatus relatedStatus;

    EvolutionStageType(int code, String name, String description, AlmondMaturityStatus relatedStatus) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.relatedStatus = relatedStatus;
    }

    /**
     * 根据code获取枚举
     */
    public static EvolutionStageType fromCode(int code) {
        for (EvolutionStageType stage : values()) {
            if (stage.code == code) {
                return stage;
            }
        }
        return CREATED; // 默认返回CREATED
    }

    /**
     * 获取对应的状态码（字符串）
     */
    public String getStatusCode() {
        return relatedStatus.getCode();
    }
}
