import { describe, it, expect, vi, beforeEach } from 'vitest';
import type { Mock } from 'vitest';

const mockGet = vi.fn();
const mockPost = vi.fn();

vi.mock('../client', () => ({
  default: {
    get: mockGet,
    post: mockPost,
  },
}));

// Import after mock
const datasource = await import('../datasource');

describe('datasource API', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('HBase', () => {
    it('insertHbaseSource should POST to /v1/source/hbase/insertSource', () => {
      const conf = { name: 'test-hbase', hbaseSitePath: '/path', coreSitePath: '/path2' };
      datasource.insertHbaseSource(conf);
      expect(mockPost).toHaveBeenCalledWith('/v1/source/hbase/insertSource', conf);
    });

    it('listHbaseSources should GET /v1/source/hbase/listSource with params', () => {
      const req = { pageNum: 1, pageSize: 10 };
      datasource.listHbaseSources(req);
      expect(mockGet).toHaveBeenCalledWith('/v1/source/hbase/listSource', { params: req });
    });

    it('enableHbaseSource should GET /v1/source/hbase/enableSource with id', () => {
      datasource.enableHbaseSource(42);
      expect(mockGet).toHaveBeenCalledWith('/v1/source/hbase/enableSource', { params: { id: 42 } });
    });

    it('disableHbaseSource should GET /v1/source/hbase/disableSource with id', () => {
      datasource.disableHbaseSource(42);
      expect(mockGet).toHaveBeenCalledWith('/v1/source/hbase/disableSource', { params: { id: 42 } });
    });
  });

  describe('Solr', () => {
    it('insertSolrSource should POST to /v1/source/solr/insertSource', () => {
      const conf = { name: 'test-solr', zkHosts: 'host:2181', zkChroot: '/' };
      datasource.insertSolrSource(conf);
      expect(mockPost).toHaveBeenCalledWith('/v1/source/solr/insertSource', conf);
    });

    it('listSolrSources should GET /v1/source/solr/listSource with params', () => {
      const req = { pageNum: 1, pageSize: 20 };
      datasource.listSolrSources(req);
      expect(mockGet).toHaveBeenCalledWith('/v1/source/solr/listSource', { params: req });
    });

    it('enableSolrSource should GET /v1/source/solr/enableSource with id', () => {
      datasource.enableSolrSource(7);
      expect(mockGet).toHaveBeenCalledWith('/v1/source/solr/enableSource', { params: { id: 7 } });
    });

    it('disableSolrSource should GET /v1/source/solr/disableSource with id', () => {
      datasource.disableSolrSource(7);
      expect(mockGet).toHaveBeenCalledWith('/v1/source/solr/disableSource', { params: { id: 7 } });
    });
  });

  describe('SQL', () => {
    it('insertSqlSource should POST to /v1/source/sql/insertSource', () => {
      const conf = { name: 'test-sql', dialect: 'MYSQL', url: 'jdbc:mysql://localhost', username: 'root', password: 'pass' };
      datasource.insertSqlSource(conf);
      expect(mockPost).toHaveBeenCalledWith('/v1/source/sql/insertSource', conf);
    });

    it('listSqlSources should GET /v1/source/sql/listSource with params', () => {
      const req = { pageNum: 2, pageSize: 15 };
      datasource.listSqlSources(req);
      expect(mockGet).toHaveBeenCalledWith('/v1/source/sql/listSource', { params: req });
    });

    it('enableSqlSource should GET /v1/source/sql/enableSource with id', () => {
      datasource.enableSqlSource(99);
      expect(mockGet).toHaveBeenCalledWith('/v1/source/sql/enableSource', { params: { id: 99 } });
    });

    it('disableSqlSource should GET /v1/source/sql/disableSource with id', () => {
      datasource.disableSqlSource(99);
      expect(mockGet).toHaveBeenCalledWith('/v1/source/sql/disableSource', { params: { id: 99 } });
    });
  });
});
