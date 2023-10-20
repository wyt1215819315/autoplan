package com.github.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.system.base.dao.SysConfigDao;
import com.github.system.base.entity.SysConfig;
import org.springframework.stereotype.Service;

@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigDao, SysConfig> implements SysConfigService {

    @Override
    public String getValueByKey(String key) {
        SysConfig sysConfig = baseMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getKey, key));
        if (sysConfig == null) {
            return null;
        }
        return sysConfig.getValue();
    }

    @Override
    public boolean saveOrUpdate(String key, String value) {
        SysConfig sysConfig = baseMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getKey, key));
        if (sysConfig == null) {
            sysConfig = new SysConfig(key, value);
            return baseMapper.insert(sysConfig) > 0;
        } else {
            sysConfig.setValue(value);
            return baseMapper.updateById(sysConfig) > 0;
        }
    }

}
