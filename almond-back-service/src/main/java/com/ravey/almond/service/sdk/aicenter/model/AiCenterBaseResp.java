package com.ravey.almond.service.sdk.aicenter.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author ravey
 * @since 1.0.0
 */
@Data
public class AiCenterBaseResp {
    private boolean success;
    @JsonProperty("error_message")
    private String errorMessage;
    private String rawJson;
}
