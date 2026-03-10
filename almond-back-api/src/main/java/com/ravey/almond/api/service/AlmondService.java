package com.ravey.almond.api.service;

import com.ravey.almond.api.model.req.AlmondListReq;
import com.ravey.almond.api.model.req.CreateAlmondReq;
import com.ravey.almond.api.model.req.RetryAlmondReq;
import com.ravey.almond.api.model.req.UpdateAlmondReq;
import com.ravey.almond.api.model.res.AlmondListResp;
import com.ravey.almond.api.model.res.AlmondResp;

/**
 * 杏仁服务接口
 * 
 * @author Ravey
 * @since 1.0.0
 */
public interface AlmondService {
    
    /**
     * 创建杏仁
     * 创建后自动触发AI处理
     *
     * @param req 创建请求
     * @return 杏仁信息
     */
    AlmondResp create(CreateAlmondReq req);
    
    /**
     * 获取杏仁详情
     *
     * @param id 杏仁ID
     * @return 杏仁信息
     */
    AlmondResp get(Long id);
    
    /**
     * 查询杏仁列表
     *
     * @param req 查询请求
     * @return 列表响应
     */
    AlmondListResp list(AlmondListReq req);
    
    /**
     * 更新杏仁
     *
     * @param id  杏仁ID
     * @param req 更新请求
     * @return 更新后的杏仁信息
     */
    AlmondResp update(Long id, UpdateAlmondReq req);
    
    /**
     * 删除杏仁
     *
     * @param id 杏仁ID
     */
    void delete(Long id);
    
    /**
     * 重试AI处理（仅failed状态可用）
     *
     * @param id  杏仁ID
     * @param req 重试请求
     * @return 杏仁信息
     */
    AlmondResp retry(Long id, RetryAlmondReq req);
}
