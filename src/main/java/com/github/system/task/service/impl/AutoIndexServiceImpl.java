package com.github.system.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.system.base.dto.AjaxResult;
import com.github.system.task.dao.AutoIndexDao;
import com.github.system.task.dto.display.SettingDisplayDto;
import com.github.system.task.dto.display.UserInfoDisplayDto;
import com.github.system.task.entity.AutoIndex;
import com.github.system.task.service.AutoIndexService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.system.task.init.TaskInit.*;

@Service
public class AutoIndexServiceImpl extends ServiceImpl<AutoIndexDao, AutoIndex> implements AutoIndexService {

    @Override
    public List<AutoIndex> userList() {
        LambdaQueryWrapper<AutoIndex> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(AutoIndex::getId, AutoIndex::getCode, AutoIndex::getName)
                .eq(AutoIndex::getEnable, 1);
        return list(lambdaQueryWrapper);
    }

    @Override
    public List<UserInfoDisplayDto> getTaskUserInfoColumn(String indexId) {
        AutoIndex autoIndex = getById(indexId);
        if (autoIndex == null || autoIndex.getEnable() == 0) {
            return new ArrayList<>();
        }
        return userInfoDisplayDataMap.getOrDefault(autoIndex.getCode(), new ArrayList<>());
    }

    @Override
    public List<SettingDisplayDto> getSettingColumn(String indexId) {
        AutoIndex autoIndex = getById(indexId);
        if (autoIndex == null || autoIndex.getEnable() == 0) {
            return new ArrayList<>();
        }
        return settingDisplayDataMap.getOrDefault(autoIndex.getCode(), new ArrayList<>());
    }

    @Override
    public Map<String, List<UserInfoDisplayDto>> getTaskUserInfoColumnAll() {
        return userInfoDisplayDataMap;
    }

    @Override
    public Map<String, Object> getColumn() {
        List<AutoIndex> autoIndices = userList();
        Map<String, Object> resultMap = new HashMap<>();
        settingDisplayDataMap.forEach((k, v) -> {
            Map<String, Object> map = new HashMap<>();
            List<AutoIndex> list = autoIndices.stream().filter(m -> m.getCode().equals(k)).toList();
            if (list.isEmpty()) {
                return;
            }
            map.put("index", list.get(0));
            map.put("settings", v);
            map.put("display", userInfosClassesMap.get(k));
            resultMap.put(k, map);
        });
        return resultMap;
    }
}
