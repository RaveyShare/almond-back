package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ravey.almond.api.dto.req.MemoryItemListReq;
import com.ravey.almond.service.dao.entity.MemoryItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 分页查询记忆项列表
     *
     * @param page   分页参数
     * @param req    查询请求
     * @param userId 用户ID
     * @return 分页结果
     */
    IPage<MemoryItem> selectPageList(@Param("page") Page<MemoryItem> page, @Param("req") MemoryItemListReq req,
            @Param("userId") Long userId);
}
