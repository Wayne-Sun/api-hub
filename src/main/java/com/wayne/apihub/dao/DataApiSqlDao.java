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
package com.wayne.apihub.dao;

import com.wayne.apihub.modules.common.entity.SqlParam;
import com.wayne.apihub.modules.dataapi.conf.SqlApiConf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wayne
 */
@Mapper
public interface DataApiSqlDao {
    /**
     * insert sql API configuration
     *
     * @param sqlApiConf SqlApiConf
     */
    void insertSqlApi(@Param("sqlApiConf") SqlApiConf sqlApiConf);

    /**
     * insert sql API parameter definition
     *
     * @param paramList imput SqlParam list
     */
    void insertSqlParam(@Param("paramList") List<SqlParam> paramList);

    /**
     * list sql API configuration
     *
     * @return SqlApiConf list
     */
    List<SqlApiConf> listSqlApi();

    /**
     * get sql API configuration by id
     *
     * @param id id of the API
     * @return SqlApiConf
     */
    SqlApiConf getSqlApiById(@Param("id") Long id);

    /**
     * list sql API parameter by API id
     *
     * @param apiId id of the API
     * @return SqlParam list
     */
    List<SqlParam> listApiParamByApiId(@Param("apiId") Long apiId);

    /**
     * update Mysql API status
     *
     * @param id     id of the API
     * @param status status of the API to update
     */
    void updateSqlApiStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * count enabled mysql api by source id
     *
     * @param sourceId sql source id
     * @return count
     */
    Long countSqlApiBySourceId(@Param("sourceId") Long sourceId);

    /**
     * delete sql API parameter by API id
     *
     * @param apiId id of the API
     */
    void deleteApiParamByApiId(@Param("apiId") Long apiId);
}
