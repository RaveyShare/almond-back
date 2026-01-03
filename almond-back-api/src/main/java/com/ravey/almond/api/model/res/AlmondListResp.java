package com.ravey.almond.api.model.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 杏仁库列表响应
 *
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "杏仁库列表响应")
public class AlmondListResp implements Serializable {

    @Schema(description = "总数")
    private Long total;

    @Schema(description = "杏仁列表")
    private List<AlmondListItemResp> list;

    @Schema(description = "统计信息")
    private AlmondStatistics statistics;
}
