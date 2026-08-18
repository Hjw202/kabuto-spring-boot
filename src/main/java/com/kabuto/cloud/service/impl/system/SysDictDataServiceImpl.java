package com.kabuto.cloud.service.impl.system;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kabuto.cloud.common.constant.Constants;
import com.kabuto.cloud.common.enums.ResultCode;
import com.kabuto.cloud.common.result.PageResult;
import com.kabuto.cloud.dao.system.SysDictDataMapper;
import com.kabuto.cloud.dto.system.CreateDictDataDTO;
import com.kabuto.cloud.dto.system.SearchDictDataDTO;
import com.kabuto.cloud.dto.system.UpdateDictDataDTO;
import com.kabuto.cloud.entity.system.SysDictData;
import com.kabuto.cloud.exception.BizException;
import com.kabuto.cloud.service.system.SysDictDataService;
import com.kabuto.cloud.vo.system.DictDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 字典数据服务实现
 *
 * <p><b>需求描述：</b>大王要求参考 nest-admin DictDataService 实现字典数据业务逻辑</p>
 * <p><b>解决方案：</b>实现字典数据 CRUD，按 dictType 分组缓存到 Redis（24h TTL）</p>
 * <p><b>原因说明：</b>对应 nest-admin DictDataService。字典数据变动频率低但查询频繁，
 * 使用 Redis 缓存可大幅减少数据库查询。缓存结构：sys:dict:{dictType} → JSON数组</p>
 */
@Slf4j
@Service
public class SysDictDataServiceImpl implements SysDictDataService {

    private final SysDictDataMapper dictDataMapper;
    private final StringRedisTemplate redisTemplate;

    public SysDictDataServiceImpl(SysDictDataMapper dictDataMapper, StringRedisTemplate redisTemplate) {
        this.dictDataMapper = dictDataMapper;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PageResult<DictDataVO> page(Integer pageNum, Integer pageSize, SearchDictDataDTO dto) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(dto.getDictLabel()), SysDictData::getDictLabel, dto.getDictLabel())
                .eq(StringUtils.hasText(dto.getDictType()), SysDictData::getDictType, dto.getDictType())
                .eq(dto.getStatus() != null, SysDictData::getStatus, dto.getStatus())
                .orderByAsc(SysDictData::getDictSort);

        Page<SysDictData> page = dictDataMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<DictDataVO> voList = page.getRecords().stream()
                .map(this::convertToDictDataVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, page.getTotal(), pageNum, pageSize);
    }

    @Override
    public List<DictDataVO> getDictsByType(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            return Collections.emptyList();
        }

        // 优先从 Redis 缓存读取
        String cacheKey = Constants.SYS_DICT_KEY + dictType;
        String cacheJson = redisTemplate.opsForValue().get(cacheKey);

        if (StringUtils.hasText(cacheJson)) {
            try {
                List<DictDataVO> cached = JSON.parseArray(cacheJson, DictDataVO.class);
                return cached != null ? cached : Collections.emptyList();
            } catch (Exception e) {
                log.warn("[字典缓存解析失败] dictType={}, error={}", dictType, e.getMessage());
            }
        }

        // 缓存未命中，查库并回填
        List<SysDictData> dataList = dictDataMapper.selectList(
                new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, 1)
                        .orderByAsc(SysDictData::getDictSort));

        List<DictDataVO> voList = dataList.stream()
                .map(this::convertToDictDataVO)
                .collect(Collectors.toList());

        // 写入缓存（24h TTL）
        try {
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(voList), 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("[字典缓存写入失败] dictType={}, error={}", dictType, e.getMessage());
        }

        return voList;
    }

    @Override
    public DictDataVO getDictDataById(Long id) {
        SysDictData dictData = dictDataMapper.selectById(id);
        if (dictData == null) {
            throw new BizException(ResultCode.NOT_FOUND, "字典数据不存在");
        }
        return convertToDictDataVO(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDictData(CreateDictDataDTO dto) {
        SysDictData dictData = new SysDictData();
        dictData.setDictLabel(dto.getDictLabel());
        dictData.setDictValue(dto.getDictValue());
        dictData.setDictType(dto.getDictType());
        dictData.setDictSort(dto.getDictSort());
        dictData.setCssClass(dto.getCssClass());
        dictData.setListClass(dto.getListClass());
        dictData.setIsDefault(dto.getIsDefault());
        dictData.setStatus(dto.getStatus());
        dictData.setCreateBy("admin");
        dictData.setCreateTime(LocalDateTime.now());
        dictDataMapper.insert(dictData);

        // 刷新该类型的缓存
        refreshCacheByType(dto.getDictType());

        log.info("[创建字典数据] dictDataId={}, dictType={}", dictData.getDictDataId(), dto.getDictType());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDictData(Long id, UpdateDictDataDTO dto) {
        SysDictData dictData = dictDataMapper.selectById(id);
        if (dictData == null) {
            throw new BizException(ResultCode.NOT_FOUND, "字典数据不存在");
        }

        String oldDictType = dictData.getDictType();

        dictData.setDictLabel(dto.getDictLabel());
        dictData.setDictValue(dto.getDictValue());
        if (StringUtils.hasText(dto.getDictType())) {
            dictData.setDictType(dto.getDictType());
        }
        if (dto.getDictSort() != null) dictData.setDictSort(dto.getDictSort());
        if (dto.getCssClass() != null) dictData.setCssClass(dto.getCssClass());
        if (dto.getListClass() != null) dictData.setListClass(dto.getListClass());
        if (dto.getIsDefault() != null) dictData.setIsDefault(dto.getIsDefault());
        if (dto.getStatus() != null) dictData.setStatus(dto.getStatus());
        dictData.setUpdateBy("admin");
        dictData.setUpdateTime(LocalDateTime.now());
        dictDataMapper.updateById(dictData);

        // 刷新旧类型和新类型的缓存
        refreshCacheByType(oldDictType);
        if (StringUtils.hasText(dto.getDictType()) && !dto.getDictType().equals(oldDictType)) {
            refreshCacheByType(dto.getDictType());
        }

        log.info("[更新字典数据] dictDataId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDictData(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }

        // 先收集所有涉及的 dictType，删除后需要刷新缓存
        List<String> dictTypes = ids.stream()
                .map(dictDataMapper::selectById)
                .filter(d -> d != null)
                .map(SysDictData::getDictType)
                .distinct()
                .collect(Collectors.toList());

        for (Long id : ids) {
            dictDataMapper.deleteById(id);
        }

        // 刷新涉及的所有类型的缓存
        for (String dictType : dictTypes) {
            refreshCacheByType(dictType);
        }

        log.info("[批量删除字典数据] ids={}", ids);
    }

    @Override
    public void refreshCacheByType(String dictType) {
        if (!StringUtils.hasText(dictType)) {
            return;
        }

        String cacheKey = Constants.SYS_DICT_KEY + dictType;
        List<SysDictData> dataList = dictDataMapper.selectList(
                new LambdaQueryWrapper<SysDictData>()
                        .eq(SysDictData::getDictType, dictType)
                        .eq(SysDictData::getStatus, 1)
                        .orderByAsc(SysDictData::getDictSort));

        List<DictDataVO> voList = dataList.stream()
                .map(this::convertToDictDataVO)
                .collect(Collectors.toList());

        try {
            redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(voList), 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("[字典缓存刷新失败] dictType={}, error={}", dictType, e.getMessage());
        }
    }

    @Override
    public void clearDictCache() {
        try {
            java.util.Set<String> keys = redisTemplate.keys(Constants.SYS_DICT_KEY + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("[清空字典缓存] keyCount={}", keys.size());
            }
        } catch (Exception e) {
            log.error("[清空字典缓存失败] error={}", e.getMessage(), e);
        }
    }

    // ==================== 私有辅助方法 ====================

    private DictDataVO convertToDictDataVO(SysDictData dictData) {
        DictDataVO vo = new DictDataVO();
        vo.setDictDataId(dictData.getDictDataId());
        vo.setDictType(dictData.getDictType());
        vo.setDictLabel(dictData.getDictLabel());
        vo.setDictValue(dictData.getDictValue());
        vo.setDictSort(dictData.getDictSort());
        vo.setCssClass(dictData.getCssClass());
        vo.setListClass(dictData.getListClass());
        vo.setIsDefault(dictData.getIsDefault());
        vo.setStatus(dictData.getStatus());
        vo.setCreateTime(dictData.getCreateTime());
        return vo;
    }
}
