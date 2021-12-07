/**
 * Copyright 2021 Wayne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wayne.apihub.modules.dataapi.controller;

import com.wayne.apihub.model.BasePageRequest;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.factory.SolrApiHandlerFactory;
import com.wayne.apihub.modules.dataapi.info.SolrApiConf;
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
@Api(tags = {"Solr API 管理"})
@Slf4j
@RestController
@RequestMapping("/v1/api/solr")
public class SolrApiController {
    private final DataApiConfService dataApiConfService;
    private final SolrApiHandlerFactory solrApiHandlerFactory;

    @Autowired
    public SolrApiController(DataApiConfService dataApiConfService, SolrApiHandlerFactory solrApiHandlerFactory) {
        this.dataApiConfService = dataApiConfService;
        this.solrApiHandlerFactory = solrApiHandlerFactory;
    }

    @ApiOperation(value = "注册 Solr API", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/registerApi")
    public BaseResponse registerApi(@RequestBody SolrApiConf solrApiInfo) {
        BaseResponse baseResponse;
        try {
            solrApiHandlerFactory.registerApi(solrApiInfo);
            baseResponse = BaseResponse.ok();
            log.info("Register Solr API Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Register Solr API Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "分页获取 Solr API 信息", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/listApi")
    public BaseResponse listApi(@RequestBody BasePageRequest basePageRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataApiConfService.listSolrApiConfs(basePageRequest.getPageNum(), basePageRequest.getPageSize());
            log.info("List Solr API Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List Solr API Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "启用 Solr API", response = BaseResponse.class, httpMethod = "GET", produces = "application/json")
    @GetMapping("/enableApi")
    public BaseResponse enableApi(@ApiParam(value = "API ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            solrApiHandlerFactory.enableApi(id);
            baseResponse = BaseResponse.ok();
            log.info("Enable Solr API Succeeded.");
        } catch (DataApiException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Enable Solr API Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Enable Solr API Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "禁用 Solr API", response = BaseResponse.class, httpMethod = "GET", produces = "application/json")
    @GetMapping("/disableApi")
    public BaseResponse disableApi(@ApiParam(value = "API ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            solrApiHandlerFactory.disableApi(id);
            baseResponse = BaseResponse.ok();
            log.info("Disable Solr API Succeeded.");
        } catch (DataApiException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Disable Solr API Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Disable Solr API Failed. ", e);
        }
        return baseResponse;
    }
}
