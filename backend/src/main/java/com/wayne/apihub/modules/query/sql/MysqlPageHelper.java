package com.wayne.apihub.modules.query.sql;

import com.wayne.apihub.modules.query.result.QueryPageInfo;
import com.wayne.apihub.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MysqlPageHelper implements CommonPageHelper {
    @Override
    public String getPageSql(String sql, QueryPageInfo queryPageInfo) {
        return sql + " LIMIT :" + Constants.PARAM_START_ROW + ", :" + Constants.PARAM_END_ROW;
    }

    @Override
    public Map<String, Object> processParamMap(Map<String, Object> paramMap, QueryPageInfo queryPageInfo) {
        paramMap.put(Constants.PARAM_START_ROW, queryPageInfo.getStartRow());
        paramMap.put(Constants.PARAM_END_ROW, queryPageInfo.getEndRow());
        return paramMap;
    }
}
