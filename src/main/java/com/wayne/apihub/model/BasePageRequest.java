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
package com.wayne.apihub.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author Wayne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("分页请求父类")
public class BasePageRequest {
    @NonNull
    @ApiModelProperty("页码号")
    private Integer pageNum;
    @NonNull
    @ApiModelProperty("单页数据量")
    private Integer pageSize;
}
