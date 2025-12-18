package com.ravey.almond.web.controller;

import com.ravey.almond.api.dto.req.CreateMemoryItemReq;
import com.ravey.almond.api.dto.req.MemoryItemListReq;
import com.ravey.almond.api.dto.resp.PageResult;
import com.ravey.almond.api.dto.dto.MemoryItemDTO;
import com.ravey.almond.service.MemoryItemService;
import com.ravey.common.service.web.result.HttpResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 记忆项控制器
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/front/memory/items")
@RequiredArgsConstructor
public class MemoryItemController {

    private final MemoryItemService memoryItemService;

    /**
     * 创建记忆项
     */
    @PostMapping("/create")
    public HttpResult<Long> create(@RequestBody CreateMemoryItemReq request) {
        Long id = memoryItemService.createMemoryItem(request);
        return HttpResult.success(id);
    }

    /**
     * 获取记忆项详情
     */
    @GetMapping("/{id}")
    public HttpResult<MemoryItemDTO> get(@PathVariable Long id) {
        MemoryItemDTO dto = memoryItemService.getMemoryItem(id);
        return HttpResult.success(dto);
    }

    /**
     * 分页查询记忆项
     */
    @GetMapping("/list")
    public HttpResult<PageResult<MemoryItemDTO>> list(@ModelAttribute MemoryItemListReq req) {
        PageResult<MemoryItemDTO> result = memoryItemService.listMemoryItems(req);
        return HttpResult.success(result);
    }

    /**
     * 更新记忆项
     */
    @PostMapping("/update")
    public HttpResult<Boolean> update(@RequestBody MemoryItemDTO dto) {
        boolean success = memoryItemService.updateMemoryItem(dto);
        return HttpResult.success(success);
    }
}
