package com.ravey.almond.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 更新用户设置请求
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "更新用户设置请求")
public class UpdateUserSettingsReq {

    @NotBlank(message = "自动分类权限不能为空")
    @Schema(description = "自动分类权限: always/ask/never/not_asked")
    private String autoClassifyPermission;
}
