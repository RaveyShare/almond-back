package com.ravey.almond.api.model.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 杏仁库统计信息
 *
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "杏仁库统计信息")
public class AlmondStatistics implements Serializable {

    @Schema(description = "总数量")
    private Long totalCount;

    @Schema(description = "按状态统计: {raw: 5, understood: 8, ...}")
    private Map<String, Long> statusCount;

    @Schema(description = "按类型统计: {memory: 20, action: 30, ...}")
    private Map<String, Long> typeCount;

    @Schema(description = "星标数量")
    private Long starredCount;
}
