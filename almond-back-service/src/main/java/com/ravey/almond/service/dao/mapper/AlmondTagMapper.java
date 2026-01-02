package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.AlmondTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlmondTagMapper extends BaseMapper<AlmondTag> {

    Long selectIdByName(@Param("name") String name);

    /**
     * 根据杏仁ID查询标签名称列表
     * @param almondId 杏仁ID
     * @return 标签名称列表
     */
    List<String> selectTagNamesByAlmondId(@Param("almondId") Long almondId);
}

