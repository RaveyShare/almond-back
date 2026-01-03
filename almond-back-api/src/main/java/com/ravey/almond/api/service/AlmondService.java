package com.ravey.almond.api.service;

import com.ravey.almond.api.model.req.AlmondListReq;
import com.ravey.almond.api.model.req.CreateAlmondReq;
import com.ravey.almond.api.model.res.AlmondDetailResp;
import com.ravey.almond.api.model.res.AlmondItemResp;
import com.ravey.almond.api.model.res.AlmondListResp;

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
     * 获取杏仁详情（简单）
     *
     * @param id 杏仁ID
     * @return 杏仁信息
     */
    AlmondItemResp getAlmond(Long id);

    /**
     * 获取杏仁完整详情
     *
     * @param id 杏仁ID
     * @return 杏仁完整详情
     */
    AlmondDetailResp getAlmondDetail(Long id);

    /**
     * 查询杏仁库列表
     *
     * @param req 查询请求
     * @return 杏仁列表
     */
    AlmondListResp listAlmonds(AlmondListReq req);
}
