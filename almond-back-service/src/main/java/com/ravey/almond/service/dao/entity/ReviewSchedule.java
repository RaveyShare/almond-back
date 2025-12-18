package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 复习计划实体
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_schedules")
public class ReviewSchedule extends BaseEntity {

    /**
     * 关联的任务/记忆项ID
     */
    @TableField("task_id")
    @com.fasterxml.jackson.annotation.JsonProperty("memory_item_id")
    private Long taskId;

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    @com.fasterxml.jackson.annotation.JsonProperty("user_id")
    private Long userId;

    /**
     * 计划复习时间
     */
    @TableField("review_date")
    @com.fasterxml.jackson.annotation.JsonProperty("review_date")
    private LocalDateTime reviewDate;

    /**
     * 复习间隔天数
     */
    @TableField("interval_days")
    @com.fasterxml.jackson.annotation.JsonProperty("interval_days")
    private Integer intervalDays = 0;

    /**
     * 复习次数
     */
    @TableField("repetition")
    private Integer repetition = 0;

    /**
     * 简易系数
     */
    @TableField("easiness_factor")
    @com.fasterxml.jackson.annotation.JsonProperty("easiness_factor")
    private Double easinessFactor = 2.5;

    /**
     * 是否完成(0否/1是)
     */
    @TableField("completed")
    private Integer completed = 0;
}
