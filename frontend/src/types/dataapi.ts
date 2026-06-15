export interface BaseApiConf {
  id?: number;
  dataSourceId: number;
  name: string;
  comments?: string;
}

export interface HbaseApiConf extends BaseApiConf {
  type: number;       // 1=get, 2=scan
  tableName: string;
  columns: string;
}

export interface SolrApiConf extends BaseApiConf {
  collection: string;
  fields: string;
  conditions?: string;
  orders?: string;
}

export interface SqlParam {
  apiId?: number;
  name: string;
  type: string;       // "STRING" | "NUMERIC"
  description?: string;
}

export interface SqlApiConf extends BaseApiConf {
  sql: string;
  paramList: SqlParam[];
  pageTag: number;    // 0=no paging, 1=paging
  pageSize?: number;
}

export type ApiType = 'hbase' | 'solr' | 'sql';
