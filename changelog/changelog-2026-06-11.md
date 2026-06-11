# 变更日志 - 2026-06-11

## 概述
移除项目中所有不必要的 `@Autowired` 注解及其对应的 import 语句。

## 问题背景
在 Spring 4.3+ 版本中，如果一个类只有一个构造函数，Spring 会自动进行构造函数注入，不需要显式添加 `@Autowired` 注解。这是一个代码优化特性，用于减少样板代码。

## 变更详情

### 1. 移除不必要的 @Autowired 注解（21个文件）

#### Service 层（2个文件）
- `backend/src/main/java/com/wayne/apihub/service/DataApiConfService.java`
- `backend/src/main/java/com/wayne/apihub/service/DataSourceConfService.java`

#### Factory 层（7个文件）
- `backend/src/main/java/com/wayne/apihub/modules/query/sql/SqlPageHelperFactory.java`
- `backend/src/main/java/com/wayne/apihub/modules/dataapi/factory/HbaseApiHandlerFactory.java`
- `backend/src/main/java/com/wayne/apihub/modules/dataapi/factory/SolrApiHandlerFactory.java`
- `backend/src/main/java/com/wayne/apihub/modules/dataapi/factory/SqlApiHandlerFactory.java`
- `backend/src/main/java/com/wayne/apihub/modules/datasource/factory/HbaseClientFactory.java`
- `backend/src/main/java/com/wayne/apihub/modules/datasource/factory/SolrClientFactory.java`
- `backend/src/main/java/com/wayne/apihub/modules/datasource/factory/SqlClientFactory.java`

#### Handler 层（3个文件）
- `backend/src/main/java/com/wayne/apihub/modules/query/handler/SqlQueryHandler.java`
- `backend/src/main/java/com/wayne/apihub/modules/query/handler/HbaseQueryHandler.java`
- `backend/src/main/java/com/wayne/apihub/modules/query/handler/SolrQueryHandler.java`

#### QueryController 层（3个文件）
- `backend/src/main/java/com/wayne/apihub/modules/query/controller/SqlQueryController.java`
- `backend/src/main/java/com/wayne/apihub/modules/query/controller/HbaseQueryController.java`
- `backend/src/main/java/com/wayne/apihub/modules/query/controller/SolrQueryController.java`

#### ApiController 层（3个文件）
- `backend/src/main/java/com/wayne/apihub/modules/dataapi/controller/HbaseApiController.java`
- `backend/src/main/java/com/wayne/apihub/modules/dataapi/controller/SolrApiController.java`
- `backend/src/main/java/com/wayne/apihub/modules/dataapi/controller/SqlApiController.java`

#### SourceController 层（3个文件）
- `backend/src/main/java/com/wayne/apihub/modules/datasource/controller/HbaseSourceController.java`
- `backend/src/main/java/com/wayne/apihub/modules/datasource/controller/SolrSourceController.java`
- `backend/src/main/java/com/wayne/apihub/modules/datasource/controller/SqlSourceController.java`

### 2. 移除未使用的 import 语句（4个文件）
在移除 `@Autowired` 注解后，以下文件中留下了未使用的 import 语句，已全部清理：
- `backend/src/main/java/com/wayne/apihub/modules/dataapi/controller/SqlApiController.java`
- `backend/src/main/java/com/wayne/apihub/modules/dataapi/controller/SolrApiController.java`
- `backend/src/main/java/com/wayne/apihub/modules/datasource/controller/SolrSourceController.java`
- `backend/src/main/java/com/wayne/apihub/modules/datasource/controller/SqlSourceController.java`

## 修改内容

每个文件都进行了以下修改：
1. **移除构造函数上的 `@Autowired` 注解**
   ```java
   // 修改前
   @Autowired
   public ClassName(Dependency dep) {
       this.dep = dep;
   }

   // 修改后
   public ClassName(Dependency dep) {
       this.dep = dep;
   }
   ```

2. **移除对应的 import 语句**
   ```java
   // 移除
   import org.springframework.beans.factory.annotation.Autowired;
   ```

## 影响范围
- 修改文件总数：25个
- 移除注解数：21个
- 移除 import 数：25个

## 兼容性说明
✅ 代码在 Spring 4.3+ 环境下仍能正常工作，因为 Spring 会自动识别唯一构造函数并进行依赖注入。

## 验证结果
- ✅ 项目中已不存在任何不必要的 `@Autowired` 注解
- ✅ 项目中已不存在任何未使用的 `org.springframework.beans.factory.annotation.Autowired` import

---

## 补充变更：移除废弃常量

### 变更概述
移除项目中标记为 `@Deprecated` 的旧常量声明及其所有引用。

### 变更详情

#### 1. 删除废弃常量声明（Constants.java）
移除了以下4个标记为 `@Deprecated` 的常量别名：
- `STRING` → 已删除
- `NUMERIC` → 已删除  
- `ORACLE` → 已删除
- `MYSQL` → 已删除

#### 2. 替换废弃常量引用（JsonUtils.java）
```java
// 修改前
if (Constants.NUMERIC.equals(sqlParam.getType()))

// 修改后
if (Constants.PARAM_TYPE_NUMERIC.equals(sqlParam.getType()))
```

#### 3. 清理未使用的 import（ResponseUtils.java）
删除了未使用的 import 语句：
```java
import com.wayne.apihub.utils.Constants;
```

#### 4. 更新历史文档（changelog-2026-06-08.md）
更新了关于旧常量向后兼容的说明。

### 影响范围
- 修改文件数：4个
- 删除常量声明：4个
- 删除 import：1个
- 替换引用：1处

### 验证结果
- ✅ 项目中已不存在任何对废弃常量（`Constants.STRING`、`Constants.NUMERIC`、`Constants.ORACLE`、`Constants.MYSQL`）的引用