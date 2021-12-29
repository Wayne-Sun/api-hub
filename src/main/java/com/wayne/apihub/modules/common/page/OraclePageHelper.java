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
package com.wayne.apihub.modules.common.page;

import com.wayne.apihub.modules.query.result.QueryPageInfo;
import com.wayne.apihub.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Wayne
 */
@Service
public class OraclePageHelper implements CommonPageHelper {
    @Override
    public String getPageSql(String sql, QueryPageInfo queryPageInfo) {
        return "SELECT * FROM ( SELECT TMP_PAGE.*, ROWNUM PAGEHELPER_ROW_ID FROM ( " +
                sql +
                " ) TMP_PAGE) WHERE PAGEHELPER_ROW_ID >= :" + Constants.PARAM_START_ROW +
                " AND PAGEHELPER_ROW_ID <= :" + Constants.PARAM_END_ROW;
    }

    @Override
    public Map<String, Object> processParamMap(Map<String, Object> paramMap, QueryPageInfo queryPageInfo) {
        paramMap.put(Constants.PARAM_START_ROW, queryPageInfo.getStartRow());
        paramMap.put(Constants.PARAM_END_ROW, queryPageInfo.getEndRow());
        return paramMap;
    }
}
