package com.kabuto.cloud.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabuto.cloud.entity.system.SysUser;
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
}
