package com.ravey.almond.api.model.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 杏仁列表项响应
 *
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "杏仁列表项响应")
public class AlmondListItemResp implements Serializable {

    @Schema(description = "杏仁ID")
    private Long id;

    @Schema(description = "父杏仁ID")
    private Long parentId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "原始内容")
    private String content;

    @Schema(description = "澄清后的内容")
    private String clarifiedContent;

    @Schema(description = "杏仁状态: raw/understood/evolving/converged/archived")
    private String almondStatus;

    @Schema(description = "终态类型: memory/action/goal/decision/review")
    private String finalType;

    @Schema(description = "成熟度评分(0-100)")
    private Integer maturityScore;

    @Schema(description = "用户反馈: accept/modify/reject")
    private String userFeedback;

    @Schema(description = "是否星标: 0-否, 1-是")
    private Integer starred;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "标签列表")
    private List<TagInfo> tags;

    @Schema(description = "行动执行信息(仅action类型)")
    private ActionInfo actionInfo;

    @Schema(description = "最新状态日志")
    private StateLogInfo latestStateLog;

    @Schema(description = "最新AI分析")
    private AiAnalysisInfo latestAiAnalysis;

    /**
     * 标签信息
     */
    @Data
    @Schema(description = "标签信息")
    public static class TagInfo implements Serializable {
        @Schema(description = "标签ID")
        private Long id;

        @Schema(description = "标签名称")
        private String name;

        @Schema(description = "标签类型")
        private String tagType;
    }

    /**
     * 行动执行信息
     */
    @Data
    @Schema(description = "行动执行信息")
    public static class ActionInfo implements Serializable {
        @Schema(description = "实际开始时间")
        private LocalDateTime actualStart;

        @Schema(description = "实际结束时间")
        private LocalDateTime actualEnd;
    }

    /**
     * 状态日志信息
     */
    @Data
    @Schema(description = "状态日志信息")
    public static class StateLogInfo implements Serializable {
        @Schema(description = "原状态")
        private String fromStatus;

        @Schema(description = "新状态")
        private String toStatus;

        @Schema(description = "触发类型: ai/user/system/time")
        private String triggerType;

        @Schema(description = "描述")
        private String description;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    /**
     * AI分析信息
     */
    @Data
    @Schema(description = "AI分析信息")
    public static class AiAnalysisInfo implements Serializable {
        @Schema(description = "分析类型")
        private String analysisType;

        @Schema(description = "置信度")
        private Double confidence;

        @Schema(description = "推理原因")
        private String reasoning;
    }
}
