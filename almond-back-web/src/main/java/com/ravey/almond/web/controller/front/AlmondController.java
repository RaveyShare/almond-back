package com.ravey.almond.web.controller.front;

import com.ravey.almond.api.model.req.AlmondListReq;
import com.ravey.almond.api.model.req.CreateAlmondReq;
import com.ravey.almond.api.model.res.AlmondDetailResp;
import com.ravey.almond.api.model.res.AlmondItemResp;
import com.ravey.almond.api.model.res.AlmondListResp;
import com.ravey.almond.api.service.AlmondService;
import com.ravey.common.service.web.result.HttpResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 杏仁前端接口
 *
 * @author ravey
 * @since 1.0.0
 */
@RestController
@RequestMapping("/front/almonds")
@RequiredArgsConstructor
@Tag(name = "杏仁管理")
public class AlmondController {

    private final AlmondService almondService;

    @PostMapping("/create")
    @Operation(summary = "创建杏仁")
    public HttpResult<AlmondItemResp> create(@RequestBody @Validated CreateAlmondReq req) {
        return HttpResult.success(almondService.createAlmond(req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取杏仁简单详情")
    public HttpResult<AlmondItemResp> get(@PathVariable Long id) {
        return HttpResult.success(almondService.getAlmond(id));
    }

    @GetMapping("/{id}/detail")
    @Operation(summary = "获取杏仁完整详情")
    public HttpResult<AlmondDetailResp> getDetail(@PathVariable Long id) {
        return HttpResult.success(almondService.getAlmondDetail(id));
    }

    @PostMapping("/list")
    @Operation(summary = "查询杏仁库列表")
    public HttpResult<AlmondListResp> list(@RequestBody AlmondListReq req) {
        return HttpResult.success(almondService.listAlmonds(req));
    }
}
