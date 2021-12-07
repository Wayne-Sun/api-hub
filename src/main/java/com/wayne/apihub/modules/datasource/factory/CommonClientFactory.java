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
package com.wayne.apihub.modules.datasource.factory;

import com.wayne.apihub.modules.datasource.client.CommonClient;
import com.wayne.apihub.modules.datasource.info.BaseSourceConf;

/**
 * @author Wayne
 */
public interface CommonClientFactory {
    /**
     * initialize client factory
     *
     * @throws Exception exception
     */
    void init() throws Exception;

    /**
     * create a client and add to the client factory
     *
     * @param baseSourceConf source info of the client
     * @throws Exception exception
     */
    void createClient(BaseSourceConf baseSourceConf) throws Exception;

    /**
     * initialize client while initializing clientFactory
     *
     * @param baseSourceConf BaseSourceInfo
     * @throws Exception exception
     */
    void initClient(BaseSourceConf baseSourceConf) throws Exception;

    /**
     * enable a client
     *
     * @param id id of the client
     * @throws Exception exception
     */
    void enableClient(Long id) throws Exception;

    /**
     * disable a client
     *
     * @param id id of the client
     * @throws Exception exception
     */
    void disableClient(Long id) throws Exception;

    /**
     * get a specific client from client factory
     *
     * @param id the id of client
     * @return client
     */
    CommonClient getClient(Long id);

    /**
     * close client factory and all clients of the factory
     *
     * @throws Exception exception
     */
    void close() throws Exception;
}
