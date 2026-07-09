# 变更日志 - 2026-07-09

## 概述
项目文档更新（AGENTS.md 同步）、前端测试覆盖率补全（组件/Store/View 层），以及基础设施配置优化。

## 变更详情

### 项目知识库同步（2个文件）

#### AGENTS.md（+3/-3）
- 生成时间戳 `2026-06-17` → `2026-07-07`
- 提交哈希 `ed0015b` → `3f737f7`
- 后端测试记录更新：`无后端测试` → `存在 8 个单元测试（6 controller + 2 service，本地未跟踪）`

#### backend/AGENTS.md（+1/-2）
- 移除已修复的 `@ModelAttribute @RequestBody` 已知问题条目
- 后端测试记录同步更新

---

### 基础设施

#### .gitignore（+1/-1）
- `# OMO` → `# AGENTS & MCP` 注释规范化
- 新增 `.codegraph/` 模式（CodeGraph 索引目录）

---

### 前端测试覆盖率补充（6个文件，+662 行）

##### Store 层测试：datasource.spec.ts / dataapi.spec.ts（各 +98 行）
- **fetchSources/fetchApis**：错误状态重置测试（调用前 `error` 置 `null`）；默认分页参数验证（pageNum=1, pageSize=10）
- **createSource/registerApi**：loading 状态切换测试（true → false）；错误前置重置；失败不 re-throw 验证
- **enableSource/enableApi**：error 重置测试；loading 完成状态测试

##### 组件测试：DataSourceFormDialog.spec.ts（+161 行）
- **NForm stub 增强**：注入验证逻辑（name 为空时 reject）
- **`mountSourceDialog` 辅助函数**：带 show 过渡的标准挂载方式
- **提交负载验证**：HBase/Solr/SQL 三种类型 payload 结构与字段正确性
- **表单重置测试**：dialog 打开时 formData 字段重置为初始值
- **验证规则测试**：name 必填字段校验；name 为空时提交被阻止

##### 组件测试：DataApiFormDialog.spec.ts（+83 行）
- **Solr payload 提交测试**：collection/fields/conditions/orders 字段正确性
- **成功提交后关闭测试**：`close` 事件在异步完成后发出
- **验证规则测试**：SQL `sql` 字段必填、HBase `tableName` 必填、Solr `collection` 与 `fields` 必填

##### 视图测试：DataApiView.spec.ts（+133 行）
- **注册按钮交互**：点击打开/关闭 form dialog 流程
- **异常消息测试**：DataApiException 错误信息正确显示在 snackbar
- **Tab 切换**：`activeTab` 变化同步到 `DataApiFormDialog.apiType` prop；切换时自动 fetch 新 tab 数据
- **分页测试**：page 变化触发 `fetchApis(tab, newPage)`

##### 视图测试：DataSourceView.spec.ts（+87 行）
- **标题与新增按钮渲染测试**
- **form dialog 打开/关闭交互**
- **Tab 切换**：`activeTab` → `DataSourceFormDialog.sourceType` prop 同步
- **分页测试**：page 变化触发 `fetchSources(tab, newPage)`

---

### 新增后端测试文件（未跟踪，8 个文件）

`backend/src/test/java/com/wayne/apihub/` 目录下新增：

##### Service 层测试（2个）
- `service/DataApiConfServiceTest.java` — DataApiConfService 的 HBase/Solr/SQL CRUD、分页查询、SQL 参数操作测试
- `service/DataSourceConfServiceTest.java` — DataSourceConfService 的 HBase/Solr/SQL 数据源增删改查测试

##### Controller 层测试（6个）
- `modules/dataapi/controller/HbaseApiControllerTest.java`
- `modules/dataapi/controller/SolrApiControllerTest.java`
- `modules/dataapi/controller/SqlApiControllerTest.java`
- `modules/datasource/controller/HbaseSourceControllerTest.java`
- `modules/datasource/controller/SolrSourceControllerTest.java`
- `modules/datasource/controller/SqlSourceControllerTest.java`

测试框架：JUnit 5 + Mockito + AssertJ，纯单元测试（无 Spring Context 加载）。

## 文件变更统计

| 分类 | 文件数 | 说明 |
|------|--------|------|
| 项目知识库 | 2 | AGENTS.md、backend/AGENTS.md |
| 基础设施 | 1 | .gitignore |
| Store 测试 | 2 | datasource.spec.ts、dataapi.spec.ts |
| 组件测试 | 2 | DataSourceFormDialog.spec.ts、DataApiFormDialog.spec.ts |
| 视图测试 | 2 | DataApiView.spec.ts、DataSourceView.spec.ts |
| 后端测试（新增未跟踪） | 8 | 6 controller + 2 service |
| **总计** | **17** | **修改 9 + 新增未跟踪 8** |

## 已知遗留问题
- 数据库密码 `api_hub@2021` 仍硬编码在 `application.yml` 中
- `enableApi`/`disableApi` 使用 GET 方式修改状态（应为 POST/PUT）
- `DataApiSqlMapper.xml` 引用了不存在的包路径 `modules.common.entity.SqlParam`
- `BaseSourceConf.subTypes` 缺少 `SqlSourceConf.class`
- 后端无集成测试/DAO 测试
