package com.ravey.almond.api.dto.req;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
     * 任务描述/记忆项正文
     */
    private String description;

    /**
     * 任务类型(task/memory/goal)
     */
    private String taskType;

    /**
     * 任务层级(year/quarter/month/week/day/inbox)
     */
    private String level;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 难度(easy/medium/hard)
     */
    private String difficulty;

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
