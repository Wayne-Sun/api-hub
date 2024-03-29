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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author Wayne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@ToString
@Schema(name = "BasePageRequest", description = "Base class for paging request")
public class BasePageRequest {
    @NonNull
    @Schema(description = "Page number")
    private Integer pageNum;
    @NonNull
    @Schema(description = "Page size")
    private Integer pageSize;
}
