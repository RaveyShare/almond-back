package com.ravey.almond.api.model.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 杏仁信息响应
 *
 * @author ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "杏仁信息响应")
public class AlmondItemResp implements Serializable {

    @Schema(description = "杏仁ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述/内容")
    private String description;

    @Schema(description = "原始内容")
    private String content;

    @Schema(description = "澄清后的内容")
    private String clarifiedContent;

    @Schema(description = "状态")
    private String almondStatus;

    @Schema(description = "AI分类")
    private String aiClassification;

    @Schema(description = "分类置信度")
    private Double classificationConfidence;

    @Schema(description = "演化阶段")
    private Integer evolutionStage;

    @Schema(description = "AI分析次数")
    private Integer aiAnalysisCount;

    @Schema(description = "用户反馈")
    private String userFeedback;

    @Schema(description = "任务类型")
    private String taskType;

    @Schema(description = "层级")
    private String level;

    @Schema(description = "业务状态")
    private String status;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "标签")
    private java.util.List<String> tags;

    @Schema(description = "开始时间")
    private LocalDateTime startDate;

    @Schema(description = "结束时间")
    private LocalDateTime endDate;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
