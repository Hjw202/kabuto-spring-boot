package com.kabuto.cloud.service.impl.system;

import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.common.enums.LoginStatusEnum;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.security.config.SecurityProperties;
import com.kabuto.cloud.security.jwt.JwtUtil;
import com.kabuto.cloud.dto.system.LoginUserDTO;
import com.kabuto.cloud.entity.system.*;
import com.kabuto.cloud.dao.system.*;
import com.kabuto.cloud.service.system.AuthService;
import com.kabuto.cloud.vo.system.LoginVO;
import com.kabuto.cloud.vo.system.RouterVO;
import com.kabuto.cloud.vo.system.UserInfoVO;
import com.kabuto.cloud.utils.BrowserUtil;
import com.kabuto.cloud.utils.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 认证服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin AuthService 实现完整的认证业务逻辑</p>
 * <p><b>解决方案：</b>实现登录、登出、获取路由、获取用户信息等核心功能，
 * 包含 JWT 生成、Redis 缓存、权限聚合、登录日志记录</p>
 * <p><b>原因说明：</b>对应 nest-admin AuthService 的完整实现。使用 BCrypt strength=12 加密密码，
 * Redis 缓存用户信息减少数据库查询，异步记录登录日志不阻塞主流程</p>
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysLoginInfoMapper loginInfoMapper;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final SecurityProperties securityProperties;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(SysUserMapper userMapper, SysRoleMapper roleMapper,
                           SysMenuMapper menuMapper, SysPermissionMapper permissionMapper,
                           SysLoginInfoMapper loginInfoMapper, JwtUtil jwtUtil,
                           StringRedisTemplate redisTemplate, SecurityProperties securityProperties) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.permissionMapper = permissionMapper;
        this.loginInfoMapper = loginInfoMapper;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.securityProperties = securityProperties;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO login(LoginUserDTO dto, String ip, String userAgent) {
        String username = dto.getUsername();
        String password = dto.getPassword();

        // ========== 1. 解析 IP 和浏览器信息 ==========
        String clientIp = IpUtil.getIpAddress(ip);
        String ipLocation = IpUtil.getIpLocation(clientIp);
        BrowserUtil.BrowserInfo browserInfo = BrowserUtil.parse(userAgent);

        // ========== 2. 准备登录日志 ==========
        SysLoginInfo loginLog = new SysLoginInfo();
        loginLog.setLoginInfoId(generateId());
        loginLog.setAccount(username);
        loginLog.setIpAddress(clientIp);
        loginLog.setLoginLocation(ipLocation);
        loginLog.setBrowser(browserInfo.getBrowser());
        loginLog.setOs(browserInfo.getOs());
        loginLog.setStatus(LoginStatusEnum.FAIL.getCode());
        loginLog.setLoginTime(LocalDateTime.now());

        try {
            // ========== 3. IP 黑名单检查 ==========
            String blackIpList = redisTemplate.opsForValue().get(Constants.SYS_CONFIG_KEY + "sys.login.blackIPList");
            if (StringUtils.hasText(blackIpList)) {
                List<String> blackIps = Arrays.asList(blackIpList.split(","));
                if (blackIps.contains(clientIp)) {
                    loginLog.setMsg("IP:" + clientIp + "被封禁");
                    saveLoginLog(loginLog);
                    throw new BizException("IP:" + clientIp + "被封禁");
                }
            }

            // ========== 4. 查询用户 ==========
            SysUser user = userMapper.selectUserWithRolesAndPermissions(username);
            if (user == null) {
                loginLog.setMsg("账号不存在");
                saveLoginLog(loginLog);
                throw new BizException(ResultCode.BAD_REQUEST, "账号不存在");
            }

            if (!Integer.valueOf(1).equals(user.getStatus())) {
                loginLog.setMsg("账号禁用");
                saveLoginLog(loginLog);
                throw new BizException(ResultCode.BAD_REQUEST, "账号禁用");
            }

            // ========== 5. 密码比对 ==========
            if (!passwordEncoder.matches(password, user.getPassword())) {
                loginLog.setMsg("密码错误");
                saveLoginLog(loginLog);
                throw new BizException(ResultCode.BAD_REQUEST, "密码错误");
            }

            // 登录成功
            loginLog.setStatus(LoginStatusEnum.SUCCESS.getCode());
            loginLog.setMsg("登录成功");

            // 更新用户登录信息
            user.setLoginIp(clientIp);
            user.setLoginDate(LocalDateTime.now());
            userMapper.updateById(user);

            // ========== 6. 生成 JWT ==========
            String tokenId = UUID.randomUUID().toString().replace("-", "");
            Map<String, Object> claims = new HashMap<>();
            claims.put("tokenId", tokenId);
            claims.put("id", user.getUserId());
            claims.put("username", user.getUsername());
            claims.put("ipAddress", clientIp);
            claims.put("loginLocation", ipLocation);
            claims.put("browser", browserInfo.getBrowser());
            claims.put("os", browserInfo.getOs());
            claims.put("loginTime", System.currentTimeMillis());

            String token = jwtUtil.generateToken(claims);
            long expiresMillis = securityProperties.getExpires();

            // ========== 7. 聚合权限 ==========
            // 7.1 角色权限
            List<String> rolePermissions = collectRolePermissions(user.getRoles());
            // 7.2 用户直接权限
            List<String> directPermissions = user.getPermissions() != null
                    ? user.getPermissions().stream()
                    .filter(p -> Integer.valueOf(1).equals(p.getStatus()))
                    .map(SysPermission::getPerms)
                    .filter(StringUtils::hasText)
                    .toList()
                    : Collections.emptyList();
            // 7.3 合并去重
            Set<String> allPermissions = new HashSet<>();
            allPermissions.addAll(rolePermissions);
            allPermissions.addAll(directPermissions);
            List<String> permissionList = new ArrayList<>(allPermissions);

            // 7.4 角色Key列表
            List<String> roleKeys = user.getRoles() != null
                    ? user.getRoles().stream()
                    .filter(r -> Integer.valueOf(1).equals(r.getStatus()))
                    .map(SysRole::getRoleKey)
                    .filter(StringUtils::hasText)
                    .toList()
                    : Collections.emptyList();

            // ========== 8. 构建缓存用户信息 ==========
            UserInfoVO cacheUser = new UserInfoVO();
            cacheUser.setUserId(user.getUserId());
            cacheUser.setUsername(user.getUsername());
            cacheUser.setName(user.getName());
            cacheUser.setAge(user.getAge());
            cacheUser.setSex(user.getSex());
            cacheUser.setPhone(user.getPhone());
            cacheUser.setEmail(user.getEmail());
            cacheUser.setAvatar(user.getAvatar());
            cacheUser.setStatus(user.getStatus());
            cacheUser.setRoles(roleKeys);
            cacheUser.setPermissions(permissionList);

            // ========== 9. 并行写入 Redis ==========
            redisTemplate.opsForValue().set(
                    Constants.LOGIN_TOKEN_KEY + tokenId,
                    token,
                    expiresMillis,
                    TimeUnit.MILLISECONDS
            );
            redisTemplate.opsForValue().set(
                    Constants.LOGIN_CACHE_TOKEN_KEY + user.getUserId(),
                    com.alibaba.fastjson2.JSON.toJSONString(cacheUser),
                    expiresMillis,
                    TimeUnit.MILLISECONDS
            );

            // ========== 10. 异步保存登录日志 ==========
            saveLoginLogAsync(loginLog);

            // ========== 11. 构建响应 ==========
            LoginVO loginVO = new LoginVO();

            LoginVO.UserInfo userInfo = new LoginVO.UserInfo();
            userInfo.setId(user.getUserId());
            userInfo.setUsername(user.getUsername());
            userInfo.setName(user.getName());
            userInfo.setEmail(user.getEmail());
            userInfo.setPhone(user.getPhone());
            userInfo.setStatus(user.getStatus());
            userInfo.setRoles(user.getRoles() != null
                    ? user.getRoles().stream().map(SysRole::getRoleId).toList()
                    : Collections.emptyList());

            loginVO.setUser(userInfo);
            loginVO.setToken(token);
            loginVO.setPermissions(permissionList);

            return loginVO;

        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("[登录异常] username={}, error={}", username, e.getMessage(), e);
            loginLog.setMsg("登录异常: " + e.getMessage());
            saveLoginLogAsync(loginLog);
            throw new BizException(ResultCode.ERROR, "登录失败");
        }
    }

    @Override
    public void logout(String token) {
        if (!StringUtils.hasText(token) || !token.startsWith(Constants.TOKEN_PREFIX)) {
            return;
        }

        String realToken = token.substring(Constants.TOKEN_PREFIX.length()).trim();

        try {
            String tokenId = jwtUtil.getTokenId(realToken);
            if (tokenId != null) {
                redisTemplate.delete(Constants.LOGIN_TOKEN_KEY + tokenId);
            }
        } catch (Exception e) {
            log.warn("[退出登录] Token 解析失败: {}", e.getMessage());
        }
    }

    @Override
    public List<RouterVO> getRouters(Long userId) {
        List<SysMenu> menus;

        if (isAdmin(userId)) {
            // 超管：查询全部路由菜单
            menus = menuMapper.selectAllRouters();
        } else {
            // 普通用户：通过角色关联查询
            menus = menuMapper.selectRoutersByUserId(userId);
        }

        if (CollectionUtils.isEmpty(menus)) {
            return Collections.emptyList();
        }

        // 转换为 VO（过滤系统字段）
        return menus.stream()
                .map(this::convertToRouterVO)
                .collect(Collectors.toList());
    }

    @Override
    public UserInfoVO getInfo(Long userId) {
        // 1. 优先从 Redis 缓存获取
        String cacheKey = Constants.LOGIN_CACHE_TOKEN_KEY + userId;
        String cacheJson = redisTemplate.opsForValue().get(cacheKey);

        if (StringUtils.hasText(cacheJson)) {
            try {
                UserInfoVO cached = com.alibaba.fastjson2.JSON.parseObject(cacheJson, UserInfoVO.class);
                if (cached != null) {
                    return cached;
                }
            } catch (Exception e) {
                log.warn("[获取用户信息] Redis 缓存解析失败，回查数据库: {}", e.getMessage());
            }
        }

        // 2. 缓存未命中：查询数据库
        UserInfoVO userInfo = buildUserInfoFromDb(userId);
        if (userInfo == null) {
            throw new BizException("用户不存在");
        }

        // 3. 写入缓存
        long expiresMillis = securityProperties.getExpires();
        redisTemplate.opsForValue().set(
                cacheKey,
                com.alibaba.fastjson2.JSON.toJSONString(userInfo),
                expiresMillis,
                TimeUnit.MILLISECONDS
        );

        return userInfo;
    }

    @Override
    public boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        // 超管ID为 1
        if (Constants.SUPER_ADMIN_ID.equals(userId.toString())) {
            return true;
        }
        // 或拥有 *:*:* 权限
        List<String> permissions = getUserPermissions(userId);
        return permissions.contains("*:*:*");
    }

    @Override
    public boolean refreshUserInfo(Long userId) {
        UserInfoVO userInfo = buildUserInfoFromDb(userId);
        if (userInfo == null) {
            return false;
        }

        long expiresMillis = securityProperties.getExpires();
        redisTemplate.opsForValue().set(
                Constants.LOGIN_CACHE_TOKEN_KEY + userId,
                com.alibaba.fastjson2.JSON.toJSONString(userInfo),
                expiresMillis,
                TimeUnit.MILLISECONDS
        );

        log.info("[刷新用户信息] userId={}", userId);
        return true;
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 收集角色的权限标识
     */
    private List<String> collectRolePermissions(List<SysRole> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }

        // 过滤有效角色
        List<Long> roleIds = roles.stream()
                .filter(r -> Integer.valueOf(1).equals(r.getStatus()))
                .map(SysRole::getRoleId)
                .toList();

        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }

        // 批量查询权限（避免 N+1）
        String roleIdStr = roleIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        Set<String> perms = new HashSet<>();

        // 从角色-权限关联查询
        List<String> rolePerms = roleMapper.selectPermissionsByRoleIds(roleIdStr);
        perms.addAll(rolePerms);

        // 从角色-菜单关联查询 rule 字段（兼容旧数据）
        List<String> menuRules = roleMapper.selectMenuRulesByRoleIds(roleIdStr);
        for (String rule : menuRules) {
            if (StringUtils.hasText(rule) && rule.contains(":")) {
                perms.add(rule);
            }
        }

        return new ArrayList<>(perms);
    }

    /**
     * 从数据库构建用户信息
     */
    private UserInfoVO buildUserInfoFromDb(Long userId) {
        SysUser user = userMapper.selectUserWithRolesAndPermissionsById(userId);
        if (user == null) {
            return null;
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setName(user.getName());
        vo.setAge(user.getAge());
        vo.setSex(user.getSex());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());

        // 角色
        List<String> roleKeys = user.getRoles() != null
                ? user.getRoles().stream()
                .filter(r -> Integer.valueOf(1).equals(r.getStatus()))
                .map(SysRole::getRoleKey)
                .filter(StringUtils::hasText)
                .toList()
                : Collections.emptyList();
        vo.setRoles(roleKeys);

        // 权限
        List<String> rolePermissions = collectRolePermissions(user.getRoles());
        List<String> directPermissions = user.getPermissions() != null
                ? user.getPermissions().stream()
                .filter(p -> Integer.valueOf(1).equals(p.getStatus()))
                .map(SysPermission::getPerms)
                .filter(StringUtils::hasText)
                .toList()
                : Collections.emptyList();

        Set<String> allPerms = new HashSet<>();
        allPerms.addAll(rolePermissions);
        allPerms.addAll(directPermissions);
        vo.setPermissions(new ArrayList<>(allPerms));

        return vo;
    }

    /**
     * 获取用户权限列表
     */
    private List<String> getUserPermissions(Long userId) {
        String cacheKey = Constants.LOGIN_CACHE_TOKEN_KEY + userId;
        String cacheJson = redisTemplate.opsForValue().get(cacheKey);

        if (StringUtils.hasText(cacheJson)) {
            try {
                UserInfoVO cached = com.alibaba.fastjson2.JSON.parseObject(cacheJson, UserInfoVO.class);
                if (cached != null && cached.getPermissions() != null) {
                    return cached.getPermissions();
                }
            } catch (Exception ignored) {
            }
        }

        UserInfoVO userInfo = buildUserInfoFromDb(userId);
        return userInfo != null ? userInfo.getPermissions() : Collections.emptyList();
    }

    /**
     * 转换菜单为路由 VO
     */
    private RouterVO convertToRouterVO(SysMenu menu) {
        RouterVO vo = new RouterVO();
        vo.setMenuId(menu.getMenuId());
        vo.setParentId(menu.getParentId());
        vo.setName(menu.getName());
        vo.setRouter(menu.getRouter());
        vo.setComponent(menu.getComponent());
        vo.setQuery(menu.getQuery());
        vo.setIsFrame(menu.getIsFrame());
        vo.setIsCache(menu.getIsCache());
        vo.setMenuType(menu.getMenuType());
        vo.setVisible(menu.getVisible());
        vo.setRule(menu.getRule());
        vo.setIcon(menu.getIcon());
        return vo;
    }

    /**
     * 同步保存登录日志
     */
    private void saveLoginLog(SysLoginInfo loginLog) {
        try {
            loginInfoMapper.insert(loginLog);
        } catch (Exception e) {
            log.error("[保存登录日志失败] {}", e.getMessage());
        }
    }

    /**
     * 异步保存登录日志
     */
    @Async
    public void saveLoginLogAsync(SysLoginInfo loginLog) {
        saveLoginLog(loginLog);
    }

    /**
     * 生成唯一ID（雪花算法替代方案）
     */
    private Long generateId() {
        // 使用 UUID 的 hashCode + 时间戳作为ID
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}
