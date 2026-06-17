# PROJECT KNOWLEDGE BASE

**Generated:** 2026-06-17
**Commit:** ed0015b
**Branch:** master

## OVERVIEW

api-hub — Internal enterprise data platform API management. Supports unified registration, authorization, and monitoring of data APIs across SQL (MySQL/Oracle), Solr, and HBase data sources.

**Stack:** Java 17 + Spring Boot 4 + Vue 3 + TypeScript 6 + MySQL.

## STRUCTURE

```
api-hub/
├── backend/           # Spring Boot + Maven (Java 17)
│   └── src/main/java/com/wayne/apihub/
│       ├── config/       # CORS + OpenAPI config
│       ├── dao/          # MyBatis @Mapper interfaces (6)
│       ├── model/        # Base entity + response classes
│       ├── modules/      # 3 domains: dataapi, datasource, query
│       ├── service/      # Facade services (DataSourceConfService, DataApiConfService)
│       └── utils/        # Constants, JsonUtils, ResponseUtils
├── frontend/          # Vue 3 + Vite + TypeScript
│   └── src/
│       ├── api/          # Axios HTTP layer
│       ├── components/     # Reusable & domain-specific Vue components
│       ├── router/       # Vue Router config (2 routes)
│       ├── stores/       # Pinia state (app, dataapi, datasource)
│       ├── types/        # TypeScript interfaces & types
│       └── views/        # Page views (DataApiView, DataSourceView)
└── changelog/         # Daily changelog markdown files
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Add new data source type | `backend/.../modules/datasource/` | Follow factory pattern: AbstractClientFactory → {Type}ClientFactory |
| Add new API type | `backend/.../modules/dataapi/` | Follow factory pattern: AbstractApiHandlerFactory → {Type}ApiHandlerFactory |
| Add query support | `backend/.../modules/query/` | Direct @Service handlers, no factory pattern |
| Add REST endpoint | `backend/.../modules/{domain}/controller/` | Copy try-catch template from existing controllers |
| Add frontend page | `frontend/src/views/{entity}/` | Create view + subcomponents + store + api + types |
| Add Pinia store | `frontend/src/stores/{entity}.ts` | Use Composition API store pattern |
| Write frontend test | `frontend/src/{module}/__tests__/*.spec.ts` | Vitest + Vue Test Utils, co-located in `__tests__/` |
| Add MyBatis mapper | `backend/src/main/resources/mybatis/mappers/` | + matching DAO in `dao/` |

## CONVENTIONS

### Backend (Java/Spring Boot)
- **Controllers**: `@Tag` + `@Slf4j` + `@RestController` + `@RequestMapping("/v1/{domain}/{type}")`. All return `BaseResponse`. Constructor injection.
- **Error handling**: try-catch with `BaseResponse.ok()/.bad()/.error()`. No `@ControllerAdvice`.
- **Models**: Lombok `@Data` + `@ToString(callSuper=true)` + inheritance from `BaseDataObject`.
- **DAO**: `@Mapper` interfaces with `@Param` on all parameters. XML mappers `useGeneratedKeys=true`.
- **Services**: Concrete classes with `@Service`. No interfaces. No `@Transactional`.
- **Factories**: Template Method pattern (`AbstractClientFactory`/`AbstractApiHandlerFactory`) with `ConcurrentHashMap` for stateful lifecycle management.
- **URL pattern**: `/v1/{domain}/{type}/{action}` — e.g., `/v1/source/sql/insertSource`, `/v1/api/hbase/enableApi`.

### Frontend (Vue 3/TypeScript)
- **Components**: `<script setup lang="ts">`. Type-only `defineProps`/`defineEmits`. No Options API. No scoped CSS.
- **Stores**: Pinia Composition API (`defineStore('id', () => {...})`). Polymorphic dispatch via `Record<UnionType, Function>` maps.
- **API layer**: Named export functions → Axios instance with response interceptor (unwraps `BaseResponse`). Vite proxy `/v1` → `localhost:8080`.
- **Tests**: Vitest + Vue Test Utils + jsdom. Co-located `__tests__/*.spec.ts`. Mock naive-ui + API modules.
- **Formatting**: No semicolons, single quotes, 2-space indent, LF line endings. Prettier + EditorConfig enforced.
- **Linting**: Dual pipeline — `oxlint` (fast Rust) then `ESLint` (Vue essential + TS recommended).
- **Types**: String literal unions over enums. `import type` consistently. Generics: `BaseResponse<T>`, `PageData<T>`.
- **Path alias**: `@/` maps to `./src/`.

## ANTI-PATTERNS (THIS PROJECT)

- **No `any` types** — `noUncheckedIndexedAccess` is enabled. Use proper interfaces.
- **No empty catch blocks** — every catch must log or handle the error.
- **No `@ModelAttribute @RequestBody` together** — `@ModelAttribute` is redundant with `@RequestBody`.
- **No hardcoded credentials** — DB passwords in `application.yml` are plaintext; must be extracted to env vars.
- **No GET for state changes** — `enableApi`/`disableApi` use GET; should be POST/PUT.

## UNIQUE STYLES

- Stateful in-memory client/handler management via `ConcurrentHashMap` in abstract factories.
- Factories use `@PostConstruct init()` to restore state from DB on startup.
- Query module is architecturally distinct: uses direct `@Service` handlers instead of factory pattern.
- Frontend uses `Record<UnionType, Function>` dispatch maps to avoid if/else chains across data source types.
- `noUncheckedIndexedAccess: true` in TypeScript — guarded index access required.
- Dual linter pipeline: oxlint (Rust, fast) + ESLint (comprehensive) run sequentially via `npm-run-all2`.

## COMMANDS

```bash
# Backend
cd backend && mvn clean package

# Frontend
cd frontend && npm install && npm run dev    # dev server on :5173
npm run build                                 # type-check + production build
npm run test:unit                             # Vitest unit tests
npm run lint                                  # oxlint + ESLint
npm run format                                # Prettier
```

## NOTES

- Vite dev proxy forwards `/v1/*` → `http://localhost:8080` (backend context path is `/api_hub` — verify proxy target if API calls fail).
- `DataApiSqlMapper.xml` references `com.wayne.apihub.modules.common.entity.SqlParam` — this package does NOT exist. Actual class is at `modules.query.sql.SqlParam`. Fix before running.
- `BaseSourceConf.subTypes` is missing `SqlSourceConf.class` (only declares HBase/Solr).
- No Docker, no CI/CD, no backend tests exist.
- All dependency versions are cutting-edge (Spring Boot 4, Vue Router 5, Vite 8, TypeScript 6).
