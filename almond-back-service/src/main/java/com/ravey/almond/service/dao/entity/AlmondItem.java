package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 杏仁核心表
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("almond_item")
public class AlmondItem extends BaseEntity {

    /**
     * 父杏仁ID（拆解/派生）
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * AI生成原始输入摘要
     */
    @TableField("title")
    private String title;

    /**
     * 原始内容
     */
    @TableField("content")
    private String content;

    /**
     * AI澄清后的内容，用户可修改
     */
    @TableField("clarified_content")
    private String clarifiedContent;

    /**
     * 成熟度状态: raw/understood/evolving/converged/archived
     */
    @TableField("almond_status")
    private String almondStatus;

    /**
     * 终态类型: memory/action/goal/decision/review
     */
    @TableField("final_type")
    private String finalType;

    /**
     * 成熟度评分(0-100)
     */
    @TableField("maturity_score")
    private Integer maturityScore;

    /**
     * 演化阶段
     */
    @TableField("evolution_stage")
    private Integer evolutionStage;

    /**
     * accept/modify/reject
     */
    @TableField("user_feedback")
    private String userFeedback;

    /**
     * 优先级
     */
    @TableField("priority")
    private Integer priority;

    /**
     * 是否标星
     */
    @TableField("starred")
    private Integer starred;
}
