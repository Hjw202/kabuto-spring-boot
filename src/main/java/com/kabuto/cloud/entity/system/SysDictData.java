package com.kabuto.cloud.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据实体
 *
 * <p><b>需求描述：</b>大王要求补齐表结构对应的 Java 实体类</p>
 * <p><b>解决方案：</b>使用 MyBatis-Plus 注解映射 sys_dict_data 表</p>
 * <p><b>原因说明：</b>字典数据存储具体的字典选项值，通过 dict_type 字段关联字典类型</p>
 */
@Data
@TableName("sys_dict_data")
public class SysDictData {

    /** 字典数据ID */
    @TableId(value = "dict_data_id", type = IdType.AUTO)
    private Long dictDataId;

    /** 字典类型 */
    @TableField("dict_type")
    private String dictType;

    /** 字典标签 */
    @TableField("dict_label")
    private String dictLabel;

    /** 字典键值 */
    @TableField("dict_value")
    private String dictValue;

    /** 字典排序 */
    @TableField("dict_sort")
    private Integer dictSort;

    /** 样式属性 */
    @TableField("css_class")
    private String cssClass;

    /** 表格回显样式 */
    @TableField("list_class")
    private String listClass;

    /** 是否默认 0=否 1=是 */
    @TableField("is_default")
    private Integer isDefault;

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
