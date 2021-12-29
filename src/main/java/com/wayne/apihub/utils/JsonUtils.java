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
package com.wayne.apihub.utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wayne.apihub.modules.common.entity.SqlParam;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Wayne
 */
public class JsonUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonFactory FACTORY = new JsonFactory();

    public static String toString(Object obj) throws JsonProcessingException {
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return MAPPER.writeValueAsString(obj);
    }

    public static <T> T toBean(String json, Class<T> tClass) throws JsonProcessingException {
        return MAPPER.readValue(json, tClass);
    }

    public static <E> List<E> toList(String json, Class<E> eClass) throws JsonProcessingException {
        return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, eClass));
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) throws JsonProcessingException {
        return MAPPER.readValue(json, MAPPER.getTypeFactory().constructMapType(Map.class, kClass, vClass));
    }

    public static <T> T nativeRead(String json, TypeReference<T> type) throws JsonProcessingException {
        return MAPPER.readValue(json, type);
    }

    public static Map<String, Object> toMap(String json, Map<String, SqlParam> paramMap) throws IOException {
        JsonParser jsonParser = FACTORY.createParser(json);
        Map<String, Object> result = new HashMap<>();
        while (!jsonParser.isClosed()) {
            JsonToken token = jsonParser.nextToken();
            if (JsonToken.FIELD_NAME == token) {
                String fieldName = jsonParser.getCurrentName();
                jsonParser.nextToken();
                SqlParam sqlParam = paramMap.get(fieldName);
                if (Constants.NUMERIC.equals(sqlParam.getType())) {
                    result.put(fieldName, jsonParser.getValueAsDouble());
                } else {
                    result.put(fieldName, jsonParser.getValueAsString());
                }
            }
        }
        return result;
    }

    public static Boolean checkSchema(String json, Map<String, SqlParam> paramMap) throws IOException {
        JsonParser jsonParser = FACTORY.createParser(json);
        boolean result = true;
        while (!jsonParser.isClosed()) {
            JsonToken token = jsonParser.nextToken();
            if (JsonToken.FIELD_NAME == token) {
                String fieldName = jsonParser.getCurrentName();
                jsonParser.nextToken();
                result = paramMap.containsKey(fieldName);
            }
        }
        return result;
    }
}
