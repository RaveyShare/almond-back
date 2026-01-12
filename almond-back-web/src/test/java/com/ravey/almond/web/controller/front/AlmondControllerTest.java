package com.ravey.almond.web.controller.front;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ravey.almond.api.model.req.AlmondListReq;
import com.ravey.almond.api.model.req.CreateAlmondReq;
import com.ravey.almond.api.model.res.AlmondDetailResp;
import com.ravey.almond.api.model.res.AlmondItemResp;
import com.ravey.almond.api.model.res.AlmondListResp;
import com.ravey.almond.api.service.AlmondService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AlmondController 接口层单元测试
 */
@ExtendWith(MockitoExtension.class)
class AlmondControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AlmondService almondService;

    @InjectMocks
    private AlmondController almondController;

    /**
     * 初始化 MockMvc，基于 AlmondController 构建独立环境
     */
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(almondController).build();
    }

    /**
     * 创建接口：校验请求并委托给服务层，返回创建结果
     */
    @Test
    void create_shouldValidateAndDelegateToService() throws Exception {
        CreateAlmondReq req = new CreateAlmondReq();
        req.setContent("controller create");

        AlmondItemResp resp = new AlmondItemResp();
        resp.setId(10L);
        resp.setContent("controller create");

        Mockito.when(almondService.createAlmond(any(CreateAlmondReq.class))).thenReturn(resp);

        mockMvc.perform(post("/front/almonds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(10)))
                .andExpect(jsonPath("$.data.content", is("controller create")));
    }

    /**
     * 创建接口：当内容为空时返回 400 错误
     */
    @Test
    void create_shouldReturnBadRequestWhenContentBlank() throws Exception {
        CreateAlmondReq req = new CreateAlmondReq();
        req.setContent(" ");

        mockMvc.perform(post("/front/almonds/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    /**
     * 简单查询接口：返回 AlmondItem 简要信息
     */
    @Test
    void get_shouldReturnItemFromService() throws Exception {
        AlmondItemResp resp = new AlmondItemResp();
        resp.setId(20L);
        resp.setContent("simple detail");

        Mockito.when(almondService.getAlmond(20L)).thenReturn(resp);

        mockMvc.perform(get("/front/almonds/20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(20)))
                .andExpect(jsonPath("$.data.content", is("simple detail")));
    }

    /**
     * 详情查询接口：返回 Almond 详情信息
     */
    @Test
    void getDetail_shouldReturnDetailFromService() throws Exception {
        AlmondDetailResp detailResp = new AlmondDetailResp();
        detailResp.setId(30L);

        Mockito.when(almondService.getAlmondDetail(30L)).thenReturn(detailResp);

        mockMvc.perform(get("/front/almonds/30/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(30)));
    }

    /**
     * 列表查询接口：委托服务层按条件分页查询 Almond 列表
     */
    @Test
    void list_shouldDelegateToService() throws Exception {
        AlmondListResp listResp = new AlmondListResp();
        listResp.setTotal(1L);
        listResp.setList(Collections.emptyList());

        Mockito.when(almondService.listAlmonds(any(AlmondListReq.class))).thenReturn(listResp);

        AlmondListReq req = new AlmondListReq();
        req.setPageNum(1);
        req.setPageSize(10);

        mockMvc.perform(post("/front/almonds/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.total", is(1)));
    }
}
