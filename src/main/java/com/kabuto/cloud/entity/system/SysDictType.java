package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型实体
 *
 * <p><b>需求描述：</b>大王要求补齐表结构对应的 Java 实体类</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_dict_type 表</p>
 * <p><b>原因说明：</b>字典类型用于管理系统中各类下拉选项的分类，如性别、状态等</p>
 */
@Data
@TableName("sys_dict_type")
public class SysDictType {

    /** 字典类型ID */
    @TableId(value = "dict_type_id", type = IdType.AUTO)
    private Long dictTypeId;

    /** 字典名称 */
    @TableField("dict_name")
    private String dictName;

    /** 字典类型 */
    @TableField("dict_type")
    private String dictType;

    /** 字典状态 0=停用 1=启用 */
    private Integer status;

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
