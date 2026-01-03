package com.ravey.almond.api.model.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 杏仁详情响应
 *
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "杏仁详情响应")
public class AlmondDetailResp implements Serializable {

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

    @Schema(description = "演化阶段")
    private Integer evolutionStage;

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

    @Schema(description = "状态演化历程（按时间倒序）")
    private List<StateLogInfo> stateLogs;

    @Schema(description = "AI分析历史（按时间倒序）")
    private List<AiSnapshotInfo> aiSnapshots;

    @Schema(description = "行动执行信息（仅action类型）")
    private ActionExecutionInfo actionExecution;

    @Schema(description = "记忆辅助信息（仅memory类型）")
    private MemoryAidsInfo memoryAids;

    @Schema(description = "复习计划（仅memory类型）")
    private List<ReviewScheduleInfo> reviewSchedules;

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
     * 状态日志信息
     */
    @Data
    @Schema(description = "状态日志信息")
    public static class StateLogInfo implements Serializable {
        @Schema(description = "日志ID")
        private Long id;

        @Schema(description = "原状态")
        private String fromStatus;

        @Schema(description = "新状态")
        private String toStatus;

        @Schema(description = "触发类型: ai/user/system/time")
        private String triggerType;

        @Schema(description = "触发事件")
        private String triggerEvent;

        @Schema(description = "上下文数据")
        private String contextData;

        @Schema(description = "描述")
        private String description;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    /**
     * AI分析快照信息
     */
    @Data
    @Schema(description = "AI分析快照信息")
    public static class AiSnapshotInfo implements Serializable {
        @Schema(description = "快照ID")
        private Long id;

        @Schema(description = "分析类型")
        private String analysisType;

        @Schema(description = "AI模型")
        private String aiModel;

        @Schema(description = "提示词内容")
        private String promptContent;

        @Schema(description = "分析结果")
        private String analysisResult;

        @Schema(description = "状态")
        private String status;

        @Schema(description = "耗时(ms)")
        private Integer costTime;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    /**
     * 行动执行信息
     */
    @Data
    @Schema(description = "行动执行信息")
    public static class ActionExecutionInfo implements Serializable {
        @Schema(description = "实际开始时间")
        private LocalDateTime actualStart;

        @Schema(description = "实际结束时间")
        private LocalDateTime actualEnd;
    }

    /**
     * 记忆辅助信息
     */
    @Data
    @Schema(description = "记忆辅助信息")
    public static class MemoryAidsInfo implements Serializable {
        @Schema(description = "思维导图数据")
        private String mindMapData;

        @Schema(description = "助记符数据")
        private String mnemonicsData;

        @Schema(description = "感官数据")
        private String sensoryData;
    }

    /**
     * 复习计划信息
     */
    @Data
    @Schema(description = "复习计划信息")
    public static class ReviewScheduleInfo implements Serializable {
        @Schema(description = "计划ID")
        private Long id;

        @Schema(description = "复习日期")
        private LocalDateTime reviewDate;

        @Schema(description = "是否完成: 0-否, 1-是")
        private Integer completed;

        @Schema(description = "间隔天数")
        private Integer intervalDays;

        @Schema(description = "重复次数")
        private Integer repetition;

        @Schema(description = "难易度")
        private Double easiness;
    }
}
