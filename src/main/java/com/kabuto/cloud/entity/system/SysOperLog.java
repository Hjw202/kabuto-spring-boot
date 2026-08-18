package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 *
 * <p><b>需求描述：</b>大王要求补齐表结构对应的 Java 实体类</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_oper_log 表</p>
 * <p><b>原因说明：</b>操作日志记录用户的后台操作行为，用于审计和问题追踪</p>
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog {

    /** 日志主键 */
    @TableId(value = "oper_id", type = IdType.AUTO)
    private Long operId;

    /** 模块标题 */
    private String title;

    /** 业务类型 0=其它 1=新增 2=修改 3=删除 */
    @TableField("business_type")
    private Integer businessType;

    /** 方法名称 */
    private String method;

    /** 请求方式 */
    @TableField("request_method")
    private String requestMethod;

    /** 操作类别 0=其它 1=后台用户 2=手机端用户 */
    @TableField("operator_type")
    private Integer operatorType;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 操作人员 */
    @TableField("oper_name")
    private String operName;

    /** 部门名称（预留字段） */
    @TableField("dept_name")
    private String deptName;

    /** 请求URL */
    @TableField("oper_url")
    private String operUrl;

    /** 主机地址 */
    @TableField("oper_ip")
    private String operIp;

    /** 操作地点 */
    @TableField("oper_location")
    private String operLocation;

    /** 请求参数 */
    @TableField("oper_param")
    private String operParam;

    /** 返回参数 */
    @TableField("json_result")
    private String jsonResult;

    /** 操作时间 */
    @TableField("oper_time")
    private LocalDateTime operTime;

    /** 操作状态 0=正常 1=失败 */
    private Integer status;

    /** 错误消息 */
    @TableField("error_msg")
    private String errorMsg;

    /** 消耗时间（毫秒） */
    @TableField("cost_time")
    private Integer costTime;

    /** 记录创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;

    /** 逻辑删除 0=正常 1=删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
