---
name: nest-admin-system-module-migration
description: 将 nest-admin 的 system 模块迁移至 Spring Boot，含用户/角色/菜单/权限/字典/配置/公告/日志/License 等
metadata:
  type: project
---

# 需求：nest-admin system 模块迁移至 Spring Boot

## 需求描述

大王要求将 nest-admin（NestJS + TypeORM）项目中 `src/framework/system` 下的全部模块迁移至 kabuto-spring-boot（Spring Boot + MyBatis-Plus）。

## 解决方案

### 已完成

1. **Entity 实体层**（12 个实体）：SysUser / SysRole / SysMenu / SysPermission / SysLoginInfo / SysDictType / SysDictData / SysNotice / SysConfig / SysOperLog / SysLicense / SysLicenseDevice
2. **Mapper 数据访问层**（12 个 Mapper 接口 + 2 个 XML Mapper）
3. **数据库初始化脚本**（db/init.sql，含 15 张表 + 种子数据）
4. **Auth 认证模块**（AuthService + Impl + Controller + DTO + VO）

### 待迁移（分 5 期）

- **第一期（P0）**：用户管理 — Service + Controller + DTO + VO
- **第二期（P0）**：RBAC 管理 — 角色 / 菜单 / 权限
- **第三期（P1）**：字典 + 配置 + 公告
- **第四期（P1）**：登录日志 + 操作日志
- **第五期（P2）**：License 授权模块

### 新增基础设施

- `@RequirePermission` 注解 + `PermissionAspect` AOP 切面（权限校验）
- `@RequireSuperAdmin` 注解（超管校验）

## 技术映射

| NestJS | Spring Boot |
|--------|-------------|
| TypeORM Repository | MyBatis-Plus BaseMapper + IService |
| TypeORM DataSource.transaction() | Spring @Transactional |
| class-validator DTO | Jakarta Validation (@NotBlank, @NotNull, @Size) |
| @Req() req.userId | SecurityContext.getUserId()（ThreadLocal） |
| @RequirePermission | 自定义 @RequirePermission + AOP |
| @Root | 自定义 @RequireSuperAdmin + AOP |

## 原因说明

nest-admin 是大王已有的后台管理系统，kabuto-spring-boot 是其 Java 版本。Entity/Mapper/SQL 已就位，需补齐 Service/Controller/DTO/VO 三层。编码风格严格匹配项目现有规范（构造器注入、@Slf4j、Swagger @Schema、R<T> 统一响应等）。
