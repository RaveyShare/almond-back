package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 杏仁标签表
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("almond_tag")
public class AlmondTag extends BaseEntity {

    /**
     * 标签名称
     */
    @TableField("name")
    private String name;

    /**
     * 标签类型
     */
    @TableField("tag_type")
    private String tagType;
}

