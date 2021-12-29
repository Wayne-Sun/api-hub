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

import com.wayne.apihub.model.BasePageRequest;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.dataapi.conf.SqlApiConf;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.factory.SqlApiHandlerFactory;
import com.wayne.apihub.service.DataApiConfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wayne
 */
@Tag(name = "SqlApiController", description = "sql API Management")
@Slf4j
@RestController
@RequestMapping("/v1/api/sql")
public class SqlApiController {
    private final DataApiConfService dataApiConfService;
    private final SqlApiHandlerFactory sqlApiHandlerFactory;

    @Autowired
    public SqlApiController(DataApiConfService dataApiConfService, SqlApiHandlerFactory sqlApiHandlerFactory) {
        this.dataApiConfService = dataApiConfService;
        this.sqlApiHandlerFactory = sqlApiHandlerFactory;
    }

    @Operation(description = "Register sql API", method = "POST")
    @PostMapping("/registerApi")
    public BaseResponse registerApi(@ModelAttribute @RequestBody SqlApiConf sqlApiConf) {
        BaseResponse baseResponse;
        try {
            sqlApiHandlerFactory.registerApi(sqlApiConf);
            baseResponse = BaseResponse.ok();
            log.info("Register sql API Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Register sql API Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "List sql API Configuration", method = "POST")
    @PostMapping("/listApi")
    public BaseResponse listApi(@ModelAttribute @RequestBody BasePageRequest basePageRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataApiConfService.listSqlApiConfs(basePageRequest.getPageNum(), basePageRequest.getPageSize());
            log.info("List sql API Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List sql API Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "List sql API Parameter", method = "GET")
    @GetMapping("/listApiParam")
    public BaseResponse listApiParam(@RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataApiConfService.listApiParamByApiId(id);
            log.info("List sql API parameter Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List sql API parameter Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "List sql API parameter", method = "GET")
    @GetMapping("/deleteApiParamByApiId")
    public BaseResponse deleteApiParamByApiId(@RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            dataApiConfService.deleteApiParamByApiId(id);
            baseResponse = BaseResponse.ok();
            log.info("List sql API parameter Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List sql API parameter Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Enable sql API", method = "GET")
    @GetMapping("/enableApi")
    public BaseResponse enableApi(@Parameter(description = "API ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            sqlApiHandlerFactory.enableApi(id);
            baseResponse = BaseResponse.ok();
            log.info("Enable sql API Succeeded.");
        } catch (DataApiException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Enable sql API Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Enable sql API Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Disable sql API", method = "GET")
    @GetMapping("/disableApi")
    public BaseResponse disableApi(@Parameter(description = "API ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            sqlApiHandlerFactory.disableApi(id);
            baseResponse = BaseResponse.ok();
            log.info("Disable sql API Succeeded.");
        } catch (DataApiException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Disable sql API Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Disable sql API Failed. ", e);
        }
        return baseResponse;
    }
}
