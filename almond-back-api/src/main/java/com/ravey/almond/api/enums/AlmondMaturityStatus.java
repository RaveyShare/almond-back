package com.ravey.almond.api.enums;

import lombok.Getter;

/**
 * 杏仁认知成熟度状态枚举
 * 描述一条想法从“模糊”到“收敛”的演化过程
 *
 * 只关心：是否清晰、是否在演化、是否已收敛
 *
 * @author ravey
 * @since 1.0.0
 */
@Getter
public enum AlmondMaturityStatus {

    RAW(
            "raw",
            "🌱 原始",
            "随手记下的模糊想法，尚未被理解或结构化"
    ),

    UNDERSTOOD(
            "understood",
            "👀 已澄清",
            "AI 或用户已澄清其含义，知道它在说什么"
    ),

    EVOLVING(
            "evolving",
            "🔄 演化中",
            "想法在不断补充、修正或扩展，尚未定型"
    ),

    CONVERGED(
            "converged",
            "🎯 已收敛",
            "想法已成熟，可被定性为记忆 / 行动 / 目标 / 决策等"
    ),

    ARCHIVED(
            "archived",
            "🌰 冻结",
            "想法已完成使命，不再继续演化"
    );

    private final String code;
    private final String name;
    private final String description;

    AlmondMaturityStatus(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * 根据 code 获取枚举
     */
    public static AlmondMaturityStatus fromCode(String code) {
        for (AlmondMaturityStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid almond maturity status code: " + code);
    }

    /**
     * 是否为终态
     */
    public boolean isFinal() {
        return this == ARCHIVED;
    }

    /**
     * 是否允许 AI 自动推进
     */
    public boolean allowAutoAdvance() {
        return this == RAW || this == UNDERSTOOD || this == EVOLVING;
    }

    /**
     * 是否允许确定 final_type
     */
    public boolean canConverge() {
        return this == CONVERGED;
    }

    /**
     * 是否为有效状态码
     */
    public static boolean isValidCode(String code) {
        for (AlmondMaturityStatus status : values()) {
            if (status.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}
