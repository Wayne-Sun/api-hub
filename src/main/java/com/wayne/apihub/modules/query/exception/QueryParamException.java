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
package com.wayne.apihub.modules.query.exception;

/**
 * @author Wayne
 */
public class QueryParamException extends Exception {
    private static final String PARAM_NUM_MESSAGE = "Wrong parameter number";
    private static final String API_NOT_EXISTS = "API not exists or disabled";
    private static final String SRC_NOT_EXISTS = "Data source not exists or disabled";

    public QueryParamException(String message) {
        super(message);
    }

    public QueryParamException() {
        super(PARAM_NUM_MESSAGE);
    }

    public static QueryParamException apiNotExists() {
        return new QueryParamException(API_NOT_EXISTS);
    }

    public static QueryParamException srcNotExists() {
        return new QueryParamException(SRC_NOT_EXISTS);
    }
}
