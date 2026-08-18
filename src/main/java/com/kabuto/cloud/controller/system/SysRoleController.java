package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.annotation.RequireSuperAdmin;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateRoleDTO;
import com.kabuto.cloud.dto.system.SearchRoleDTO;
import com.kabuto.cloud.dto.system.UpdateRoleDTO;
import com.kabuto.cloud.service.system.SysRoleService;
import com.kabuto.cloud.vo.system.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin RoleController 实现角色管理接口</p>
 * <p><b>解决方案：</b>实现角色 CRUD、授权查询等接口</p>
 * <p><b>原因说明：</b>对应 nest-admin RoleController。接口路径：/v1/system/role/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/role")
@Tag(name = "角色管理", description = "角色管理相关接口")
public class SysRoleController {

    private final SysRoleService roleService;

    public SysRoleController(SysRoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色列表")
    @RequirePermission("system:role:query")
    @GetMapping("/page")
    public R<PageResult<RoleVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchRoleDTO dto) {
        return roleService.page(pageNum, pageSize, dto);
    }

    /**
     * 查询所有正常状态角色（下拉选择用）
     */
    @Operation(summary = "查询所有正常状态角色")
    @GetMapping("/list")
    public R<List<RoleVO>> list() {
        return roleService.getAllRoles();
    }

    /**
     * 查询角色详情
     */
    @Operation(summary = "查询角色详情")
    @RequirePermission("system:role:query")
    @GetMapping("/detail")
    public R<RoleVO> detail(@RequestParam Long id) {
        return roleService.getRoleDetail(id);
    }

    /**
     * 创建角色
     */
    @Operation(summary = "创建角色")
    @RequirePermission("system:role:add")
    @PostMapping("/create")
    public R<Void> create(@Valid @RequestBody CreateRoleDTO dto) {
        return roleService.createRole(dto);
    }

    /**
     * 编辑角色
     */
    @Operation(summary = "编辑角色")
    @RequirePermission("system:role:edit")
    @PutMapping("/update/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateRoleDTO dto) {
        return roleService.updateRole(id, dto);
    }

    /**
     * 启停角色状态
     */
    @Operation(summary = "启停角色状态")
    @RequirePermission("system:role:edit")
    @RequireSuperAdmin
    @PostMapping("/status")
    public R<Void> changeStatus(@RequestParam Long id, @RequestParam Integer status) {
        return roleService.changeStatus(id, status);
    }

    /**
     * 批量删除角色
     */
    @Operation(summary = "批量删除角色")
    @RequirePermission("system:role:remove")
    @RequireSuperAdmin
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        return roleService.deleteRoles(ids);
    }

    /**
     * 批量删除角色（POST方式，兼容前端）
     */
    @Operation(summary = "批量删除角色")
    @RequirePermission("system:role:remove")
    @RequireSuperAdmin
    @PostMapping("/batchDelete")
    public R<Void> batchDelete(@RequestBody List<Long> ids) {
        return roleService.deleteRoles(ids);
    }

    /**
     * 查询角色授权信息
     */
    @Operation(summary = "查询角色授权信息")
    @RequirePermission("system:role:query")
    @GetMapping("/authorize")
    public R<SysRoleService.RoleAuthorizeVO> getRoleAuthorize(@RequestParam Long id) {
        return roleService.getRoleAuthorize(id);
    }
}
