package com.ravey.almond.api.enums;

import lombok.Getter;

/**
 * 用户反馈类型枚举
 *
 * @author ravey
 * @since 1.0.0
 */
@Getter
public enum UserFeedbackType {
    
    ACCEPT("accept", "接受", "用户接受AI的分类结果"),
    MODIFY("modify", "修改", "用户修改AI的分类结果"),
    IGNORE("ignore", "忽略", "用户忽略AI的分类结果");
    
    private final String code;
    private final String name;
    private final String description;
    
    UserFeedbackType(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static UserFeedbackType fromCode(String code) {
        for (UserFeedbackType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid user feedback type code: " + code);
    }
}