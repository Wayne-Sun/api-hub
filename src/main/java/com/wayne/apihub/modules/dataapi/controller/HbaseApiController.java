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
import com.wayne.apihub.modules.dataapi.conf.HbaseApiConf;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.factory.HbaseApiHandlerFactory;
import com.wayne.apihub.service.DataApiConfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wayne
 */
@Api(tags = {"Hbase API 管理"})
@Slf4j
@RestController
@RequestMapping("/v1/api/hbase")
public class HbaseApiController {
    private final DataApiConfService dataApiConfService;
    private final HbaseApiHandlerFactory hbaseApiHandlerFactory;

    @Autowired
    public HbaseApiController(DataApiConfService dataApiConfService, HbaseApiHandlerFactory hbaseApiHandlerFactory) {
        this.dataApiConfService = dataApiConfService;
        this.hbaseApiHandlerFactory = hbaseApiHandlerFactory;
    }

    @ApiOperation(value = "注册 Hbase API", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/registerApi")
    public BaseResponse registerApi(@RequestBody HbaseApiConf hbaseApiConf) {
        BaseResponse baseResponse;
        try {
            hbaseApiHandlerFactory.registerApi(hbaseApiConf);
            baseResponse = BaseResponse.ok();
            log.info("Register Hbase API Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Register Hbase API Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "分页获取 Hbase API 信息", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/listApi")
    public BaseResponse listApi(@RequestBody BasePageRequest basePageRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataApiConfService.listHbaseApiConfs(basePageRequest.getPageNum(), basePageRequest.getPageSize());
            log.info("List Hbase API Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List Hbase API Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "启用 Hbase API", response = BaseResponse.class, httpMethod = "GET", produces = "application/json")
    @GetMapping("/enableApi")
    public BaseResponse enableApi(@ApiParam(value = "API ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            hbaseApiHandlerFactory.enableApi(id);
            baseResponse = BaseResponse.ok();
            log.info("Enable Hbase API Succeeded.");
        } catch (DataApiException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Enable Hbase API Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Enable Hbase API Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "禁用 Hbase API", response = BaseResponse.class, httpMethod = "GET", produces = "application/json")
    @GetMapping("/disableApi")
    public BaseResponse disableApi(@ApiParam(value = "API ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            hbaseApiHandlerFactory.disableApi(id);
            baseResponse = BaseResponse.ok();
            log.info("Disable Hbase API Succeeded.");
        } catch (DataApiException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Disable Hbase API Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Disable Hbase API Failed. ", e);
        }
        return baseResponse;
    }
}
