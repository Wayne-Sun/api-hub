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
package com.wayne.apihub.modules.query.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author Wayne
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(name = "QueryPageInfo", description = "Response paging information")
public class QueryPageInfo {
    @Schema(description = "The total size of the query result set")
    private long total;
    @Schema(description = "Current page number")
    private int pageNum;
    @Schema(description = "Current page size")
    private int pageSize;

    public int getStartRow() {
        return (pageNum == 0) ? 1 : (pageNum - 1) * pageSize + 1;
    }

    public int getEndRow() {
        return (pageNum == 0) ? pageSize : pageNum * pageSize;
    }
}
