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
package com.wayne.apihub.modules.dataapi.exception;

/**
 * @author Wayne
 */
public class DataApiException extends Exception {
    private static final String ALREADY_ENABLED = "API 已启用，无需操作";
    private static final String ALREADY_DISABLED = "API 已停用，无需操作";

    public DataApiException(String message) {
        super(message);
    }

    public static DataApiException alreadyEnabled() {
        return new DataApiException(ALREADY_ENABLED);
    }

    public static DataApiException alreadyDisabled() {
        return new DataApiException(ALREADY_DISABLED);
    }
}
