package com.ravey.almond.api.enums;

import lombok.Getter;

/**
 * 杏仁最终类型枚举
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Getter
public enum AlmondFinalType {
    
    MEMORY("memory", "记忆", "需要记住的知识或信息"),
    ACTION("action", "行动", "需要执行的具体任务"),
    GOAL("goal", "目标", "中长期的目标规划"),
    IDEA("idea", "想法", "未明确分类的想法");
    
    private final String code;
    private final String name;
    private final String description;
    
    AlmondFinalType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static AlmondFinalType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AlmondFinalType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
