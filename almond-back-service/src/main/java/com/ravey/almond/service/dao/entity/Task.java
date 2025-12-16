package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("task")
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
     * 任务描述
     */
    @TableField("description")
    private String description;

    /**
     * 任务层级(year/quarter/month/week/day/inbox)
     */
    @TableField("level")
    private String level;

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
    private String status;

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 排序
     */
    @TableField("order_index")
    private Integer orderIndex;
}
