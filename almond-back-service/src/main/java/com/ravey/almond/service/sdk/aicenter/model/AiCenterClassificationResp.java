package com.ravey.almond.service.sdk.aicenter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiCenterClassificationResp extends AiCenterBaseResp {
    @JsonProperty("suggested_type")
    private String suggestedType;
    private double confidence;
    private String reasoning;
    private String model;
    @JsonProperty("cost_time")
    private int costTime;
}

