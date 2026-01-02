package com.ravey.almond.service.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlmondTagRelationMapper {

    int insertIgnore(@Param("almondId") Long almondId, @Param("tagId") Long tagId);
}

