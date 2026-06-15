import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { BaseSourceConf, HbaseSourceConf, SolrSourceConf, SqlSourceConf, BaseResponse, PageData, BasePageRequest } from '@/types';
import type { SourceType } from '@/types';
import * as datasourceApi from '@/api/datasource';

type SourceConfMap = {
  hbase: HbaseSourceConf;
  solr: SolrSourceConf;
  sql: SqlSourceConf;
};

type ListSourceApiResponse = Promise<{ data: BaseResponse<PageData<BaseSourceConf>> }>;

const listApiMap: Record<SourceType, (req: BasePageRequest) => ListSourceApiResponse> = {
  hbase: datasourceApi.listHbaseSources as (req: BasePageRequest) => ListSourceApiResponse,
  solr: datasourceApi.listSolrSources as (req: BasePageRequest) => ListSourceApiResponse,
  sql: datasourceApi.listSqlSources as (req: BasePageRequest) => ListSourceApiResponse,
};

const insertApiMap: Record<SourceType, (conf: HbaseSourceConf | SolrSourceConf | SqlSourceConf) => Promise<{ data: BaseResponse }>> = {
  hbase: datasourceApi.insertHbaseSource as (conf: HbaseSourceConf | SolrSourceConf | SqlSourceConf) => Promise<{ data: BaseResponse }>,
  solr: datasourceApi.insertSolrSource as (conf: HbaseSourceConf | SolrSourceConf | SqlSourceConf) => Promise<{ data: BaseResponse }>,
  sql: datasourceApi.insertSqlSource as (conf: HbaseSourceConf | SolrSourceConf | SqlSourceConf) => Promise<{ data: BaseResponse }>,
};

const enableApiMap: Record<SourceType, (id: number) => Promise<{ data: BaseResponse }>> = {
  hbase: datasourceApi.enableHbaseSource,
  solr: datasourceApi.enableSolrSource,
  sql: datasourceApi.enableSqlSource,
};

const disableApiMap: Record<SourceType, (id: number) => Promise<{ data: BaseResponse }>> = {
  hbase: datasourceApi.disableHbaseSource,
  solr: datasourceApi.disableSolrSource,
  sql: datasourceApi.disableSqlSource,
};

export const useDatasourceStore = defineStore('datasource', () => {
  const sources = ref<Record<SourceType, { list: BaseSourceConf[]; total: number; loading: boolean }>>({
    hbase: { list: [], total: 0, loading: false },
    solr: { list: [], total: 0, loading: false },
    sql: { list: [], total: 0, loading: false },
  });

  const error = ref<string | null>(null);

  async function fetchSources(type: SourceType, pageNum = 1, pageSize = 10) {
    sources.value[type].loading = true;
    error.value = null;
    try {
      const response = await listApiMap[type]({ pageNum, pageSize });
      sources.value[type].list = response.data.data.list;
      sources.value[type].total = response.data.data.total;
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to fetch sources';
    } finally {
      sources.value[type].loading = false;
    }
  }

  async function createSource<T extends SourceType>(type: T, conf: SourceConfMap[T]) {
    error.value = null;
    try {
      await insertApiMap[type](conf);
      await fetchSources(type, 1, sources.value[type].total || 10);
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to create source';
    }
  }

  async function enableSource(type: SourceType, id: number) {
    error.value = null;
    try {
      await enableApiMap[type](id);
      await fetchSources(type, 1, sources.value[type].total || 10);
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to enable source';
      throw e;
    }
  }

  async function disableSource(type: SourceType, id: number) {
    error.value = null;
    try {
      await disableApiMap[type](id);
      await fetchSources(type, 1, sources.value[type].total || 10);
    } catch (e) {
      error.value = e instanceof Error ? e.message : 'Failed to disable source';
      throw e;
    }
  }

  return { sources, error, fetchSources, createSource, enableSource, disableSource };
});
