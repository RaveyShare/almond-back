package com.ravey.almond.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新杏仁请求
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "更新杏仁请求")
public class UpdateAlmondReq {
    
    @Schema(description = "标题", example = "购买牛奶")
    private String title;
    
    @Schema(description = "澄清后的内容", example = "明天去超市购买一箱牛奶")
    private String clarifiedContent;
    
    @Schema(description = "最终类型: memory/action/goal/idea", example = "action")
    private String finalType;
    
    @Schema(description = "是否星标: 1-是, 0-否", example = "1")
    private Integer starred;
}
