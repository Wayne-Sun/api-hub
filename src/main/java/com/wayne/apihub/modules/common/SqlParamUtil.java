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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wayne
 */
public class SqlParamUtil {

    public static Set<String> extractParam(String inputSql) {
        Pattern pattern = Pattern.compile(":(\\w+)");
        Matcher matcher = pattern.matcher(inputSql);
        Set<String> params = new LinkedHashSet<>();
        while (matcher.find()) {
            params.add(matcher.group(1));
        }
        return params;
    }

    public static String formatSql(String inputSql) {
        Pattern pattern = Pattern.compile("\\$\\{(\\w+)}");
        Matcher matcher = pattern.matcher(inputSql);
        while (matcher.find()) {
            inputSql = inputSql.replace(matcher.group(0), ":" + matcher.group(1));
        }
        return inputSql;
    }

    public static void main(String[] args) {
        String sql = "select * from test where id = :id and name = :name";
        System.out.println(extractParam(sql));
        System.out.println(formatSql(sql));
    }
}
