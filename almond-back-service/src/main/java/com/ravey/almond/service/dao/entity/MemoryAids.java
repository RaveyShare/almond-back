package com.ravey.almond.service.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ravey.common.dao.mp.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 记忆辅助表
 *
 * @author Ravey
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("memory_aids")
public class MemoryAids extends BaseEntity {

    /**
     * 杏仁ID
     */
    @TableField("almond_id")
    private Long almondId;

    /**
     * 思维导图数据
     */
    @TableField("mind_map_data")
    private String mindMapData;

    /**
     * 助记符数据
     */
    @TableField("mnemonics_data")
    private String mnemonicsData;

    /**
     * 感官数据
     */
    @TableField("sensory_data")
    private String sensoryData;
}
