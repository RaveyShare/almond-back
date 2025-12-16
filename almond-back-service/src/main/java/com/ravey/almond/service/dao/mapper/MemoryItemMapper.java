package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.MemoryItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 记忆项 Mapper
 *
 * @author ravey
 * @since 1.0.0
 */
@Mapper
public interface MemoryItemMapper extends BaseMapper<MemoryItem> {
    
    /**
     * 根据ID查询详情（演示用，实际使用BaseMapper即可）
     * 
     * @param id ID
     * @return 记忆项
     */
    MemoryItem selectDetailById(@Param("id") Long id);
}
