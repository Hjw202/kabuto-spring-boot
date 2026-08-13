# Redis 7 Docker 本地开发环境

## 服务信息

| 配置项 | 值 |
|--------|-----|
| 镜像 | `redis:7`（社区版，免费） |
| 容器名 | `kabuto-redis` |
| 端口 | `6379:6379` |
| 密码 | `kabuto@2026` |

## 数据持久化路径

```
D:/DockerData/redis-kabuto/
├── data/        # Redis 数据文件（AOF 持久化）
└── logs/        # Redis 运行日志
```

## 配置说明

| 配置项 | 值 | 说明 |
|--------|-----|------|
| `appendonly` | `yes` | 开启 AOF 持久化 |
| `appendfsync` | `everysec` | 每秒同步一次 |
| `maxmemory` | `256mb` | 最大内存限制 |
| `maxmemory-policy` | `allkeys-lru` | 内存满时淘汰最近最少使用的键 |

## 常用命令

```bash
# 启动 Redis
cd docker && docker-compose up -d redis

# 停止 Redis
docker-compose stop redis

# 查看日志
docker-compose logs -f redis

# 进入 Redis CLI
docker exec -it kabuto-redis redis-cli -a kabuto@2026

# 查看 Redis 信息
docker exec -it kabuto-redis redis-cli -a kabuto@2026 INFO

# 清空所有数据（慎用）
docker exec -it kabuto-redis redis-cli -a kabuto@2026 FLUSHALL
```
