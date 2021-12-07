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
package com.wayne.apihub.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author Wayne
 */
@Slf4j
public class ResponseUtils {
    public static void responseJson(ServletResponse response, Object data) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try (OutputStream out = response.getOutputStream()) {
            StreamUtils.copy(JsonUtils.toString(data), StandardCharsets.UTF_8, out);
        } catch (Exception e) {
            log.error("Response输出Json异常：", e);
        }
    }
}
