package com.ravey.almond.service.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 杏仁-标签关联Mapper
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Mapper
public interface AlmondTagRelationMapper {
    
    /**
     * 插入关联（忽略重复）
     */
    int insertIgnore(@Param("almondId") Long almondId, @Param("tagId") Long tagId);
    
    /**
     * 删除杏仁的所有标签关联
     */
    int deleteByAlmondId(@Param("almondId") Long almondId);
}
