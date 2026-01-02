package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.AlmondItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 杏仁Mapper
 *
 * @author ravey
 * @since 1.0.0
 */
@Mapper
public interface AlmondItemMapper extends BaseMapper<AlmondItem> {

    int updateUnderstanding(
            @Param("id") Long id,
            @Param("title") String title,
            @Param("clarifiedContent") String clarifiedContent,
            @Param("almondStatus") String almondStatus
    );
}
