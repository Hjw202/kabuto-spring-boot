package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreatePermissionDTO;
import com.kabuto.cloud.dto.system.SearchPermissionDTO;
import com.kabuto.cloud.dto.system.UpdatePermissionDTO;
import com.kabuto.cloud.service.system.SysPermissionService;
import com.kabuto.cloud.vo.system.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin PermissionController 实现权限管理接口</p>
 * <p><b>解决方案：</b>实现权限 CRUD 接口</p>
 * <p><b>原因说明：</b>对应 nest-admin PermissionController。接口路径：/v1/system/permission/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/permission")
@Tag(name = "权限管理", description = "权限管理相关接口")
public class SysPermissionController {

    private final SysPermissionService permissionService;

    public SysPermissionController(SysPermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 分页查询权限列表
     */
    @Operation(summary = "分页查询权限列表")
    @RequirePermission("system:permission:query")
    @GetMapping("/")
    public R<PageResult<PermissionVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchPermissionDTO dto) {
        return permissionService.page(pageNum, pageSize, dto);
    }

    /**
     * 查询所有权限列表
     */
    @Operation(summary = "查询所有权限列表")
    @GetMapping("/all")
    public R<List<PermissionVO>> all() {
        return permissionService.getAllPermissions();
    }

    /**
     * 查询权限详情
     */
    @Operation(summary = "查询权限详情")
    @GetMapping("/{id}")
    public R<PermissionVO> detail(@PathVariable Long id) {
        return permissionService.getPermissionById(id);
    }

    /**
     * 创建权限
     */
    @Operation(summary = "创建权限")
    @RequirePermission("system:permission:add")
    @PostMapping("/")
    public R<Void> create(@Valid @RequestBody CreatePermissionDTO dto) {
        return permissionService.createPermission(dto);
    }

    /**
     * 更新权限
     */
    @Operation(summary = "更新权限")
    @RequirePermission("system:permission:edit")
    @PutMapping("/")
    public R<Void> update(@Valid @RequestBody UpdatePermissionDTO dto) {
        return permissionService.updatePermission(dto);
    }

    /**
     * 批量删除权限
     */
    @Operation(summary = "批量删除权限")
    @RequirePermission("system:permission:remove")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        return permissionService.deletePermissions(ids);
    }
}
