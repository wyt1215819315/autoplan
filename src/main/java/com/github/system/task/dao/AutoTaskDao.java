package com.github.system.task.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.system.task.entity.AutoTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AutoTaskDao extends BaseMapper<AutoTask> {
}
