package com.kabuto.cloud.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.dao.system.SysMenuMapper;
import com.kabuto.cloud.dao.system.SysPermissionMapper;
import com.kabuto.cloud.dto.system.CreateMenuDTO;
import com.kabuto.cloud.dto.system.SearchMenuDTO;
import com.kabuto.cloud.dto.system.UpdateMenuDTO;
import com.kabuto.cloud.entity.system.SysMenu;
import com.kabuto.cloud.entity.system.SysPermission;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.system.SysMenuService;
import com.kabuto.cloud.vo.system.MenuVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 菜单管理服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin MenuService 实现菜单管理业务逻辑</p>
 * <p><b>解决方案：</b>实现菜单 CRUD、树形结构构建、按钮类型自动关联权限等核心功能</p>
 * <p><b>原因说明：</b>对应 nest-admin MenuService。菜单类型 3(按钮) 时自动创建/关联 SysPermission 记录</p>
 */
@Slf4j
@Service
public class SysMenuServiceImpl implements SysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysPermissionMapper permissionMapper;

    public SysMenuServiceImpl(SysMenuMapper menuMapper, SysPermissionMapper permissionMapper) {
        this.menuMapper = menuMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public PageResult<MenuVO> pageTree(Integer pageNum, Integer pageSize, SearchMenuDTO dto) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getName()), SysMenu::getName, dto.getName())
                .eq(dto.getStatus() != null, SysMenu::getStatus, dto.getStatus())
                .orderByAsc(SysMenu::getSort);

        List<SysMenu> allMenus = menuMapper.selectList(wrapper);
        List<MenuVO> tree = buildTree(allMenus);

        // 分页根节点
        int total = tree.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<MenuVO> pageList = fromIndex < total ? tree.subList(fromIndex, toIndex) : new ArrayList<>();

        return new PageResult<>(pageList, (long) total, pageNum, pageSize);
    }

    @Override
    public List<MenuVO> getMenuList() {
        List<SysMenu> allMenus = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .orderByAsc(SysMenu::getSort));
        return buildTree(allMenus);
    }

    @Override
    public MenuVO getMenuById(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BizException(ResultCode.NOT_FOUND, "菜单不存在");
        }
        return convertToMenuVO(menu);
    }

    @Override
    public List<MenuVO> getPermissionList() {
        List<SysMenu> menus = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .in(SysMenu::getMenuType, 1, 2)
                        .orderByAsc(SysMenu::getSort));

        List<MenuVO> voList = menus.stream()
                .map(this::convertToMenuVO)
                .collect(Collectors.toList());
        return voList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMenu(CreateMenuDTO dto) {
        SysMenu menu = new SysMenu();
        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        menu.setName(dto.getName());
        menu.setSort(dto.getSort());
        menu.setRouter(dto.getRouter());
        menu.setComponent(dto.getComponent());
        menu.setQuery(dto.getQuery());
        menu.setIsFrame(dto.getIsFrame());
        menu.setIsCache(dto.getIsCache());
        menu.setMenuType(dto.getMenuType());
        menu.setVisible(dto.getVisible());
        menu.setStatus(dto.getStatus());
        menu.setRule(dto.getRule());
        menu.setIcon(dto.getIcon());
        menu.setCreateBy("admin");
        menu.setCreateTime(LocalDateTime.now());

        // 按钮类型（menuType=3）且有 rule 时，自动创建/关联权限
        if (Integer.valueOf(3).equals(dto.getMenuType()) && StringUtils.hasText(dto.getRule())) {
            SysPermission permission = findOrCreatePermission(dto.getRule(), dto.getName());
            menu.setPermissionId(permission.getPermissionId());
        }

        // 构建祖先路径
        if (menu.getParentId() != null && menu.getParentId() > 0) {
            SysMenu parent = menuMapper.selectById(menu.getParentId());
            if (parent != null) {
                menu.setAncestors(parent.getAncestors() + "," + parent.getMenuId());
            }
        } else {
            menu.setAncestors("0");
        }

        menuMapper.insert(menu);

        log.info("[创建菜单] menuId={}, name={}", menu.getMenuId(), menu.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMenu(Long id, UpdateMenuDTO dto) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BizException(ResultCode.NOT_FOUND, "菜单不存在");
        }

        // 更新基本信息
        if (dto.getParentId() != null) menu.setParentId(dto.getParentId());
        if (dto.getName() != null) menu.setName(dto.getName());
        if (dto.getSort() != null) menu.setSort(dto.getSort());
        if (dto.getRouter() != null) menu.setRouter(dto.getRouter());
        if (dto.getComponent() != null) menu.setComponent(dto.getComponent());
        if (dto.getQuery() != null) menu.setQuery(dto.getQuery());
        if (dto.getIsFrame() != null) menu.setIsFrame(dto.getIsFrame());
        if (dto.getIsCache() != null) menu.setIsCache(dto.getIsCache());
        if (dto.getMenuType() != null) menu.setMenuType(dto.getMenuType());
        if (dto.getVisible() != null) menu.setVisible(dto.getVisible());
        if (dto.getStatus() != null) menu.setStatus(dto.getStatus());
        if (dto.getRule() != null) menu.setRule(dto.getRule());
        if (dto.getIcon() != null) menu.setIcon(dto.getIcon());
        menu.setUpdateBy("admin");
        menu.setUpdateTime(LocalDateTime.now());

        // 按钮类型且有 rule 时，同步权限
        if (Integer.valueOf(3).equals(menu.getMenuType()) && StringUtils.hasText(menu.getRule())) {
            SysPermission permission = findOrCreatePermission(menu.getRule(), menu.getName());
            menu.setPermissionId(permission.getPermissionId());
        }

        menuMapper.updateById(menu);

        log.info("[更新菜单] menuId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long menuId, Integer status) {
        SysMenu menu = menuMapper.selectById(menuId);
        if (menu == null) {
            throw new BizException(ResultCode.NOT_FOUND, "菜单不存在");
        }

        menu.setStatus(status);
        menu.setUpdateBy("admin");
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.updateById(menu);

        log.info("[启停菜单状态] menuId={}, status={}", menuId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMenu(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        if (menu == null) {
            throw new BizException(ResultCode.NOT_FOUND, "菜单不存在");
        }

        // 检查是否有子菜单（目录/页面类型）
        Long childCount = menuMapper.selectCount(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getParentId, id)
                        .in(SysMenu::getMenuType, 1, 2));
        if (childCount > 0) {
            throw new BizException(ResultCode.BAD_REQUEST, "存在子菜单，不能删除");
        }

        // 删除子按钮权限
        List<SysMenu> buttons = menuMapper.selectList(
                new LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getParentId, id)
                        .eq(SysMenu::getMenuType, 3));
        for (SysMenu button : buttons) {
            if (button.getPermissionId() != null) {
                permissionMapper.deleteById(button.getPermissionId());
            }
            menuMapper.deleteById(button.getMenuId());
        }

        // 删除自身关联权限
        if (menu.getPermissionId() != null) {
            permissionMapper.deleteById(menu.getPermissionId());
        }

        // 逻辑删除菜单
        menuMapper.deleteById(id);

        log.info("[删除菜单] menuId={}", id);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 构建菜单树
     */
    private List<MenuVO> buildTree(List<SysMenu> allMenus) {
        Map<Long, MenuVO> voMap = allMenus.stream()
                .collect(Collectors.toMap(SysMenu::getMenuId, this::convertToMenuVO));

        List<MenuVO> tree = new ArrayList<>();
        for (SysMenu menu : allMenus) {
            MenuVO vo = voMap.get(menu.getMenuId());
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                tree.add(vo);
            } else {
                MenuVO parent = voMap.get(menu.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(vo);
                }
            }
        }
        return tree;
    }

    /**
     * 转换实体为 VO
     */
    private MenuVO convertToMenuVO(SysMenu menu) {
        MenuVO vo = new MenuVO();
        vo.setMenuId(menu.getMenuId());
        vo.setParentId(menu.getParentId());
        vo.setName(menu.getName());
        vo.setSort(menu.getSort());
        vo.setIcon(menu.getIcon());
        vo.setRouter(menu.getRouter());
        vo.setComponent(menu.getComponent());
        vo.setQuery(menu.getQuery());
        vo.setIsFrame(menu.getIsFrame());
        vo.setIsCache(menu.getIsCache());
        vo.setMenuType(menu.getMenuType());
        vo.setStatus(menu.getStatus());
        vo.setVisible(menu.getVisible());
        vo.setRule(menu.getRule());
        vo.setPermissionId(menu.getPermissionId());
        vo.setCreateTime(menu.getCreateTime());
        return vo;
    }

    /**
     * 查找或创建权限记录（按钮菜单自动关联）
     */
    private SysPermission findOrCreatePermission(String perms, String name) {
        // 先按 perms 查找
        SysPermission exist = permissionMapper.selectOne(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getPerms, perms));
        if (exist != null) {
            return exist;
        }

        // 不存在则创建
        SysPermission permission = new SysPermission();
        permission.setName(name);
        permission.setPerms(perms);
        permission.setStatus(1);
        permission.setCreateBy("admin");
        permission.setCreateTime(LocalDateTime.now());
        permissionMapper.insert(permission);

        log.info("[自动创建权限] permissionId={}, perms={}", permission.getPermissionId(), perms);
        return permission;
    }
}
