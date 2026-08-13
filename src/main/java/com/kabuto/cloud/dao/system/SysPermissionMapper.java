package com.kabuto.cloud.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabuto.cloud.entity.system.SysPermission;

/**
 * 权限 Mapper
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现权限数据访问层</p>
 * <p><b>解决方案：</b>继承 BaseMapper 获得 CRUD 能力</p>
 * <p><b>原因说明：</b>MyBatis-Plus BaseMapper 提供基础 CRUD，当前权限查询通过角色关联实现</p>
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
}
