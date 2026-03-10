package com.ravey.almond.api.enums;

import lombok.Getter;

/**
 * 杏仁状态枚举（极简版）
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Getter
public enum AlmondStatus {
    
    PROCESSING("processing", "处理中", "AI正在分析理解中"),
    DONE("done", "已完成", "AI处理完成，可以使用"),
    FAILED("failed", "失败", "AI处理失败，需要用户处理");
    
    private final String code;
    private final String name;
    private final String description;
    
    AlmondStatus(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static AlmondStatus fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AlmondStatus status : values()) {
            if (status.code.equalsIgnoreCase(code)) {
                return status;
            }
        }
        return null;
    }
    
    /**
     * 是否为终态
     */
    public boolean isTerminal() {
        return this == DONE || this == FAILED;
    }
}
