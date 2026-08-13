package com.kabuto.cloud.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabuto.cloud.entity.system.SysRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 Mapper
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现角色数据访问层</p>
 * <p><b>解决方案：</b>继承 BaseMapper 获得 CRUD 能力，自定义权限查询方法</p>
 * <p><b>原因说明：</b>MyBatis-Plus BaseMapper 提供基础 CRUD</p>
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据角色ID列表查询角色的权限标识
     */
    @Select("SELECT DISTINCT p.perms FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.permission_id = rp.permission_id " +
            "WHERE rp.role_id IN (${roleIds}) AND p.status = 1 AND p.deleted = 0")
    List<String> selectPermissionsByRoleIds(@Param("roleIds") String roleIds);

    /**
     * 根据角色ID列表查询角色关联菜单的权限标识（rule 字段）
     */
    @Select("SELECT DISTINCT m.rule FROM sys_menu m " +
            "INNER JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "WHERE rm.role_id IN (${roleIds}) AND m.status = 1 AND m.deleted = 0 " +
            "AND m.rule IS NOT NULL AND m.rule != ''")
    List<String> selectMenuRulesByRoleIds(@Param("roleIds") String roleIds);
}
