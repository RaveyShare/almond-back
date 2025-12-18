package com.ravey.almond.api.dto.resp;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * AI 聊天响应DTO
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
public class ChatResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String content;
    private Map<String, Object> usage;
}
