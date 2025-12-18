package com.ravey.almond.service;

import com.ravey.almond.api.dto.resp.ChatResponseDTO;

/**
 * AI 服务接口
 *
 * @author ravey
 * @since 1.0.0
 */
public interface AiService {

    /**
     * 生成记忆辅助内容
     *
     * @param title    标题
     * @param content  内容
     * @param provider AI 提供商
     * @param model    模型
     * @return AI 生成的内容
     */
    String generateMemoryAids(String title, String content, String provider, String model);

    /**
     * 分解任务
     *
     * @param title       任务标题
     * @param description 任务描述
     * @param provider    AI 提供商
     * @param model       模型
     * @return AI 生成的分解结果
     */
    String decomposeTask(String title, String description, String provider, String model);
}
