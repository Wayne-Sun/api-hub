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

import com.wayne.apihub.modules.dataapi.conf.SolrApiConf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Wayne
 */
@Mapper
public interface DataApiSolrDao {
    /**
     * insert Solr API configuration
     *
     * @param solrApiConf SolrApiConf
     */
    void insertSolrApi(@Param("solrApiInfo") SolrApiConf solrApiConf);

    /**
     * list Solr API configuration
     *
     * @return SolrApiConf
     */
    List<SolrApiConf> listSolrApi();

    /**
     * get Solr API configuration by id
     *
     * @param id id of the API
     * @return SolrApiConf
     */
    SolrApiConf getSolrApiById(@Param("id") Long id);

    /**
     * update Solr API status
     *
     * @param id     id of the API
     * @param status status of the API to update
     */
    void updateSolrApiStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * count enabled solr api by source id
     *
     * @param sourceId solr source id
     * @return count
     */
    Long countSolrApiBySourceId(@Param("sourceId") Long sourceId);
}
