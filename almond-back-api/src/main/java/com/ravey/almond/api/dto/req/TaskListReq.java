package com.ravey.almond.api.dto.req;

import lombok.Data;

import java.io.Serializable;

/**
 * 任务列表请求
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
public class TaskListReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 关键词
     */
    private String keyword;

    /**
     * 状态
     */
    private String status;

    /**
     * 任务类型(task/memory/goal)
     */
    private String taskType;

    /**
     * 页码
     */
    private Integer page = 1;

    /**
     * 每页大小
     */
    private Integer size = 20;
}
