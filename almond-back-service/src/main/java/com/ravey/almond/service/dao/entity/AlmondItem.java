package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 杏仁核心表
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("almond_item")
public class AlmondItem extends BaseEntity {
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 用户原始输入
     */
    @TableField("content")
    private String content;
    
    /**
     * AI生成的标题
     */
    @TableField("title")
    private String title;
    
    /**
     * AI澄清后的内容
     */
    @TableField("clarified_content")
    private String clarifiedContent;
    
    /**
     * 状态: processing/done/failed
     */
    @TableField("status")
    private String status;
    
    /**
     * 最终类型: memory/action/goal/idea
     */
    @TableField("final_type")
    private String finalType;
    
    /**
     * AI置信度(0-100)
     */
    @TableField("confidence")
    private Integer confidence;
    
    /**
     * 是否星标: 0-否, 1-是
     */
    @TableField("starred")
    private Integer starred;
    
    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;
}
