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
package com.wayne.apihub.modules.datasource.exception;

/**
 * @author Wayne
 */
public class DataSourceException extends Exception {
    private static final String ALREADY_ENABLED = "数据源已启用，无需操作";
    private static final String ALREADY_DISABLED = "数据源已停用，无需操作";
    private static final String API_NOT_DISABLED = "数据源存在API未禁用，请检查";

    public DataSourceException(String message) {
        super(message);
    }

    public static DataSourceException alreadyEnabled() {
        return new DataSourceException(ALREADY_ENABLED);
    }

    public static DataSourceException alreadyDisabled() {
        return new DataSourceException(ALREADY_DISABLED);
    }

    public static DataSourceException apiNotDisabled() {
        return new DataSourceException(API_NOT_DISABLED);
    }
}
