package com.ravey.almond.web.controller.front;

import com.ravey.almond.api.model.req.UpdateUserSettingsReq;
import com.ravey.almond.api.model.res.UserSettingsResp;
import com.ravey.almond.service.dao.entity.UserSettings;
import com.ravey.almond.service.user.UserSettingsService;
import com.ravey.common.core.user.UserCache;
import com.ravey.common.service.web.result.HttpResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户设置前端接口
 *
 * @author ravey
 * @since 1.0.0
 */
@RestController
@RequestMapping("/front/user/settings")
@RequiredArgsConstructor
@Tag(name = "用户设置")
public class UserSettingsController {

    private final UserSettingsService userSettingsService;

    @GetMapping
    @Operation(summary = "获取用户设置")
    public HttpResult<UserSettingsResp> getUserSettings() {
        Long userId = UserCache.getUserId();
        UserSettings settings = userSettingsService.getByUserId(userId);
        return HttpResult.success(convertToResp(settings));
    }

    @PostMapping
    @Operation(summary = "更新用户设置")
    public HttpResult<Void> updateUserSettings(@RequestBody @Validated UpdateUserSettingsReq req) {
        Long userId = UserCache.getUserId();
        userSettingsService.updateSettings(userId, req);
        return HttpResult.success();
    }

    private UserSettingsResp convertToResp(UserSettings settings) {
        if (settings == null) {
            return null;
        }
        UserSettingsResp resp = new UserSettingsResp();
        resp.setAutoClassifyPermission(settings.getAutoClassifyPermission());
        return resp;
    }
}
