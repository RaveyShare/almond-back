package com.ravey.almond.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ravey.almond.service.dao.entity.ReviewSchedule;
import com.ravey.almond.service.dao.mapper.ReviewScheduleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 复习计划控制器 (兼容旧版接口路径)
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/review_schedules")
@RequiredArgsConstructor
public class ReviewScheduleController {

    private final ReviewScheduleMapper reviewScheduleMapper;

    /**
     * 获取复习计划列表
     * 兼容前端可以直接接收数组的格式
     */
    @GetMapping
    public List<ReviewSchedule> list(@RequestParam("memory_item_id") Long memoryItemId) {
        log.info("查询复习计划: memoryItemId={}", memoryItemId);
        return reviewScheduleMapper.selectList(
                new LambdaQueryWrapper<ReviewSchedule>()
                        .eq(ReviewSchedule::getTaskId, memoryItemId)
                        .orderByAsc(ReviewSchedule::getReviewDate));
    }

    /**
     * 完成复习 (站位接口)
     */
    @PostMapping("/{id}/complete")
    public ReviewSchedule complete(@PathVariable Long id, @RequestBody Object body) {
        log.info("完成复习: id={}, body={}", id, body);
        ReviewSchedule schedule = reviewScheduleMapper.selectById(id);
        if (schedule != null) {
            schedule.setCompleted(1);
            reviewScheduleMapper.updateById(schedule);
        }
        return schedule;
    }
}
