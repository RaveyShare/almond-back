package com.ravey.almond.api.model.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 杏仁响应（通用）
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "杏仁响应")
public class AlmondResp {
    
    @Schema(description = "杏仁ID")
    private Long id;
    
    @Schema(description = "用户原始输入")
    private String content;
    
    @Schema(description = "AI生成的标题")
    private String title;
    
    @Schema(description = "AI澄清后的内容")
    private String clarifiedContent;
    
    @Schema(description = "状态: processing/done/failed")
    private String status;
    
    @Schema(description = "最终类型: memory/action/goal/idea")
    private String finalType;
    
    @Schema(description = "AI置信度(0-100)")
    private Integer confidence;
    
    @Schema(description = "是否星标")
    private Integer starred;
    
    @Schema(description = "错误信息（仅failed状态）")
    private String errorMessage;
    
    @Schema(description = "标签列表")
    private List<String> tags;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
