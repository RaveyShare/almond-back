package com.ravey.almond.api.model.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 杏仁列表响应
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "杏仁列表响应")
public class AlmondListResp {
    
    @Schema(description = "数据列表")
    private List<AlmondResp> list;
    
    @Schema(description = "总数")
    private Long total;
    
    @Schema(description = "统计信息")
    private Statistics statistics;
    
    @Data
    @Schema(description = "统计信息")
    public static class Statistics {
        
        @Schema(description = "总数")
        private Long totalCount;
        
        @Schema(description = "处理中数量")
        private Long processingCount;
        
        @Schema(description = "已完成数量")
        private Long doneCount;
        
        @Schema(description = "失败数量")
        private Long failedCount;
        
        @Schema(description = "星标数量")
        private Long starredCount;
        
        @Schema(description = "按类型统计")
        private Map<String, Long> typeCount;
    }
}
