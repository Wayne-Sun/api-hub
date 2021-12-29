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
import com.wayne.apihub.modules.datasource.conf.SqlSourceConf;
import com.wayne.apihub.modules.datasource.exception.DataSourceException;
import com.wayne.apihub.modules.datasource.factory.SqlClientFactory;
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
@Tag(name = "SqlSourceController", description = "Sql Data Source Management")
@Slf4j
@RestController
@RequestMapping("/v1/source/sql")
public class SqlSourceController {
    private final DataSourceConfService dataSourceConfService;
    private final SqlClientFactory sqlClientFactory;

    @Autowired
    public SqlSourceController(DataSourceConfService dataSourceConfService, SqlClientFactory sqlClientFactory) {
        this.dataSourceConfService = dataSourceConfService;
        this.sqlClientFactory = sqlClientFactory;
    }

    @Operation(description = "Register sql data source", method = "POST")
    @PostMapping("/insertSource")
    public BaseResponse insertSource(@ModelAttribute @RequestBody SqlSourceConf sqlSourceConf) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataSourceConfService.insertSqlSourceConf(sqlSourceConf);
            log.info("Insert sql Source Info Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Insert sql Source Info Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "List sql data source", method = "POST")
    @PostMapping("/listSource")
    public BaseResponse listSource(@ModelAttribute @RequestBody BasePageRequest basePageRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = dataSourceConfService.listSqlSourceConfs(basePageRequest.getPageNum(), basePageRequest.getPageSize());
            log.info("List sql Source Info Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("List sql Source Info Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Initialize Mysql data source", method = "POST")
    @PostMapping("/initSource")
    public BaseResponse initSource(@ModelAttribute @RequestBody SqlSourceConf sqlSourceConf) {
        BaseResponse baseResponse;
        try {
            sqlClientFactory.initClient(sqlSourceConf);
            baseResponse = BaseResponse.ok();
            log.info("Initialize sql Source Succeeded.");
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Initialize sql Source Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Enable sql data sourcce", method = "GET")
    @GetMapping("/enableSource")
    public BaseResponse enableSource(@Parameter(description = "data source ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            sqlClientFactory.enableClient(id);
            baseResponse = BaseResponse.ok();
            log.info("Enable sql Source Succeeded.");
        } catch (DataSourceException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Enable sql Source Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Enable sql Source Failed. ", e);
        }
        return baseResponse;
    }

    @Operation(description = "Disable sql data source", method = "GET")
    @GetMapping("/disableSource")
    public BaseResponse disableSource(@Parameter(description = "data source ID") @RequestParam("id") Long id) {
        BaseResponse baseResponse;
        try {
            sqlClientFactory.disableClient(id);
            baseResponse = BaseResponse.ok();
            log.info("Disable sql Source Succeeded.");
        } catch (DataSourceException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Disable sql Source Failed: {}", e.getMessage());
        } catch (Exception e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Disable sql Source Failed. ", e);
        }
        return baseResponse;
    }
}
