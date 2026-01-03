package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 行动执行表
 *
 * @author Ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("action_execution")
public class ActionExecution extends BaseEntity {

    /**
     * 杏仁ID
     */
    @TableField("almond_id")
    private Long almondId;

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
}
