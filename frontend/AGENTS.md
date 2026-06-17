# FRONTEND KNOWLEDGE BASE

## OVERVIEW
Frontend — Vue 3 + TypeScript 6 management UI for data platform APIs. Vite 8, Pinia 3, Naive UI, Vitest 4.

## STRUCTURE
```
src/
├── api/
│   ├── client.ts          # Axios instance + unwrap-BaseResponse interceptor
│   ├── dataapi.ts         # listHbaseApis, registerSqlApi, enable/disable per type
│   ├── datasource.ts      # listSources, register/updateSource per type
│   └── __tests__/
├── components/
│   ├── ConfirmDialog.vue   # NModal preset="card" confirmation
│   ├── EmptyState.vue      # Table placeholder
│   ├── SnackbarBridge.vue  # Watches app store → useMessage()
│   ├── StatusChip.vue      # Colored status tag
│   ├── dataapi/
│   │   ├── DataApiFormDialog.vue    # Tabbed form: hbase | solr | sql sections
│   │   └── __tests__/
│   ├── datasource/
│   │   ├── DataSourceFormDialog.vue # Dynamic fields per type
│   │   └── __tests__/
│   └── __tests__/
├── router/index.ts         # 3 routes: / → /datasource, /datasource, /dataapi
├── stores/
│   ├── app.ts              # drawerOpen, snackbar state
│   ├── dataapi.ts          # Record<ApiType, Function> dispatch × 4 ops
│   ├── datasource.ts       # Same dispatch pattern
│   └── __tests__/
├── types/
│   ├── api.ts              # BaseResponse<T>, PageData<T>, BasePageRequest
│   ├── dataapi.ts          # BaseApiConf → HbaseApiConf|SolrApiConf|SqlApiConf, ApiType
│   ├── datasource.ts       # BaseSourceConf → HbaseSourceConf|SolrSourceConf|SqlSourceConf
│   └── index.ts            # Barrel re-exports
├── views/
│   ├── dataapi/DataApiView.vue     # NDataTable + type tabs
│   └── datasource/DataSourceView.vue
├── App.vue
├── main.ts
└── __tests__/App.spec.ts
```

## PATTERNS

- **Polymorphic dispatch**: Stores use `Record<UnionType, Function>` lookup maps per operation. Functions dispatch through these maps, no if/else chains. Each domain store defines separate maps for list, register, enable, disable.
- **Discriminated inheritance**: `BaseApiConf → HbaseApiConf | SolrApiConf | SqlApiConf`. Union type `ApiType = 'hbase' | 'solr' | 'sql'` keys all dispatch maps. Same pattern for datasource types.
- **Form dialogs**: `NModal preset="card" + NForm` with validation rules. Dynamic sections toggle based on selected type tab.
- **Snackbar**: `app.ts` store holds snackbar state (`{show, message, color}`) → `SnackbarBridge.vue` watches store → proxies to Naive UI `useMessage()`.
- **Tests**: Co-located `__tests__/*.spec.ts`. Vitest + Vue Test Utils + jsdom. `beforeEach: setActivePinia(createPinia())`. Mock naive-ui components + API modules.

## CONVENTIONS

- **Formatting**: no semicolons, single quotes, 2-space indent, LF. Prettier + EditorConfig.
- **Linting**: oxlint (Rust, fast) then ESLint (Vue essential + TS recommended), both auto-fix. Run via `npm-run-all2`.
- **Path alias**: `@/` maps to `./src/`. Always `@/components/Foo`, never relative.
- **noUncheckedIndexedAccess**: true. Guarded access required: `apis.value[type]!` or early return.
- **Naming**: Components `PascalCase.vue`, scripts `camelCase.ts`, tests `{name}.spec.ts`.
- **Generics**: `BaseResponse<T>`, `PageData<T>`. `import type` consistently.

## KNOWN ISSUES

- `src/plugins/` is empty but listed in frontend README tree
- `src/stores/app.ts` missing from frontend README tree
- `listApi`/`listSource` called with GET params but backend expects POST bodies — verify Vite proxy path before fixing
- No type guards for discriminated union fields: accessing `.tableName` on `BaseApiConf` requires explicit cast

## WHERE TO ADD

| Item | Location | Action |
|------|----------|--------|
| New view | `views/{entity}/XxxView.vue` | + route in `router/index.ts` |
| New store | `stores/{entity}.ts` | Composition API + Record dispatch maps per operation |
| New API module | `api/{entity}.ts` | Named exports using client from `client.ts` |
| New component | `components/{Entity}FormDialog.vue` (domain) or `components/` (shared) |  |
| New types | `types/{entity}.ts` | Base + extensions + union type. Re-export in `types/index.ts` |
| New test | `{module}/__tests__/{name}.spec.ts` | Co-located with source |
