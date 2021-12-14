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

import com.wayne.apihub.modules.datasource.conf.MysqlSourceConf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wayne
 */
@Mapper
public interface DataSourceMysqlDao {
    /**
     * insert mysql source configuration
     *
     * @param mysqlSourceConf MysqlSourceConf
     */
    void insertMysqlSource(@Param("mysqlSourceConf") MysqlSourceConf mysqlSourceConf);

    /**
     * list mysql source configuration
     *
     * @return MysqlSourceConf
     */
    List<MysqlSourceConf> listMysqlSource();

    /**
     * get mysql source configuration by id
     *
     * @param id id of mysql source
     * @return MysqlSourceConf
     */
    MysqlSourceConf getMysqlSourceById(@Param("id") Long id);

    /**
     * update mysql source status
     *
     * @param id     id of mysql source
     * @param status status to update
     */
    void updateMysqlSourceStatus(@Param("id") Long id, @Param("status") Integer status);
}
