import apiClient from './client';
import type { BaseResponse, PageData, BasePageRequest } from '@/types';
import type { HbaseSourceConf, SolrSourceConf, SqlSourceConf } from '@/types';

// ---- HBase ----
export function insertHbaseSource(conf: HbaseSourceConf): Promise<{ data: BaseResponse }> {
  return apiClient.post('/v1/source/hbase/insertSource', conf);
}

export function listHbaseSources(req: BasePageRequest): Promise<{ data: BaseResponse<PageData<HbaseSourceConf>> }> {
  return apiClient.post('/v1/source/hbase/listSource', req);
}

export function enableHbaseSource(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/source/hbase/enableSource', { params: { id } });
}

export function disableHbaseSource(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/source/hbase/disableSource', { params: { id } });
}

// ---- Solr ----
export function insertSolrSource(conf: SolrSourceConf): Promise<{ data: BaseResponse }> {
  return apiClient.post('/v1/source/solr/insertSource', conf);
}

export function listSolrSources(req: BasePageRequest): Promise<{ data: BaseResponse<PageData<SolrSourceConf>> }> {
  return apiClient.post('/v1/source/solr/listSource', req);
}

export function enableSolrSource(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/source/solr/enableSource', { params: { id } });
}

export function disableSolrSource(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/source/solr/disableSource', { params: { id } });
}

// ---- SQL ----
export function insertSqlSource(conf: SqlSourceConf): Promise<{ data: BaseResponse }> {
  return apiClient.post('/v1/source/sql/insertSource', conf);
}

export function listSqlSources(req: BasePageRequest): Promise<{ data: BaseResponse<PageData<SqlSourceConf>> }> {
  return apiClient.post('/v1/source/sql/listSource', req);
}

export function enableSqlSource(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/source/sql/enableSource', { params: { id } });
}

export function disableSqlSource(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/source/sql/disableSource', { params: { id } });
}
