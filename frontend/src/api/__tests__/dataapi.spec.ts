import { describe, it, expect, vi, beforeEach } from 'vitest';

const mockGet = vi.fn();
const mockPost = vi.fn();

vi.mock('../client', () => ({
  default: {
    get: mockGet,
    post: mockPost,
  },
}));

// Import after mock
const dataapi = await import('../dataapi');

describe('dataapi API', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('HBase', () => {
    it('registerHbaseApi should POST to /v1/api/hbase/registerApi', () => {
      const conf = { dataSourceId: 1, name: 'api1', type: 1, tableName: 't1', columns: 'c1' };
      dataapi.registerHbaseApi(conf);
      expect(mockPost).toHaveBeenCalledWith('/v1/api/hbase/registerApi', conf);
    });

    it('listHbaseApis should GET /v1/api/hbase/listApi with params', () => {
      const req = { pageNum: 1, pageSize: 10 };
      dataapi.listHbaseApis(req);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/hbase/listApi', { params: req });
    });

    it('enableHbaseApi should GET /v1/api/hbase/enableApi with id', () => {
      dataapi.enableHbaseApi(42);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/hbase/enableApi', { params: { id: 42 } });
    });

    it('disableHbaseApi should GET /v1/api/hbase/disableApi with id', () => {
      dataapi.disableHbaseApi(42);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/hbase/disableApi', { params: { id: 42 } });
    });
  });

  describe('Solr', () => {
    it('registerSolrApi should POST to /v1/api/solr/registerApi', () => {
      const conf = { dataSourceId: 2, name: 'solr-api', collection: 'coll1', fields: 'f1,f2' };
      dataapi.registerSolrApi(conf);
      expect(mockPost).toHaveBeenCalledWith('/v1/api/solr/registerApi', conf);
    });

    it('listSolrApis should GET /v1/api/solr/listApi with params', () => {
      const req = { pageNum: 1, pageSize: 20 };
      dataapi.listSolrApis(req);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/solr/listApi', { params: req });
    });

    it('enableSolrApi should GET /v1/api/solr/enableApi with id', () => {
      dataapi.enableSolrApi(7);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/solr/enableApi', { params: { id: 7 } });
    });

    it('disableSolrApi should GET /v1/api/solr/disableApi with id', () => {
      dataapi.disableSolrApi(7);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/solr/disableApi', { params: { id: 7 } });
    });
  });

  describe('SQL', () => {
    it('registerSqlApi should POST to /v1/api/sql/registerApi', () => {
      const conf = { dataSourceId: 3, name: 'sql-api', sql: 'SELECT * FROM t', paramList: [], pageTag: 0 };
      dataapi.registerSqlApi(conf);
      expect(mockPost).toHaveBeenCalledWith('/v1/api/sql/registerApi', conf);
    });

    it('listSqlApis should GET /v1/api/sql/listApi with params', () => {
      const req = { pageNum: 1, pageSize: 15 };
      dataapi.listSqlApis(req);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/sql/listApi', { params: req });
    });

    it('enableSqlApi should GET /v1/api/sql/enableApi with id', () => {
      dataapi.enableSqlApi(99);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/sql/enableApi', { params: { id: 99 } });
    });

    it('disableSqlApi should GET /v1/api/sql/disableApi with id', () => {
      dataapi.disableSqlApi(99);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/sql/disableApi', { params: { id: 99 } });
    });
  });

  describe('SQL-specific', () => {
    it('listSqlApiParams should GET /v1/api/sql/listApiParam with apiId', () => {
      dataapi.listSqlApiParams(55);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/sql/listApiParam', { params: { apiId: 55 } });
    });

    it('deleteSqlApiParamsByApiId should GET /v1/api/sql/deleteApiParamByApiId with apiId', () => {
      dataapi.deleteSqlApiParamsByApiId(55);
      expect(mockGet).toHaveBeenCalledWith('/v1/api/sql/deleteApiParamByApiId', { params: { apiId: 55 } });
    });
  });
});
