package com.ravey.almond.service.profile;

import com.ravey.almond.service.dao.mapper.AlmondItemMapper;
import com.ravey.almond.service.dao.mapper.AlmondStateLogMapper;
import com.ravey.almond.service.dao.mapper.AlmondTagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户画像服务
 * 用于AI分类时的上下文学习
 *
 * @author Ravey
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final AlmondItemMapper almondItemMapper;
    private final AlmondTagMapper almondTagMapper;
    private final AlmondStateLogMapper almondStateLogMapper;

    /**
     * 构建用户画像
     */
    public UserProfile buildUserProfile(Long userId) {
        log.info("开始构建用户画像，userId: {}", userId);
        
        UserProfile profile = new UserProfile();
        profile.setUserId(userId);
        
        // 1. 统计杏仁总数
        Long totalCount = almondItemMapper.countByUserId(userId);
        profile.setTotalCount(totalCount);
        
        // 2. 统计各类型数量
        Map<String, Long> typeDistribution = buildTypeDistribution(userId);
        profile.setTypeDistribution(typeDistribution);
        
        // 3. 查询常用标签
        List<String> frequentTags = almondTagMapper.selectFrequentTagsByUserId(userId, 10);
        profile.setFrequentTags(frequentTags);
        
        // 4. 统计用户反馈
        Map<String, Long> feedbackStats = buildFeedbackStats(userId);
        profile.setFeedbackStats(feedbackStats);
        
        // 5. 分析关注领域
        List<String> focusAreas = analyzeFocusAreas(typeDistribution, frequentTags);
        profile.setFocusAreas(focusAreas);
        
        log.info("用户画像构建完成，userId: {}, totalCount: {}", userId, totalCount);
        
        return profile;
    }

    /**
     * 构建类型分布
     */
    private Map<String, Long> buildTypeDistribution(Long userId) {
        List<Map<String, Object>> typeCounts = almondItemMapper.countByFinalType(userId);
        Map<String, Long> distribution = new HashMap<>();

        if (typeCounts != null) {
            for (Map<String, Object> map : typeCounts) {
                String type = (String) map.get("type");
                Object countObj = map.get("count");
                if (type == null || countObj == null) {
                    continue;
                }
                distribution.put(type, ((Number) countObj).longValue());
            }
        }
        
        // 确保所有类型都有值
        distribution.putIfAbsent("memory", 0L);
        distribution.putIfAbsent("action", 0L);
        distribution.putIfAbsent("goal", 0L);
        distribution.putIfAbsent("decision", 0L);
        distribution.putIfAbsent("review", 0L);
        
        return distribution;
    }

    /**
     * 构建反馈统计
     */
    private Map<String, Long> buildFeedbackStats(Long userId) {
        Map<String, Long> stats = almondStateLogMapper.countFeedbackStats(userId);
        
        if (stats == null) {
            stats = new HashMap<>();
        }
        
        stats.putIfAbsent("accept", 0L);
        stats.putIfAbsent("modify", 0L);
        stats.putIfAbsent("reject", 0L);
        
        return stats;
    }

    /**
     * 分析关注领域
     * 根据类型分布和常用标签推断
     */
    private List<String> analyzeFocusAreas(Map<String, Long> typeDistribution, 
                                           List<String> frequentTags) {
        // 简单实现：基于最多的类型和标签
        // TODO: 未来可以用更复杂的算法
        
        List<String> areas = new java.util.ArrayList<>();
        
        // 根据类型推断
        long memoryCount = typeDistribution.getOrDefault("memory", 0L);
        long actionCount = typeDistribution.getOrDefault("action", 0L);
        long goalCount = typeDistribution.getOrDefault("goal", 0L);
        
        if (memoryCount > actionCount && memoryCount > goalCount) {
            areas.add("知识学习");
        }
        if (actionCount > memoryCount && actionCount > goalCount) {
            areas.add("任务执行");
        }
        if (goalCount > 5) {
            areas.add("目标管理");
        }
        
        // 根据标签推断（简单映射）
        for (String tag : frequentTags) {
            if (tag.contains("技术") || tag.contains("编程") || tag.contains("代码")) {
                if (!areas.contains("技术成长")) {
                    areas.add("技术成长");
                }
            }
            if (tag.contains("工作") || tag.contains("职场")) {
                if (!areas.contains("职业发展")) {
                    areas.add("职业发展");
                }
            }
            if (tag.contains("学习") || tag.contains("读书")) {
                if (!areas.contains("自我提升")) {
                    areas.add("自我提升");
                }
            }
        }
        
        return areas;
    }

    /**
     * 判断是否为首次用户
     */
    public boolean isFirstTimeUser(Long userId) {
        Long count = almondItemMapper.countByUserId(userId);
        return count < 5;
    }

    /**
     * 计算用户的AI确认率
     */
    public double calculateAcceptRate(Long userId) {
        Map<String, Long> feedbackStats = almondStateLogMapper.countFeedbackStats(userId);
        
        if (feedbackStats == null || feedbackStats.isEmpty()) {
            return 0.0;
        }
        
        long acceptCount = feedbackStats.getOrDefault("accept", 0L);
        long modifyCount = feedbackStats.getOrDefault("modify", 0L);
        long rejectCount = feedbackStats.getOrDefault("reject", 0L);
        
        long total = acceptCount + modifyCount + rejectCount;
        
        if (total == 0) {
            return 0.0;
        }
        
        return (double) acceptCount / total;
    }
}
