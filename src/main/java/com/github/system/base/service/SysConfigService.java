package com.github.system.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.system.base.entity.SysConfig;

public interface SysConfigService extends IService<SysConfig> {
    String getValueByKey(String key);

    String getValueByKeyOrDefault(String key, String defaultValue);

    boolean saveOrUpdate(String key, String value);
}
