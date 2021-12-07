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

import com.wayne.apihub.modules.datasource.conf.SolrSourceConf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wayne
 */
@Mapper
public interface DataSourceSolrDao {
    /**
     * insert solr source info
     *
     * @param solrSourceInfo SolrSourceInfo
     */
    void insertSolrSource(@Param("solrSourceInfo") SolrSourceConf solrSourceInfo);

    /**
     * list solr source info
     *
     * @return SolrSourceInfo
     */
    List<SolrSourceConf> listSolrSource();

    /**
     * get solr source info by id
     *
     * @param id id of solr source
     * @return SolrSourceInfo
     */
    SolrSourceConf getSolrSourceById(@Param("id") Long id);

    /**
     * update solr source status
     *
     * @param id     id of solr source
     * @param status status to update
     */
    void updateSolrSourceStatus(@Param("id") Long id, @Param("status") Integer status);
}
