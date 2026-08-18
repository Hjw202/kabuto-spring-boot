package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.Public;
import com.kabuto.cloud.common.annotation.RequirePermission;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.*;
import com.kabuto.cloud.service.system.SysLicenseService;
import com.kabuto.cloud.vo.system.LicenseActivateVO;
import com.kabuto.cloud.vo.system.LicenseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * License 授权码控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LicenseController 实现授权码管理接口</p>
 * <p><b>解决方案：</b>实现客户端接口（@Public）和管理端接口两组 API</p>
 * <p><b>原因说明：</b>对应 nest-admin LicenseController。客户端接口免认证（激活/验证/解绑/公钥），
 * 管理端接口需要登录（创建/详情/列表）。接口路径：/v1/system/license/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/license")
@Tag(name = "授权码模块", description = "授权码管理相关接口（含客户端接口）")
public class SysLicenseController {

    private final SysLicenseService licenseService;

    public SysLicenseController(SysLicenseService licenseService) {
        this.licenseService = licenseService;
    }

    // ==================== 客户端接口（@Public，免认证） ====================

    /**
     * 激活授权码
     */
    @Operation(summary = "激活授权码")
    @Public
    @PostMapping("/activate")
    public R<LicenseActivateVO> activate(@Valid @RequestBody ActivateLicenseDTO dto) {
        return R.ok(licenseService.activate(dto));
    }

    /**
     * 验证授权码
     */
    @Operation(summary = "验证授权码")
    @Public
    @PostMapping("/validate")
    public R<SysLicenseService.ValidateResult> validate(@Valid @RequestBody ValidateLicenseDTO dto) {
        return R.ok(licenseService.validate(dto));
    }

    /**
     * 解绑设备
     */
    @Operation(summary = "解绑设备")
    @Public
    @PostMapping("/deactivate")
    public R<Boolean> deactivate(@RequestBody DeactivateDTO dto) {
        return R.ok(licenseService.deactivate(dto.getLicenseCode(), dto.getDeviceFingerprint()));
    }

    /**
     * 获取公钥（供客户端本地验证签名）
     */
    @Operation(summary = "获取公钥")
    @Public
    @GetMapping("/public-key")
    public R<String> getPublicKey() {
        return R.ok(licenseService.getPublicKey());
    }

    // ==================== 管理端接口（需登录） ====================

    /**
     * 创建授权码
     */
    @Operation(summary = "创建授权码（管理端）")
    @PostMapping("/admin/create")
    public R<LicenseVO> createLicense(@Valid @RequestBody CreateLicenseDTO dto) {
        return R.ok(licenseService.createLicense(dto));
    }

    /**
     * 查询授权码详情
     */
    @Operation(summary = "查询授权码详情（管理端）")
    @GetMapping("/admin/detail")
    public R<LicenseVO> detail(@RequestParam Long licenseId) {
        return R.ok(licenseService.getLicenseDetail(licenseId));
    }

    /**
     * 分页查询授权码列表
     */
    @Operation(summary = "分页查询授权码列表（管理端）")
    @GetMapping("/admin/list")
    public R<PageResult<LicenseVO>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchLicenseDTO dto) {
        return R.ok(licenseService.page(page, pageSize, dto));
    }

    // ==================== 内部 DTO ====================

    @lombok.Data
    @io.swagger.v3.oas.annotations.media.Schema(description = "解绑设备请求")
    public static class DeactivateDTO {
        @io.swagger.v3.oas.annotations.media.Schema(description = "授权码", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
        private String licenseCode;
        @io.swagger.v3.oas.annotations.media.Schema(description = "设备指纹", requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
        private String deviceFingerprint;
    }
}
