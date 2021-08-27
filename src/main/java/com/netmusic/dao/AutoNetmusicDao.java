package com.netmusic.dao;

import com.netmusic.model.AutoNetmusic;

import java.util.List;

public interface AutoNetmusicDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AutoNetmusic record);

    int insertSelective(AutoNetmusic record);

    AutoNetmusic selectByPrimaryKey(Integer id);

    AutoNetmusic selectByUid(String uid);

    List<AutoNetmusic> selectAll();

    int updateByPrimaryKeySelective(AutoNetmusic record);

    int updateByPrimaryKey(AutoNetmusic record);
}