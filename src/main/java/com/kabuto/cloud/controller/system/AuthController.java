package com.kabuto.cloud.controller.system;

import com.kabuto.cloud.common.annotation.Public;
import com.kabuto.cloud.common.annotation.Throttle;
import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.security.context.SecurityContext;
import com.kabuto.cloud.dto.system.LoginUserDTO;
import com.kabuto.cloud.service.system.AuthService;
import com.kabuto.cloud.vo.system.LoginVO;
import com.kabuto.cloud.vo.system.RouterVO;
import com.kabuto.cloud.vo.system.UserInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 认证控制器
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin AuthController 实现 Java 版本认证接口</p>
 * <p><b>解决方案：</b>实现 4 个核心接口：登录、退出登录、获取路由、获取用户信息。
 * 使用 Swagger 注解生成接口文档，@Throttle 实现限流，@Public 标记免认证接口</p>
 * <p><b>原因说明：</b>对应 nest-admin AuthController 的 4 个接口。
 * 接口路径：/api/v1/system/auth/*</p>
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/v1/system/auth")
@Tag(name = "登录模块", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     */
    @Operation(summary = "登录")
    @Public
    @Throttle(limit = 30, ttl = 60 * 60, unit = TimeUnit.SECONDS,
            message = "登录请求过于频繁，请稍后再试")
    @PostMapping("/login")
    public R<LoginVO> login(
            @Valid @RequestBody LoginUserDTO dto,
            HttpServletRequest request,
            @RequestHeader(value = "User-Agent", defaultValue = "") String userAgent) {

        String ip = request.getRemoteAddr();
        LoginVO loginVO = authService.login(dto, ip, userAgent);
        return R.ok(loginVO);
    }

    /**
     * 退出登录
     */
    @Operation(summary = "退出登录")
    @Throttle(dimension = Throttle.Dimension.TOKEN, limit = 8, ttl = 60 * 60, unit = TimeUnit.SECONDS)
    @GetMapping("/logout")
    public R<Void> logout(
            @RequestHeader(value = Constants.TOKEN_HEADER, defaultValue = "") String token) {
        authService.logout(token);
        return R.ok();
    }

    /**
     * 获取用户能访问的路由
     */
    @Operation(summary = "获取用户能访问的路由")
    @GetMapping("/getRouters")
    public R<List<RouterVO>> getRouters() {
        Long userId = Long.valueOf(SecurityContext.getUserId());
        List<RouterVO> routers = authService.getRouters(userId);
        return R.ok(routers);
    }

    /**
     * 获取用户信息，包括权限和角色
     */
    @Operation(summary = "获取用户信息，包括权限和角色")
    @GetMapping("/getInfo")
    public R<UserInfoVO> getInfo() {
        Long userId = Long.valueOf(SecurityContext.getUserId());
        UserInfoVO info = authService.getInfo(userId);
        return R.ok(info);
    }
}
