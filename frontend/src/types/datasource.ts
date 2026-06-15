export interface BaseSourceConf {
  id?: number;
  name: string;
  comments?: string;
}

export interface HbaseSourceConf extends BaseSourceConf {
  hbaseSitePath: string;
  coreSitePath: string;
}

export interface SolrSourceConf extends BaseSourceConf {
  zkHosts: string;
  zkChroot: string;
}

export interface SqlSourceConf extends BaseSourceConf {
  dialect: string;   // "MYSQL" | "ORACLE"
  url: string;
  username: string;
  password: string;
}

export type SourceType = 'hbase' | 'solr' | 'sql';
