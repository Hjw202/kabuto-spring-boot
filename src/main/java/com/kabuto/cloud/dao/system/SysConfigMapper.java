package com.kabuto.cloud.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabuto.cloud.entity.system.SysConfig;

/**
 * 系统配置 Mapper
 *
 * <p><b>需求描述：</b>大王要求补齐表结构对应的数据访问层</p>
 * <p><b>解决方案：</b>继承 BaseMapper 获得 CRUD 能力</p>
 * <p><b>原因说明：</b>MyBatis-Plus BaseMapper 提供基础 CRUD</p>
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {
}
