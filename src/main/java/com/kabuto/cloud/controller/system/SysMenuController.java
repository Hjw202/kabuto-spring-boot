package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateMenuDTO;
import com.kabuto.cloud.dto.system.SearchMenuDTO;
import com.kabuto.cloud.dto.system.UpdateMenuDTO;
import com.kabuto.cloud.service.system.SysMenuService;
import com.kabuto.cloud.vo.system.MenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin MenuController 实现菜单管理接口</p>
 * <p><b>解决方案：</b>实现菜单 CRUD、树形查询、状态变更等接口</p>
 * <p><b>原因说明：</b>对应 nest-admin MenuController。接口路径：/v1/system/menu/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/menu")
@Tag(name = "菜单管理", description = "菜单管理相关接口")
public class SysMenuController {

    private final SysMenuService menuService;

    public SysMenuController(SysMenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * 分页查询菜单树形列表
     */
    @Operation(summary = "分页查询菜单树形列表")
    @RequirePermission("system:menu:query")
    @GetMapping("/page")
    public R<PageResult<MenuVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchMenuDTO dto) {
        return R.ok(menuService.pageTree(pageNum, pageSize, dto));
    }

    /**
     * 查询所有菜单（树形）
     */
    @Operation(summary = "查询所有菜单（树形）")
    @GetMapping("/list")
    public R<List<MenuVO>> list() {
        return R.ok(menuService.getMenuList());
    }

    /**
     * 查询菜单详情
     */
    @Operation(summary = "查询菜单详情")
    @RequirePermission("system:menu:query")
    @GetMapping("/detail")
    public R<MenuVO> detail(@RequestParam Long id) {
        return R.ok(menuService.getMenuById(id));
    }

    /**
     * 查询权限菜单列表（目录/页面类型）
     */
    @Operation(summary = "查询权限菜单列表")
    @GetMapping("/permissionList")
    public R<List<MenuVO>> permissionList() {
        return R.ok(menuService.getPermissionList());
    }

    /**
     * 创建菜单
     */
    @Operation(summary = "创建菜单")
    @RequirePermission("system:menu:add")
    @PostMapping("/create")
    public R<Void> create(@Valid @RequestBody CreateMenuDTO dto) {
        menuService.createMenu(dto);
        return R.ok();
    }

    /**
     * 更新菜单
     */
    @Operation(summary = "更新菜单")
    @RequirePermission("system:menu:edit")
    @PutMapping("/update/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateMenuDTO dto) {
        menuService.updateMenu(id, dto);
        return R.ok();
    }

    /**
     * 启停菜单状态
     */
    @Operation(summary = "启停菜单状态")
    @RequirePermission("system:menu:edit")
    @PutMapping("/changeState")
    public R<Void> changeStatus(@RequestParam Long menuId, @RequestParam Integer status) {
        menuService.changeStatus(menuId, status);
        return R.ok();
    }

    /**
     * 删除菜单
     */
    @Operation(summary = "删除菜单")
    @RequirePermission("system:menu:remove")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return R.ok();
    }
}
