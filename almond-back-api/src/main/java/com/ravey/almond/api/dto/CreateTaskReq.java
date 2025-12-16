package com.ravey.almond.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 创建任务请求
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
public class CreateTaskReq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 父任务ID
     */
    private Long parentId;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务层级(year/quarter/month/week/day/inbox)
     */
    private String level;

    /**
     * 计划开始时间
     */
    private LocalDateTime startDate;

    /**
     * 计划结束时间
     */
    private LocalDateTime endDate;

    /**
     * 优先级
     */
    private Integer priority;
}
