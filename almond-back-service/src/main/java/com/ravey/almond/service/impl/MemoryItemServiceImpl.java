package com.ravey.almond.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ravey.almond.api.dto.req.CreateMemoryItemReq;
import com.ravey.almond.api.dto.dto.MemoryItemDTO;
import com.ravey.almond.api.dto.req.MemoryItemListReq;
import com.ravey.almond.api.dto.resp.PageResult;
import com.ravey.almond.service.dao.entity.MemoryItem;
import com.ravey.almond.service.dao.mapper.MemoryItemMapper;
import com.ravey.almond.service.MemoryItemService;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.core.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 记忆项服务实现
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@Service
public class MemoryItemServiceImpl extends ServiceImpl<MemoryItemMapper, MemoryItem> implements MemoryItemService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMemoryItem(CreateMemoryItemReq request) {
        MemoryItem memoryItem = new MemoryItem();
        BeanUtil.copyProperties(request, memoryItem, CopyOptions.create().setIgnoreNullValue(true));
        memoryItem.setTaskType("memory");

        // 设置当前用户ID
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo == null) {
            throw new RuntimeException("User not logged in");
        }
        memoryItem.setUserId(Long.valueOf(userInfo.getUserId()));

        // 如果没有设置时间，设置为当前时间
        LocalDateTime now = LocalDateTime.now();
        if (memoryItem.getReviewDate() == null) {
            memoryItem.setReviewDate(now);
        }
        if (memoryItem.getNextReviewDate() == null) {
            memoryItem.setNextReviewDate(now);
        }

        this.save(memoryItem);
        return memoryItem.getId();
    }

    @Override
    public MemoryItemDTO getMemoryItem(Long id) {
        // 使用自定义的XML查询方法
        MemoryItem memoryItem = baseMapper.selectDetailById(id);
        if (memoryItem == null) {
            return null;
        }
        MemoryItemDTO dto = new MemoryItemDTO();
        BeanUtils.copyProperties(memoryItem, dto);
        return dto;
    }

    @Override
    public PageResult<MemoryItemDTO> listMemoryItems(MemoryItemListReq req) {
        Page<MemoryItem> page = new Page<>(req.getPage(), req.getSize());

        // 获取当前用户
        UserInfo userInfo = UserCache.getUserInfo();
        Long userId = userInfo != null ? Long.valueOf(userInfo.getUserId()) : null;

        IPage<MemoryItem> resultPage = baseMapper.selectPageList(page, req, userId);

        List<MemoryItemDTO> list = resultPage.getRecords().stream().map(item -> {
            MemoryItemDTO dto = new MemoryItemDTO();
            BeanUtils.copyProperties(item, dto);
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize(), list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMemoryItem(MemoryItemDTO dto) {
        if (dto.getId() == null) {
            throw new RuntimeException("ID cannot be null");
        }
        MemoryItem memoryItem = this.getById(dto.getId());
        if (memoryItem == null) {
            throw new RuntimeException("Memory item not found");
        }

        // 修改为只更新非空字段
        BeanUtil.copyProperties(dto, memoryItem, CopyOptions.create().setIgnoreNullValue(true));

        // 确保 user_id 匹配（简单防越权）
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo != null && !memoryItem.getUserId().equals(Long.valueOf(userInfo.getUserId()))) {
            throw new RuntimeException("No permission to update this item");
        }

        return this.updateById(memoryItem);
    }
}
