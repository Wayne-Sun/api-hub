import apiClient from './client';
import type { BaseResponse, PageData, BasePageRequest } from '@/types';
import type { HbaseApiConf, SolrApiConf, SqlApiConf, SqlParam } from '@/types';

// ---- HBase ----
export function registerHbaseApi(conf: HbaseApiConf): Promise<{ data: BaseResponse }> {
  return apiClient.post('/v1/api/hbase/registerApi', conf);
}

export function listHbaseApis(req: BasePageRequest): Promise<{ data: BaseResponse<PageData<HbaseApiConf>> }> {
  return apiClient.post('/v1/api/hbase/listApi', req);
}

export function enableHbaseApi(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/api/hbase/enableApi', { params: { id } });
}

export function disableHbaseApi(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/api/hbase/disableApi', { params: { id } });
}

// ---- Solr ----
export function registerSolrApi(conf: SolrApiConf): Promise<{ data: BaseResponse }> {
  return apiClient.post('/v1/api/solr/registerApi', conf);
}

export function listSolrApis(req: BasePageRequest): Promise<{ data: BaseResponse<PageData<SolrApiConf>> }> {
  return apiClient.post('/v1/api/solr/listApi', req);
}

export function enableSolrApi(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/api/solr/enableApi', { params: { id } });
}

export function disableSolrApi(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/api/solr/disableApi', { params: { id } });
}

// ---- SQL ----
export function registerSqlApi(conf: SqlApiConf): Promise<{ data: BaseResponse }> {
  return apiClient.post('/v1/api/sql/registerApi', conf);
}

export function listSqlApis(req: BasePageRequest): Promise<{ data: BaseResponse<PageData<SqlApiConf>> }> {
  return apiClient.post('/v1/api/sql/listApi', req);
}

export function enableSqlApi(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/api/sql/enableApi', { params: { id } });
}

export function disableSqlApi(id: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/api/sql/disableApi', { params: { id } });
}

// ---- SQL-specific ----
export function listSqlApiParams(apiId: number): Promise<{ data: BaseResponse<SqlParam[]> }> {
  return apiClient.get('/v1/api/sql/listApiParam', { params: { apiId } });
}

export function deleteSqlApiParamsByApiId(apiId: number): Promise<{ data: BaseResponse }> {
  return apiClient.get('/v1/api/sql/deleteApiParamByApiId', { params: { apiId } });
}
