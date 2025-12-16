package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 任务 Mapper
 *
 * @author ravey
 * @since 1.0.0
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
    /**
     * 根据ID查询详情（演示用，实际使用BaseMapper即可）
     * 
     * @param id ID
     * @return 任务
     */
    Task selectDetailById(@Param("id") Long id);
}
