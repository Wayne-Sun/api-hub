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

import com.wayne.apihub.modules.common.entity.SqlParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author Wayne
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SqlApiConf extends BaseApiConf {
    @Schema(description = "Formated SQL")
    private String sql;
    @Schema(description = "Definition of SQL parameter")
    private List<SqlParam> paramList;
    @Schema(description = "Whether to page")
    private Integer pageTag;
    @Schema(description = "Fixed page size")
    private Integer pageSize;
}
