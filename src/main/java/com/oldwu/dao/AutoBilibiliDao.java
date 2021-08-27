package com.oldwu.dao;

import com.oldwu.entity.AutoBilibili;

import java.util.List;

public interface AutoBilibiliDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AutoBilibili record);

    int insertSelective(AutoBilibili record);

    AutoBilibili selectByPrimaryKey(Integer id);

    List<AutoBilibili> selectAll();

    int updateByPrimaryKeySelective(AutoBilibili record);

    int updateByPrimaryKey(AutoBilibili record);
}