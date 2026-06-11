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
package com.wayne.apihub.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Wayne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(name = "BaseResponse", description = "Base class for response entity")
public class BaseResponse {
    public static final Integer STATUS_OK = 200;
    public static final Integer STATUS_BAD = 400;
    public static final Integer STATUS_NOT_LOGIN = 401;
    public static final Integer STATUS_REJECT = 403;
    public static final Integer STATUS_ERROR = 500;

    private static final String LOGIN_SUCCESS_MESSAGE = "Login complete";
    private static final String LOGIN_FAILED_MESSAGE = "Login fail";
    private static final String LOGOUT_SUCCESS_MESSAGE = "Logout complete";

    private static final String OK_MESSAGE = "SUCCESS";
    private static final String BAD_MESSAGE = "Wrong Parameter";
    private static final String NOT_LOGIN_MESSAGE = "User not login";
    private static final String REJECT_MESSAGE = "Access denied";
    private static final String ERROR_MESSAGE = "ERROR";

    private static final BaseResponse COMMON_SUCCESS_RESPONSE = new BaseResponse(STATUS_OK, OK_MESSAGE, null);

    @Schema(description = "Response code")
    private Integer code;
    @Schema(description = "Response message")
    private String message;
    @Schema(description = "Response data")
    private Object data;

    public static BaseResponse ok() {
        return COMMON_SUCCESS_RESPONSE;
    }

    public static BaseResponse ok(Object data) {
        return new BaseResponse(STATUS_OK, OK_MESSAGE, data);
    }

    public static BaseResponse loginSuccess(Object data) {
        return new BaseResponse(STATUS_OK, LOGIN_SUCCESS_MESSAGE, data);
    }

    public static BaseResponse loginFailure(Object data) {
        return new BaseResponse(STATUS_ERROR, LOGIN_FAILED_MESSAGE, data);
    }

    public static BaseResponse logoutSuccess() {
        return new BaseResponse(STATUS_OK, LOGOUT_SUCCESS_MESSAGE, null);
    }

    public static BaseResponse notLogin(Object data) {
        return new BaseResponse(STATUS_NOT_LOGIN, NOT_LOGIN_MESSAGE, data);
    }

    public static BaseResponse reject(Object data) {
        return new BaseResponse(STATUS_REJECT, REJECT_MESSAGE, data);
    }

    public static BaseResponse bad(Object data) {
        return new BaseResponse(STATUS_BAD, BAD_MESSAGE, data);
    }

    public static BaseResponse error(Object data) {
        return new BaseResponse(STATUS_ERROR, ERROR_MESSAGE, data);
    }
}
