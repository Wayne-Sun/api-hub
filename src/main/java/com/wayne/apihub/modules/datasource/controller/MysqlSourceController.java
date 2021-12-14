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
import com.wayne.apihub.modules.datasource.conf.MysqlSourceConf;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.modules.datasource.factory.MysqlClientFactory;
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
@Tag(name = "MysqlSourceController", description = "Mysql Data Source Management")
@Slf4j
@RestController
@RequestMapping("/v1/source/mysql")
public class MysqlSourceController {
    private final DataSourceConfService dataSourceConfService;
    private final MysqlClientFactory mysqlClientFactory;

    @Autowired
    public MysqlSourceController(DataSourceConfService dataSourceConfService, MysqlClientFactory mysqlClientFactory) {
        this.dataSourceConfService = dataSourceConfService;
        this.mysqlClientFactory = mysqlClientFactory;
    }

    @Operation(description = "Register Mysql data source", method = "POST")
    @PostMapping("/insertSource")
    public BaseResponse insertSource(@ModelAttribute @RequestBody MysqlSourceConf mysqlSourceInfo) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataSourceConfService.insertMysqlSourceConf(mysqlSourceInfo);
            log.info("Insert Mysql Source Info Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Insert Mysql Source Info Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "List Mysql data source", method = "POST")
    @PostMapping("/listSource")
    public BaseResponse listSource(@ModelAttribute @RequestBody BasePageRequest basePageRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataSourceConfService.listMysqlSourceConfs(basePageRequest.getPageNum(), basePageRequest.getPageSize());
            log.info("List Mysql Source Info Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List Mysql Source Info Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Initialize Mysql data source", method = "POST")
    @PostMapping("/initSource")
    public BaseResponse initSource(@ModelAttribute @RequestBody HbaseSourceConf hbaseSourceInfo) {
        BaseResponse baseResponse;
        try {
            mysqlClientFactory.initClient(hbaseSourceInfo);
            baseResponse = BaseResponse.ok();
            log.info("Initialize Mysql Source Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Initialize Mysql Source Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Enable Mysql data sourcce", method = "GET")
    @GetMapping("/enableSource")
    public BaseResponse enableSource(@Parameter(description = "data source ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            mysqlClientFactory.enableClient(id);
            baseResponse = BaseResponse.ok();
            log.info("Enable Mysql Source Succeeded.");
        } catch (DataSourceException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Enable Mysql Source Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Enable Mysql Source Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Disable Mysql data source", method = "GET")
    @GetMapping("/disableSource")
    public BaseResponse disableSource(@Parameter(description = "data source ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            mysqlClientFactory.disableClient(id);
            baseResponse = BaseResponse.ok();
            log.info("Disable Mysql Source Succeeded.");
        } catch (DataSourceException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Disable Mysql Source Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Disable Mysql Source Failed. ", e);
        }
        return baseResponse;
    }
}
