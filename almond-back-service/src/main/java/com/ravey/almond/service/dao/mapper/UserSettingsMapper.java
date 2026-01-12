package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户设置Mapper
 *
 * @author ravey
 * @since 1.0.0
 */
@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {
}
