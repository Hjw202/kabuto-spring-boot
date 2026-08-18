-- ============================================================
-- MySQL 初始化脚本（用户创建 + 权限授予）
-- 容器首次启动时按文件名排序执行，00- 先于 01- 执行
-- ============================================================

-- 创建 kabuto 用户并授权（兼容 Docker 环境）
-- 如果用户已存在则跳过
CREATE USER IF NOT EXISTS 'kabuto'@'%' IDENTIFIED BY 'kabuto@2026';

-- 授权 kabuto 用户对 kabuto_db 数据库的所有权限
GRANT ALL PRIVILEGES ON kabuto_db.* TO 'kabuto'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- 显示初始化完成信息
SELECT 'MySQL initialization completed successfully!' AS status;
