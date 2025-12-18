package com.ravey.almond.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ravey.almond.api.dto.req.CreateMemoryItemReq;
import com.ravey.almond.api.dto.dto.MemoryItemDTO;
import com.ravey.almond.api.dto.req.MemoryItemListReq;
import com.ravey.almond.api.dto.resp.PageResult;
import com.ravey.almond.service.dao.entity.MemoryItem;

/**
 * 记忆项服务接口
 *
 * @author ravey
 * @since 1.0.0
 */
public interface MemoryItemService extends IService<MemoryItem> {

    /**
     * 创建记忆项
     *
     * @param request 创建请求
     * @return 记忆项ID
     */
    Long createMemoryItem(CreateMemoryItemReq request);

    /**
     * 获取记忆项详情
     *
     * @param id 记忆项ID
     * @return 记忆项DTO
     */
    MemoryItemDTO getMemoryItem(Long id);

    /**
     * 分页查询记忆项
     *
     * @param req 查询请求
     * @return 分页结果
     */
    PageResult<MemoryItemDTO> listMemoryItems(MemoryItemListReq req);

    /**
     * 更新记忆项
     *
     * @param dto 记忆项DTO
     * @return 是否成功
     */
    boolean updateMemoryItem(MemoryItemDTO dto);
}
