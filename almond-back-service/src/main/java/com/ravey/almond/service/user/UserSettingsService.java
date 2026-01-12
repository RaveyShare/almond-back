package com.ravey.almond.service.user;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ravey.almond.api.model.req.UpdateUserSettingsReq;
import com.ravey.almond.service.dao.entity.UserSettings;
import com.ravey.almond.service.dao.mapper.UserSettingsMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户设置服务
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSettingsService {

    private final UserSettingsMapper userSettingsMapper;

    /**
     * 获取用户设置，如果不存在则创建默认设置
     *
     * @param userId 用户ID
     * @return 用户设置
     */
    public UserSettings getByUserId(Long userId) {
        UserSettings settings = userSettingsMapper.selectOne(
                new LambdaQueryWrapper<UserSettings>()
                        .eq(UserSettings::getUserId, userId)
        );

        if (settings == null) {
            log.info("用户设置不存在，创建默认设置，userId: {}", userId);
            settings = new UserSettings();
            settings.setUserId(userId);
            settings.setAutoClassifyPermission("not_asked");
            userSettingsMapper.insert(settings);
        }

        return settings;
    }

    /**
     * 更新用户设置
     *
     * @param userId 用户ID
     * @param req    更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateSettings(Long userId, UpdateUserSettingsReq req) {
        UserSettings settings = getByUserId(userId);
        settings.setAutoClassifyPermission(req.getAutoClassifyPermission());
        userSettingsMapper.updateById(settings);
        log.info("更新用户设置成功，userId: {}, settings: {}", userId, settings);
    }
}
