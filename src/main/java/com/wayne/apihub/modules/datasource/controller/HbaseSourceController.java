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
package com.wayne.apihub.modules.datasource.controller;

import com.wayne.apihub.model.BasePageRequest;
import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.modules.datasource.factory.HbaseClientFactory;
import com.wayne.apihub.modules.datasource.info.HbaseSourceConf;
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
@Api(tags = {"Hbase 数据源管理"})
@Slf4j
@RestController
@RequestMapping("/v1/source/hbase")
public class HbaseSourceController {
    private final DataSourceConfService dataSourceConfService;
    private final HbaseClientFactory hbaseClientFactory;

    @Autowired
    public HbaseSourceController(DataSourceConfService dataSourceConfService, HbaseClientFactory hbaseClientFactory) {
        this.dataSourceConfService = dataSourceConfService;
        this.hbaseClientFactory = hbaseClientFactory;
    }

    @ApiOperation(value = "创建 Hbase 数据源", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/insertSource")
    public BaseResponse insertSource(@RequestBody HbaseSourceConf hbaseSourceInfo) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataSourceConfService.insertHbaseSourceConf(hbaseSourceInfo);
            log.info("Insert Hbase Source Info Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Insert Hbase Source Info Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "分页获取 Hbase 数据源", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/listSource")
    public BaseResponse listSource(@RequestBody BasePageRequest basePageRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataSourceConfService.listHbaseSourceConfs(basePageRequest.getPageNum(), basePageRequest.getPageSize());
            log.info("List Hbase Source Info Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List Hbase Source Info Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "初始化 Hbase 数据源", response = BaseResponse.class, httpMethod = "POST", consumes = "application/json", produces = "application/json")
    @PostMapping("/initSource")
    public BaseResponse initSource(@RequestBody HbaseSourceConf hbaseSourceInfo) {
        BaseResponse baseResponse;
        try {
            hbaseClientFactory.initClient(hbaseSourceInfo);
            baseResponse = BaseResponse.ok();
            log.info("Initialize Hbase Source Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Initialize Hbase Source Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "启用 Hbase 数据源", response = BaseResponse.class, httpMethod = "GET", produces = "application/json")
    @GetMapping("/enableSource")
    public BaseResponse enableSource(@ApiParam(value = "数据源 ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            hbaseClientFactory.enableClient(id);
            baseResponse = BaseResponse.ok();
            log.info("Enable Hbase Source Succeeded.");
        } catch (DataSourceException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Enable Hbase Source Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Enable Hbase Source Failed. ", e);
        }
        return baseResponse;
    }

    @ApiOperation(value = "禁用 Hbase 数据源", response = BaseResponse.class, httpMethod = "GET", produces = "application/json")
    @GetMapping("/disableSource")
    public BaseResponse disableSource(@ApiParam(value = "数据源 ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            hbaseClientFactory.disableClient(id);
            baseResponse = BaseResponse.ok();
            log.info("Disable Hbase Source Succeeded.");
        } catch (DataSourceException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Disable Hbase Source Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Disable Hbase Source Failed. ", e);
        }
        return baseResponse;
    }
}
