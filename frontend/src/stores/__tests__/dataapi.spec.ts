import { describe, it, expect, vi, beforeEach } from 'vitest';
import { setActivePinia, createPinia } from 'pinia';

const mockListHbaseApis = vi.fn();
const mockListSolrApis = vi.fn();
const mockListSqlApis = vi.fn();
const mockRegisterHbaseApi = vi.fn();
const mockRegisterSolrApi = vi.fn();
const mockRegisterSqlApi = vi.fn();
const mockEnableHbaseApi = vi.fn();
const mockEnableSolrApi = vi.fn();
const mockEnableSqlApi = vi.fn();
const mockDisableHbaseApi = vi.fn();
const mockDisableSolrApi = vi.fn();
const mockDisableSqlApi = vi.fn();

vi.mock('@/api/dataapi', () => ({
  listHbaseApis: mockListHbaseApis,
  listSolrApis: mockListSolrApis,
  listSqlApis: mockListSqlApis,
  registerHbaseApi: mockRegisterHbaseApi,
  registerSolrApi: mockRegisterSolrApi,
  registerSqlApi: mockRegisterSqlApi,
  enableHbaseApi: mockEnableHbaseApi,
  enableSolrApi: mockEnableSolrApi,
  enableSqlApi: mockEnableSqlApi,
  disableHbaseApi: mockDisableHbaseApi,
  disableSolrApi: mockDisableSolrApi,
  disableSqlApi: mockDisableSqlApi,
}));

const { useDataapiStore } = await import('../dataapi');

function makePageResponse<T>(list: T[], total = list.length) {
  return {
    data: {
      code: 200,
      message: 'success',
      data: { list, total, pages: 1, pageNum: 1, pageSize: 10 },
    },
  };
}

describe('useDataapiStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  describe('fetchApis', () => {
    it('calls listHbaseApis and updates hbase state', async () => {
      const items = [{ id: 1, dataSourceId: 1, name: 'hbase-api', type: 1, tableName: 't', columns: 'c' }];
      mockListHbaseApis.mockResolvedValue(makePageResponse(items));

      const store = useDataapiStore();
      await store.fetchApis('hbase');

      expect(mockListHbaseApis).toHaveBeenCalledWith({ pageNum: 1, pageSize: 10 });
      expect(store.apis.hbase.list).toEqual(items);
      expect(store.apis.hbase.total).toBe(1);
      expect(store.apis.hbase.loading).toBe(false);
    });

    it('calls listSolrApis and updates solr state', async () => {
      const items = [{ id: 2, dataSourceId: 1, name: 'solr-api', collection: 'col', fields: 'f' }];
      mockListSolrApis.mockResolvedValue(makePageResponse(items));

      const store = useDataapiStore();
      await store.fetchApis('solr', 2, 20);

      expect(mockListSolrApis).toHaveBeenCalledWith({ pageNum: 2, pageSize: 20 });
      expect(store.apis.solr.list).toEqual(items);
      expect(store.apis.solr.total).toBe(1);
    });

    it('calls listSqlApis and updates sql state', async () => {
      const items = [{ id: 3, dataSourceId: 1, name: 'sql-api', sql: 'SELECT 1', paramList: [], pageTag: 0 }];
      mockListSqlApis.mockResolvedValue(makePageResponse(items));

      const store = useDataapiStore();
      await store.fetchApis('sql');

      expect(mockListSqlApis).toHaveBeenCalledWith({ pageNum: 1, pageSize: 10 });
      expect(store.apis.sql.list).toEqual(items);
      expect(store.apis.sql.total).toBe(1);
    });

    it('sets loading true during fetch and false after', async () => {
      mockListHbaseApis.mockResolvedValue(makePageResponse([]));

      const store = useDataapiStore();
      const promise = store.fetchApis('hbase');
      expect(store.apis.hbase.loading).toBe(true);
      await promise;
      expect(store.apis.hbase.loading).toBe(false);
    });

    it('sets error on API failure', async () => {
      mockListHbaseApis.mockRejectedValue(new Error('Network error'));

      const store = useDataapiStore();
      await store.fetchApis('hbase');

      expect(store.error).toBe('Network error');
      expect(store.apis.hbase.loading).toBe(false);
    });
  });

  describe('registerApi', () => {
    it('calls registerHbaseApi and refreshes list', async () => {
      const newConf = { name: 'new-hbase-api', dataSourceId: 1, type: 1, tableName: 't', columns: 'c' };
      mockRegisterHbaseApi.mockResolvedValue({ data: { code: 200, message: 'success', data: null } });
      mockListHbaseApis.mockResolvedValue(makePageResponse([newConf]));

      const store = useDataapiStore();
      await store.registerApi('hbase', newConf);

      expect(mockRegisterHbaseApi).toHaveBeenCalledWith(newConf);
      expect(mockListHbaseApis).toHaveBeenCalled();
      expect(store.apis.hbase.list).toEqual([newConf]);
    });

    it('sets error on register failure', async () => {
      mockRegisterSolrApi.mockRejectedValue(new Error('Register failed'));

      const store = useDataapiStore();
      await store.registerApi('solr', { name: 'fail' } as any);

      expect(store.error).toBe('Register failed');
    });
  });

  describe('enableApi', () => {
    it('calls enableSqlApi and refreshes list', async () => {
      mockEnableSqlApi.mockResolvedValue({ data: { code: 200, message: 'success', data: null } });
      const items = [{ id: 3, dataSourceId: 1, name: 'enabled-api', sql: 'SELECT 1', paramList: [], pageTag: 0 }];
      mockListSqlApis.mockResolvedValue(makePageResponse(items));

      const store = useDataapiStore();
      await store.enableApi('sql', 3);

      expect(mockEnableSqlApi).toHaveBeenCalledWith(3);
      expect(mockListSqlApis).toHaveBeenCalled();
      expect(store.apis.sql.list).toEqual(items);
    });
  });

  describe('disableApi', () => {
    it('calls disableSolrApi and refreshes list', async () => {
      mockDisableSolrApi.mockResolvedValue({ data: { code: 200, message: 'success', data: null } });
      const items = [{ id: 2, dataSourceId: 1, name: 'disabled-api', collection: 'col', fields: 'f' }];
      mockListSolrApis.mockResolvedValue(makePageResponse(items));

      const store = useDataapiStore();
      await store.disableApi('solr', 2);

      expect(mockDisableSolrApi).toHaveBeenCalledWith(2);
      expect(mockListSolrApis).toHaveBeenCalled();
      expect(store.apis.solr.list).toEqual(items);
    });
  });
});
