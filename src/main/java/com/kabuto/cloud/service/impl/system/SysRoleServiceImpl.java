package com.kabuto.cloud.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dao.system.SysRoleMapper;
import com.kabuto.cloud.dao.system.SysUserMapper;
import com.kabuto.cloud.dto.system.CreateRoleDTO;
import com.kabuto.cloud.dto.system.SearchRoleDTO;
import com.kabuto.cloud.dto.system.UpdateRoleDTO;
import com.kabuto.cloud.entity.system.SysRole;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.system.AuthService;
import com.kabuto.cloud.service.system.SysRoleService;
import com.kabuto.cloud.vo.system.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin RoleService 实现角色管理业务逻辑</p>
 * <p><b>解决方案：</b>实现角色 CRUD、菜单/权限关联管理等核心功能</p>
 * <p><b>原因说明：</b>对应 nest-admin RoleService。使用事务保证角色与菜单/权限关联的一致性</p>
 */
@Slf4j
@Service
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysUserMapper userMapper;
    private final AuthService authService;
    private final StringRedisTemplate redisTemplate;

    public SysRoleServiceImpl(SysRoleMapper roleMapper, SysUserMapper userMapper,
                              AuthService authService, StringRedisTemplate redisTemplate) {
        this.roleMapper = roleMapper;
        this.userMapper = userMapper;
        this.authService = authService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public R<PageResult<RoleVO>> page(Integer pageNum, Integer pageSize, SearchRoleDTO dto) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getName()), SysRole::getName, dto.getName())
                .eq(StringUtils.hasText(dto.getRoleKey()), SysRole::getRoleKey, dto.getRoleKey())
                .eq(dto.getStatus() != null, SysRole::getStatus, dto.getStatus())
                .ge(dto.getStartTime() != null, SysRole::getCreateTime, dto.getStartTime())
                .le(dto.getEndTime() != null, SysRole::getCreateTime, dto.getEndTime())
                .orderByAsc(SysRole::getSort);

        Page<SysRole> page = roleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<RoleVO> voList = page.getRecords().stream()
                .map(this::convertToRoleVO)
                .collect(Collectors.toList());

        return R.tableData(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public R<List<RoleVO>> getAllRoles() {
        List<SysRole> roles = roleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .eq(SysRole::getStatus, 1)
                        .orderByAsc(SysRole::getSort));

        List<RoleVO> voList = roles.stream()
                .map(this::convertToRoleVO)
                .collect(Collectors.toList());

        return R.ok(voList);
    }

    @Override
    public R<RoleVO> getRoleDetail(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            return R.notFound("角色不存在");
        }

        RoleVO vo = convertToRoleVO(role);
        vo.setMenuIds(roleMapper.selectMenuIdsByRoleId(id));
        vo.setPermissionIds(roleMapper.selectPermissionIdsByRoleId(id));

        return R.ok(vo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> createRole(CreateRoleDTO dto) {
        // 校验角色名称唯一
        Long existCount = roleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getName, dto.getName()));
        if (existCount > 0) {
            throw new BizException(ResultCode.BAD_REQUEST, "角色名称已存在");
        }

        // 校验权限字符唯一
        existCount = roleMapper.selectCount(
                new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, dto.getRoleKey()));
        if (existCount > 0) {
            throw new BizException(ResultCode.BAD_REQUEST, "权限字符已存在");
        }

        // 创建角色
        SysRole role = new SysRole();
        role.setName(dto.getName());
        role.setRoleKey(dto.getRoleKey());
        role.setSort(dto.getSort());
        role.setStatus(dto.getStatus());
        role.setDescription(dto.getDescription());
        role.setCreateBy("admin");
        role.setCreateTime(LocalDateTime.now());
        roleMapper.insert(role);

        // 关联菜单
        if (!CollectionUtils.isEmpty(dto.getMenuIds())) {
            for (Long menuId : dto.getMenuIds()) {
                roleMapper.insertRoleMenu(role.getRoleId(), menuId);
            }
        }

        // 关联权限
        if (!CollectionUtils.isEmpty(dto.getPermissionIds())) {
            for (Long permissionId : dto.getPermissionIds()) {
                roleMapper.insertRolePermission(role.getRoleId(), permissionId);
            }
        }

        log.info("[创建角色] roleId={}, name={}", role.getRoleId(), role.getName());
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> updateRole(Long id, UpdateRoleDTO dto) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BizException(ResultCode.NOT_FOUND, "角色不存在");
        }

        // 校验角色名称唯一（排除自身）
        if (StringUtils.hasText(dto.getName())) {
            Long existCount = roleMapper.selectCount(
                    new LambdaQueryWrapper<SysRole>()
                            .eq(SysRole::getName, dto.getName())
                            .ne(SysRole::getRoleId, id));
            if (existCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST, "角色名称已存在");
            }
        }

        // 校验权限字符唯一（排除自身）
        if (StringUtils.hasText(dto.getRoleKey())) {
            Long existCount = roleMapper.selectCount(
                    new LambdaQueryWrapper<SysRole>()
                            .eq(SysRole::getRoleKey, dto.getRoleKey())
                            .ne(SysRole::getRoleId, id));
            if (existCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST, "权限字符已存在");
            }
        }

        // 更新角色基本信息
        role.setName(dto.getName());
        role.setRoleKey(dto.getRoleKey());
        role.setSort(dto.getSort());
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
        role.setDescription(dto.getDescription());
        role.setUpdateBy("admin");
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);

        // 重建菜单关联
        if (dto.getMenuIds() != null) {
            roleMapper.deleteRoleMenus(id);
            for (Long menuId : dto.getMenuIds()) {
                roleMapper.insertRoleMenu(id, menuId);
            }
        }

        // 重建权限关联
        if (dto.getPermissionIds() != null) {
            roleMapper.deleteRolePermissions(id);
            for (Long permissionId : dto.getPermissionIds()) {
                roleMapper.insertRolePermission(id, permissionId);
            }
        }

        // 刷新拥有该角色的所有用户的缓存
        refreshRoleUsersCache(id);

        log.info("[更新角色] roleId={}", id);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> changeStatus(Long id, Integer status) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BizException(ResultCode.NOT_FOUND, "角色不存在");
        }

        role.setStatus(status);
        role.setUpdateBy("admin");
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);

        // 禁用角色时，清除拥有该角色的所有用户的登录 token
        if (Integer.valueOf(0).equals(status)) {
            refreshRoleUsersCache(id);
        }

        log.info("[启停角色状态] roleId={}, status={}", id, status);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> deleteRoles(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return R.ok();
        }

        // 校验不能删除超管角色（role_id=1）
        if (ids.contains(1L)) {
            throw new BizException(ResultCode.BAD_REQUEST, "不能删除超级管理员角色");
        }

        for (Long id : ids) {
            SysRole role = roleMapper.selectById(id);
            if (role == null) {
                continue;
            }

            // 删除角色关联
            roleMapper.deleteRoleMenus(id);
            roleMapper.deleteRolePermissions(id);

            // 逻辑删除角色
            roleMapper.deleteById(id);

            // 清除拥有该角色的用户缓存
            refreshRoleUsersCache(id);
        }

        log.info("[批量删除角色] ids={}", ids);
        return R.ok();
    }

    @Override
    public R<RoleAuthorizeVO> getRoleAuthorize(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            return R.notFound("角色不存在");
        }

        RoleAuthorizeVO vo = new RoleAuthorizeVO();
        vo.setMenuIds(roleMapper.selectMenuIdsByRoleId(id));
        return R.ok(vo);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换实体为 VO
     */
    private RoleVO convertToRoleVO(SysRole role) {
        RoleVO vo = new RoleVO();
        vo.setRoleId(role.getRoleId());
        vo.setName(role.getName());
        vo.setRoleKey(role.getRoleKey());
        vo.setSort(role.getSort());
        vo.setIsAdmin(role.getIsAdmin());
        vo.setStatus(role.getStatus());
        vo.setDescription(role.getDescription());
        vo.setCreateTime(role.getCreateTime());
        return vo;
    }

    /**
     * 刷新拥有指定角色的所有用户的 Redis 缓存
     */
    private void refreshRoleUsersCache(Long roleId) {
        try {
            List<Long> userIds = userMapper.selectUserIdsByRoleId(roleId);

            for (Long userId : userIds) {
                authService.refreshUserInfo(userId);
            }

            log.info("[刷新角色用户缓存] roleId={}, userCount={}", roleId, userIds.size());
        } catch (Exception e) {
            log.error("[刷新角色用户缓存失败] roleId={}, error={}", roleId, e.getMessage(), e);
        }
    }
}
