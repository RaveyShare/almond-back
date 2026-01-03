package com.ravey.almond.service.sdk.aicenter.model;

import lombok.Data;

import java.util.Map;

@Data
public class AiCenterClassificationReq {
    private Long taskId;
    private Long userId;
    private String title;
    private String clarifiedContent;
    private Map<String, Object> userProfile;
    private String model;
}

