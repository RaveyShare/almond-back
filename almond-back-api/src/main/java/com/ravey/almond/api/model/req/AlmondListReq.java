package com.ravey.almond.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 杏仁列表查询请求
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "杏仁列表查询请求")
public class AlmondListReq {
    
    @Schema(description = "状态筛选: processing/done/failed", example = "done")
    private String status;
    
    @Schema(description = "类型筛选: memory/action/goal/idea", example = "action")
    private String finalType;
    
    @Schema(description = "是否星标: 1-是, 0-否")
    private Integer starred;
    
    @Schema(description = "关键词搜索", example = "牛奶")
    private String keyword;
    
    @Schema(description = "排序字段: create_time/update_time", example = "create_time", defaultValue = "create_time")
    private String sortBy = "create_time";
    
    @Schema(description = "排序方向: asc/desc", example = "desc", defaultValue = "desc")
    private String sortOrder = "desc";
    
    @Schema(description = "页码，从1开始", example = "1", defaultValue = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "每页数量", example = "20", defaultValue = "20")
    private Integer pageSize = 20;
}
