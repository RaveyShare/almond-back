package com.ravey.almond.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 记忆项列表请求
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
public class MemoryItemListReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 分类
     */
    private String category;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 20;
}
