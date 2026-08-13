package com.kabuto.cloud.controller;

import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Swagger 示例 Controller
 * <p>
 * 本文件演示如何在 Controller 中使用 Swagger / OpenAPI 3.0 注解，
 * 为接口生成详细的文档说明。开发者可以参考此示例为真实业务接口添加注解。
 * </p>
 * <p><b>需求描述：</b>大王要求封装统一响应类，统一前后端交互数据结构</p>
 * <p><b>解决方案：</b>所有接口返回值统一使用 R<T> 包装，内嵌 PageResult 提取为独立公共类</p>
 * <p><b>原因说明：</b>参考 nest-admin R.ts 设计，Spring Boot 项目通用做法。
 * 统一响应格式便于前端统一处理，Swagger 文档自动生成</p>
 */
@Tag(name = "示例接口", description = "Swagger 注解使用示例，展示如何为接口添加文档说明")
@RestController
@RequestMapping("/api/demo")
public class DemoController {

    /**
     * 获取用户信息（GET 示例）
     */
    @Operation(
            summary = "获取用户信息",
            description = "根据用户 ID 查询用户的详细信息，包含昵称、邮箱、创建时间等字段。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = R.class))),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/user/{id}")
    public R<UserInfoDTO> getUser(
            @Parameter(description = "用户 ID", required = true, example = "10001")
            @PathVariable Long id) {
        // 模拟返回数据
        return R.ok(UserInfoDTO.builder()
                .id(id)
                .username("kabuto_user")
                .nickname("Kabuto")
                .email("user@kabuto.cloud")
                .avatarUrl("https://kabuto.cloud/avatar/10001.png")
                .status(1)
                .createTime(LocalDateTime.now())
                .build());
    }

    /**
     * 创建用户（POST 示例）
     */
    @Operation(
            summary = "创建用户",
            description = "传入用户信息，创建一个新用户并返回创建后的用户信息。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "创建成功",
                    content = @Content(schema = @Schema(implementation = R.class))),
            @ApiResponse(responseCode = "400", description = "参数校验失败"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/user")
    public R<UserInfoDTO> createUser(
            @Parameter(description = "用户创建信息", required = true)
            @RequestBody CreateUserRequest request) {
        // 模拟返回数据
        return R.ok(UserInfoDTO.builder()
                .id(System.currentTimeMillis())
                .username(request.getUsername())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .avatarUrl(request.getAvatarUrl())
                .status(1)
                .createTime(LocalDateTime.now())
                .build());
    }

    /**
     * 更新用户信息（PUT 示例）
     */
    @Operation(
            summary = "更新用户信息",
            description = "根据用户 ID 更新用户的昵称、邮箱、头像等信息。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(schema = @Schema(implementation = R.class))),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "400", description = "参数校验失败")
    })
    @PutMapping("/user/{id}")
    public R<UserInfoDTO> updateUser(
            @Parameter(description = "用户 ID", required = true, example = "10001")
            @PathVariable Long id,
            @Parameter(description = "用户更新信息", required = true)
            @RequestBody UpdateUserRequest request) {
        // 模拟返回数据
        return R.ok(UserInfoDTO.builder()
                .id(id)
                .username("kabuto_user")
                .nickname(request.getNickname())
                .email(request.getEmail())
                .avatarUrl(request.getAvatarUrl())
                .status(1)
                .createTime(LocalDateTime.now().minusDays(7))
                .updateTime(LocalDateTime.now())
                .build());
    }

    /**
     * 删除用户（DELETE 示例）
     */
    @Operation(
            summary = "删除用户",
            description = "根据用户 ID 删除指定用户（逻辑删除）。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/user/{id}")
    public R<Void> deleteUser(
            @Parameter(description = "用户 ID", required = true, example = "10001")
            @PathVariable Long id) {
        // 模拟删除操作
        return R.ok();
    }

    /**
     * 查询用户列表（带分页参数的 GET 示例）
     */
    @Operation(
            summary = "查询用户列表",
            description = "分页查询用户列表，支持按用户名模糊搜索。"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = R.class)))
    })
    @GetMapping("/users")
    public R<PageResult<UserInfoDTO>> listUsers(
            @Parameter(description = "用户名关键字（模糊搜索）", example = "kabuto")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，从 1 开始", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        // 模拟返回数据
        UserInfoDTO user = UserInfoDTO.builder()
                .id(10001L)
                .username("kabuto_user")
                .nickname("Kabuto")
                .email("user@kabuto.cloud")
                .avatarUrl("https://kabuto.cloud/avatar/10001.png")
                .status(1)
                .createTime(LocalDateTime.now())
                .build();
        return R.tableData(List.of(user), 1L, page, pageSize);
    }

    // ==================== 异常测试接口 ====================

    /**
     * 模拟业务异常
     */
    @Operation(summary = "模拟业务异常", description = "抛出自定义 BizException，测试全局异常处理器捕获效果。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "异常被捕获，返回业务错误码")
    })
    @GetMapping("/exception/biz")
    public R<Void> throwBizException() {
        throw new BizException("模拟业务异常：用户不存在");
    }

    /**
     * 模拟系统异常
     */
    @Operation(summary = "模拟系统异常", description = "抛出 NullPointerException，测试全局异常处理器兜底捕获效果。")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "异常被捕获，返回 500 错误码")
    })
    @GetMapping("/exception/system")
    public R<Void> throwSystemException() {
        throw new NullPointerException("模拟系统异常");
    }

    // ==================== DTO 定义 ====================

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "用户信息响应体")
    public static class UserInfoDTO {
        @Schema(description = "用户 ID", example = "10001")
        private Long id;

        @Schema(description = "用户名", example = "kabuto_user")
        private String username;

        @Schema(description = "昵称", example = "Kabuto")
        private String nickname;

        @Schema(description = "邮箱", example = "user@kabuto.cloud")
        private String email;

        @Schema(description = "头像 URL", example = "https://kabuto.cloud/avatar/10001.png")
        private String avatarUrl;

        @Schema(description = "状态：0-禁用，1-正常", example = "1")
        private Integer status;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;

        @Schema(description = "更新时间")
        private LocalDateTime updateTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "创建用户请求体")
    public static class CreateUserRequest {
        @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "new_user")
        private String username;

        @Schema(description = "昵称", example = "New User")
        private String nickname;

        @Schema(description = "邮箱", example = "new@kabuto.cloud")
        private String email;

        @Schema(description = "头像 URL", example = "https://kabuto.cloud/avatar/default.png")
        private String avatarUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Schema(description = "更新用户请求体")
    public static class UpdateUserRequest {
        @Schema(description = "昵称", example = "Updated Name")
        private String nickname;

        @Schema(description = "邮箱", example = "updated@kabuto.cloud")
        private String email;

        @Schema(description = "头像 URL", example = "https://kabuto.cloud/avatar/new.png")
        private String avatarUrl;
    }
}
