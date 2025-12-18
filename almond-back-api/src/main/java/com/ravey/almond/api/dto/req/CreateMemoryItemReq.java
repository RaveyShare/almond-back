package com.ravey.almond.api.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建记忆项请求
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
public class CreateMemoryItemReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容正文
     */
    private String content;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 记忆项类型(例如 general/qa/flashcard 等)
     */
    private String itemType;

    /**
     * 难度(例如 easy/medium/hard)
     */
    private String difficulty;
}
