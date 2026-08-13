package com.kabuto.cloud.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabuto.cloud.entity.system.SysLoginInfo;

/**
 * 登录日志 Mapper
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现登录日志数据访问层</p>
 * <p><b>解决方案：</b>继承 BaseMapper 获得 CRUD 能力</p>
 * <p><b>原因说明：</b>MyBatis-Plus BaseMapper 提供基础 CRUD，登录日志主要是插入操作</p>
 */
public interface SysLoginInfoMapper extends BaseMapper<SysLoginInfo> {
}
