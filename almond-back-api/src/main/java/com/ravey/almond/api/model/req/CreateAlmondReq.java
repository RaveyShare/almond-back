package com.ravey.almond.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 创建杏仁请求
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "创建杏仁请求")
public class CreateAlmondReq implements Serializable {

    @Schema(description = "原始内容")
    @NotBlank(message = "内容不能为空")
    private String content;
}
