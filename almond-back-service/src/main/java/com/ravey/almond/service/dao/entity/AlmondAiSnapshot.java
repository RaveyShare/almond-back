package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 杏仁AI分析快照表
 *
 * @author ravey
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
     * 分析类型
     */
    @TableField("analysis_type")
    private String analysisType;

    /**
     * AI模型
     */
    @TableField("ai_model")
    private String aiModel;

    /**
     * 提示词内容
     */
    @TableField("prompt_content")
    private String promptContent;

    /**
     * 分析结果
     */
    @TableField("analysis_result")
    private String analysisResult;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 耗时(ms)
     */
    @TableField("cost_time")
    private Integer costTime;
}

