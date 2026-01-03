package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.ReviewSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 复习计划Mapper
 *
 * @author Ravey
 * @since 1.0.0
 */
@Mapper
public interface ReviewScheduleMapper extends BaseMapper<ReviewSchedule> {

    /**
     * 根据杏仁ID查询复习计划列表
     */
    List<ReviewSchedule> selectByAlmondId(@Param("almondId") Long almondId);
}
