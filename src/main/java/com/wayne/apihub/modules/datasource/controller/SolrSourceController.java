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
package com.wayne.apihub.modules.datasource.controller;

import com.wayne.apihub.model.BasePageRequest;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.datasource.conf.HbaseSourceConf;
import com.wayne.apihub.modules.datasource.conf.SolrSourceConf;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.modules.datasource.factory.SolrClientFactory;
import com.wayne.apihub.service.DataSourceConfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wayne
 */
@Tag(name = "SolrSourceController", description = "Solr Data Source Management")
@Slf4j
@RestController
@RequestMapping("/v1/source/solr")
public class SolrSourceController {
    private final DataSourceConfService dataSourceConfService;
    private final SolrClientFactory solrClientFactory;

    @Autowired
    public SolrSourceController(DataSourceConfService dataSourceConfService, SolrClientFactory solrClientFactory) {
        this.dataSourceConfService = dataSourceConfService;
        this.solrClientFactory = solrClientFactory;
    }

    @Operation(description = "Register Solr data source", method = "POST")
    @PostMapping("/insertSource")
    public BaseResponse insertSource(@ModelAttribute @RequestBody SolrSourceConf solrSourceInfo) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataSourceConfService.insertSolrSourceConf(solrSourceInfo);
            log.info("Insert Solr Source Info Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Insert Solr Source Info Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "List Solr data source", method = "POST")
    @PostMapping("/listSource")
    public BaseResponse listSource(@ModelAttribute @RequestBody BasePageRequest basePageRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataSourceConfService.listSolrSourceConfs(basePageRequest.getPageNum(), basePageRequest.getPageSize());
            log.info("List Solr Source Info Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List Solr Source Info Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Initialize Solr data source", method = "POST")
    @PostMapping("/initSource")
    public BaseResponse initSource(@ModelAttribute @RequestBody HbaseSourceConf hbaseSourceInfo) {
        BaseResponse baseResponse;
        try {
            solrClientFactory.initClient(hbaseSourceInfo);
            baseResponse = BaseResponse.ok();
            log.info("Initialize Solr Source Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Initialize Solr Source Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Enable Solr data source", method = "GET")
    @GetMapping("/enableSource")
    public BaseResponse enableSource(@Parameter(description = "data source ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            solrClientFactory.enableClient(id);
            baseResponse = BaseResponse.ok();
            log.info("Enable Solr Source Succeeded.");
        } catch (DataSourceException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Enable Solr Source Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Enable Solr Source Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Disable Solr data source", method = "GET")
    @GetMapping("/disableSource")
    public BaseResponse disableSource(@Parameter(description = "data source ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            solrClientFactory.disableClient(id);
            baseResponse = BaseResponse.ok();
            log.info("Disable Solr Source Succeeded.");
        } catch (DataSourceException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Disable Solr Source Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Disable Solr Source Failed. ", e);
        }
        return baseResponse;
    }
}
