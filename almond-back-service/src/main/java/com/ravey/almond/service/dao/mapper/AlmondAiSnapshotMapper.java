package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.AlmondAiSnapshot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 杏仁AI快照Mapper
 *
 * @author ravey
 * @since 1.0.0
 */
@Mapper
public interface AlmondAiSnapshotMapper extends BaseMapper<AlmondAiSnapshot> {

    /**
     * 根据杏仁ID查询AI快照列表（按时间倒序）
     */
    List<AlmondAiSnapshot> selectByAlmondId(@Param("almondId") Long almondId);
}
