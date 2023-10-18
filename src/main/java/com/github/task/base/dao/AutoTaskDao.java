package com.github.task.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.task.base.entity.AutoTask;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AutoTaskDao extends BaseMapper<AutoTask> {
}
