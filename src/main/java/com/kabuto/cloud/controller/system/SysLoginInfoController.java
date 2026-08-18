package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.SearchLoginInfoDTO;
import com.kabuto.cloud.service.system.SysLoginInfoService;
import com.kabuto.cloud.vo.system.LoginInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 登录日志控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin LoginInfoController 实现登录日志管理接口</p>
 * <p><b>解决方案：</b>实现登录日志分页查询、清空、批量删除等接口</p>
 * <p><b>原因说明：</b>对应 nest-admin LoginInfoController。接口路径：/v1/system/login-info/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/login-info")
@Tag(name = "登录日志", description = "登录日志管理相关接口")
public class SysLoginInfoController {

    private final SysLoginInfoService loginInfoService;

    public SysLoginInfoController(SysLoginInfoService loginInfoService) {
        this.loginInfoService = loginInfoService;
    }

    /**
     * 分页查询登录日志
     */
    @Operation(summary = "分页查询登录日志")
    @GetMapping("/")
    public R<PageResult<LoginInfoVO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            SearchLoginInfoDTO dto) {
        return loginInfoService.page(pageNum, pageSize, dto);
    }

    /**
     * 批量删除登录日志
     */
    @Operation(summary = "批量删除登录日志")
    @DeleteMapping("/{ids}")
    public R<Void> delete(@PathVariable List<Long> ids) {
        return loginInfoService.deleteLoginInfos(ids);
    }

    /**
     * 清空登录日志
     */
    @Operation(summary = "清空登录日志")
    @DeleteMapping("/clear")
    public R<Void> clear() {
        return loginInfoService.clear();
    }
}
