package com.kabuto.cloud.service.system;

import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.dto.system.CreatePermissionDTO;
import com.kabuto.cloud.dto.system.SearchPermissionDTO;
import com.kabuto.cloud.dto.system.UpdatePermissionDTO;
import com.kabuto.cloud.vo.system.PermissionVO;

import java.util.List;

/**
 * 权限管理服务接口
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin PermissionService 实现权限管理服务层</p>
 * <p><b>解决方案：</b>定义权限 CRUD 服务接口</p>
 * <p><b>原因说明：</b>对应 nest-admin PermissionService</p>
 */
public interface SysPermissionService {

    /**
     * 分页查询权限列表
     */
    PageResult<PermissionVO> page(Integer pageNum, Integer pageSize, SearchPermissionDTO dto);

    /**
     * 查询所有权限列表
     */
    List<PermissionVO> getAllPermissions();

    /**
     * 查询权限详情
     */
    PermissionVO getPermissionById(Long id);

    /**
     * 创建权限
     */
    void createPermission(CreatePermissionDTO dto);

    /**
     * 更新权限
     */
    void updatePermission(UpdatePermissionDTO dto);

    /**
     * 批量删除权限
     */
    void deletePermissions(List<Long> ids);
}
