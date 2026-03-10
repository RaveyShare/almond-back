package com.ravey.almond.service.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ravey.almond.service.dao.entity.AlmondItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 杏仁Mapper
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Mapper
public interface AlmondItemMapper extends BaseMapper<AlmondItem> {
    
    /**
     * 查询杏仁列表
     */
    List<AlmondItem> selectList(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("finalType") String finalType,
            @Param("starred") Integer starred,
            @Param("keyword") String keyword,
            @Param("sortBy") String sortBy,
            @Param("sortOrder") String sortOrder,
            @Param("offset") Integer offset,
            @Param("pageSize") Integer pageSize
    );
    
    /**
     * 统计总数
     */
    Long countList(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("finalType") String finalType,
            @Param("starred") Integer starred,
            @Param("keyword") String keyword
    );
    
    /**
     * 按状态统计
     */
    List<Map<String, Object>> countByStatus(@Param("userId") Long userId);
    
    /**
     * 按类型统计
     */
    List<Map<String, Object>> countByFinalType(@Param("userId") Long userId);
    
    /**
     * 统计星标数量
     */
    Long countStarred(@Param("userId") Long userId);
    
    /**
     * 更新AI处理结果
     */
    int updateAiResult(
            @Param("id") Long id,
            @Param("title") String title,
            @Param("clarifiedContent") String clarifiedContent,
            @Param("finalType") String finalType,
            @Param("confidence") Integer confidence,
            @Param("status") String status
    );
    
    /**
     * 更新为失败状态
     */
    int updateFailed(
            @Param("id") Long id,
            @Param("errorMessage") String errorMessage
    );
}
