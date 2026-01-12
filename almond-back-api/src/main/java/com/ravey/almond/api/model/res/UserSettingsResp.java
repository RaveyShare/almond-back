package com.ravey.almond.api.model.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户设置响应
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "用户设置响应")
public class UserSettingsResp {

    @Schema(description = "自动分类权限: always/ask/never/not_asked")
    private String autoClassifyPermission;
}
