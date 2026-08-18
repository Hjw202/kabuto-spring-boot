package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.annotation.RequireSuperAdmin;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.*;
import com.kabuto.cloud.security.context.SecurityContext;
import com.kabuto.cloud.service.system.SysUserService;
import com.kabuto.cloud.vo.system.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UserController 实现用户管理接口</p>
 * <p><b>解决方案：</b>实现用户 CRUD、个人中心、授权管理等接口。
 * 使用 @RequirePermission 进行权限校验，@RequireSuperAdmin 校验超管操作</p>
 * <p><b>原因说明：</b>对应 nest-admin UserController。接口路径：/v1/system/user/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/user")
@Tag(name = "用户模块", description = "用户管理相关接口")
public class SysUserController {

    private final SysUserService userService;

    public SysUserController(SysUserService userService) {
        this.userService = userService;
    }

    // ==================== 用户管理 ====================

    /**
     * 分页查询用户列表
     */
    @Operation(summary = "分页查询用户列表")
    @RequirePermission("system:user:query")
    @GetMapping("/page")
    public R<PageResult<UserVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchUserDTO dto) {
        return userService.page(pageNum, pageSize, dto);
    }

    /**
     * 查询用户详情
     */
    @Operation(summary = "查询用户详情")
    @RequirePermission("system:user:query")
    @GetMapping("/detail")
    public R<UserVO> detail(@RequestParam Long id) {
        UserVO user = userService.getUserDetail(id);
        if (user == null) {
            return R.notFound("用户不存在");
        }
        return R.ok(user);
    }

    /**
     * 根据ID查询用户
     */
    @Operation(summary = "根据ID查询用户")
    @RequirePermission("system:user:query")
    @GetMapping("/{id}")
    public R<UserVO> findOne(@PathVariable Long id) {
        UserVO user = userService.getUserById(id);
        if (user == null) {
            return R.notFound("用户不存在");
        }
        return R.ok(user);
    }

    /**
     * 创建用户
     */
    @Operation(summary = "创建用户")
    @PostMapping("/create")
    public R<Void> create(@Valid @RequestBody CreateUserDTO dto) {
        return userService.createUser(dto);
    }

    /**
     * 编辑用户
     */
    @Operation(summary = "编辑用户")
    @RequirePermission("system:user:edit")
    @PutMapping("/update/{id}")
    public R<Void> update(@PathVariable Long id, @Valid @RequestBody UpdateUserDTO dto) {
        return userService.updateUser(id, dto);
    }

    /**
     * 启停用户状态
     */
    @Operation(summary = "启停用户状态")
    @RequirePermission("system:user:edit")
    @RequireSuperAdmin
    @PostMapping("/status")
    public R<Void> changeStatus(@RequestParam Long id, @RequestParam Integer status) {
        return userService.changeStatus(id, status);
    }

    /**
     * 批量删除用户
     */
    @Operation(summary = "批量删除用户")
    @RequirePermission("system:user:remove")
    @RequireSuperAdmin
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        return userService.deleteUsers(ids);
    }

    /**
     * 批量删除用户（POST方式，兼容前端）
     */
    @Operation(summary = "批量删除用户")
    @RequirePermission("system:user:remove")
    @RequireSuperAdmin
    @PostMapping("/batchDelete")
    public R<Void> batchDelete(@RequestBody List<Long> ids) {
        return userService.deleteUsers(ids);
    }

    /**
     * 管理员重置用户密码
     */
    @Operation(summary = "管理员重置用户密码")
    @RequirePermission("system:user:resetPwd")
    @RequireSuperAdmin
    @PutMapping("/resetpwd")
    public R<Void> resetPwd(@Valid @RequestBody ResetPwdDTO dto) {
        return userService.resetPwd(dto);
    }

    // ==================== 角色分配 ====================

    /**
     * 查看用户角色
     */
    @Operation(summary = "查看用户角色")
    @GetMapping("/authRole/{id}")
    public R<UserVO> authRole(@PathVariable Long id) {
        UserVO user = userService.getUserRoles(id);
        if (user == null) {
            return R.notFound("用户不存在");
        }
        return R.ok(user);
    }

    /**
     * 查看用户授权信息
     */
    @Operation(summary = "查看用户授权信息")
    @RequirePermission("system:user:query")
    @GetMapping("/authorize")
    public R<SysUserService.AuthorizeVO> getUserAuthorize(@RequestParam Long id) {
        return userService.getUserAuthorize(id);
    }

    /**
     * 保存用户授权
     */
    @Operation(summary = "保存用户授权")
    @RequirePermission("system:user:edit")
    @PutMapping("/authorize/save")
    public R<Void> saveAuthorize(@RequestBody SaveAuthorizeDTO dto) {
        return userService.saveAuthorize(dto.getUserId(), dto.getRoleIds(), dto.getPermissionIds());
    }

    // ==================== 个人中心 ====================

    /**
     * 获取个人信息
     */
    @Operation(summary = "获取个人信息")
    @GetMapping("/profile")
    public R<UserVO> getUserProfile() {
        Long userId = Long.valueOf(SecurityContext.getUserId());
        return userService.getUserProfile(userId);
    }

    /**
     * 更新个人资料
     */
    @Operation(summary = "更新个人资料")
    @PutMapping("/profile")
    public R<Void> updateProfile(@Valid @RequestBody UpdateProfileDTO dto) {
        Long userId = Long.valueOf(SecurityContext.getUserId());
        return userService.updateProfile(userId, dto);
    }

    /**
     * 修改个人密码
     */
    @Operation(summary = "修改个人密码")
    @PutMapping("/profile/updatePwd")
    public R<Void> updatePwd(@RequestBody UpdatePwdDTO dto) {
        Long userId = Long.valueOf(SecurityContext.getUserId());
        return userService.updatePwd(userId, dto.getOldPassword(), dto.getNewPassword());
    }

    /**
     * 更新头像
     */
    @Operation(summary = "更新头像")
    @PostMapping("/profile/avatar")
    public R<Void> updateAvatar(@RequestParam String avatar) {
        Long userId = Long.valueOf(SecurityContext.getUserId());
        return userService.updateAvatar(userId, avatar);
    }

    // ==================== 内部DTO ====================

    /**
     * 保存授权请求
     */
    @lombok.Data
    @io.swagger.v3.oas.annotations.media.Schema(description = "保存授权请求")
    public static class SaveAuthorizeDTO {
        @io.swagger.v3.oas.annotations.media.Schema(description = "用户ID", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
        private Long userId;
        @io.swagger.v3.oas.annotations.media.Schema(description = "角色ID列表")
        private List<Long> roleIds;
        @io.swagger.v3.oas.annotations.media.Schema(description = "权限ID列表")
        private List<Long> permissionIds;
    }

    /**
     * 修改密码请求
     */
    @lombok.Data
    @io.swagger.v3.oas.annotations.media.Schema(description = "修改密码请求")
    public static class UpdatePwdDTO {
        @jakarta.validation.constraints.NotBlank(message = "旧密码不能为空")
        @io.swagger.v3.oas.annotations.media.Schema(description = "旧密码", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
        private String oldPassword;
        @jakarta.validation.constraints.NotBlank(message = "新密码不能为空")
        @jakarta.validation.constraints.Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
        @io.swagger.v3.oas.annotations.media.Schema(description = "新密码", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
        private String newPassword;
    }
}
