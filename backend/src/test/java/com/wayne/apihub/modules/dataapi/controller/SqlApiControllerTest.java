/**
 * Copyright 2021 Wayne
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wayne.apihub.modules.dataapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayne.apihub.model.BasePageRequest;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.dataapi.conf.SqlApiConf;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.factory.SqlApiHandlerFactory;
import com.wayne.apihub.service.DataApiConfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SqlApiControllerTest {

    @Mock
    private DataApiConfService dataApiConfService;

    @Mock
    private SqlApiHandlerFactory sqlApiHandlerFactory;

    @InjectMocks
    private SqlApiController sqlApiController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sqlApiController).build();
    }

    @Test
    void registerApi_success_shouldReturn200() throws Exception {
        SqlApiConf apiConf = new SqlApiConf();
        apiConf.setName("test-sql-api");
        apiConf.setSql("SELECT * FROM test WHERE id = ?");
        apiConf.setPageTag(0);

        doNothing().when(sqlApiHandlerFactory).registerApi(any(SqlApiConf.class));

        mockMvc.perform(post("/v1/api/sql/registerApi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiConf)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void registerApi_exception_shouldReturn500() throws Exception {
        SqlApiConf apiConf = new SqlApiConf();
        apiConf.setName("test-sql-api");

        doThrow(new RuntimeException("Register failed"))
                .when(sqlApiHandlerFactory).registerApi(any(SqlApiConf.class));

        mockMvc.perform(post("/v1/api/sql/registerApi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(apiConf)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void listApi_success_shouldReturn200() throws Exception {
        BasePageRequest pageRequest = new BasePageRequest(1, 10);

        when(dataApiConfService.listSqlApiConfs(1, 10))
                .thenReturn(BaseResponse.ok("mock data"));

        mockMvc.perform(post("/v1/api/sql/listApi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void enableApi_success_shouldReturn200() throws Exception {
        doNothing().when(sqlApiHandlerFactory).enableApi(anyLong());

        mockMvc.perform(get("/v1/api/sql/enableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void enableApi_DataApiException_shouldReturn400() throws Exception {
        doThrow(DataApiException.alreadyEnabled())
                .when(sqlApiHandlerFactory).enableApi(anyLong());

        mockMvc.perform(get("/v1/api/sql/enableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void enableApi_genericException_shouldReturn500() throws Exception {
        doThrow(new RuntimeException("Unexpected error"))
                .when(sqlApiHandlerFactory).enableApi(anyLong());

        mockMvc.perform(get("/v1/api/sql/enableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void disableApi_success_shouldReturn200() throws Exception {
        doNothing().when(sqlApiHandlerFactory).disableApi(anyLong());

        mockMvc.perform(get("/v1/api/sql/disableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void disableApi_DataApiException_shouldReturn400() throws Exception {
        doThrow(DataApiException.alreadyDisabled())
                .when(sqlApiHandlerFactory).disableApi(anyLong());

        mockMvc.perform(get("/v1/api/sql/disableApi")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void listApiParam_success_shouldReturn200() throws Exception {
        List<String> paramList = List.of("param1", "param2");
        when(dataApiConfService.listApiParamByApiId(1L))
                .thenReturn(BaseResponse.ok(paramList));

        mockMvc.perform(get("/v1/api/sql/listApiParam")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteApiParamByApiId_success_shouldReturn200() throws Exception {
        doNothing().when(dataApiConfService).deleteApiParamByApiId(anyLong());

        mockMvc.perform(get("/v1/api/sql/deleteApiParamByApiId")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void deleteApiParamByApiId_exception_shouldReturn500() throws Exception {
        doThrow(new RuntimeException("Delete failed"))
                .when(dataApiConfService).deleteApiParamByApiId(anyLong());

        mockMvc.perform(get("/v1/api/sql/deleteApiParamByApiId")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
