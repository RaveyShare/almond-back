package com.ravey.almond.api.enums;

import lombok.Getter;

/**
 * AI分析类型枚举
 *
 * @author ravey
 * @since 1.0.0
 */
@Getter
public enum AIAnalysisType {
    
    CLASSIFICATION("classification", "分类分析", "对杏仁进行初始分类"),
    EVOLUTION("evolution", "演化分析", "分析杏仁的演化路径"),
    RETROSPECT("retrospect", "复盘分析", "对完成的杏仁进行复盘总结");
    
    private final String code;
    private final String name;
    private final String description;
    
    AIAnalysisType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static AIAnalysisType fromCode(String code) {
        for (AIAnalysisType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid AI analysis type code: " + code);
    }
}