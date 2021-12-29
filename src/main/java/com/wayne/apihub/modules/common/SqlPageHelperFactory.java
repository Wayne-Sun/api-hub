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
package com.wayne.apihub.modules.common;

import com.wayne.apihub.modules.common.page.CommonPageHelper;
import com.wayne.apihub.modules.common.page.MysqlPageHelper;
import com.wayne.apihub.modules.common.page.OraclePageHelper;
import com.wayne.apihub.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Wayne
 */
@Service
public class SqlPageHelperFactory {
    private final MysqlPageHelper mysqlPageHelper;
    private final OraclePageHelper oraclePageHelper;

    @Autowired
    public SqlPageHelperFactory(MysqlPageHelper mysqlPageHelper, OraclePageHelper oraclePageHelper) {
        this.mysqlPageHelper = mysqlPageHelper;
        this.oraclePageHelper = oraclePageHelper;
    }

    public CommonPageHelper getPageHelper(String dialect) {
        CommonPageHelper result;
        if (Constants.ORACLE.equals(dialect)) {
            result = oraclePageHelper;
        } else {
            result = mysqlPageHelper;
        }
        return result;
    }
}
