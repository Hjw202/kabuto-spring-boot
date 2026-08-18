package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.CreateMenuDTO;
import com.kabuto.cloud.dto.system.SearchMenuDTO;
import com.kabuto.cloud.dto.system.UpdateMenuDTO;
import com.kabuto.cloud.vo.system.MenuVO;

import java.util.List;

/**
 * 菜单管理服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin MenuService 实现菜单管理服务层</p>
 * <p><b>解决方案：</b>定义菜单 CRUD、树形结构等服务接口</p>
 * <p><b>原因说明：</b>对应 nest-admin MenuService</p>
 */
public interface SysMenuService {

    /**
     * 分页查询菜单树形列表
     */
    R<PageResult<MenuVO>> pageTree(Integer pageNum, Integer pageSize, SearchMenuDTO dto);

    /**
     * 查询所有菜单列表（树形）
     */
    R<List<MenuVO>> getMenuList();

    /**
     * 查询菜单详情
     */
    R<MenuVO> getMenuById(Long id);

    /**
     * 查询权限菜单列表（D/P类型）
     */
    R<List<MenuVO>> getPermissionList();

    /**
     * 创建菜单（按钮类型自动创建/关联权限）
     */
    R<Void> createMenu(CreateMenuDTO dto);

    /**
     * 更新菜单（同步更新关联权限）
     */
    R<Void> updateMenu(Long id, UpdateMenuDTO dto);

    /**
     * 启停菜单状态
     */
    R<Void> changeStatus(Long menuId, Integer status);

    /**
     * 删除菜单
     */
    R<Void> deleteMenu(Long id);
}
