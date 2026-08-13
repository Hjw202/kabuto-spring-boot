package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.LoginUserDTO;
import com.kabuto.cloud.vo.system.LoginVO;
import com.kabuto.cloud.vo.system.RouterVO;
import com.kabuto.cloud.vo.system.UserInfoVO;

import java.util.List;

/**
 * 认证服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin AuthService 实现认证服务层</p>
 * <p><b>解决方案：</b>定义认证相关服务接口，包含登录、登出、获取路由、获取用户信息</p>
 * <p><b>原因说明：</b>对应 nest-admin AuthService。接口层定义契约，实现层处理具体业务逻辑</p>
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param dto       登录参数
     * @param ip        客户端IP
     * @param userAgent 客户端User-Agent
     * @return 登录响应（Token + 用户信息 + 权限）
     */
    R<LoginVO> login(LoginUserDTO dto, String ip, String userAgent);

    /**
     * 退出登录
     *
     * @param token 用户Token
     * @return 操作结果
     */
    R<Void> logout(String token);

    /**
     * 获取用户可访问的路由菜单
     *
     * @param userId 用户ID
     * @return 路由列表
     */
    R<List<RouterVO>> getRouters(Long userId);

    /**
     * 获取用户信息（含角色和权限）
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    R<UserInfoVO> getInfo(Long userId);

    /**
     * 判断用户是否为超级管理员
     *
     * @param userId 用户ID
     * @return true=是超管
     */
    boolean isAdmin(Long userId);

    /**
     * 刷新用户缓存信息
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean refreshUserInfo(Long userId);
}
