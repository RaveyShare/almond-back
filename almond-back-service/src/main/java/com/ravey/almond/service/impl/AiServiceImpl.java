package com.ravey.almond.service.impl;

import com.alibaba.fastjson.JSON;
import com.ravey.almond.api.dto.resp.ChatResponseDTO;
import com.ravey.almond.service.AiService;
import com.ravey.common.utils.http.HttpUtil;
import com.ravey.common.utils.http.HttpRespons;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * AI 服务实现
 *
 * @author ravey
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    @Value("${almond.ai-center.url:http://ai-center:8000}")
    private String aiCenterUrl;

    @Value("${almond.ai-center.token:}")
    private String aiCenterToken;

    @Override
    public String generateMemoryAids(String title, String content, String provider, String model) {
        String url = aiCenterUrl + "/v1/ai/workflow/memory";
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("content", content);
        body.put("provider", provider);
        body.put("model", model);

        return callAiCenter(url, body);
    }

    @Override
    public String decomposeTask(String title, String description, String provider, String model) {
        String url = aiCenterUrl + "/v1/ai/workflow/decompose";
        Map<String, Object> body = new HashMap<>();
        body.put("title", title);
        body.put("description", description);
        body.put("provider", provider);
        body.put("model", model);

        return callAiCenter(url, body);
    }

    private String callAiCenter(String url, Map<String, Object> body) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            if (aiCenterToken != null && !aiCenterToken.isEmpty()) {
                headers.put("Authorization", "Bearer " + aiCenterToken);
            }

            String bodyJson = JSON.toJSONString(body);
            HttpRespons responseObj = HttpUtil.postBody(url, bodyJson, headers);
            String responseStr = responseObj.getContent();
            ChatResponseDTO response = JSON.parseObject(responseStr, ChatResponseDTO.class);

            return response != null ? response.getContent() : null;
        } catch (Exception e) {
            log.error("Error calling ai-center: {}", e.getMessage(), e);
            return "AI 服务暂时不可用: " + e.getMessage();
        }
    }
}
