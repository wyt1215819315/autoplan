package com.github.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.system.entity.AutoLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AutoLogDao extends BaseMapper<AutoLog> {

    int deleteByAutoId(AutoLog autoLog);

    AutoLog selectByCondition(AutoLog autoLog);

}