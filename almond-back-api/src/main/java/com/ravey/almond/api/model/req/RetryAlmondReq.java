package com.ravey.almond.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 重试杏仁AI处理请求
 * 
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "重试杏仁AI处理请求")
public class RetryAlmondReq {
    
    @Schema(description = "修改后的内容（可选，不传则使用原内容重试）", example = "明天早上去超市买牛奶")
    private String content;
}
