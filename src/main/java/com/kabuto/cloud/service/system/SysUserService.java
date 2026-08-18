package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dto.system.*;
import com.kabuto.cloud.vo.system.UserVO;

import java.util.List;

/**
 * 用户管理服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin UserService 实现用户管理服务层</p>
 * <p><b>解决方案：</b>定义用户 CRUD、个人中心、授权管理等服务接口</p>
 * <p><b>原因说明：</b>对应 nest-admin UserService。接口层定义契约，实现层处理具体业务逻辑</p>
 */
public interface SysUserService {

    /**
     * 分页查询用户列表
     */
    R<PageResult<UserVO>> page(Integer pageNum, Integer pageSize, SearchUserDTO dto);

    /**
     * 根据ID查询用户基本信息
     */
    UserVO getUserById(Long id);

    /**
     * 查询用户详情（含角色ID列表）
     */
    UserVO getUserDetail(Long id);

    /**
     * 创建用户
     */
    R<Void> createUser(CreateUserDTO dto);

    /**
     * 编辑用户
     */
    R<Void> updateUser(Long id, UpdateUserDTO dto);

    /**
     * 启停用户状态
     */
    R<Void> changeStatus(Long id, Integer status);

    /**
     * 批量删除用户
     */
    R<Void> deleteUsers(List<Long> ids);

    /**
     * 管理员重置用户密码
     */
    R<Void> resetPwd(ResetPwdDTO dto);

    /**
     * 查看用户角色
     */
    UserVO getUserRoles(Long id);

    /**
     * 查看用户授权信息（角色 + 权限）
     */
    R<AuthorizeVO> getUserAuthorize(Long id);

    /**
     * 保存用户授权（角色 + 权限）
     */
    R<Void> saveAuthorize(Long userId, List<Long> roleIds, List<Long> permissionIds);

    // ==================== 个人中心 ====================

    /**
     * 获取个人信息
     */
    R<UserVO> getUserProfile(Long userId);

    /**
     * 更新个人资料
     */
    R<Void> updateProfile(Long userId, UpdateProfileDTO dto);

    /**
     * 更新头像
     */
    R<Void> updateAvatar(Long userId, String avatar);

    /**
     * 修改个人密码
     */
    R<Void> updatePwd(Long userId, String oldPwd, String newPwd);

    /**
     * 授权信息 VO
     */
    @lombok.Data
    @io.swagger.v3.oas.annotations.media.Schema(description = "授权信息")
    class AuthorizeVO {
        @io.swagger.v3.oas.annotations.media.Schema(description = "角色ID列表")
        private List<Long> roles;
        @io.swagger.v3.oas.annotations.media.Schema(description = "权限ID列表")
        private List<Long> permissions;
    }
}
