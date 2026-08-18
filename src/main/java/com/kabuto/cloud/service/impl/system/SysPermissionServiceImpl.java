package com.kabuto.cloud.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dao.system.SysPermissionMapper;
import com.kabuto.cloud.dto.system.CreatePermissionDTO;
import com.kabuto.cloud.dto.system.SearchPermissionDTO;
import com.kabuto.cloud.dto.system.UpdatePermissionDTO;
import com.kabuto.cloud.entity.system.SysPermission;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.system.SysPermissionService;
import com.kabuto.cloud.vo.system.PermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin PermissionService 实现权限管理业务逻辑</p>
 * <p><b>解决方案：</b>实现权限 CRUD 核心功能</p>
 * <p><b>原因说明：</b>对应 nest-admin PermissionService。独立于菜单的权限实体管理</p>
 */
@Slf4j
@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionMapper permissionMapper;

    public SysPermissionServiceImpl(SysPermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public R<PageResult<PermissionVO>> page(Integer pageNum, Integer pageSize, SearchPermissionDTO dto) {
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getName()), SysPermission::getName, dto.getName())
                .eq(StringUtils.hasText(dto.getPerms()), SysPermission::getPerms, dto.getPerms())
                .eq(dto.getStatus() != null, SysPermission::getStatus, dto.getStatus())
                .orderByAsc(SysPermission::getCreateTime);

        Page<SysPermission> page = permissionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<PermissionVO> voList = page.getRecords().stream()
                .map(this::convertToPermissionVO)
                .collect(Collectors.toList());

        return R.tableData(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public R<List<PermissionVO>> getAllPermissions() {
        List<SysPermission> permissions = permissionMapper.selectList(
                new LambdaQueryWrapper<SysPermission>()
                        .orderByAsc(SysPermission::getCreateTime));

        List<PermissionVO> voList = permissions.stream()
                .map(this::convertToPermissionVO)
                .collect(Collectors.toList());

        return R.ok(voList);
    }

    @Override
    public R<PermissionVO> getPermissionById(Long id) {
        SysPermission permission = permissionMapper.selectById(id);
        if (permission == null) {
            return R.notFound("权限不存在");
        }
        return R.ok(convertToPermissionVO(permission));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> createPermission(CreatePermissionDTO dto) {
        // 校验权限标识唯一
        Long existCount = permissionMapper.selectCount(
                new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getPerms, dto.getPerms()));
        if (existCount > 0) {
            throw new BizException(ResultCode.BAD_REQUEST, "权限标识已存在");
        }

        SysPermission permission = new SysPermission();
        permission.setName(dto.getName());
        permission.setPerms(dto.getPerms());
        permission.setDescription(dto.getDescription());
        permission.setStatus(dto.getStatus());
        permission.setCreateBy("admin");
        permission.setCreateTime(LocalDateTime.now());
        permissionMapper.insert(permission);

        log.info("[创建权限] permissionId={}, perms={}", permission.getPermissionId(), permission.getPerms());
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> updatePermission(UpdatePermissionDTO dto) {
        SysPermission permission = permissionMapper.selectById(dto.getId());
        if (permission == null) {
            throw new BizException(ResultCode.NOT_FOUND, "权限不存在");
        }

        // 校验权限标识唯一（排除自身）
        if (StringUtils.hasText(dto.getPerms())) {
            Long existCount = permissionMapper.selectCount(
                    new LambdaQueryWrapper<SysPermission>()
                            .eq(SysPermission::getPerms, dto.getPerms())
                            .ne(SysPermission::getPermissionId, dto.getId()));
            if (existCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST, "权限标识已存在");
            }
        }

        permission.setName(dto.getName());
        permission.setPerms(dto.getPerms());
        permission.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            permission.setStatus(dto.getStatus());
        }
        permission.setUpdateBy("admin");
        permission.setUpdateTime(LocalDateTime.now());
        permissionMapper.updateById(permission);

        log.info("[更新权限] permissionId={}", dto.getId());
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> deletePermissions(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return R.ok();
        }

        for (Long id : ids) {
            SysPermission permission = permissionMapper.selectById(id);
            if (permission == null) {
                continue;
            }
            permissionMapper.deleteById(id);
        }

        log.info("[批量删除权限] ids={}", ids);
        return R.ok();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 转换实体为 VO
     */
    private PermissionVO convertToPermissionVO(SysPermission permission) {
        PermissionVO vo = new PermissionVO();
        vo.setPermissionId(permission.getPermissionId());
        vo.setName(permission.getName());
        vo.setPerms(permission.getPerms());
        vo.setDescription(permission.getDescription());
        vo.setStatus(permission.getStatus());
        vo.setCreateTime(permission.getCreateTime());
        return vo;
    }
}
