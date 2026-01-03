package com.ravey.almond.service.profile;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 用户画像
 * 用于AI分类时提供用户上下文
 *
 * @author Ravey
 * @since 1.0.0
 */
@Data
public class UserProfile implements Serializable {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 杏仁总数
     */
    private Long totalCount;

    /**
     * 类型分布
     * key: memory/action/goal/decision/review
     * value: 数量
     */
    private Map<String, Long> typeDistribution;

    /**
     * 常用标签（最多10个）
     */
    private List<String> frequentTags;

    /**
     * 关注领域
     */
    private List<String> focusAreas;

    /**
     * 反馈统计
     * key: accept/modify/reject
     * value: 数量
     */
    private Map<String, Long> feedbackStats;

    /**
     * 获取类型数量
     */
    public Long getTypeCount(String type) {
        if (typeDistribution == null) {
            return 0L;
        }
        return typeDistribution.getOrDefault(type, 0L);
    }

    /**
     * 获取反馈数量
     */
    public Long getFeedbackCount(String feedback) {
        if (feedbackStats == null) {
            return 0L;
        }
        return feedbackStats.getOrDefault(feedback, 0L);
    }

    /**
     * 计算确认率
     */
    public double getAcceptRate() {
        if (feedbackStats == null) {
            return 0.0;
        }
        
        long accept = feedbackStats.getOrDefault("accept", 0L);
        long modify = feedbackStats.getOrDefault("modify", 0L);
        long reject = feedbackStats.getOrDefault("reject", 0L);
        
        long total = accept + modify + reject;
        
        if (total == 0) {
            return 0.0;
        }
        
        return (double) accept / total;
    }

    /**
     * 获取最常用的类型
     */
    public String getMostFrequentType() {
        if (typeDistribution == null || typeDistribution.isEmpty()) {
            return null;
        }
        
        return typeDistribution.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    /**
     * 计算类型占比
     */
    public double getTypePercentage(String type) {
        if (totalCount == null || totalCount == 0) {
            return 0.0;
        }
        
        Long count = getTypeCount(type);
        return (double) count / totalCount;
    }
}
