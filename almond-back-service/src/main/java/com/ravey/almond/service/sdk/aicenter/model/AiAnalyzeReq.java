package com.ravey.almond.service.sdk.aicenter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * AI分析请求（理解+分类一体化）
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Data
public class AiAnalyzeReq {
    
    /**
     * 任务ID（杏仁ID）
     */
    @JsonProperty("task_id")
    private Long taskId;
    
    /**
     * 用户ID
     */
    @JsonProperty("user_id")
    private Long userId;
    
    /**
     * 用户输入的原始文本
     */
    @JsonProperty("text")
    private String text;
}
