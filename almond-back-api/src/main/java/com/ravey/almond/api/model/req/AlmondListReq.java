package com.ravey.almond.api.model.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 杏仁库列表查询请求
 *
 * @author Ravey
 * @since 1.0.0
 */
@Data
@Schema(description = "杏仁库列表查询请求")
public class AlmondListReq implements Serializable {

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页数量", example = "20")
    private Integer pageSize = 20;

    @Schema(description = "杏仁状态: raw/understood/evolving/converged/archived")
    private String almondStatus;

    @Schema(description = "终态类型: memory/action/goal/decision/review")
    private String finalType;

    @Schema(description = "是否星标: 0-否, 1-是")
    private Integer starred;

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "排序字段: update_time/create_time/maturity_score", example = "update_time")
    private String sortBy = "update_time";

    @Schema(description = "排序方式: asc/desc", example = "desc")
    private String sortOrder = "desc";
}
