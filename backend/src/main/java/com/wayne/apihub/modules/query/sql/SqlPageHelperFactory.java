package com.wayne.apihub.modules.query.sql;

import com.wayne.apihub.utils.Constants;
import org.springframework.stereotype.Service;

@Service
public class SqlPageHelperFactory {
    private final MysqlPageHelper mysqlPageHelper;
    private final OraclePageHelper oraclePageHelper;

    public SqlPageHelperFactory(MysqlPageHelper mysqlPageHelper, OraclePageHelper oraclePageHelper) {
        this.mysqlPageHelper = mysqlPageHelper;
        this.oraclePageHelper = oraclePageHelper;
    }

    public CommonPageHelper getPageHelper(String dialect) {
        CommonPageHelper result;
        if (Constants.DIALECT_ORACLE.equals(dialect)) {
            result = oraclePageHelper;
        } else {
            result = mysqlPageHelper;
        }
        return result;
    }
}
