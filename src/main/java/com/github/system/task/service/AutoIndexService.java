package com.github.system.task.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.system.task.entity.AutoIndex;

import java.util.List;

public interface AutoIndexService extends IService<AutoIndex> {
    List<AutoIndex> userList();
}
