package com.kabuto.cloud.service.impl.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.common.result.R;
import com.kabuto.cloud.dao.system.SysDictDataMapper;
import com.kabuto.cloud.dao.system.SysDictTypeMapper;
import com.kabuto.cloud.dto.system.CreateDictTypeDTO;
import com.kabuto.cloud.dto.system.SearchDictTypeDTO;
import com.kabuto.cloud.dto.system.UpdateDictTypeDTO;
import com.kabuto.cloud.entity.system.SysDictData;
import com.kabuto.cloud.entity.system.SysDictType;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.system.SysDictDataService;
import com.kabuto.cloud.service.system.SysDictTypeService;
import com.kabuto.cloud.vo.system.DictTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 字典类型服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin DictTypeService 实现字典类型业务逻辑</p>
 * <p><b>解决方案：</b>实现字典类型 CRUD，修改 dictType 时事务更新关联的 dictData</p>
 * <p><b>原因说明：</b>对应 nest-admin DictTypeService。dictType 作为字典数据的外键，
 * 变更时必须同步更新所有子记录，否则会导致字典数据丢失</p>
 */
@Slf4j
@Service
public class SysDictTypeServiceImpl implements SysDictTypeService {

    private final SysDictTypeMapper dictTypeMapper;
    private final SysDictDataMapper dictDataMapper;
    private final SysDictDataService dictDataService;
    private final StringRedisTemplate redisTemplate;

    public SysDictTypeServiceImpl(SysDictTypeMapper dictTypeMapper, SysDictDataMapper dictDataMapper,
                                  SysDictDataService dictDataService, StringRedisTemplate redisTemplate) {
        this.dictTypeMapper = dictTypeMapper;
        this.dictDataMapper = dictDataMapper;
        this.dictDataService = dictDataService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public R<PageResult<DictTypeVO>> page(Integer pageNum, Integer pageSize, SearchDictTypeDTO dto) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getDictName()), SysDictType::getDictName, dto.getDictName())
                .like(StringUtils.hasText(dto.getDictType()), SysDictType::getDictType, dto.getDictType())
                .eq(dto.getStatus() != null, SysDictType::getStatus, dto.getStatus())
                .ge(dto.getStartTime() != null, SysDictType::getCreateTime, dto.getStartTime())
                .le(dto.getEndTime() != null, SysDictType::getCreateTime, dto.getEndTime())
                .orderByAsc(SysDictType::getCreateTime);

        Page<SysDictType> page = dictTypeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<DictTypeVO> voList = page.getRecords().stream()
                .map(this::convertToDictTypeVO)
                .collect(Collectors.toList());

        return R.tableData(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public R<List<DictTypeVO>> optionselect() {
        List<SysDictType> types = dictTypeMapper.selectList(
                new LambdaQueryWrapper<SysDictType>()
                        .eq(SysDictType::getStatus, 1)
                        .orderByAsc(SysDictType::getCreateTime));

        List<DictTypeVO> voList = types.stream()
                .map(this::convertToDictTypeVO)
                .collect(Collectors.toList());

        return R.ok(voList);
    }

    @Override
    public R<DictTypeVO> getDictTypeById(Long id) {
        SysDictType dictType = dictTypeMapper.selectById(id);
        if (dictType == null) {
            return R.notFound("字典类型不存在");
        }
        return R.ok(convertToDictTypeVO(dictType));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> createDictType(CreateDictTypeDTO dto) {
        // 校验字典类型唯一
        Long existCount = dictTypeMapper.selectCount(
                new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getDictType, dto.getDictType()));
        if (existCount > 0) {
            throw new BizException(ResultCode.BAD_REQUEST, "字典类型已存在");
        }

        SysDictType dictType = new SysDictType();
        dictType.setDictName(dto.getDictName());
        dictType.setDictType(dto.getDictType());
        dictType.setStatus(dto.getStatus());
        dictType.setCreateBy("admin");
        dictType.setCreateTime(LocalDateTime.now());
        dictTypeMapper.insert(dictType);

        log.info("[创建字典类型] dictTypeId={}, dictType={}", dictType.getDictTypeId(), dictType.getDictType());
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> updateDictType(Long id, UpdateDictTypeDTO dto) {
        SysDictType dictType = dictTypeMapper.selectById(id);
        if (dictType == null) {
            throw new BizException(ResultCode.NOT_FOUND, "字典类型不存在");
        }

        // 校验字典类型唯一（排除自身）
        if (StringUtils.hasText(dto.getDictType())) {
            Long existCount = dictTypeMapper.selectCount(
                    new LambdaQueryWrapper<SysDictType>()
                            .eq(SysDictType::getDictType, dto.getDictType())
                            .ne(SysDictType::getDictTypeId, id));
            if (existCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST, "字典类型已存在");
            }
        }

        // 若 dictType 键变更，同步更新关联的 dictData
        String oldDictType = dictType.getDictType();
        if (StringUtils.hasText(dto.getDictType()) && !dto.getDictType().equals(oldDictType)) {
            SysDictData updateData = new SysDictData();
            updateData.setDictType(dto.getDictType());
            dictDataMapper.update(updateData,
                    new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, oldDictType));

            // 删除旧缓存
            redisTemplate.delete(Constants.SYS_DICT_KEY + oldDictType);

            log.info("[字典类型键变更] {} -> {}, 已同步更新 dictData", oldDictType, dto.getDictType());
        }

        dictType.setDictName(dto.getDictName());
        dictType.setDictType(dto.getDictType());
        if (dto.getStatus() != null) {
            dictType.setStatus(dto.getStatus());
        }
        dictType.setUpdateBy("admin");
        dictType.setUpdateTime(LocalDateTime.now());
        dictTypeMapper.updateById(dictType);

        // 刷新新类型的缓存
        dictDataService.refreshCacheByType(dictType.getDictType());

        log.info("[更新字典类型] dictTypeId={}", id);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<Void> deleteDictTypes(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return R.ok();
        }

        for (Long id : ids) {
            SysDictType dictType = dictTypeMapper.selectById(id);
            if (dictType == null) {
                continue;
            }

            // 校验无子 dictData 记录
            Long dataCount = dictDataMapper.selectCount(
                    new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictType, dictType.getDictType()));
            if (dataCount > 0) {
                throw new BizException(ResultCode.BAD_REQUEST,
                        "字典类型「" + dictType.getDictName() + "」下存在字典数据，不能删除");
            }

            // 删除缓存
            redisTemplate.delete(Constants.SYS_DICT_KEY + dictType.getDictType());

            dictTypeMapper.deleteById(id);
        }

        log.info("[批量删除字典类型] ids={}", ids);
        return R.ok();
    }

    @Override
    public R<Void> refreshDictCache() {
        dictDataService.clearDictCache();

        // 重新加载所有字典数据到缓存
        List<SysDictType> allTypes = dictTypeMapper.selectList(
                new LambdaQueryWrapper<SysDictType>().eq(SysDictType::getStatus, 1));
        for (SysDictType type : allTypes) {
            dictDataService.refreshCacheByType(type.getDictType());
        }

        log.info("[刷新字典缓存] typeCount={}", allTypes.size());
        return R.ok();
    }

    // ==================== 私有辅助方法 ====================

    private DictTypeVO convertToDictTypeVO(SysDictType dictType) {
        DictTypeVO vo = new DictTypeVO();
        vo.setDictTypeId(dictType.getDictTypeId());
        vo.setDictName(dictType.getDictName());
        vo.setDictType(dictType.getDictType());
        vo.setStatus(dictType.getStatus());
        vo.setCreateTime(dictType.getCreateTime());
        return vo;
    }
}
