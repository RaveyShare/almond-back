package com.ravey.almond.service.client;

import com.ravey.almond.api.dto.resp.ChatResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * AI 服务 Feign 客户端
 *
 * @author ravey
 * @since 1.0.0
 */
@FeignClient(name = "ai-center")
public interface AiFeignClient {

    @PostMapping("/v1/ai/workflow/memory")
    String generateMemoryAids(@RequestBody Map<String, Object> body, @RequestHeader("Authorization") String token);

    @PostMapping("/v1/ai/workflow/decompose")
    String decomposeTask(@RequestBody Map<String, Object> body, @RequestHeader("Authorization") String token);
}
