package com.wayne.apihub.modules.query.sql;

import com.wayne.apihub.modules.query.result.QueryPageInfo;

import java.util.Map;

public interface CommonPageHelper {
    String getPageSql(String sql, QueryPageInfo queryPageInfo);

    Map<String, Object> processParamMap(Map<String, Object> paramMap, QueryPageInfo queryPageInfo);
}
