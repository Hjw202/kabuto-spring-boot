package com.kabuto.cloud.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabuto.cloud.entity.system.SysUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户 Mapper
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现用户数据访问层</p>
 * <p><b>解决方案：</b>继承 BaseMapper 获得 CRUD 能力，自定义关联查询方法</p>
 * <p><b>原因说明：</b>MyBatis-Plus BaseMapper 提供基础 CRUD，自定义方法通过注解/XML 实现关联查询</p>
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户（包含角色和权限）
     */
    SysUser selectUserWithRolesAndPermissions(@Param("username") String username);

    /**
     * 根据用户ID查询用户（包含角色和权限）
     */
    SysUser selectUserWithRolesAndPermissionsById(@Param("userId") Long userId);

    /**
     * 查询用户的角色Key列表
     */
    @Select("SELECT r.role_key FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 AND r.deleted = 0")
    List<String> selectRoleKeysByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的直接权限标识列表
     */
    @Select("SELECT p.perms FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.permission_id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 1 AND p.deleted = 0")
    List<String> selectDirectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的角色ID列表
     */
    @Select("SELECT role_id FROM sys_user_role WHERE user_id = #{userId}")
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户的直接权限ID列表
     */
    @Select("SELECT permission_id FROM sys_user_permission WHERE user_id = #{userId}")
    List<Long> selectPermissionIdsByUserId(@Param("userId") Long userId);

    /**
     * 插入用户-角色关联
     */
    @Insert("INSERT INTO sys_user_role (user_id, role_id) VALUES (#{userId}, #{roleId})")
    int insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户的所有角色关联
     */
    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    int deleteUserRoles(@Param("userId") Long userId);

    /**
     * 插入用户-权限关联
     */
    @Insert("INSERT INTO sys_user_permission (user_id, permission_id) VALUES (#{userId}, #{permissionId})")
    int insertUserPermission(@Param("userId") Long userId, @Param("permissionId") Long permissionId);

    /**
     * 删除用户的所有直接权限关联
     */
    @Delete("DELETE FROM sys_user_permission WHERE user_id = #{userId}")
    int deleteUserPermissions(@Param("userId") Long userId);

    /**
     * 根据角色ID查询拥有该角色的用户ID列表
     */
    @Select("SELECT user_id FROM sys_user_role WHERE role_id = #{roleId}")
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);
}
