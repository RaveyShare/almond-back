package com.ravey.almond.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ravey.almond.api.dto.CreateTaskReq;
import com.ravey.almond.api.dto.PageResult;
import com.ravey.almond.api.dto.TaskDTO;
import com.ravey.almond.api.dto.TaskListReq;
import com.ravey.almond.service.dao.entity.Task;
import com.ravey.almond.service.dao.mapper.TaskMapper;
import com.ravey.almond.service.TaskService;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.core.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务服务实现
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(CreateTaskReq request) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task);

        // 设置当前用户ID
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo != null) {
            try {
                task.setUserId(Long.valueOf(userInfo.getUserId()));
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID format: {}", userInfo.getUserId());
                // Handle or throw exception appropriate for your system
                // For now, let's assume if it fails it might be a system call or we can't associate
                 throw new RuntimeException("Invalid user ID");
            }
        } else {
             throw new RuntimeException("User not logged in");
        }

        // 设置默认值
        if (task.getStatus() == null) {
            task.setStatus("todo");
        }
        if (task.getPriority() == null) {
            task.setPriority(0);
        }
        if (task.getOrderIndex() == null) {
            task.setOrderIndex(0);
        }

        this.save(task);
        return task.getId();
    }

    @Override
    public TaskDTO getTask(Long id) {
        // 使用自定义的XML查询方法
        Task task = baseMapper.selectDetailById(id);
        if (task == null) {
            return null;
        }
        TaskDTO dto = new TaskDTO();
        BeanUtils.copyProperties(task, dto);
        return dto;
    }

    @Override
    public PageResult<TaskDTO> listTasks(TaskListReq req) {
        // 检查用户
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo == null) {
            throw new RuntimeException("User not logged in");
        }
        Long userId = Long.valueOf(userInfo.getUserId());

        Page<Task> page = new Page<>(req.getPage(), req.getSize());
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getUserId, userId);

        if (StringUtils.hasText(req.getStatus())) {
            wrapper.eq(Task::getStatus, req.getStatus());
        }

        if (StringUtils.hasText(req.getKeyword())) {
            wrapper.and(w -> w.like(Task::getTitle, req.getKeyword())
                    .or().like(Task::getDescription, req.getKeyword()));
        }

        wrapper.orderByDesc(Task::getCreateTime);

        IPage<Task> resultPage = this.page(page, wrapper);

        List<TaskDTO> list = resultPage.getRecords().stream().map(task -> {
            TaskDTO dto = new TaskDTO();
            BeanUtils.copyProperties(task, dto);
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize(), list);
    }
}
