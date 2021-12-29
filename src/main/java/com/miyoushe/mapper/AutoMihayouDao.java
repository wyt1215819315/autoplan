package com.miyoushe.mapper;

import com.miyoushe.model.AutoMihayou;

import java.util.List;

public interface AutoMihayouDao {
    int deleteByPrimaryKey(Integer id);

    int insert(AutoMihayou record);

    int insertSelective(AutoMihayou record);

    AutoMihayou selectByPrimaryKey(Integer id);

    List<AutoMihayou> selectAll();

    List<AutoMihayou> selectMine(Integer userid);

    int updateByPrimaryKeySelective(AutoMihayou record);

    int updateByPrimaryKey(AutoMihayou record);

    AutoMihayou selectBystuid(String suid);

    AutoMihayou selectByGenshinUid(String genshinUid);
}