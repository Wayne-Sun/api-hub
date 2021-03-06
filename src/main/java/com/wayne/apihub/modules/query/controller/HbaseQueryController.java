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
import com.wayne.apihub.modules.query.handler.HbaseQueryHandler;
import com.wayne.apihub.modules.query.request.HbaseQueryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author Wayne
 */
@Tag(name = "HbaseQueryController", description = "Hbase API Query Handler")
@Slf4j
@RestController
@RequestMapping("/v1/query")
public class HbaseQueryController {
    private final HbaseQueryHandler hbaseQueryHandler;

    @Autowired
    public HbaseQueryController(HbaseQueryHandler hbaseQueryHandler) {
        this.hbaseQueryHandler = hbaseQueryHandler;
    }

    @Operation(description = "Hbase API query", method = "POST")
    @PostMapping("/hbase")
    public BaseResponse query(@ModelAttribute @RequestBody HbaseQueryRequest hbaseQueryRequest) {
        BaseResponse baseResponse;
        try {
            baseResponse = hbaseQueryHandler.handle(hbaseQueryRequest);
            log.info("Hbase Request Handled.");
        } catch (QueryParamException e) {
            baseResponse = BaseResponse.bad(e.getMessage());
            log.info("Hbase Request Param Error: {}", e.getMessage());
        } catch (IOException e) {
            baseResponse = BaseResponse.error(e.getMessage());
            log.error("Hbase Request Error. ", e);
        }
        return baseResponse;
    }
}
