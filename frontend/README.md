# API HUB — Frontend

Vue 3 前端项目，为 API HUB 后端提供管理界面，支持数据源和 API 的注册、管理功能。

## 技术栈

- **构建工具**: Vite 8
- **框架**: Vue 3 + TypeScript 6
- **UI 组件库**: Naive UI
- **图标**: @vicons/ionicons5
- **路由**: Vue Router 5
- **状态管理**: Pinia 3
- **HTTP 客户端**: Axios
- **测试**: Vitest + Vue Test Utils + jsdom
- **代码规范**: ESLint + Prettier + oxlint
- **类型检查**: vue-tsc

## 项目结构

```
frontend/
├── src/
│   ├── __tests__/         # App 级测试
│   ├── api/                # API 请求层
│   ├── assets/             # 静态资源
│   ├── components/         # 公共组件
│   │   ├── __tests__/      # 组件测试
│   │   ├── dataapi/        # DataAPI 表单对话框
│   │   └── datasource/     # DataSource 表单对话框
│   ├── plugins/            # 插件注册
│   ├── router/             # 路由配置
│   ├── stores/             # Pinia 状态管理
│   ├── types/              # TypeScript 类型定义
│   ├── views/              # 页面视图
│   │   ├── dataapi/        # API 管理页面
│   │   └── datasource/     # 数据源管理页面
│   ├── App.vue             # 根组件
│   └── main.ts             # 入口文件
├── public/                 # 公共资源
├── package.json
├── vite.config.ts
├── vitest.config.ts
├── tsconfig*.json
└── eslint.config.ts
```

## 环境要求

- Node.js >= 20.19.0 或 >= 22.12.0

## 快速开始

```sh
npm install
npm run dev
```

## 脚本命令

| 命令 | 说明 |
|------|------|
| `npm run dev` | 启动开发服务器 |
| `npm run build` | 类型检查 + 生产构建 |
| `npm run build-only` | 仅生产构建（跳过类型检查） |
| `npm run preview` | 预览生产构建 |
| `npm run type-check` | TypeScript 类型检查 |
| `npm run test:unit` | 运行单元测试（Vitest） |
| `npm run lint` | 运行全部代码检查（oxlint + eslint） |
| `npm run lint:oxlint` | oxlint 代码检查 + 自动修复 |
| `npm run lint:eslint` | ESLint 代码检查 + 自动修复 |
| `npm run format` | Prettier 代码格式化 |

## 测试

单元测试使用 Vitest + Vue Test Utils，运行于 jsdom 环境：

```sh
npm run test:unit
```

测试文件与源码一一对应，位于各模块的 `__tests__/` 目录下。

## 代码规范

项目使用 ESLint + oxlint + Prettier 三重检查：

- **oxlint**: 快速 Rust 实现的代码检查
- **ESLint**: 全面的 Vue/TypeScript 代码检查
- **Prettier**: 代码格式化
