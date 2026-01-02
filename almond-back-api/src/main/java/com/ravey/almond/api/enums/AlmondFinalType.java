package com.ravey.almond.api.enums;

import lombok.Getter;

/**
 * 杏仁最终定性类型
 * 仅在 AlmondMaturityStatus = CONVERGED 时有效
 *
 * 描述一条想法“最终被当作什么来处理”
 *
 * @author ravey
 * @since 1.0.0
 */
@Getter
public enum AlmondFinalType {

    MEMORY(
            "memory",
            "🧠 记忆",
            "需要被记住的信息，适合进入复习/记忆系统"
    ),

    ACTION(
            "action",
            "✅ 行动",
            "需要执行的具体事项，通常有明确动作"
    ),

    GOAL(
            "goal",
            "🎯 目标",
            "长期或阶段性目标，需要拆解和推进"
    ),

    DECISION(
            "decision",
            "⚖ 决策",
            "需要权衡选项、做出选择的事项"
    ),

    REVIEW(
            "review",
            "🪞 复盘",
            "对已发生事情的总结、反思与提炼"
    ),

    REFERENCE(
            "reference",
            "📚 参考",
            "有价值但不需要行动或记忆的资料型内容"
    ),

    DISCARD(
            "discard",
            "🗑 放弃",
            "当前无价值或暂不处理的想法"
    );

    private final String code;
    private final String name;
    private final String description;

    AlmondFinalType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * 根据 code 获取枚举
     */
    public static AlmondFinalType fromCode(String code) {
        for (AlmondFinalType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid almond final type code: " + code);
    }

    /**
     * 是否为“可执行型”
     */
    public boolean isExecutable() {
        return this == ACTION || this == GOAL;
    }

    /**
     * 是否进入记忆系统
     */
    public boolean needMemorySystem() {
        return this == MEMORY;
    }

    /**
     * 是否需要用户明确确认
     */
    public boolean needUserConfirm() {
        return this == DECISION || this == DISCARD;
    }

    /**
     * 是否为有效 code
     */
    public static boolean isValidCode(String code) {
        for (AlmondFinalType type : values()) {
            if (type.code.equals(code)) {
                return true;
            }
        }
        return false;
    }
}
