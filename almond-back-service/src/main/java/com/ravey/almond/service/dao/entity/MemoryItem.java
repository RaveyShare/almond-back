package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 记忆项实体
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "memory_items", autoResultMap = true)
public class MemoryItem extends BaseEntity {

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 内容正文
     */
    @TableField("content")
    private String content;

    /**
     * 分类
     */
    @TableField("category")
    private String category;

    /**
     * 标签列表(JSON数组)
     */
    @TableField(value = "tags", typeHandler = JacksonTypeHandler.class)
    private List<String> tags;

    /**
     * 记忆项类型(例如 general/qa/flashcard 等)
     */
    @TableField("item_type")
    private String itemType;

    /**
     * 难度(例如 easy/medium/hard)
     */
    @TableField("difficulty")
    private String difficulty;

    /**
     * 掌握度(数值，越大越掌握)
     */
    @TableField("mastery")
    private Integer mastery;

    /**
     * 复习次数
     */
    @TableField("review_count")
    private Integer reviewCount;

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
    private Integer starred;
}
