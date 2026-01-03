package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.AlmondStateLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 杏仁状态日志Mapper
 *
 * @author ravey
 * @since 1.0.0
 */
@Mapper
public interface AlmondStateLogMapper extends BaseMapper<AlmondStateLog> {

    /**
     * 根据杏仁ID查询状态日志列表（按时间倒序）
     */
    List<AlmondStateLog> selectByAlmondId(@Param("almondId") Long almondId);

    /**
     * 统计用户反馈数据
     * @param userId 用户ID
     * @return Map<feedback_type, count>
     */
    Map<String, Long> countFeedbackStats(@Param("userId") Long userId);
}
