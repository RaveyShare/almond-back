package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 复习计划表
 *
 * @author Ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_schedule")
public class ReviewSchedule extends BaseEntity {

    /**
     * 杏仁ID
     */
    @TableField("almond_id")
    private Long almondId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 复习日期
     */
    @TableField("review_date")
    private LocalDateTime reviewDate;

    /**
     * 是否完成
     */
    @TableField("completed")
    private Integer completed;

    /**
     * 间隔天数
     */
    @TableField("interval_days")
    private Integer intervalDays;

    /**
     * 重复次数
     */
    @TableField("repetition")
    private Integer repetition;

    /**
     * 难易度
     */
    @TableField("easiness")
    private Double easiness;
}
