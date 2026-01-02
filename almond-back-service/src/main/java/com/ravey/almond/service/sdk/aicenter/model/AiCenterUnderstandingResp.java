package com.ravey.almond.service.sdk.aicenter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AiCenterUnderstandingResp extends AiCenterBaseResp {
    private double confidence;
    private String reasoning;
    @JsonProperty("recommended_status")
    private String recommendedStatus;
    private String title;
    @JsonProperty("clarified_text")
    private String clarifiedText;
    private List<String> tags;
    private AiCenterUnderstandingCoreResp core;
    private String model;
    @JsonProperty("cost_time")
    private int costTime;
}
