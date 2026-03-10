package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI分析快照表
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("almond_ai_snapshot")
public class AlmondAiSnapshot extends BaseEntity {
    
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
     * AI模型名称
     */
    @TableField("ai_model")
    private String aiModel;
    
    /**
     * 请求内容
     */
    @TableField("request_content")
    private String requestContent;
    
    /**
     * 响应内容
     */
    @TableField("response_content")
    private String responseContent;
    
    /**
     * 状态: success/failed
     */
    @TableField("status")
    private String status;
    
    /**
     * 耗时(ms)
     */
    @TableField("cost_time")
    private Integer costTime;
}
