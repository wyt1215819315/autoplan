package com.github.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.system.base.entity.SysConfig;

public interface SysConfigService extends IService<SysConfig> {
    String getValueByKey(String key);

    boolean saveOrUpdate(String key, String value);
}
