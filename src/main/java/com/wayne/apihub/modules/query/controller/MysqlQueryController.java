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
package com.wayne.apihub.modules.query.controller;

import com.wayne.apihub.model.BaseResponse;
import com.wayne.apihub.modules.query.exception.QueryParamException;
import com.wayne.apihub.modules.query.handler.MysqlQueryHandler;
import com.wayne.apihub.modules.query.request.MysqlQueryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

/**
 * @author Wayne
 */
@Tag(name = "MysqlQueryController", description = "Mysql API Query Handler")
@Slf4j
@RestController
@RequestMapping("/v1/query")
public class MysqlQueryController {
    private final MysqlQueryHandler mysqlQueryHandler;

    @Autowired
    public MysqlQueryController(MysqlQueryHandler mysqlQueryHandler) {
        this.mysqlQueryHandler = mysqlQueryHandler;
    }

    @Operation(description = "Mysql API query", method = "POST")
    @PostMapping("/mysql")
    public BaseResponse query(@ModelAttribute @RequestBody MysqlQueryRequest mysqlQueryRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = mysqlQueryHandler.handle(mysqlQueryRequest);
            log.info("Mysql Request Handled.");
        } catch (QueryParamException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Mysql Request Param Error: {}", e.getMessage());
        } catch (SQLException e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Mysql Request Error. ", e);
        }
        return baseResponse;
    }
}
