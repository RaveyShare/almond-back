package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.MemoryAids;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 记忆辅助Mapper
 *
 * @author Ravey
 * @since 1.0.0
 */
@Mapper
public interface MemoryAidsMapper extends BaseMapper<MemoryAids> {

    /**
     * 根据杏仁ID查询记忆辅助信息
     */
    MemoryAids selectByAlmondId(@Param("almondId") Long almondId);
}
