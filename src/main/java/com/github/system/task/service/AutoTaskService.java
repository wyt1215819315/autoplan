package com.github.system.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.system.task.dto.AutoTaskDto;
import com.github.system.task.dto.CheckResult;
import com.github.system.task.entity.AutoTask;
import com.github.system.task.vo.AutoTaskVo;

import java.util.List;

public interface AutoTaskService extends IService<AutoTask> {
    Page taskPage(Page<AutoTask> page, String indexId) throws Exception;

    List<AutoTaskDto> turnAutoTaskEntity(List<AutoTask> autoTaskList);

    CheckResult checkOrSaveUser(Long indexId, AutoTaskVo autoTaskVo, boolean save) throws Exception;

    CheckResult checkAndUpdate(AutoTaskVo autoTaskVo) throws Exception;
}
