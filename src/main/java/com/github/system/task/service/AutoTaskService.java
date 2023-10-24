package com.github.system.task.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.system.task.dto.AutoTaskDto;
import com.github.system.task.entity.AutoTask;

import java.util.List;

public interface AutoTaskService extends IService<AutoTask> {
    Page taskPage(Page<AutoTask> page, String indexId) throws Exception;

    List<AutoTaskDto> turnAutoTaskEntity(List<AutoTask> autoTaskList);
}
