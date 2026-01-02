package com.ravey.almond.api.service;

import com.ravey.almond.api.model.req.CreateAlmondReq;
import com.ravey.almond.api.model.res.AlmondItemResp;

/**
 * 杏仁服务接口
 *
 * @author ravey
 * @since 1.0.0
 */
public interface AlmondService {

    /**
     * 创建杏仁
     *
     * @param req 创建请求
     * @return 杏仁信息
     */
    AlmondItemResp createAlmond(CreateAlmondReq req);

    /**
     * 获取杏仁详情
     *
     * @param id 杏仁ID
     * @return 杏仁信息
     */
    AlmondItemResp getAlmond(Long id);
}
