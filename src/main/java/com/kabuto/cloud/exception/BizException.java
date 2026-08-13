package com.kabuto.cloud.exception;

import com.kabuto.cloud.common.enums.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 *
 * <p><b>需求描述：</b>大王要求按照网上项目习惯封装异常捕获，需要自定义业务异常类</p>
 * <p><b>解决方案：</b>创建继承 RuntimeException 的业务异常，支持传入消息和状态码</p>
 * <p><b>原因说明：</b>RuntimeException 无需在方法签名中声明 throws，代码更简洁；
 * 参考若依、芋道等主流开源项目设计。通过 ResultCode 实现状态码与消息的灵活组合</p>
 */
@Getter
public class BizException extends RuntimeException {

    /** 业务状态码 */
    private final ResultCode resultCode;

    /**
     * 指定错误消息（默认使用 ERROR 状态码）
     *
     * @param message 错误消息
     */
    public BizException(String message) {
        super(message);
        this.resultCode = ResultCode.ERROR;
    }

    /**
     * 指定状态码（使用状态码默认消息）
     *
     * @param resultCode 业务状态码
     */
    public BizException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }

    /**
     * 指定状态码 + 自定义消息
     *
     * @param resultCode 业务状态码
     * @param message    自定义错误消息
     */
    public BizException(ResultCode resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    /**
     * 指定错误消息 + 异常 cause
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public BizException(String message, Throwable cause) {
        super(message, cause);
        this.resultCode = ResultCode.ERROR;
    }

    /**
     * 获取状态码数值
     */
    public int getCode() {
        return resultCode.getCode();
    }
}
