# 变更日志 - 2026-06-16

## 概述
前端 UI 框架从 Vuetify 3 迁移至 Naive UI，完成全部 9 个组件、8 个测试文件的迁移，共 125 个测试用例通过。

## 变更详情

### 阶段一：前端应用搭建（commit: 794498e）

初始搭建基于 Vuetify 3 的完整前端应用，实现数据源与 API 管理的 CRUD 功能。

#### 新增文件（48个）

##### 基础设施
- `frontend/package.json` — 添加 Vuetify 3、@mdi/font、sass、axios 等依赖
- `frontend/vite.config.ts` — 配置 Vite 代理解决跨域
- `frontend/src/main.ts` — 挂载 Vuetify 插件

##### API 层
- `frontend/src/api/client.ts` — Axios 实例封装，统一请求/响应拦截
- `frontend/src/api/dataapi.ts` — DataAPI 增删改查 API
- `frontend/src/api/datasource.ts` — 数据源增删改查 API
- `frontend/src/api/__tests__/client.spec.ts`、`dataapi.spec.ts`、`datasource.spec.ts`

##### 状态管理
- `frontend/src/stores/app.ts` — 全局状态（侧边栏、Snackbar）
- `frontend/src/stores/dataapi.ts` — DataAPI 列表/分页状态
- `frontend/src/stores/datasource.ts` — 数据源列表/分页状态
- `frontend/src/stores/__tests__/dataapi.spec.ts`、`datasource.spec.ts`

##### 类型定义
- `frontend/src/types/api.ts` — 通用 API 响应类型
- `frontend/src/types/dataapi.ts` — DataAPI 相关类型
- `frontend/src/types/datasource.ts` — 数据源相关类型
- `frontend/src/types/index.ts` — 类型导出入口

##### 路由
- `frontend/src/router/index.ts` — 数据源管理 + API 管理路由
- `frontend/src/router/__tests__/index.spec.ts`

##### 公共组件
- `frontend/src/components/ConfirmDialog.vue` — 通用确认对话框
- `frontend/src/components/EmptyState.vue` — 空状态展示
- `frontend/src/components/ErrorSnackbar.vue` — 错误提示条
- `frontend/src/components/StatusChip.vue` — 状态标签

##### 表单对话框
- `frontend/src/components/dataapi/DataApiFormDialog.vue` — API 注册/编辑表单
- `frontend/src/components/datasource/DataSourceFormDialog.vue` — 数据源注册/编辑表单

##### 页面视图
- `frontend/src/views/dataapi/DataApiView.vue` — API 管理列表页
- `frontend/src/views/datasource/DataSourceView.vue` — 数据源管理列表页

##### 测试文件（9个）
- 各类组件测试：`ConfirmDialog`、`EmptyState`、`ErrorSnackbar`、`StatusChip`
- 表单测试：`DataApiFormDialog`、`DataSourceFormDialog`
- 视图测试：`DataApiView`、`DataSourceView`
- App 测试：`App.spec.ts`

#### 删除文件（11个）
移除 Vue 脚手架默认文件：`HelloWorld.vue`、`TheWelcome.vue`、`WelcomeItem.vue`、`AboutView.vue`、`HomeView.vue`、`Icon*.vue` 系列、`counter.ts`、`assets/base.css`、`assets/logo.svg`

#### 修改文件
- `frontend/src/App.vue` — 重写为 Vuetify 布局（导航栏 + 侧边栏 + 内容区）
- `frontend/src/router/index.ts` — 移除默认路由，添加数据源/API 路由

#### 影响范围
- 新增：48 个文件
- 删除：11 个文件
- 修改：4 个文件
- 测试：133 个（ErrorSnackbar 等 5 个测试后因组件删除被移除）

---

### 阶段二：替换依赖（commit: f64ba43）

#### 变更内容
- `package.json`：Vuetify 3 + @mdi/font → Naive UI + @vicons/ionicons5
- `package-lock.json`：对应依赖更新
- 删除 `src/plugins/vuetify.ts`：移除 Vuetify 插件注册
- 更新 `src/main.ts`：移除 Vuetify 引用

---

### 阶段三：迁移 App.vue 布局与公共组件（commit: 048bf99）

#### App.vue 迁移
- Vuetify 布局（`v-app`、`v-navigation-drawer`、`v-app-bar`、`v-main`）→ Naive UI 布局（`n-layout`、`n-layout-header`、`n-layout-sider`、`n-layout-content`）
- 添加 Snackbar 桥接（`watch` 监听 store → `useMessage()` 显示通知）
- 菜单渲染函数适配 Naive UI `h()` 方式

#### 公共组件迁移
- `ConfirmDialog.vue`：`v-dialog` + `v-card` → `n-modal preset="card"`
- `EmptyState.vue`：Vuetify 图标 + 样式 → Naive UI 图标 + 内联样式
- `StatusChip.vue`：`v-chip` + `v-icon` → `n-tag` + `n-icon`
- `ErrorSnackbar.vue`：**删除**（功能由 store + `useMessage()` 替代）

#### 测试更新
- 更新 `ErrorSnackbar.spec.ts` → **删除**（减少 5 个测试）
- 更新 `DataApiView.spec.ts`、`DataSourceView.spec.ts` 的 stubs

#### 影响范围
- 修改：5 个组件 + 3 个视图测试
- 删除：`ErrorSnackbar.vue` + `ErrorSnackbar.spec.ts`
- 测试：133 → 128

---

### 阶段四：迁移对话框与视图（当前暂存）

Naive UI 迁移的剩余工作，包括表单对话框、视图组件、测试桩更新及 Snackbar 桥接 Bug 修复。

#### 表单对话框迁移

##### DataSourceFormDialog.vue（+82/-35）
- `v-dialog` → `n-modal preset="card"`
- `v-card` → 移除，使用 `n-modal` 自带卡片
- `v-form` + `v-text-field` + `v-select` → `n-form` + `n-input` + `n-select`
- `v-btn` → `n-button`
- 内联样式替换 Vuetify 工具类（`d-flex`、`mb-4` 等）

##### DataApiFormDialog.vue（+131/-178）
- `v-dialog` → `n-modal preset="card"`
- `v-textarea` → `n-input type="textarea"`
- `v-switch` → `n-switch`（使用 `checked-value`/`unchecked-value`）
- `v-select` → `n-select`
- 参数列表编辑器适配 Naive UI 表单
- 按钮 `v-btn` → `n-button`，`v-icon` → `n-icon`

#### 视图组件迁移

##### DataSourceView.vue（+57/-54）
- `v-tabs` + `v-tab` → `n-tabs` + `n-tab-pane`
- `v-data-table` → `n-data-table`
- `v-pagination` → `n-pagination`
- `v-progress-linear` → `n-spin`
- `v-select` → `n-select`
- `v-btn` + `v-icon` → `n-button` + `n-icon`
- 内联样式替换 Vuetify CSS 工具类

##### DataApiView.vue（+57/-54）
- 同上视图组件迁移模式

#### 组件测试桩更新
- `ConfirmDialog.spec.ts` — 更新 Naive UI stubs
- `EmptyState.spec.ts` — 更新 Naive UI stubs
- `StatusChip.spec.ts` — 更新 Naive UI stubs

#### 表单对话框测试更新
- `DataApiFormDialog.spec.ts` — 迁移至 Naive UI stubs
- `DataSourceFormDialog.spec.ts` — 迁移至 Naive UI stubs

#### 视图测试更新
- `DataApiView.spec.ts` — 迁移至 Naive UI stubs
- `DataSourceView.spec.ts` — 迁移至 Naive UI stubs

#### Bug 修复：Snackbar 桥接（+SnackbarBridge.vue）

**问题**：`useMessage()` 在 App.vue 的 `setup()` 中调用，但 `<n-message-provider />` 在 App.vue 的模板中声明，setup 执行时 provider 尚未渲染 → "No outer `<n-message-provider />` founded."

**修复**：
- 新建 `frontend/src/components/SnackbarBridge.vue` — 无渲染组件，将 `useMessage()` 调用 + Snackbar 监听器提取到 provider 子树内
- App.vue 模板用 `<SnackbarBridge>` 包裹内容
- `App.spec.ts` 添加 SnackbarBridge 桩组件

#### 配置与文档更新
- `.gitignore` — 添加 `.omo/`、`.playwright-mcp/`
- `backend/doc/architecture.md` — 更新后端架构文档（技术栈版本、模块结构）
- `frontend/README.md` — 更新项目结构、技术栈、命令说明

#### 影响范围
- 新增：`SnackbarBridge.vue`
- 修改：16 个文件（2 个对话框、2 个视图、5 个测试、App.vue、App.spec.ts、README 等）
- 测试：128 → 125（删除 2 个 EmptyState 图标 prop 测试 + 1 个 App Snackbar 测试）

## 验证结果

| 验证项 | 结果 |
|--------|------|
| TypeScript 类型检查 | ✅ 通过（0 错误） |
| 单元测试 | ✅ 125/125 通过（14 个文件） |
| 生产构建 | ✅ 构建成功 |
| Vuetify 残留 | ✅ 源代码中无 Vuetify 组件 |

## 全部变更统计

| 阶段 | 文件数 | 说明 |
|------|--------|------|
| 阶段一：前端搭建 | 56 | 完整 CRUD 前端（Vuetify 3） |
| 阶段二：替换依赖 | 4 | Vuetify → Naive UI |
| 阶段三：布局+公共组件迁移 | 10 | App.vue + 5 公共组件 |
| 阶段四（暂存）：对话框+视图迁移 | 17 | 剩余迁移 + Bug 修复 + 文档 |
| **总计** | **87** | **初始提交以来的全部变更** |
