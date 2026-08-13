---
name: redis-docker-setup
description: Redis 7 Docker 本地开发环境配置需求记录
metadata:
  type: project
---

# Redis 7 Docker 本地开发环境配置

## 需求描述

大王要求为 Spring Boot 项目配置本地 Redis 缓存服务：
- 使用 Redis 7 稳定版本
- 数据持久化到本地 D 盘
- 密码：`kabuto@2026`

## 解决方案

### 技术选型

| 项目 | 选择 | 原因 |
|------|------|------|
| 镜像 | `redis:7` | Redis 7 是稳定版，社区版完全免费 |
| 部署方式 | Docker Compose | 与 MySQL 统一管理 |
| 数据持久化 | AOF + 本地卷映射 | `D:/DockerData/redis-kabuto/` |
| 内存策略 | `allkeys-lru` | 内存满时自动淘汰最少使用的键 |

### 关于收费问题

`redis:7` 是 Redis 官方发布的 **社区版（Community Edition）**，采用 BSD 开源协议，**完全免费**，可放心用于商业项目。Redis 企业版（Redis Enterprise）才需要付费。

### 生成的文件

- `docker/docker-compose.yml` — Docker Compose 主配置（与 MySQL 统一管理）
- `docker/.env` — 环境变量配置
- `docker/redis/README.md` — Redis 使用说明

### 数据持久化路径

```
D:/DockerData/redis-kabuto/
├── data/        # Redis 数据文件（AOF 持久化）
└── logs/        # Redis 运行日志
```

### 服务配置

| 配置项 | 值 |
|--------|-----|
| 容器名 | `kabuto-redis` |
| 端口 | `6379:6379` |
| 密码 | `kabuto@2026` |
| 最大内存 | 256MB |
| 内存淘汰策略 | allkeys-lru |

### Redis 持久化配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `appendonly` | `yes` | 开启 AOF 持久化 |
| `appendfsync` | `everysec` | 每秒同步一次，平衡性能与数据安全 |

## 操作命令

```bash
# 启动 Redis
cd docker && docker-compose up -d redis

# 停止 Redis
docker-compose stop redis

# 查看日志
docker-compose logs -f redis

# 进入 Redis CLI
docker exec -it kabuto-redis redis-cli -a kabuto@2026
```

## 原因说明

1. **选择 AOF 持久化**：比 RDB 更安全，每秒同步一次，几乎不会丢失数据
2. **内存限制 256MB**：本地开发环境足够使用，防止内存无限增长
3. **allkeys-lru 淘汰策略**：内存满时自动淘汰最近最少使用的键，适合缓存场景
4. **与 MySQL 统一管理**：通过同一个 docker-compose.yml 管理，方便维护
5. **Healthcheck 配置**：确保 Redis 完全启动后再对外提供服务

## 关联需求

- [[mysql-docker-setup]] — MySQL Docker 配置
- [[add-web-support]] — Web 支持
- [[mybatis-plus-integration]] — MyBatis-Plus 集成
