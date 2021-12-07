/**
 * Copyright 2021 Wayne
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wayne.apihub.modules.datasource.client;

import com.wayne.apihub.modules.datasource.info.HbaseSourceConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wayne
 */
public class HbaseClient implements CommonClient {
    private final Connection connection;

    public HbaseClient(HbaseSourceConf info) throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.addResource(new File(info.getHbaseSitePath()).getPath());
        conf.addResource(new File(info.getCoreSitePath()).getPath());
        connection = ConnectionFactory.createConnection(conf);
    }

    public List<Result> scan(String tableName, Scan scan) throws IOException {
        List<Result> resultList = new ArrayList<>();
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            ResultScanner scanner = table.getScanner(scan);
            scanner.forEach(resultList::add);
            scanner.close();
        }
        return resultList;
    }

    public Result get(String tableName, Get get) throws IOException {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            return table.get(get);
        }
    }

    @Override
    public void close() throws IOException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
