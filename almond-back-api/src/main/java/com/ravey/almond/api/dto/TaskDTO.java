package com.ravey.almond.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务DTO
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
public class TaskDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentId;
    private Long userId;
    private String title;
    private String description;
    private String level;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;
    private String status;
    private Integer priority;
    private Integer orderIndex;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
