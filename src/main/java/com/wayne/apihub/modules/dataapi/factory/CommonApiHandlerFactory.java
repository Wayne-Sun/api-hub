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
package com.wayne.apihub.modules.dataapi.factory;

import com.wayne.apihub.modules.dataapi.handler.CommonApiHandler;
import com.wayne.apihub.modules.dataapi.info.BaseApiConf;

/**
 * @author Wayne
 */
public interface CommonApiHandlerFactory {
    /**
     * initialize an API register
     *
     * @throws Exception exception
     */
    void init() throws Exception;

    /**
     * register an API
     *
     * @param baseApiConf api info
     */
    void registerApi(BaseApiConf baseApiConf);

    /**
     * initialize an API
     *
     * @param baseApiConf api info
     */
    void initApi(BaseApiConf baseApiConf);

    /**
     * enable an API
     *
     * @param id id of the API
     * @throws Exception exception
     */
    void enableApi(Long id) throws Exception;

    /**
     * disable an API
     *
     * @param id id of the API
     * @throws Exception exception
     */
    void disableApi(Long id) throws Exception;

    /**
     * get API handler by id
     *
     * @param id id of the API
     * @return CommonApiHandler
     */
    CommonApiHandler getApiHandler(Long id);

    /**
     * close an API register
     *
     * @throws Exception exception
     */
    void close() throws Exception;
}
