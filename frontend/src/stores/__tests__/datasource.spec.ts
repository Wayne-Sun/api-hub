import { describe, it, expect, vi, beforeEach } from 'vitest';
import { setActivePinia, createPinia } from 'pinia';
import type { Mock } from 'vitest';

const mockListHbaseSources = vi.fn();
const mockListSolrSources = vi.fn();
const mockListSqlSources = vi.fn();
const mockInsertHbaseSource = vi.fn();
const mockInsertSolrSource = vi.fn();
const mockInsertSqlSource = vi.fn();
const mockEnableHbaseSource = vi.fn();
const mockEnableSolrSource = vi.fn();
const mockEnableSqlSource = vi.fn();
const mockDisableHbaseSource = vi.fn();
const mockDisableSolrSource = vi.fn();
const mockDisableSqlSource = vi.fn();

vi.mock('@/api/datasource', () => ({
  listHbaseSources: mockListHbaseSources,
  listSolrSources: mockListSolrSources,
  listSqlSources: mockListSqlSources,
  insertHbaseSource: mockInsertHbaseSource,
  insertSolrSource: mockInsertSolrSource,
  insertSqlSource: mockInsertSqlSource,
  enableHbaseSource: mockEnableHbaseSource,
  enableSolrSource: mockEnableSolrSource,
  enableSqlSource: mockEnableSqlSource,
  disableHbaseSource: mockDisableHbaseSource,
  disableSolrSource: mockDisableSolrSource,
  disableSqlSource: mockDisableSqlSource,
}));

const { useDatasourceStore } = await import('../datasource');

function makePageResponse<T>(list: T[], total = list.length) {
  return {
    data: {
      code: 200,
      message: 'success',
      data: { list, total, pages: 1, pageNum: 1, pageSize: 10 },
    },
  };
}

describe('useDatasourceStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  describe('fetchSources', () => {
    it('calls listHbaseSources and updates hbase state', async () => {
      const items = [{ id: 1, name: 'hbase-src', hbaseSitePath: '/a', coreSitePath: '/b' }];
      mockListHbaseSources.mockResolvedValue(makePageResponse(items));

      const store = useDatasourceStore();
      await store.fetchSources('hbase');

      expect(mockListHbaseSources).toHaveBeenCalledWith({ pageNum: 1, pageSize: 10 });
      expect(store.sources.hbase.list).toEqual(items);
      expect(store.sources.hbase.total).toBe(1);
      expect(store.sources.hbase.loading).toBe(false);
    });

    it('calls listSolrSources and updates solr state', async () => {
      const items = [{ id: 2, name: 'solr-src', zkHosts: 'host:2181', zkChroot: '/' }];
      mockListSolrSources.mockResolvedValue(makePageResponse(items));

      const store = useDatasourceStore();
      await store.fetchSources('solr', 2, 20);

      expect(mockListSolrSources).toHaveBeenCalledWith({ pageNum: 2, pageSize: 20 });
      expect(store.sources.solr.list).toEqual(items);
      expect(store.sources.solr.total).toBe(1);
    });

    it('calls listSqlSources and updates sql state', async () => {
      const items = [{ id: 3, name: 'sql-src', dialect: 'MYSQL', url: 'jdbc:mysql://localhost', username: 'root', password: 'pass' }];
      mockListSqlSources.mockResolvedValue(makePageResponse(items));

      const store = useDatasourceStore();
      await store.fetchSources('sql');

      expect(mockListSqlSources).toHaveBeenCalledWith({ pageNum: 1, pageSize: 10 });
      expect(store.sources.sql.list).toEqual(items);
      expect(store.sources.sql.total).toBe(1);
    });

    it('sets loading true during fetch and false after', async () => {
      mockListHbaseSources.mockResolvedValue(makePageResponse([]));

      const store = useDatasourceStore();
      const promise = store.fetchSources('hbase');
      expect(store.sources.hbase.loading).toBe(true);
      await promise;
      expect(store.sources.hbase.loading).toBe(false);
    });

    it('sets error on API failure', async () => {
      mockListHbaseSources.mockRejectedValue(new Error('Network error'));

      const store = useDatasourceStore();
      await store.fetchSources('hbase');

      expect(store.error).toBe('Network error');
      expect(store.sources.hbase.loading).toBe(false);
    });
  });

  describe('createSource', () => {
    it('calls insertHbaseSource and refreshes list', async () => {
      const newConf = { name: 'new-hbase', hbaseSitePath: '/a', coreSitePath: '/b' };
      mockInsertHbaseSource.mockResolvedValue({ data: { code: 200, message: 'success', data: null } });
      mockListHbaseSources.mockResolvedValue(makePageResponse([newConf]));

      const store = useDatasourceStore();
      await store.createSource('hbase', newConf);

      expect(mockInsertHbaseSource).toHaveBeenCalledWith(newConf);
      expect(mockListHbaseSources).toHaveBeenCalled();
      expect(store.sources.hbase.list).toEqual([newConf]);
    });

    it('sets error on insert failure', async () => {
      mockInsertHbaseSource.mockRejectedValue(new Error('Insert failed'));

      const store = useDatasourceStore();
      await store.createSource('hbase', { name: 'fail' } as any);

      expect(store.error).toBe('Insert failed');
    });
  });

  describe('enableSource', () => {
    it('calls enableHbaseSource and refreshes list', async () => {
      mockEnableHbaseSource.mockResolvedValue({ data: { code: 200, message: 'success', data: null } });
      const items = [{ id: 1, name: 'enabled-src', hbaseSitePath: '/a', coreSitePath: '/b' }];
      mockListHbaseSources.mockResolvedValue(makePageResponse(items));

      const store = useDatasourceStore();
      await store.enableSource('hbase', 1);

      expect(mockEnableHbaseSource).toHaveBeenCalledWith(1);
      expect(mockListHbaseSources).toHaveBeenCalled();
      expect(store.sources.hbase.list).toEqual(items);
    });
  });

  describe('disableSource', () => {
    it('calls disableSolrSource and refreshes list', async () => {
      mockDisableSolrSource.mockResolvedValue({ data: { code: 200, message: 'success', data: null } });
      const items = [{ id: 2, name: 'disabled-src', zkHosts: 'host:2181', zkChroot: '/' }];
      mockListSolrSources.mockResolvedValue(makePageResponse(items));

      const store = useDatasourceStore();
      await store.disableSource('solr', 2);

      expect(mockDisableSolrSource).toHaveBeenCalledWith(2);
      expect(mockListSolrSources).toHaveBeenCalled();
      expect(store.sources.solr.list).toEqual(items);
    });
  });
});
