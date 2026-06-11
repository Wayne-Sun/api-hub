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

import com.wayne.apihub.modules.datasource.conf.HbaseSourceConf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wayne
 */
@Mapper
public interface DataSourceHbaseDao {
    /**
     * insert hbase source configuration
     *
     * @param hbaseSourceConf HbaseSourceConf
     */
    void insertHbaseSource(@Param("hbaseSourceConf") HbaseSourceConf hbaseSourceConf);

    /**
     * list hbase source configuration
     *
     * @return HbaseSourceConf
     */
    List<HbaseSourceConf> listHbaseSource();

    /**
     * get hbase source configuration by id
     *
     * @param id id of hbase source
     * @return HbaseSourceConf
     */
    HbaseSourceConf getHbaseSourceById(@Param("id") Long id);

    /**
     * update hbase source status
     *
     * @param id     id of hbase source
     * @param status status to update
     */
    void updateHbaseSourceStatus(@Param("id") Long id, @Param("status") Integer status);
}
