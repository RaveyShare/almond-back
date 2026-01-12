package com.ravey.almond.service.sdk.aicenter;

import com.ravey.almond.service.sdk.aicenter.model.AiCenterBaseResp;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterClassificationReq;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterClassificationResp;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterUnderstandingReq;
import com.ravey.almond.service.sdk.aicenter.model.AiCenterUnderstandingResp;
import com.ravey.common.utils.http.HttpRespons;
import com.ravey.common.utils.http.HttpUtil;
import com.ravey.common.utils.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * AI Center SDK
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@Component
public class AiCenterSdk {

    @Value("${almond.ai-center.url:http://almond-ai-center:8000}")
    private String aiCenterUrl;

    @Value("${almond.ai-center.token:}")
    private String aiCenterToken;

    public AiCenterUnderstandingResp understanding(AiCenterUnderstandingReq req) {
        return post("/v1/ai/analyze/understanding", req, AiCenterUnderstandingResp.class);
    }

    public AiCenterClassificationResp classify(AiCenterClassificationReq req) {
        return post("/v1/ai/analyze/classification", req, AiCenterClassificationResp.class);
    }

    private <T extends AiCenterBaseResp> T post(String path, Object req, Class<T> clazz) {
        String url = aiCenterUrl + path;
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        if (StringUtils.hasText(aiCenterToken)) {
            headers.put("Authorization", "Bearer " + aiCenterToken);
        }

        T resp = null;
        try {
            HttpRespons httpResp = HttpUtil.postBody(url, JsonUtil.bean2Json(req), headers);

            if (httpResp.getCode() != 200) {
                resp = clazz.getDeclaredConstructor().newInstance();
                resp.setRawJson(httpResp.getContent());
                resp.setSuccess(false);
                resp.setErrorMessage(httpResp.getContent());
                return resp;
            }

            resp = JsonUtil.json2Bean(httpResp.getContent(), clazz);
            if (resp == null) {
                resp = clazz.getDeclaredConstructor().newInstance();
                resp.setSuccess(false);
                resp.setErrorMessage("JSON解析返回空");
            }
            resp.setRawJson(httpResp.getContent());
            return resp;
        } catch (Exception e) {
            log.error("ai-center call failed: url={}, req={}", url, JsonUtil.bean2Json(req), e);
            try {
                if (resp == null) {
                    resp = clazz.getDeclaredConstructor().newInstance();
                }
                resp.setSuccess(false);
                resp.setErrorMessage(e.getMessage());
                return resp;
            } catch (Exception ex) {
                log.error("instantiate response class failed", ex);
                return null;
            }
        }
    }
}
