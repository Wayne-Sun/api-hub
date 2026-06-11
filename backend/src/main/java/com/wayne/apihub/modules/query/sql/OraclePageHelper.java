package com.wayne.apihub.modules.query.sql;

import com.wayne.apihub.modules.query.result.QueryPageInfo;
import com.wayne.apihub.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.Map;

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
        paramMap.put(Constants.PARAM_START_ROW, queryPageInfo.getStartRow() + 1);
        paramMap.put(Constants.PARAM_END_ROW, queryPageInfo.getEndRow());
        return paramMap;
    }
}
