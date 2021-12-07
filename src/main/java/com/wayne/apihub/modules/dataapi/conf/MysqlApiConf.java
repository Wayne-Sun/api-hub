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
package com.wayne.apihub.modules.dataapi.conf;

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
@ApiModel(value = "Mysql API 配置信息", parent = BaseApiConf.class)
public class MysqlApiConf extends BaseApiConf {
    @ApiModelProperty(value = "Mysql 库名")
    private String databaseName;
    @ApiModelProperty(value = "Mysql 表名")
    private String tableName;
    @ApiModelProperty(value = "Mysql 返回列名称列表")
    private String columns;
    @ApiModelProperty(value = "Mysql 条件子语句")
    private String conditions;
    @ApiModelProperty(value = "Mysql 条件类型列表")
    private String conditionTypes;
    @ApiModelProperty(value = "Mysql 排序子语句")
    private String orders;
}
