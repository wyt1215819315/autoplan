package com.github.system.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.system.task.dao.AutoIndexDao;
import com.github.system.task.entity.AutoIndex;
import com.github.system.task.service.AutoIndexService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutoIndexServiceImpl extends ServiceImpl<AutoIndexDao, AutoIndex> implements AutoIndexService {

    @Override
    public List<AutoIndex> userList() {
        LambdaQueryWrapper<AutoIndex> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(AutoIndex::getId, AutoIndex::getCode, AutoIndex::getName)
                .eq(AutoIndex::getEnable, 1);
        return list(lambdaQueryWrapper);
    }
}
