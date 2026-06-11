# API HUB

统一管理企业内部数据平台 API，支持 API 注册、授权、监控等功能。

## 项目结构

```
api-hub/
├── backend/          # Spring Boot 后端 (Maven + Java 17)
│   ├── src/
│   ├── pom.xml
│   └── ...
├── frontend/         # Vue 3 前端 (Vite + TypeScript)
│   ├── src/
│   ├── package.json
│   └── ...
└── changelog/        # 变更日志
```

## 快速开始

### 后端

```bash
cd backend
mvn clean package
java -jar target/api-hub-*.jar
```

详见 [backend/README.md](./backend/README.md)。

### 前端

```bash
cd frontend
npm install
npm run dev
```

详见 [frontend/README.md](./frontend/README.md)。
