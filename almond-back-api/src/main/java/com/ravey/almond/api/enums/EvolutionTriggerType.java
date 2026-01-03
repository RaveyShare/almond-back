package com.ravey.almond.api.enums;

import lombok.Getter;

/**
 * 演化触发类型
 *
 * @author Ravey
 * @since 1.0.0
 */
@Getter
public enum EvolutionTriggerType {

    AI("ai", "AI触发"),
    USER("user", "用户触发"),
    SYSTEM("system", "系统触发"),
    TIME("time", "定时触发");

    private final String code;
    private final String desc;

    EvolutionTriggerType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static EvolutionTriggerType fromCode(String code) {
        for (EvolutionTriggerType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
