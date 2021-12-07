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
package com.wayne.apihub.modules.dataapi.handler;

import com.wayne.apihub.model.HbaseColumnFamily;
import com.wayne.apihub.modules.dataapi.conf.HbaseApiConf;
import lombok.Data;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wayne
 */
@Data
public class HbaseApiHandler implements CommonApiHandler {
    private final Long dataSourceId;
    private final String tableName;
    private final Integer type;
    private final List<HbaseColumnFamily> hbaseColumnFamilyList = new ArrayList<>();

    public HbaseApiHandler(HbaseApiConf hbaseApiConf) {
        this.dataSourceId = hbaseApiConf.getDataSourceId();
        this.tableName = hbaseApiConf.getTableName();
        this.type = hbaseApiConf.getType();
        String columns = hbaseApiConf.getColumns();
        String[] seprateColumns = columns.split(",");
        for (String cf : seprateColumns) {
            String[] seprateCf = cf.split(":");
            String column = seprateCf[1];
            String family = seprateCf[0];
            byte[] columnBytes = column.getBytes(StandardCharsets.UTF_8);
            byte[] familyBytes = family.getBytes(StandardCharsets.UTF_8);
            this.hbaseColumnFamilyList.add(new HbaseColumnFamily(cf, column, family, columnBytes, familyBytes));
        }
    }

    public Get generateGet(String rowKey) {
        Get get = new Get(rowKey.getBytes(StandardCharsets.UTF_8));
        for (HbaseColumnFamily identifier : hbaseColumnFamilyList) {
            get.addColumn(identifier.getFamilyBytes(), identifier.getColumnBytes());
        }
        return get;
    }

    public Scan generateScan(String startRowKey, String endRowKey) {
        Scan scan = new Scan();
        scan.withStartRow(startRowKey.getBytes(StandardCharsets.UTF_8), true);
        scan.withStopRow(endRowKey.getBytes(StandardCharsets.UTF_8), false);
        for (HbaseColumnFamily identifier : hbaseColumnFamilyList) {
            scan.addColumn(identifier.getFamilyBytes(), identifier.getColumnBytes());
        }
        return scan;
    }
}
