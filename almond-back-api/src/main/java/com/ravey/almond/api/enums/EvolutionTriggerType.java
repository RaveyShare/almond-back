package com.ravey.almond.api.enums;

import lombok.Getter;

/**
 * 状态演化触发类型枚举
 *
 * @author ravey
 * @since 1.0.0
 */
@Getter
public enum EvolutionTriggerType {
    
    AI("ai", "AI触发", "AI分析结果触发状态变化"),
    USER("user", "用户触发", "用户操作触发状态变化"),
    SYSTEM("system", "系统触发", "系统规则触发状态变化"),
    TIME("time", "时间触发", "时间条件触发状态变化");
    
    private final String code;
    private final String name;
    private final String description;
    
    EvolutionTriggerType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static EvolutionTriggerType fromCode(String code) {
        for (EvolutionTriggerType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid evolution trigger type code: " + code);
    }
}