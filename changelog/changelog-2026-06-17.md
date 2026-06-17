# 变更日志 - 2026-06-17

## 概述
全面修复 api-hub 项目配置错误与代码缺陷，覆盖后端 MyBatis/HikariCP/Controller 配置、前端 TypeScript/API 调用层、以及前后端接口对齐问题，确保项目可正常编译运行。

## 变更详情

### 后端：配置修复

#### application.yml（+13/-12）
- **HikariCP 属性名修正**：12 个无效属性（如 `max-lifetime`、`idle-timeout`）全部替换为 HikariCP 兼容名（`maximumPoolSize`、`minimumIdle`、`maxLifetime` 等）
- **Vite proxy target**：追加 `/api_hub` context-path 前缀匹配后端配置

#### mybatis-config.xml（+10/-11）
- 移除 7 个冗余的 `<typeAliases>` 扫描（已有 Spring 自动扫描）
- 新增 5 个推荐 `<settings>`：`mapUnderscoreToCamelCase=true` 开启驼峰映射、`logImpl=SLF4J` 启用 SQL 日志、`aggressiveLazyLoading=false` 等

#### pom.xml（+1/-1）
- 项目版本 `0.0.1-SNAPSHOT` → `0.0.1`（稳定发布版本）

#### 后端冗余文件删除
- `backend/LICENSE` — 移除（与根目录 LICENSE 重复的 Apache 2.0 许可证全文）
- `backend/doc/architecture.md` — 移除（过时的架构设计文档，当前代码结构与文档描述不一致）

---

### 后端：MyBatis Mapper XML 修复（5个文件）

##### DataApiSqlMapper.xml（+7/-5）
- `resultMap type`：`SqlParam` → 完整类路径 `com.wayne.apihub.modules.query.sql.SqlParam`
- `insertSqlParam parameterType`：补充完整类路径
- `updateSqlApiStatus`：`<set>` 内缺失逗号导致 SQL 语法错误 — 补充
- `type` 列名：MySQL 保留字加反引号 `\`type\``

##### DataApiHbaseMapper.xml（+6/-4）
- `updateHbaseApiStatus`：`<set>` 内缺失逗号 — 补充
- SELECT 补充 5 个缺失字段（`param_json`、`status`、`create_time`、`update_time`、`creator`）
- `columns` 列名：MySQL 保留字加反引号

##### DataApiSolrMapper.xml（+7/-3）
- `updateSolrApiStatus`：`<set>` 内缺失逗号 — 补充
- SELECT 补充 8 个缺失字段

##### DataSourceSolrMapper.xml（+4/-4）
- 所有 `zk_chroots` 列名统一为单数 `zk_chroot`（resultMap、INSERT、SELECT）

##### DataApiSolrDao.java（+1/-1）
- `@Param("solrApiInfo")` → `@Param("solrApiConf")`——与 Mapper XML 中 `#\{solrApiConf\}` 的引用名对齐

#### init.sql（+19/-19）
- 删除 `data_source_mysql` / `data_api_mysql` 死表（对应已移除的 SQL Connector 模块）
- `zk_chroots` 列名统一为单数 `zk_chroot`

---

### 后端：Controller `@ModelAttribute` 移除（9个文件）

**问题**：所有 POST 接口同时声明 `@ModelAttribute @RequestBody`，Spring 优先使用 `@ModelAttribute` 从请求参数解析而非从 JSON body 反序列化 → 前端发 JSON 请求体时后端收到空对象。

**修复**：9 个 Controller 共 18 处 `@ModelAttribute @RequestBody` → `@RequestBody`

| Controller | 改动方法数 |
|------------|-----------|
| HbaseApiController | 1 |
| SolrApiController | 1 |
| SqlApiController | 1 |
| HbaseSourceController | 2 |
| SolrSourceController | 2 |
| SqlSourceController | 2 |
| HbaseQueryController | 3 |
| SolrQueryController | 3 |
| SqlQueryController | 3 |

---

### 后端：PageHelper 结果序列化修复（2个 Service）

**问题**：`Page<T>` 继承 `ArrayList`，Jackson 将其序列化为 JSON 数组 `[{...}]`，而非前端期望的分页对象 `{ list, total }`。前端访问 `response.data.data.list` 得到 `undefined`。

**修复**：两个 Service 的 6 个 list 方法将 `Page` 对象通过 `toPageData()` 转换为 `{ list, total, pages, pageNum, pageSize }` 结构的 `Map` 后再返回。

- `DataSourceConfService.java`（+22）：3 个 `list*SourceConfs` 方法 + `toPageData()` 辅助方法
- `DataApiConfService.java`（+22）：3 个 `list*ApiConfs` 方法 + `toPageData()` 辅助方法

---

### 前端：API 调用层 GET → POST（6个文件）

**问题**：后端列表接口为 `@PostMapping` + `@RequestBody`，前端却用 `GET` + `params` 调用 → 请求参数不在 body 中，后端 `@RequestBody` 收不到参数。

**修复**：

##### API 层（2个）
- `frontend/src/api/datasource.ts`：3 个 `listSource` 接口 GET + params → POST + body
- `frontend/src/api/dataapi.ts`：3 个 `listApi` 接口 GET + params → POST + body

##### 测试文件（2个）
- `datasource.spec.ts`：3 个测试 mockGet → mockPost
- `dataapi.spec.ts`：3 个测试 mockGet → mockPost

---

### 前端：配置修复（4个文件）

- `package.json`（+2/-2）：版本 `0.0.0` → `0.0.1`；移除 `dev` 命令中的 `--experimental-cli` 废弃标志
- `tsconfig.vitest.json`（+1/-1）：`"lib": []` → `"lib": ["ESNext", "DOM", "DOM.Iterable"]`（修复 Vitest 类型检查失败）
- `tsconfig.node.json`（+0/-2）：移除 `cypress.config.*` / `playwright.config.*` 不存在条目（对应文件已不存在）
- `vite.config.ts`（+1/-1）：proxy target `http://localhost:8080` → `http://localhost:8080/api_hub`（与后端 context-path 对齐）

---

### 基础设施

#### .gitignore（+4/-6）
- 移除 `.mvn/wrapper/maven-wrapper.jar` 冗余规则
- 移除 5 条 npm/yarn/pnpm debug 日志模式
- 移除 `cypress/videos/` / `cypress/screenshots/`（项目中不含 Cypress 配置）

#### AGENTS.md（新增 4 个未跟踪文件）
- `AGENTS.md`、`frontend/AGENTS.md`、`backend/AGENTS.md` — OpenCode 项目知识库自动生成文件

---

## 文件变更统计

| 分类 | 文件数 | 说明 |
|------|--------|------|
| 后端配置 | 4 | application.yml、mybatis-config.xml、pom.xml、.gitignore |
| 后端 Mapper XML | 5 | 4 个 Mapper XML + 1 个 DAO Java |
| 后端 Controller | 9 | 9 个 Controller 移除 @ModelAttribute |
| 后端 Service | 2 | 2 个 Service 修复分页序列化 |
| 数据库脚本 | 1 | init.sql |
| 后端冗余文件 | 2 | LICENSE、architecture.md（删除） |
| 前端 API 层 | 2 | dataapi.ts、datasource.ts |
| 前端测试 | 2 | dataapi.spec.ts、datasource.spec.ts |
| 前端配置 | 4 | package.json、vite.config.ts、tsconfig.* |
| 项目知识库 | 3 | AGENTS.md 系列（未跟踪） |
| **总计** | **34** | **修改 31 + 新增未跟踪 3** |

## 验证结果

| 验证项 | 结果 |
|--------|------|
| 后端编译（`mvn clean package`） | ✅ 通过 |
| 前端类型检查 | 待验证 |
| 前端单元测试 | 待验证 |
| 前端生产构建 | 待验证 |

## 已知遗留问题
- 数据库密码 `api_hub@2021` 仍硬编码在 `application.yml` 中
- `BaseResponse` 响应拦截器未自动解包：前端通过 `response.data.data.list` 手动取数据
- `BasePageRequest` 字段 `@NonNull` + `@NoArgsConstructor(force = true)` 不会生成 null 检查，反序列化失败时仍会 NPE
- 后端无任何单元测试
- `enableApi`/`disableApi` 使用 GET 方式修改状态（应为 POST/PUT）
