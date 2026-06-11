package com.wayne.apihub.modules.query.sql;

import com.wayne.apihub.utils.Constants;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlParamUtil {

    public static Set<String> extractParam(String inputSql) {
        Pattern pattern = Pattern.compile(Constants.REGEX_NAMED_PARAM);
        Matcher matcher = pattern.matcher(inputSql);
        Set<String> params = new LinkedHashSet<>();
        while (matcher.find()) {
            params.add(matcher.group(1));
        }
        return params;
    }

    public static String formatSql(String inputSql) {
        Pattern pattern = Pattern.compile(Constants.REGEX_EXPR_PARAM);
        Matcher matcher = pattern.matcher(inputSql);
        while (matcher.find()) {
            inputSql = inputSql.replace(matcher.group(0), ":" + matcher.group(1));
        }
        return inputSql;
    }
}
