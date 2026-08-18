package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体
 *
 * <p><b>需求描述：</b>大王要求补齐表结构对应的 Java 实体类</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_config 表</p>
 * <p><b>原因说明：</b>系统配置表用于存储运行时可动态调整的系统参数，如IP黑名单等</p>
 */
@Data
@TableName("sys_config")
public class SysConfig {

    /** 参数ID */
    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;

    /** 参数名称 */
    @TableField("config_name")
    private String configName;

    /** 参数键名 */
    @TableField("config_key")
    private String configKey;

    /** 参数键值 */
    @TableField("config_value")
    private String configValue;

    /** 是否系统内置 0=否 1=是 */
    @TableField("config_type")
    private Integer configType;

    /** 创建者 */
    @TableField("create_by")
    private String createBy;

    /** 创建时间 */
    @TableField("create_time")
    private LocalDateTime createTime;

    /** 更新者 */
    @TableField("update_by")
    private String updateBy;

    /** 更新时间 */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /** 逻辑删除 0=正常 1=删除 */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}
