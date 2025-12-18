package com.ravey.almond.api.dto.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
    private String taskType;
    private String level;
    private List<String> tags;
    private String difficulty;
    private Integer mastery;
    private Integer reviewCount;
    private LocalDateTime reviewDate;
    private LocalDateTime nextReviewDate;
    private Integer starred;
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
