---
name: mybatis-plus-integration
description: 接入 MyBatis Plus ORM 框架，集成 MySQL 数据库和代码生成器
metadata:
  type: project
---

# 需求：接入 MyBatis Plus

## 需求描述

大王要求为 kabuto-spring-boot 项目接入 MyBatis Plus ORM 框架，连接 MySQL 数据库，并集成代码生成器以提高开发效率。

## 解决方案

1. **依赖引入**：
   - `mybatis-plus-spring-boot3-starter:3.5.9` — Spring Boot 3.x 专用 starter
   - `mybatis-plus-jsqlparser:3.5.9` — **3.5.9+ 分页插件必需**，`PaginationInnerInterceptor` 从核心包分离至此
   - `druid-spring-boot-3-starter:1.2.23` — Druid 连接池（Spring Boot 3 适配版）
   - `mysql-connector-j` — MySQL 驱动
   - `mybatis-plus-generator:3.5.9`（test scope）— 代码生成器
   - `freemarker`（test scope）— 代码生成器模板引擎

2. **连接池**：排除 MP starter 自带的 HikariCP，使用 **Druid** 连接池，配置监控和统计功能
   - `application.yml` 中配置数据源和 MyBatis Plus 参数
   - 启用 `map-underscore-to-camel-case` 下划线转驼峰
   - 配置逻辑删除字段 `deleted`
   - 配置数据库自增主键 `id-type: auto`

3. **分页插件**：创建 `MybatisPlusConfig.java`，注册 `MybatisPlusInterceptor`，添加 `PaginationInnerInterceptor(DbType.MYSQL)`

4. **Mapper 扫描**：启动类添加 `@MapperScan("com.kabuto.cloud.mapper")`

5. **代码生成器**：在 `src/test` 下创建 `CodeGenerator.java`，基于 `FastAutoGenerator` + Freemarker 模板引擎

## 原因说明

| 决策 | 理由 |
|------|------|
| `mybatis-plus-spring-boot3-starter` | Spring Boot 3.x 基于 Jakarta EE 命名空间，必须使用 boot3 专用 starter，普通 starter 不兼容 |
| 代码生成器 scope = test | 代码生成器仅在开发阶段使用，不应打包进生产环境 |
| `druid-spring-boot-3-starter` | 大王指定使用 Druid，相比 HikariCP 提供了更丰富的监控和 SQL 防注入功能 |
| `@MapperScan` 统一扫描 | 相比在每个 Mapper 上加 `@Mapper`，统一扫描更简洁、不易遗漏 |
| `MybatisPlusInterceptor` | MyBatis Plus 3.5.x 官方推荐的新分页方式，替代已废弃的 `PaginationInterceptor` |
| Freemarker 模板引擎 | MyBatis Plus 代码生成器官方支持的模板引擎之一，社区使用广泛 |

## 注意事项

- `application.yml` 和 `CodeGenerator.java` 中的数据库密码需大王自行修改为实际值
- 数据库 `kabuto_db` 需事先创建
- 代码生成器运行前需确保目标表已存在于数据库中
