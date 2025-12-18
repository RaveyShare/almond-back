package com.ravey.almond.api.dto.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 记忆项DTO
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
public class MemoryItemDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String category;
    private List<String> tags;
    private String itemType;
    private String difficulty;
    private Integer mastery;
    private Integer reviewCount;
    private LocalDateTime reviewDate;
    private LocalDateTime nextReviewDate;
    private Integer starred;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
