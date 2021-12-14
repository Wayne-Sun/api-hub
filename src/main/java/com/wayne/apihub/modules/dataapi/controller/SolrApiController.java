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
import com.wayne.apihub.modules.dataapi.conf.SolrApiConf;
import com.wayne.apihub.modules.dataapi.exception.DataApiException;
import com.wayne.apihub.modules.dataapi.factory.SolrApiHandlerFactory;
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
@Tag(name = "SolrApiController", description = "Solr API Management")
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

    @Operation(description = "Register Solr API", method = "POST")
    @PostMapping("/registerApi")
    public BaseResponse registerApi(@ModelAttribute @RequestBody SolrApiConf solrApiInfo) {
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

    @Operation(description = "List Solr API Configuration", method = "POST")
    @PostMapping("/listApi")
    public BaseResponse listApi(@ModelAttribute @RequestBody BasePageRequest basePageRequest) {
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

    @Operation(description = "Enable Solr API", method = "GET")
    @GetMapping("/enableApi")
    public BaseResponse enableApi(@Parameter(description = "API ID") @RequestParam("id") Long id) {
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

    @Operation(description = "Disable Solr API", method = "GET")
    @GetMapping("/disableApi")
    public BaseResponse disableApi(@Parameter(description = "API ID") @RequestParam("id") Long id) {
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
