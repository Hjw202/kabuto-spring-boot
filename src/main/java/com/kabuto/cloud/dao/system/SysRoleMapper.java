package com.kabuto.cloud.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabuto.cloud.entity.system.SysRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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

    /**
     * 查询角色关联的菜单ID列表
     */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 查询角色关联的权限ID列表
     */
    @Select("SELECT permission_id FROM sys_role_permission WHERE role_id = #{roleId}")
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 插入角色-菜单关联
     */
    @Insert("INSERT INTO sys_role_menu (role_id, menu_id) VALUES (#{roleId}, #{menuId})")
    int insertRoleMenu(@Param("roleId") Long roleId, @Param("menuId") Long menuId);

    /**
     * 删除角色的所有菜单关联
     */
    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    int deleteRoleMenus(@Param("roleId") Long roleId);

    /**
     * 插入角色-权限关联
     */
    @Insert("INSERT INTO sys_role_permission (role_id, permission_id) VALUES (#{roleId}, #{permissionId})")
    int insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 删除角色的所有权限关联
     */
    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    int deleteRolePermissions(@Param("roleId") Long roleId);
}
