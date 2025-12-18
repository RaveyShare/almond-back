package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 任务实体
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "task", autoResultMap = true)
public class Task extends BaseEntity {

    /**
     * 父任务ID
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 任务标题
     */
    @TableField("title")
    private String title;

    /**
     * 任务描述/记忆项正文
     */
    @TableField("description")
    private String description;

    /**
     * 任务类型(task/memory/goal)
     */
    @TableField("task_type")
    private String taskType = "task";

    /**
     * 任务层级(year/quarter/month/week/day/inbox)
     */
    @TableField("level")
    private String level;

    /**
     * 标签列表(JSON数组)
     */
    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private java.util.List<String> tags;

    /**
     * 难度(easy/medium/hard)
     */
    @TableField("difficulty")
    private String difficulty = "medium";

    /**
     * 掌握度
     */
    @TableField("mastery")
    private Integer mastery = 0;

    /**
     * 复习次数
     */
    @TableField("review_count")
    private Integer reviewCount = 0;

    /**
     * 最近复习时间
     */
    @TableField("review_date")
    private LocalDateTime reviewDate;

    /**
     * 下次复习时间
     */
    @TableField("next_review_date")
    private LocalDateTime nextReviewDate;

    /**
     * 是否加星(0否/1是)
     */
    @TableField("starred")
    private Integer starred = 0;

    /**
     * 计划开始时间
     */
    @TableField("start_date")
    private LocalDateTime startDate;

    /**
     * 计划结束时间
     */
    @TableField("end_date")
    private LocalDateTime endDate;

    /**
     * 实际开始时间
     */
    @TableField("actual_start")
    private LocalDateTime actualStart;

    /**
     * 实际结束时间
     */
    @TableField("actual_end")
    private LocalDateTime actualEnd;

    /**
     * 状态(todo/doing/done/archived)
     */
    @TableField("status")
    private String status = "todo";

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority = 0;

    /**
     * 排序
     */
    @TableField("order_index")
    private Integer orderIndex = 0;
}
