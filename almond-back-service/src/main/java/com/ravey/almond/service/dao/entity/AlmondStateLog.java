package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 杏仁状态演化日志
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("almond_state_log")
public class AlmondStateLog extends BaseEntity {

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
     * 原状态
     */
    @TableField("from_status")
    private String fromStatus;

    /**
     * 新状态
     */
    @TableField("to_status")
    private String toStatus;

    /**
     * 触发类型: ai/user/system/time
     */
    @TableField("trigger_type")
    private String triggerType;

    /**
     * 触发事件
     */
    @TableField("trigger_event")
    private String triggerEvent;

    /**
     * 上下文数据(JSON)
     */
    @TableField("context_data")
    private String contextData;

    /**
     * 描述
     */
    @TableField("description")
    private String description;
}
