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
package com.wayne.apihub.modules.dataapi.info;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Wayne
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Solr API 配置信息", parent = BaseApiConf.class)
public class SolrApiConf extends BaseApiConf {
    @ApiModelProperty(value = "Solr 集合名")
    private String collection;
    @ApiModelProperty(value = "Solr 返回字段列表")
    private String fields;
    @ApiModelProperty(value = "Solr 查询子语句")
    private String conditions;
    @ApiModelProperty(value = "Solr 排序子语句")
    private String orders;
}
