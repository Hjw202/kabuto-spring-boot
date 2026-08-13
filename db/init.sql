-- ============================================================
--  Kabuto 认证模块数据库初始化脚本
--  参考 nest-admin 项目表结构，按 Java/MyBatis-Plus 规范调整
-- ============================================================

-- 使用 kabuto_db 数据库
USE kabuto_db;

-- ----------------------------
-- 1. 用户表 sys_user
-- ----------------------------
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id         BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '用户ID',
    username        VARCHAR(30)     NOT NULL                    COMMENT '账号',
    password        VARCHAR(60)     NOT NULL                    COMMENT '密码（BCrypt哈希）',
    name            VARCHAR(50)                                 COMMENT '昵称',
    birthday        DATE                                        COMMENT '生日',
    sex             TINYINT         DEFAULT 1                   COMMENT '性别 0=女 1=男',
    phone           VARCHAR(11)                                 COMMENT '手机号码',
    email           VARCHAR(100)                                COMMENT '邮箱',
    avatar          VARCHAR(300)                                COMMENT '头像',
    status          TINYINT         DEFAULT 1                   COMMENT '状态 0=禁用 1=正常',
    login_ip        VARCHAR(45)                                 COMMENT '上次登录IP',
    login_date      DATETIME                                    COMMENT '上次登录时间',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 2. 角色表 sys_role
-- ----------------------------
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    role_id         BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '角色ID',
    name            VARCHAR(30)     NOT NULL                    COMMENT '角色名称',
    role_key        VARCHAR(100)    NOT NULL                    COMMENT '角色权限字符',
    sort            INT             NOT NULL                    COMMENT '排序',
    is_admin        TINYINT         DEFAULT 0                   COMMENT '是否超级管理员 0=否 1=是',
    status          TINYINT         DEFAULT 1                   COMMENT '状态 0=禁用 1=正常',
    description     VARCHAR(255)                                COMMENT '角色描述',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_name (name),
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 3. 菜单表 sys_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
    menu_id         BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '菜单ID',
    parent_id       BIGINT                                      COMMENT '父菜单ID',
    name            VARCHAR(50)     NOT NULL                    COMMENT '菜单名称',
    sort            INT             NOT NULL                    COMMENT '排序',
    icon            VARCHAR(100)                                COMMENT '菜单图标',
    router          VARCHAR(500)                                COMMENT '路由地址',
    component       VARCHAR(255)                                COMMENT '组件路径',
    query           VARCHAR(255)                                COMMENT '路由参数',
    is_frame        TINYINT         DEFAULT 0                   COMMENT '是否外部链接 0=否 1=是',
    is_cache        TINYINT         DEFAULT 0                   COMMENT '是否缓存页面 0=否 1=是',
    menu_type       TINYINT         DEFAULT 1                   COMMENT '菜单类型 1=目录 2=菜单 3=按钮',
    status          TINYINT         DEFAULT 1                   COMMENT '状态 0=停用 1=启用',
    visible         TINYINT         DEFAULT 1                   COMMENT '显示状态 0=隐藏 1=显示',
    rule            VARCHAR(100)                                COMMENT '权限标识',
    permission_id   BIGINT                                      COMMENT '关联权限ID',
    ancestors       VARCHAR(500)                                COMMENT '祖先路径，如 0,1,2',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (menu_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_menu_type (menu_type),
    INDEX idx_router (router(255)),
    INDEX idx_ancestors (ancestors(255))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- 4. 登录日志表 sys_login_info
-- ----------------------------
DROP TABLE IF EXISTS sys_login_info;
CREATE TABLE sys_login_info (
    login_info_id   BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '登录日志ID',
    user_id         BIGINT                                      COMMENT '用户ID',
    account         VARCHAR(50)     NOT NULL                    COMMENT '登录账号',
    ip_address      VARCHAR(64)                                 COMMENT 'IP地址',
    login_location  VARCHAR(255)                                COMMENT '登录地点',
    browser         VARCHAR(500)                                COMMENT '浏览器类型',
    os              VARCHAR(200)                                COMMENT '操作系统',
    status          TINYINT         DEFAULT 0                   COMMENT '登录状态 0=失败 1=成功',
    msg             VARCHAR(255)                                COMMENT '提示消息',
    login_time      DATETIME                                    COMMENT '登录时间',
    PRIMARY KEY (login_info_id),
    INDEX idx_user_id (user_id),
    INDEX idx_account (account),
    INDEX idx_login_time (login_time),
    INDEX idx_status (status),
    INDEX idx_account_time (account, login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ----------------------------
-- 5. 用户-角色关联表 sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id         BIGINT          NOT NULL                    COMMENT '用户ID',
    role_id         BIGINT          NOT NULL                    COMMENT '角色ID',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '关联创建时间',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- ----------------------------
-- 6. 角色-菜单关联表 sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
    role_id         BIGINT          NOT NULL                    COMMENT '角色ID',
    menu_id         BIGINT          NOT NULL                    COMMENT '菜单ID',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '关联创建时间',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单关联表';

-- ----------------------------
-- 7. 权限表 sys_permission
-- ----------------------------
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    permission_id   BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '权限ID',
    name            VARCHAR(100)    NOT NULL                    COMMENT '权限名称',
    perms           VARCHAR(100)    NOT NULL                    COMMENT '权限标识（如 system:user:add）',
    description     TEXT                                        COMMENT '描述',
    status          TINYINT         DEFAULT 1                   COMMENT '状态 0=停用 1=正常',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (permission_id),
    UNIQUE KEY uk_perms (perms)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ----------------------------
-- 8. 角色-权限关联表 sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    role_id         BIGINT          NOT NULL                    COMMENT '角色ID',
    permission_id   BIGINT          NOT NULL                    COMMENT '权限ID',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '关联创建时间',
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-权限关联表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 超级管理员用户（密码：admin，BCrypt 加密，strength=12）
-- 生成命令：new BCryptPasswordEncoder(12).encode("admin")
INSERT INTO sys_user (user_id, username, password, name, status, sex, create_time, update_time, deleted)
VALUES (1, 'admin', '$2a$12$8H7sWv1XHu.JH7AnXaEYOuTRXrHSRP1dYp.EPrXZV2RXg0Uzcq3ZG', '超级管理员', 1, 1, NOW(), NOW(), 0);

-- 初始角色
INSERT INTO sys_role (role_id, name, role_key, sort, is_admin, status, description, create_time, update_time, deleted)
VALUES (1, '超级管理员', 'admin', 1, 1, 1, '拥有所有权限', NOW(), NOW(), 0);

INSERT INTO sys_role (role_id, name, role_key, sort, is_admin, status, description, create_time, update_time, deleted)
VALUES (2, '普通用户', 'user', 2, 0, 1, '普通用户权限', NOW(), NOW(), 0);

-- 用户-角色关联
INSERT INTO sys_user_role (user_id, role_id, create_time) VALUES (1, 1, NOW());

-- 初始菜单（目录）
INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (1, NULL, '仪表盘', 0, 'DashboardOutlined', '/dashboard', 'dashboard/index', 2, 1, 1, '/dashboard', NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (2, NULL, '组件', 1, 'AppstoreOutlined', '/demo', NULL, 1, 1, 1, NULL, NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (3, NULL, '系统管理', 2, 'SettingOutlined', '/system', NULL, 1, 1, 1, NULL, NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (4, NULL, '内容管理', 3, 'ReadOutlined', '/content', NULL, 1, 1, 1, NULL, NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (5, NULL, '外部链接', 4, 'LinkOutlined', 'https://ant-design.antgroup.com', NULL, 2, 1, 1, '/link', NOW(), NOW(), 0);

-- 系统管理下子菜单
INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (14, 3, '用户管理', 0, 'UserOutlined', '/system/user', 'system/user/index', 2, 1, 1, '/authority/user', NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (15, 3, '菜单管理', 1, 'MenuOutlined', '/system/menu', 'system/menu/index', 2, 1, 1, '/authority/menu', NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (16, 3, '角色管理', 2, 'TeamOutlined', '/system/role', 'system/role/index', 2, 1, 1, '/authority/role', NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (17, 3, '日志管理', 99, 'FileTextOutlined', '/system/log', 'system/log/index', 2, 1, 1, '/authority/log', NOW(), NOW(), 0);

-- 用户管理下的按钮（权限点）
INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (19, 14, '用户列表', 0, NULL, '/system/user', NULL, 3, 1, 1, '/authority/user/index', NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (20, 14, '查看用户', 1, NULL, '/system/user', NULL, 3, 1, 1, '/authority/user/view', NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (21, 14, '创建用户', 2, NULL, '/system/user', NULL, 3, 1, 1, '/authority/user/create', NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (22, 14, '修改用户', 3, NULL, '/system/user', NULL, 3, 1, 1, '/authority/user/update', NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (23, 14, '删除用户', 4, NULL, '/system/user', NULL, 3, 1, 1, '/authority/user/delete', NOW(), NOW(), 0);

INSERT INTO sys_menu (menu_id, parent_id, name, sort, icon, router, component, menu_type, status, visible, rule, create_time, update_time, deleted)
VALUES (24, 14, '用户权限', 5, NULL, '/system/user', NULL, 3, 1, 1, '/authority/user/authority', NOW(), NOW(), 0);

-- 角色-菜单关联（超管拥有所有菜单，含按钮）
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 1, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 2, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 3, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 4, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 5, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 14, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 15, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 16, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 17, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 19, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 20, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 21, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 22, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 23, NOW());
INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES (1, 24, NOW());

-- 初始权限
INSERT INTO sys_permission (permission_id, name, perms, status, create_time, update_time, deleted)
VALUES (1, '用户查询', 'system:user:list', 1, NOW(), NOW(), 0);

INSERT INTO sys_permission (permission_id, name, perms, status, create_time, update_time, deleted)
VALUES (2, '用户新增', 'system:user:add', 1, NOW(), NOW(), 0);

INSERT INTO sys_permission (permission_id, name, perms, status, create_time, update_time, deleted)
VALUES (3, '用户修改', 'system:user:edit', 1, NOW(), NOW(), 0);

INSERT INTO sys_permission (permission_id, name, perms, status, create_time, update_time, deleted)
VALUES (4, '用户删除', 'system:user:del', 1, NOW(), NOW(), 0);

-- 角色-权限关联（超管拥有所有权限）
INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES (1, 1, NOW());
INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES (1, 2, NOW());
INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES (1, 3, NOW());
INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES (1, 4, NOW());

-- 万能权限标识（超级管理员标志）
INSERT INTO sys_permission (permission_id, name, perms, status, create_time, update_time, deleted)
VALUES (999, '全部权限', '*:*:*', 1, NOW(), NOW(), 0);
INSERT INTO sys_role_permission (role_id, permission_id, create_time) VALUES (1, 999, NOW());

-- ============================================================
--  其他模块表结构
-- ============================================================

-- ----------------------------
-- 14. 字典类型表 sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_type;
CREATE TABLE sys_dict_type (
    dict_type_id    BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '字典类型ID',
    dict_name       VARCHAR(100)    NOT NULL                    COMMENT '字典名称',
    dict_type       VARCHAR(100)    NOT NULL                    COMMENT '字典类型',
    status          TINYINT         DEFAULT 1                   COMMENT '字典状态 0=停用 1=启用',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (dict_type_id),
    UNIQUE KEY uk_dict_type (dict_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- 15. 字典数据表 sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS sys_dict_data;
CREATE TABLE sys_dict_data (
    dict_data_id    BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '字典数据ID',
    dict_type       VARCHAR(100)    NOT NULL                    COMMENT '字典类型',
    dict_label      VARCHAR(100)    NOT NULL                    COMMENT '字典标签',
    dict_value      VARCHAR(100)    NOT NULL                    COMMENT '字典键值',
    dict_sort       INT             NOT NULL        DEFAULT 0   COMMENT '字典排序',
    css_class       VARCHAR(100)                                COMMENT '样式属性',
    list_class      VARCHAR(100)                                COMMENT '表格回显样式',
    is_default      TINYINT         DEFAULT 0                   COMMENT '是否默认 0=否 1=是',
    status          TINYINT         DEFAULT 1                   COMMENT '字典状态 0=停用 1=启用',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (dict_data_id),
    INDEX idx_dict_type (dict_type),
    INDEX idx_status (status),
    INDEX idx_dict_type_status (dict_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- 16. 公告表 sys_notice
-- ----------------------------
DROP TABLE IF EXISTS sys_notice;
CREATE TABLE sys_notice (
    notice_id       BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '公告ID',
    notice_title    VARCHAR(200)    NOT NULL                    COMMENT '公告标题',
    notice_type     TINYINT         DEFAULT 1                   COMMENT '公告类型 1=通知 2=公告',
    notice_content  TEXT                                        COMMENT '公告内容',
    status          TINYINT         DEFAULT 1                   COMMENT '公告状态 0=关闭 1=正常',
    publish_time    DATETIME                                    COMMENT '发布时间',
    end_time        DATETIME                                    COMMENT '失效时间',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (notice_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- ----------------------------
-- 17. 系统配置表 sys_config
-- ----------------------------
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    config_id       BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '参数ID',
    config_name     VARCHAR(100)    NOT NULL                    COMMENT '参数名称',
    config_key      VARCHAR(100)    NOT NULL                    COMMENT '参数键名',
    config_value    TEXT            NOT NULL                    COMMENT '参数键值',
    config_type     TINYINT         DEFAULT 0                   COMMENT '是否系统内置 0=否 1=是',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (config_id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ----------------------------
-- 18. 操作日志表 sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    oper_id         BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '日志主键',
    title           VARCHAR(100)                                COMMENT '模块标题',
    business_type   TINYINT         DEFAULT 0                   COMMENT '业务类型 0=其它 1=新增 2=修改 3=删除',
    method          VARCHAR(500)                                COMMENT '方法名称',
    request_method  VARCHAR(10)                                 COMMENT '请求方式',
    operator_type   TINYINT         DEFAULT 0                   COMMENT '操作类别 0=其它 1=后台用户 2=手机端用户',
    user_id         BIGINT                                      COMMENT '用户ID',
    oper_name       VARCHAR(50)                                 COMMENT '操作人员',
    dept_name       VARCHAR(50)                                 COMMENT '部门名称（预留字段）',
    oper_url        VARCHAR(255)                                COMMENT '请求URL',
    oper_ip         VARCHAR(64)                                 COMMENT '主机地址',
    oper_location   VARCHAR(255)                                COMMENT '操作地点',
    oper_param      TEXT                                        COMMENT '请求参数',
    json_result     TEXT                                        COMMENT '返回参数',
    oper_time       DATETIME                                    COMMENT '操作时间',
    status          TINYINT         DEFAULT 0                   COMMENT '操作状态 0=正常 1=失败',
    error_msg       TEXT                                        COMMENT '错误消息',
    cost_time       INT             DEFAULT 0                   COMMENT '消耗时间（毫秒）',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '记录创建时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (oper_id),
    INDEX idx_oper_time (oper_time),
    INDEX idx_oper_name (oper_name),
    INDEX idx_status (status),
    INDEX idx_business_type (business_type),
    INDEX idx_oper_name_time (oper_name, oper_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------
-- 19. 授权码表 sys_license
-- ----------------------------
DROP TABLE IF EXISTS sys_license;
CREATE TABLE sys_license (
    license_id      BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '授权码ID',
    license_code    VARCHAR(64)     NOT NULL                    COMMENT '授权码',
    plan_type       VARCHAR(20)     DEFAULT 'basic'             COMMENT '计划类型 trial/basic/pro/enterprise/permanent',
    max_devices     INT             DEFAULT 1                   COMMENT '最大设备数',
    status          TINYINT         DEFAULT 0                   COMMENT '授权状态 0=未使用 1=已激活 2=已过期 3=已禁用',
    activated_at    DATETIME                                    COMMENT '激活时间',
    expires_at      DATETIME                                    COMMENT '过期时间，null表示永久',
    signature       TEXT                                        COMMENT 'ECDSA签名',
    note            VARCHAR(500)                                COMMENT '备注',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (license_id),
    UNIQUE KEY uk_license_code (license_code),
    INDEX idx_status (status),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权码表';

-- ----------------------------
-- 20. 授权设备绑定表 sys_license_device
-- ----------------------------
DROP TABLE IF EXISTS sys_license_device;
CREATE TABLE sys_license_device (
    license_device_id BIGINT          AUTO_INCREMENT  NOT NULL    COMMENT '设备绑定ID',
    license_id      BIGINT          NOT NULL                    COMMENT '授权码ID',
    device_fingerprint VARCHAR(128) NOT NULL                    COMMENT '设备指纹',
    device_name     VARCHAR(100)                                COMMENT '设备名称',
    last_validated  DATETIME                                    COMMENT '最后验证时间',
    activated_at    DATETIME        NOT NULL                    COMMENT '激活时间',
    is_active       TINYINT         DEFAULT 1                   COMMENT '是否激活状态 0=否 1=是',
    create_by       VARCHAR(64)                                 COMMENT '创建者',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP   COMMENT '创建时间',
    update_by       VARCHAR(64)                                 COMMENT '更新者',
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted         TINYINT         DEFAULT 0                   COMMENT '逻辑删除 0=正常 1=删除',
    PRIMARY KEY (license_device_id),
    INDEX idx_license_id (license_id),
    INDEX idx_device_fingerprint (device_fingerprint),
    INDEX idx_license_device (license_id, device_fingerprint)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权设备绑定表';
