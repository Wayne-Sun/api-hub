import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { BaseApiConf, HbaseApiConf, SolrApiConf, SqlApiConf, BaseResponse, PageData, BasePageRequest } from '@/types';
import type { ApiType } from '@/types';
import * as dataapiApi from '@/api/dataapi';

type ApiConfMap = {
  hbase: HbaseApiConf;
  solr: SolrApiConf;
  sql: SqlApiConf;
};

type ListApiApiResponse = Promise<{ data: BaseResponse<PageData<BaseApiConf>> }>;

const listApiMap: Record<ApiType, (req: BasePageRequest) => ListApiApiResponse> = {
  hbase: dataapiApi.listHbaseApis as (req: BasePageRequest) => ListApiApiResponse,
  solr: dataapiApi.listSolrApis as (req: BasePageRequest) => ListApiApiResponse,
  sql: dataapiApi.listSqlApis as (req: BasePageRequest) => ListApiApiResponse,
};

const registerApiMap: Record<ApiType, (conf: HbaseApiConf | SolrApiConf | SqlApiConf) => Promise<{ data: BaseResponse }>> = {
  hbase: dataapiApi.registerHbaseApi as (conf: HbaseApiConf | SolrApiConf | SqlApiConf) => Promise<{ data: BaseResponse }>,
  solr: dataapiApi.registerSolrApi as (conf: HbaseApiConf | SolrApiConf | SqlApiConf) => Promise<{ data: BaseResponse }>,
  sql: dataapiApi.registerSqlApi as (conf: HbaseApiConf | SolrApiConf | SqlApiConf) => Promise<{ data: BaseResponse }>,
};

const enableApiMap: Record<ApiType, (id: number) => Promise<{ data: BaseResponse }>> = {
  hbase: dataapiApi.enableHbaseApi,
  solr: dataapiApi.enableSolrApi,
  sql: dataapiApi.enableSqlApi,
};

const disableApiMap: Record<ApiType, (id: number) => Promise<{ data: BaseResponse }>> = {
  hbase: dataapiApi.disableHbaseApi,
  solr: dataapiApi.disableSolrApi,
  sql: dataapiApi.disableSqlApi,
};

export const useDataapiStore = defineStore('dataapi', () => {
  const apis = ref<Record<ApiType, { list: BaseApiConf[]; total: number; loading: boolean }>>({
    hbase: { list: [], total: 0, loading: false },
    solr: { list: [], total: 0, loading: false },
    sql: { list: [], total: 0, loading: false },
  });

  const error = ref<string | null>(null);

  async function fetchApis(type: ApiType, pageNum = 1, pageSize = 10) {
    apis.value[type].loading = true;
    error.value = null;
    try {
      const response = await listApiMap[type]({ pageNum, pageSize });
      apis.value[type].list = response.data.data.list;
      apis.value[type].total = response.data.data.total;
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch apis';
    } finally {
      apis.value[type].loading = false;
    }
  }

  async function registerApi<T extends ApiType>(type: T, conf: ApiConfMap[T]) {
    error.value = null;
    try {
      await registerApiMap[type](conf);
      await fetchApis(type, 1, apis.value[type].total || 10);
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to register api';
    }
  }

  async function enableApi(type: ApiType, id: number) {
    error.value = null;
    try {
      await enableApiMap[type](id);
      await fetchApis(type, 1, apis.value[type].total || 10);
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to enable api';
      throw e;
    }
  }

  async function disableApi(type: ApiType, id: number) {
    error.value = null;
    try {
      await disableApiMap[type](id);
      await fetchApis(type, 1, apis.value[type].total || 10);
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to disable api';
      throw e;
    }
  }

  return { apis, error, fetchApis, registerApi, enableApi, disableApi };
});
