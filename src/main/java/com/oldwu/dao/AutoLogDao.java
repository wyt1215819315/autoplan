package com.oldwu.dao;

import com.oldwu.entity.AutoLog;
import org.apache.ibatis.annotations.Param;

public interface AutoLogDao {
    int deleteByPrimaryKey(Long id);

    int deleteByAutoId(AutoLog autoLog);

    int insert(AutoLog record);

    int insertSelective(AutoLog record);

    AutoLog selectByPrimaryKey(Long id);

    AutoLog selectByCondition(AutoLog autoLog);

    int updateByPrimaryKeySelective(AutoLog record);

    int updateByPrimaryKey(AutoLog record);
}