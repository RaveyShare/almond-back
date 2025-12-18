package com.ravey.almond.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ravey.almond.api.dto.req.CreateTaskReq;
import com.ravey.almond.api.dto.dto.TaskDTO;
import com.ravey.almond.api.dto.resp.PageResult;
import com.ravey.almond.api.dto.req.TaskListReq;
import com.ravey.almond.service.dao.entity.Task;

/**
 * 任务服务接口
 *
 * @author ravey
 * @since 1.0.0
 */
public interface TaskService extends IService<Task> {

    /**
     * 创建任务
     *
     * @param request 创建请求
     * @return 任务ID
     */
    Long createTask(CreateTaskReq request);

    /**
     * 获取任务详情
     *
     * @param id 任务ID
     * @return 任务DTO
     */
    TaskDTO getTask(Long id);

    /**
     * 分页查询任务
     *
     * @param req 查询请求
     * @return 分页结果
     */
    PageResult<TaskDTO> listTasks(TaskListReq req);

    /**
     * 分解任务
     *
     * @param id 任务ID
     * @return 分解结果
     */
    String decomposeTask(Long id);

    /**
     * 生成并保存记忆辅助
     *
     * @param id 任务ID
     * @return 生成的内容
     */
    String generateMemoryAids(Long id);
}
