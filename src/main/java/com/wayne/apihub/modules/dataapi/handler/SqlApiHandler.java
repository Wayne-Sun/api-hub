package com.wayne.apihub.modules.dataapi.handler;

import com.wayne.apihub.modules.common.entity.SqlParam;
import com.wayne.apihub.modules.dataapi.conf.SqlApiConf;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Wayne
 */
@Data
public class SqlApiHandler implements CommonApiHandler {
    private final Long dataSourceId;
    private final String sql;
    private final Integer pageTag;
    private final Integer pageSize;
    private final Map<String, SqlParam> paramMap;

    public SqlApiHandler(SqlApiConf sqlApiConf) {
        this.dataSourceId = sqlApiConf.getDataSourceId();
        this.sql = sqlApiConf.getSql();
        this.pageTag = sqlApiConf.getPageTag();
        this.pageSize = sqlApiConf.getPageSize();
        this.paramMap = sqlApiConf.getParamList().stream().collect(Collectors.toMap(SqlParam::getName, param -> param));
    }

}
