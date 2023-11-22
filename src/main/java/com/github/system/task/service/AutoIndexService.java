package com.github.system.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.system.base.dto.AjaxResult;
import com.github.system.task.dto.display.SettingDisplayDto;
import com.github.system.task.dto.display.UserInfoDisplayDto;
import com.github.system.task.entity.AutoIndex;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public interface AutoIndexService extends IService<AutoIndex> {
    List<AutoIndex> userList();

    List<UserInfoDisplayDto> getTaskUserInfoColumn(String indexId);

    List<SettingDisplayDto> getSettingColumn(String indexId);

    Map<String, List<UserInfoDisplayDto>> getTaskUserInfoColumnAll();

    Map<String,Object> getColumn();

    String getIcon(String code) throws FileNotFoundException;
}
