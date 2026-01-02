package com.ravey.almond.service.sdk.aicenter.model;

import lombok.Data;

@Data
public class AiCenterUnderstandingReq {
    private String title;
    private String content;
    private String text;
    private Long taskId;
    private Long userId;
    private String context;
    private String model;
    private Double temperature;
    private Integer maxTokens;
}

