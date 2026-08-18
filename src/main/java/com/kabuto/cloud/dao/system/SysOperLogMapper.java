package com.kabuto.cloud.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabuto.cloud.entity.system.SysOperLog;

/**
 * 操作日志 Mapper
 *
 * <p><b>需求描述：</b>大王要求补齐表结构对应的数据访问层</p>
 * <p><b>解决方案：</b>继承 BaseMapper 获得 CRUD 能力</p>
 * <p><b>原因说明：</b>MyBatis-Plus BaseMapper 提供基础 CRUD，操作日志主要是插入和查询操作</p>
 */
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {
}
