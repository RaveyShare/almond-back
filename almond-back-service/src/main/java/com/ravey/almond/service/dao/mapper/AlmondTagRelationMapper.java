package com.ravey.almond.service.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 杏仁标签关联Mapper
 *
 * @author ravey
 * @since 1.0.0
 */
@Mapper
public interface AlmondTagRelationMapper {

    /**
     * 插入标签关联（忽略重复）
     */
    int insertIgnore(@Param("almondId") Long almondId, @Param("tagId") Long tagId);
}
