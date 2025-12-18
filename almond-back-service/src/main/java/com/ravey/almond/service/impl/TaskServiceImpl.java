package com.ravey.almond.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ravey.almond.api.dto.req.CreateTaskReq;
import com.ravey.almond.api.dto.resp.PageResult;
import com.ravey.almond.api.dto.dto.TaskDTO;
import com.ravey.almond.api.dto.req.TaskListReq;
import com.ravey.almond.service.dao.entity.Task;
import com.ravey.almond.service.dao.mapper.TaskMapper;
import com.ravey.almond.service.TaskService;
import com.ravey.almond.service.AiService;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.core.user.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
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
@RequiredArgsConstructor
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    private final AiService aiService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTask(CreateTaskReq request) {
        Task task = new Task();
        BeanUtil.copyProperties(request, task, CopyOptions.create().setIgnoreNullValue(true));

        // 设置当前用户ID
        UserInfo userInfo = UserCache.getUserInfo();
        if (userInfo == null) {
            throw new RuntimeException("User not logged in");
        }
        task.setUserId(Long.valueOf(userInfo.getUserId()));

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
        IPage<Task> resultPage = baseMapper.selectPageList(page, req, userId);

        List<TaskDTO> list = resultPage.getRecords().stream().map(task -> {
            TaskDTO dto = new TaskDTO();
            BeanUtils.copyProperties(task, dto);
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(resultPage.getTotal(), resultPage.getCurrent(), resultPage.getSize(), list);
    }

    @Override
    public String decomposeTask(Long id) {
        Task task = this.getById(id);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }
        // 默认使用 openai/gpt-4o，后续可以从配置或用户设置中获取
        return aiService.decomposeTask(task.getTitle(), task.getDescription(), "openai", "gpt-4o");
    }

    @Override
    public String generateMemoryAids(Long id) {
        Task task = this.getById(id);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }
        // 默认使用 openai/gpt-4o
        return aiService.generateMemoryAids(task.getTitle(), task.getDescription(), "openai", "gpt-4o");
    }
}
