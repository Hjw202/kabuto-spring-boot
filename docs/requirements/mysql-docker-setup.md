---
name: mysql-docker-setup
description: MySQL 8.0 Docker 本地开发环境配置需求记录
metadata:
  type: project
---

# MySQL 8.0 Docker 本地开发环境配置

## 需求描述

大王要求为 Spring Boot 项目配置本地 MySQL 8 开发数据库：
- 使用 MySQL 8 稳定版本
- 数据持久化到本地 D 盘
- 账号名：`kabuto`，密码：`kabuto@2026`

## 解决方案

### 技术选型

| 项目 | 选择 | 原因 |
|------|------|------|
| 镜像 | `mysql:8.0` | MySQL 8.0 是稳定版，社区版完全免费 |
| 部署方式 | Docker Compose | 便于管理和维护，可与 Redis 统一管理 |
| 数据持久化 | 本地卷映射 | `D:/DockerData/mysql-kabuto/` |
| 认证插件 | `mysql_native_password` | 兼容更多客户端和驱动 |

### 关于收费问题

`mysql:8.0` 是 Oracle 发布的 **社区版（Community Edition）**，采用 GPL 开源协议，**完全免费**，可放心用于商业项目。只有企业版（Enterprise Edition）才需要付费。

### 生成的文件

- `docker/docker-compose.yml` — Docker Compose 主配置
- `docker/.env` — 环境变量配置
- `docker/init-scripts/mysql-init.sql` — MySQL 初始化脚本
- `docker/mysql/README.md` — MySQL 使用说明

### 数据持久化路径

```
D:/DockerData/mysql-kabuto/
├── data/        # 数据库数据文件
└── logs/        # MySQL 运行日志
```

### 服务配置

| 配置项 | 值 |
|--------|-----|
| 容器名 | `kabuto-mysql` |
| 端口 | `3306:3306` |
| 数据库 | `kabuto_db` |
| 用户名 | `kabuto` |
| 密码 | `kabuto@2026` |
| Root 密码 | `kabuto@2026` |

## 操作命令

```bash
# 启动 MySQL
cd docker && docker-compose up -d mysql

# 停止 MySQL
docker-compose stop mysql

# 查看日志
docker-compose logs -f mysql
```

## 原因说明

1. **选择 Docker Compose**：统一管理多个服务（MySQL + Redis），方便启动/停止/维护
2. **数据持久化到本地**：避免容器删除后数据丢失，D 盘空间充足
3. **mysql_native_password 认证插件**：兼容 Spring Boot 的 JDBC 驱动，避免连接问题
4. **UTF-8MB4 字符集**：支持完整的 Unicode 字符（包括 emoji）
5. **Healthcheck 配置**：确保 MySQL 完全启动后再对外提供服务

## 关联需求

- [[redis-docker-setup]] — Redis Docker 配置
- [[add-web-support]] — Web 支持
- [[mybatis-plus-integration]] — MyBatis-Plus 集成
