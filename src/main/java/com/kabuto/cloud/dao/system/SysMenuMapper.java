package com.kabuto.cloud.dao.system;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kabuto.cloud.entity.system.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 菜单 Mapper
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin 实现菜单数据访问层</p>
 * <p><b>解决方案：</b>继承 BaseMapper 获得 CRUD 能力，自定义路由查询方法</p>
 * <p><b>原因说明：</b>MyBatis-Plus BaseMapper 提供基础 CRUD，自定义 SQL 实现超管/普通用户的路由查询</p>
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 查询全部路由菜单（超管用）
     * menu_type 为 1(目录) 或 2(页面)，status = 1
     */
    List<SysMenu> selectAllRouters();

    /**
     * 根据用户ID查询路由菜单（普通用户用）
     * 通过 user_role_menu 关联查询
     */
    List<SysMenu> selectRoutersByUserId(@Param("userId") Long userId);
}
