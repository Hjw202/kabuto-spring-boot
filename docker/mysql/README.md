# MySQL 8.0 Docker 本地开发环境

## 服务信息

| 配置项 | 值 |
|--------|-----|
| 镜像 | `mysql:8.0`（社区版，免费） |
| 容器名 | `kabuto-mysql` |
| 端口 | `3306:3306` |
| 数据库 | `kabuto_db` |
| 用户名 | `kabuto` |
| 密码 | `kabuto@2026` |
| Root 密码 | `kabuto@2026` |

## 数据持久化路径

```
D:/DockerData/mysql-kabuto/
├── data/        # 数据库数据文件
└── logs/        # MySQL 运行日志
```

## 常用命令

```bash
# 启动 MySQL
cd docker && docker-compose up -d mysql

# 停止 MySQL
docker-compose stop mysql

# 查看日志
docker-compose logs -f mysql

# 进入 MySQL 容器
docker exec -it kabuto-mysql mysql -ukabuto -p

# 进入 MySQL 容器（root）
docker exec -it kabuto-mysql mysql -uroot -p

# 备份数据库
docker exec kabuto-mysql mysqldump -ukabuto -p kabuto_db > backup.sql

# 恢复数据库
docker exec -i kabuto-mysql mysql -ukabuto -p kabuto_db < backup.sql
```
