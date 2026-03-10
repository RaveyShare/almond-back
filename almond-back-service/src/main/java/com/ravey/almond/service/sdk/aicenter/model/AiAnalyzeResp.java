package com.ravey.almond.service.sdk.aicenter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * AI分析响应（理解+分类一体化）
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Data
public class AiAnalyzeResp {
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误信息
     */
    @JsonProperty("error_message")
    private String errorMessage;
    
    /**
     * AI生成的标题
     */
    private String title;
    
    /**
     * 澄清后的内容
     */
    @JsonProperty("clarified_text")
    private String clarifiedText;
    
    /**
     * 最终类型: memory/action/goal/idea
     */
    @JsonProperty("final_type")
    private String finalType;
    
    /**
     * 置信度(0-1)
     */
    private Double confidence;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * AI模型名称
     */
    private String model;
    
    /**
     * 耗时(ms)
     */
    @JsonProperty("cost_time")
    private Integer costTime;
    
    /**
     * 原始JSON响应
     */
    @JsonProperty("raw_json")
    private String rawJson;
}
