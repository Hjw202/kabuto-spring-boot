package com.kabuto.cloud.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.dao.system.SysPermissionMapper;
import com.kabuto.cloud.dao.system.SysRoleMapper;
import com.kabuto.cloud.dao.system.SysUserMapper;
import com.kabuto.cloud.dto.system.*;
import com.kabuto.cloud.entity.system.SysPermission;
import com.kabuto.cloud.entity.system.SysRole;
import com.kabuto.cloud.entity.system.SysUser;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.system.AuthService;
import com.kabuto.cloud.service.system.SysUserService;
import com.kabuto.cloud.vo.system.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UserService 实现用户管理业务逻辑</p>
 * <p><b>解决方案：</b>实现用户 CRUD、个人中心、授权管理等核心功能，
 * 包含 BCrypt 密码加密、Redis token 管理、角色/权限关联</p>
 * <p><b>原因说明：</b>对应 nest-admin UserService 的完整实现。使用 MyBatis-Plus LambdaQueryWrapper
 * 替代 TypeORM FindOptionsWhere，Spring @Transactional 替代 DataSource.transaction()</p>
 */
@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final AuthService authService;
    private final StringRedisTemplate redisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public SysUserServiceImpl(SysUserMapper userMapper, SysRoleMapper roleMapper,
                              SysPermissionMapper permissionMapper, AuthService authService,
                              StringRedisTemplate redisTemplate) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.permissionMapper = permissionMapper;
        this.authService = authService;
        this.redisTemplate = redisTemplate;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    // ==================== 用户管理 ====================

    @Override
    public PageResult<UserVO> page(Integer pageNum, Integer pageSize, SearchUserDTO dto) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getUsername()), SysUser::getUsername, dto.getUsername())
                .eq(dto.getStatus() != null, SysUser::getStatus, dto.getStatus())
                .ge(dto.getStartTime() != null, SysUser::getCreateTime, dto.getStartTime())
                .le(dto.getEndTime() != null, SysUser::getCreateTime, dto.getEndTime())
                .orderByAsc(SysUser::getCreateTime);

        Page<SysUser> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<UserVO> voList = page.getRecords().stream()
                .map(u -> convertToUserVO(u, false))
                .collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return convertToUserVO(user, false);
    }

    @Override
    public UserVO getUserDetail(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return convertToUserVO(user, true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(CreateUserDTO dto) {
        // 1. 校验用户名唯一
        Long existCount = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
        if (existCount > 0) {
            throw new BizException(ResultCode.BAD_REQUEST, "用户名已存在");
        }

        // 2. 校验手机号唯一
        if (StringUtils.hasText(dto.getPhone())) {
            existCount = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, dto.getPhone()));
            if (existCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST, "手机号已被使用");
            }
        }

        // 3. 校验邮箱唯一
        if (StringUtils.hasText(dto.getEmail())) {
            existCount = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, dto.getEmail()));
            if (existCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST, "邮箱已被使用");
            }
        }

        // 4. 构建用户实体
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setSex(dto.getSex());
        user.setStatus(dto.getStatus());
        user.setCreateBy("admin");
        user.setCreateTime(LocalDateTime.now());

        userMapper.insert(user);

        // 5. 关联角色
        if (!CollectionUtils.isEmpty(dto.getRoleIds())) {
            for (Long roleId : dto.getRoleIds()) {
                userMapper.insertUserRole(user.getUserId(), roleId);
            }
        }

        log.info("[创建用户] userId={}, username={}", user.getUserId(), user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long id, UpdateUserDTO dto) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 校验手机号唯一（排除自身）
        if (StringUtils.hasText(dto.getPhone())) {
            Long existCount = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getPhone, dto.getPhone())
                            .ne(SysUser::getUserId, id));
            if (existCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST, "手机号已被使用");
            }
        }

        // 校验邮箱唯一（排除自身）
        if (StringUtils.hasText(dto.getEmail())) {
            Long existCount = userMapper.selectCount(
                    new LambdaQueryWrapper<SysUser>()
                            .eq(SysUser::getEmail, dto.getEmail())
                            .ne(SysUser::getUserId, id));
            if (existCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST, "邮箱已被使用");
            }
        }

        // 更新基本信息
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setSex(dto.getSex());
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        user.setUpdateBy("admin");
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 重设角色关联
        if (dto.getRoleIds() != null) {
            userMapper.deleteUserRoles(id);
            for (Long roleId : dto.getRoleIds()) {
                userMapper.insertUserRole(id, roleId);
            }
        }

        // 刷新缓存
        authService.refreshUserInfo(id);

        log.info("[更新用户] userId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, Integer status) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }

        user.setStatus(status);
        user.setUpdateBy("admin");
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 禁用用户时，清除其登录 token
        if (Integer.valueOf(0).equals(status)) {
            clearUserTokens(id);
        }

        log.info("[启停用户状态] userId={}, status={}", id, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUsers(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        // 校验不能删除超管
        if (ids.contains(1L)) {
            throw new BizException(ResultCode.BAD_REQUEST, "不能删除超级管理员");
        }

        for (Long id : ids) {
            SysUser user = userMapper.selectById(id);
            if (user == null) {
                continue;
            }
            userMapper.deleteById(id);
            // 清除登录 token
            clearUserTokens(id);
        }

        log.info("[批量删除用户] ids={}", ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPwd(ResetPwdDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setUpdateBy("admin");
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 清除登录 token，强制重新登录
        clearUserTokens(dto.getId());

        log.info("[重置密码] userId={}", dto.getId());
    }

    @Override
    public UserVO getUserRoles(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return convertToUserVO(user, true);
    }

    @Override
    public AuthorizeVO getUserAuthorize(Long id) {
        AuthorizeVO vo = new AuthorizeVO();
        vo.setRoles(userMapper.selectRoleIdsByUserId(id));
        vo.setPermissions(userMapper.selectPermissionIdsByUserId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAuthorize(Long userId, List<Long> roleIds, List<Long> permissionIds) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 重建角色关联
        userMapper.deleteUserRoles(userId);
        if (!CollectionUtils.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                userMapper.insertUserRole(userId, roleId);
            }
        }

        // 重建权限关联
        userMapper.deleteUserPermissions(userId);
        if (!CollectionUtils.isEmpty(permissionIds)) {
            for (Long permissionId : permissionIds) {
                userMapper.insertUserPermission(userId, permissionId);
            }
        }

        // 刷新缓存
        authService.refreshUserInfo(userId);

        log.info("[保存授权] userId={}, roleIds={}, permissionIds={}", userId, roleIds, permissionIds);
    }

    // ==================== 个人中心 ====================

    @Override
    public UserVO getUserProfile(Long userId) {
        // 使用关联查询加载角色信息
        SysUser user = userMapper.selectUserWithRolesAndPermissionsById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }
        UserVO vo = convertToUserVO(user, true);
        // 补充 roleGroup（逗号分隔的角色名称）
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            String roleGroup = user.getRoles().stream()
                    .map(SysRole::getName)
                    .collect(Collectors.joining(", "));
            log.debug("[个人信息] userId={}, roleGroup={}", userId, roleGroup);
        }
        return vo;
    }

    @Override
    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setSex(dto.getSex());
        user.setPhone(dto.getPhone());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 刷新 Redis 缓存
        authService.refreshUserInfo(userId);
    }

    @Override
    public void updateAvatar(Long userId, String avatar) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }

        user.setAvatar(avatar);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 刷新 Redis 缓存
        authService.refreshUserInfo(userId);
    }

    @Override
    public void updatePwd(Long userId, String oldPwd, String newPwd) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 校验旧密码
        if (!passwordEncoder.matches(oldPwd, user.getPassword())) {
            throw new BizException(ResultCode.BAD_REQUEST, "旧密码错误");
        }

        user.setPassword(passwordEncoder.encode(newPwd));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("[修改密码] userId={}", userId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换实体为 VO
     *
     * @param user     用户实体
     * @param loadRole 是否加载角色ID列表
     */
    private UserVO convertToUserVO(SysUser user, boolean loadRole) {
        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setBirthday(user.getBirthday());
        vo.setSex(user.getSex());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setLoginIp(user.getLoginIp());
        vo.setLoginDate(user.getLoginDate());
        vo.setCreateTime(user.getCreateTime());

        if (loadRole) {
            List<Long> roleIds = userMapper.selectRoleIdsByUserId(user.getUserId());
            vo.setRoleIds(roleIds != null ? roleIds : Collections.emptyList());
        }

        return vo;
    }

    /**
     * 清除用户的登录 token（强制下线）
     */
    private void clearUserTokens(Long userId) {
        try {
            redisTemplate.delete(Constants.LOGIN_CACHE_TOKEN_KEY + userId);
            log.info("[清除用户Token] userId={}", userId);
        } catch (Exception e) {
            log.error("[清除用户Token失败] userId={}, error={}", userId, e.getMessage());
        }
    }
}
