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
import com.wayne.apihub.modules.dataapi.conf.MysqlApiConf;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.factory.MysqlApiHandlerFactory;
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
@Tag(name = "MysqlApiController", description = "Mysql API Management")
@Slf4j
@RestController
@RequestMapping("/v1/api/mysql")
public class MysqlApiController {
    private final DataApiConfService dataApiConfService;
    private final MysqlApiHandlerFactory mysqlApiHandlerFactory;

    @Autowired
    public MysqlApiController(DataApiConfService dataApiConfService, MysqlApiHandlerFactory mysqlApiHandlerFactory) {
        this.dataApiConfService = dataApiConfService;
        this.mysqlApiHandlerFactory = mysqlApiHandlerFactory;
    }

    @Operation(description = "Register Mysql API", method = "POST")
    @PostMapping("/registerApi")
    public BaseResponse registerApi(@ModelAttribute @RequestBody MysqlApiConf mysqlApiInfo) {
        BaseResponse baseResponse;
        try {
            mysqlApiHandlerFactory.registerApi(mysqlApiInfo);
            baseResponse = BaseResponse.ok();
            log.info("Register Mysql API Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Register Mysql API Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "List Mysql API Configuration", method = "POST")
    @PostMapping("/listApi")
    public BaseResponse listApi(@ModelAttribute @RequestBody BasePageRequest basePageRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataApiConfService.listMysqlApiConfs(basePageRequest.getPageNum(), basePageRequest.getPageSize());
            log.info("List Mysql API Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List Mysql API Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Enable Mysql API", method = "GET")
    @GetMapping("/enableApi")
    public BaseResponse enableApi(@Parameter(description = "API ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            mysqlApiHandlerFactory.enableApi(id);
            baseResponse = BaseResponse.ok();
            log.info("Enable Mysql API Succeeded.");
        } catch (DataApiException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Enable Mysql API Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Enable Mysql API Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Disable Hbase API", method = "GET")
    @GetMapping("/disableApi")
    public BaseResponse disableApi(@Parameter(description = "API ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            mysqlApiHandlerFactory.disableApi(id);
            baseResponse = BaseResponse.ok();
            log.info("Disable Mysql API Succeeded.");
        } catch (DataApiException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Disable Mysql API Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Disable Mysql API Failed. ", e);
        }
        return baseResponse;
    }
}
