package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.AlmondTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Mapper
public interface AlmondTagMapper extends BaseMapper<AlmondTag> {
    
    /**
     * 根据名称查询标签ID
     */
    Long selectIdByName(@Param("name") String name);
    
    /**
     * 查询杏仁的标签名称列表
     */
    List<String> selectTagNamesByAlmondId(@Param("almondId") Long almondId);
}
