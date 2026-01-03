package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.AlmondTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 杏仁标签Mapper
 *
 * @author ravey
 * @since 1.0.0
 */
@Mapper
public interface AlmondTagMapper extends BaseMapper<AlmondTag> {

    /**
     * 根据名称查询标签ID
     */
    Long selectIdByName(@Param("name") String name);

    /**
     * 根据杏仁ID查询标签名称列表
     */
    List<String> selectTagNamesByAlmondId(@Param("almondId") Long almondId);

    /**
     * 根据杏仁ID查询标签详情列表
     */
    List<AlmondTag> selectTagsByAlmondId(@Param("almondId") Long almondId);

    /**
     * 查询用户常用标签
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 标签名称列表
     */
    List<String> selectFrequentTagsByUserId(@Param("userId") Long userId, @Param("limit") int limit);
}
