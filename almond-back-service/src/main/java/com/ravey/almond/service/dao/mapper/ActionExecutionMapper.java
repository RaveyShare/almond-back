package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.ActionExecution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 行动执行Mapper
 *
 * @author Ravey
 * @since 1.0.0
 */
@Mapper
public interface ActionExecutionMapper extends BaseMapper<ActionExecution> {

    /**
     * 根据杏仁ID查询行动执行信息
     */
    ActionExecution selectByAlmondId(@Param("almondId") Long almondId);
}
