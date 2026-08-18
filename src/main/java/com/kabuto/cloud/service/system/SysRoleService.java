package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateRoleDTO;
import com.kabuto.cloud.dto.system.SearchRoleDTO;
import com.kabuto.cloud.dto.system.UpdateRoleDTO;
import com.kabuto.cloud.vo.system.RoleVO;

import java.util.List;

/**
 * 角色管理服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin RoleService 实现角色管理服务层</p>
 * <p><b>解决方案：</b>定义角色 CRUD、授权管理等服务接口</p>
 * <p><b>原因说明：</b>对应 nest-admin RoleService</p>
 */
public interface SysRoleService {

    /**
     * 分页查询角色列表
     */
    R<PageResult<RoleVO>> page(Integer pageNum, Integer pageSize, SearchRoleDTO dto);

    /**
     * 查询所有正常状态角色（下拉选择用）
     */
    R<List<RoleVO>> getAllRoles();

    /**
     * 查询角色详情（含 menuIds + permissionIds）
     */
    R<RoleVO> getRoleDetail(Long id);

    /**
     * 创建角色（事务：创建角色 + 关联菜单 + 关联权限）
     */
    R<Void> createRole(CreateRoleDTO dto);

    /**
     * 更新角色（事务：更新角色 + 重建菜单/权限关联）
     */
    R<Void> updateRole(Long id, UpdateRoleDTO dto);

    /**
     * 启停角色状态
     */
    R<Void> changeStatus(Long id, Integer status);

    /**
     * 批量删除角色
     */
    R<Void> deleteRoles(List<Long> ids);

    /**
     * 查询角色授权信息（menuIds）
     */
    R<RoleAuthorizeVO> getRoleAuthorize(Long id);

    /**
     * 角色授权信息 VO
     */
    @lombok.Data
    @io.swagger.v3.oas.annotations.media.Schema(description = "角色授权信息")
    class RoleAuthorizeVO {
        @io.swagger.v3.oas.annotations.media.Schema(description = "菜单ID列表")
        private List<Long> menuIds;
    }
}
