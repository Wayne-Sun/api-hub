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

import com.wayne.apihub.model.BaseDataObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Wayne
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(name = "BaseApiConf", description = "Base class for API configuration", subTypes = {HbaseApiConf.class, MysqlApiConf.class, SolrApiConf.class})
public class BaseApiConf extends BaseDataObject {
    @Schema(description = "API data source id")
    private Long dataSourceId;
    @Schema(description = "API name")
    private String name;
    @Schema(description = "API description")
    private String comments;
}
