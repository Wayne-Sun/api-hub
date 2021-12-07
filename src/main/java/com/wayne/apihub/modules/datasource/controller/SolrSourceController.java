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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Wayne
 */
@Api(tags = {"Solr 数据源管理"})
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

    @ApiOperation(value = "创建 Solr 数据源", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/insertSource")
    public BaseResponse insertSource(@RequestBody SolrSourceConf solrSourceInfo) {
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

    @ApiOperation(value = "分页获取 Solr 数据源", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/listSource")
    public BaseResponse listSource(@RequestBody BasePageRequest basePageRequest) {
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

    @ApiOperation(value = "初始化 Solr 数据源", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/initSource")
    public BaseResponse initSource(@RequestBody HbaseSourceConf hbaseSourceInfo) {
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

    @ApiOperation(value = "启用 Solr 数据源", response = BaseResponse.class, httpMethod = "GET", produces = "application/json")
    @GetMapping("/enableSource")
    public BaseResponse enableSource(@ApiParam(value = "数据源 ID") @RequestParam("id") Long id) {
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

    @ApiOperation(value = "禁用 Solr 数据源", response = BaseResponse.class, httpMethod = "GET", produces = "application/json")
    @GetMapping("/disableSource")
    public BaseResponse disableSource(@ApiParam(value = "数据源 ID") @RequestParam("id") Long id) {
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
