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
package com.wayne.apihub.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wayne.apihub.modules.dataapi.conf.BaseApiConf;
import com.wayne.apihub.modules.datasource.conf.BaseSourceConf;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author Wayne
 */
@Data
@ToString
@EqualsAndHashCode
@Schema(name = "BaseDataObject", description = "Base class for DB entity", subTypes = {BaseApiConf.class, BaseSourceConf.class})
public class BaseDataObject {
    @Schema(description = "Primary Key")
    private Long id;
    @JsonIgnore
    private Integer status;
    @JsonIgnore
    private Date createTime;
    @JsonIgnore
    private String createBy;
    @JsonIgnore
    private Date updateTime;
    @JsonIgnore
    private String updateBy;
}
