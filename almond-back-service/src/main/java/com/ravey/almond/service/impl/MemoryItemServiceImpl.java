package com.ravey.almond.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ravey.almond.api.dto.CreateMemoryItemReq;
import com.ravey.almond.api.dto.MemoryItemDTO;
import com.ravey.almond.api.dto.MemoryItemListReq;
import com.ravey.almond.api.dto.PageResult;
import com.ravey.almond.service.dao.entity.MemoryItem;
import com.ravey.almond.service.dao.mapper.MemoryItemMapper;
import com.ravey.almond.service.MemoryItemService;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.core.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
        BeanUtils.copyProperties(request, memoryItem);

        // 设置当前用户ID
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo != null) {
            memoryItem.setUserId(Long.valueOf(userInfo.getUserId()));
        } else {
            // 如果获取不到用户信息，抛出异常或处理
            throw new RuntimeException("User not logged in");
        }

        // 设置默认值
        if (memoryItem.getMastery() == null) {
            memoryItem.setMastery(0);
        }
        if (memoryItem.getReviewCount() == null) {
            memoryItem.setReviewCount(0);
        }
        if (memoryItem.getStarred() == null) {
            memoryItem.setStarred(0);
        }

        // 显式设置复习时间，避免数据库默认值时区问题
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
        LambdaQueryWrapper<MemoryItem> queryWrapper = new LambdaQueryWrapper<>();
        
        // 获取当前用户
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo != null) {
            queryWrapper.eq(MemoryItem::getUserId, Long.valueOf(userInfo.getUserId()));
        }

        // 关键词搜索
        if (req.getKeyword() != null && !req.getKeyword().isEmpty()) {
            queryWrapper.and(w -> w.like(MemoryItem::getTitle, req.getKeyword())
                    .or()
                    .like(MemoryItem::getContent, req.getKeyword()));
        }

        // 分类过滤
        if (req.getCategory() != null && !req.getCategory().isEmpty()) {
            queryWrapper.eq(MemoryItem::getCategory, req.getCategory());
        }

        // 按创建时间倒序
        queryWrapper.orderByDesc(MemoryItem::getCreateTime);

        IPage<MemoryItem> resultPage = this.page(page, queryWrapper);

        List<MemoryItemDTO> list = resultPage.getRecords().stream().map(item -> {
            MemoryItemDTO dto = new MemoryItemDTO();
            BeanUtils.copyProperties(item, dto);
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize(), list);
    }
}
