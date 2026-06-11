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

import com.wayne.apihub.modules.datasource.conf.SqlSourceConf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wayne
 */
@Mapper
public interface DataSourceSqlDao {
    /**
     * insert sql source configuration
     *
     * @param sqlSourceConf SqlSourceConf
     */
    void insertSqlSource(@Param("sqlSourceConf") SqlSourceConf sqlSourceConf);

    /**
     * list sql source configuration
     *
     * @return SqlSourceConf list
     */
    List<SqlSourceConf> listSqlSource();

    /**
     * get sql source configuration by id
     *
     * @param id id of sql source
     * @return SqlSourceConf
     */
    SqlSourceConf getSqlSourceById(@Param("id") Long id);

    /**
     * update sql source status
     *
     * @param id     id of sql source
     * @param status status to update
     */
    void updateSqlSourceStatus(@Param("id") Long id, @Param("status") Integer status);
}
