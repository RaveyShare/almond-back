package com.ravey.almond.web.controller;

import com.ravey.almond.api.dto.CreateTaskReq;
import com.ravey.almond.api.dto.PageResult;
import com.ravey.almond.api.dto.TaskDTO;
import com.ravey.almond.api.dto.TaskListReq;
import com.ravey.almond.service.TaskService;
import com.ravey.common.service.web.result.HttpResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 任务控制器
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/front/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * 创建任务
     */
    @PostMapping("/create")
    public HttpResult<Long> create(@RequestBody CreateTaskReq request) {
        Long id = taskService.createTask(request);
        return HttpResult.success(id);
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    public HttpResult<TaskDTO> get(@PathVariable Long id) {
        TaskDTO dto = taskService.getTask(id);
        return HttpResult.success(dto);
    }

    /**
     * 分页查询任务
     */
    @GetMapping("/list")
    public HttpResult<PageResult<TaskDTO>> list(@ModelAttribute TaskListReq req) {
        PageResult<TaskDTO> result = taskService.listTasks(req);
        return HttpResult.success(result);
    }
}
