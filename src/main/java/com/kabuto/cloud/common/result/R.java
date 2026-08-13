package com.kabuto.cloud.common.result;

import com.kabuto.cloud.common.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 统一响应结果类
 *
 * <p><b>需求描述：</b>大王要求封装统一响应类，统一前后端交互数据结构</p>
 * <p><b>解决方案：</b>创建泛型响应类 R<T>，包含 code / data / msg 三个字段，提供静态工厂方法快速构建响应</p>
 * <p><b>原因说明：</b>参考 nest-admin R.ts 设计，Spring Boot 项目通用做法。
 * 便于前端统一处理响应，Swagger 文档自动生成结构说明</p>
 *
 * @param <T> 返回数据类型
 */
@Data
@Schema(description = "统一响应结果")
public class R<T> {

    @Schema(description = "响应码", example = "200")
    private Integer code;

    @Schema(description = "返回数据")
    private T data;

    @Schema(description = "提示信息", example = "成功")
    private String msg;

    private R(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    // ==================== 成功响应 ====================

    /**
     * 成功响应（无数据）
     */
    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS.getCode(), null, ResultCode.SUCCESS.getMessage());
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), data, ResultCode.SUCCESS.getMessage());
    }

    /**
     * 成功响应（带数据和自定义消息）
     */
    public static <T> R<T> ok(T data, String msg) {
        return new R<>(ResultCode.SUCCESS.getCode(), data, msg);
    }

    // ==================== 失败响应 ====================

    /**
     * 失败响应（默认 400）
     */
    public static <T> R<T> fail(String msg) {
        return new R<>(ResultCode.BAD_REQUEST.getCode(), null, msg);
    }

    /**
     * 失败响应（指定状态码）
     */
    public static <T> R<T> fail(ResultCode resultCode, String msg) {
        return new R<>(resultCode.getCode(), null, msg);
    }

    /**
     * 失败响应（使用状态码默认消息）
     */
    public static <T> R<T> fail(ResultCode resultCode) {
        return new R<>(resultCode.getCode(), null, resultCode.getMessage());
    }

    // ==================== 快捷失败方法 ====================

    /**
     * 参数校验失败（422）
     */
    public static <T> R<T> validation(String msg) {
        return new R<>(ResultCode.VALIDATION.getCode(), null, msg);
    }

    /**
     * 请求过于频繁（429）
     */
    public static <T> R<T> frequent(String msg) {
        return new R<>(ResultCode.TOO_MANY_REQUESTS.getCode(), null, msg);
    }

    /**
     * 未授权（401）
     */
    public static <T> R<T> unauthorized(String msg) {
        return new R<>(ResultCode.UNAUTHORIZED.getCode(), null, msg);
    }

    /**
     * 无权限（403）
     */
    public static <T> R<T> forbidden(String msg) {
        return new R<>(ResultCode.FORBIDDEN.getCode(), null, msg);
    }

    /**
     * 资源不存在（404）
     */
    public static <T> R<T> notFound(String msg) {
        return new R<>(ResultCode.NOT_FOUND.getCode(), null, msg);
    }

    /**
     * 请求参数错误（400）
     */
    public static <T> R<T> badRequest(String msg) {
        return new R<>(ResultCode.BAD_REQUEST.getCode(), null, msg);
    }

    // ==================== 分页数据 ====================

    /**
     * 分页表格数据响应
     *
     * @param list 当前页数据列表
     * @param total 总记录数
     * @param page 当前页码
     * @param pageSize 每页条数
     */
    public static <T> R<PageResult<T>> tableData(List<T> list, Long total, Integer page, Integer pageSize) {
        PageResult<T> pageResult = new PageResult<>(list, total, page, pageSize);
        return new R<>(ResultCode.SUCCESS.getCode(), pageResult, "查询成功");
    }

    @Override
    public String toString() {
        return "R{code=" + code + ", data=" + data + ", msg='" + msg + "'}";
    }
}
