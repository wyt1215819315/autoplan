package com.oldwu.dao;

import com.oldwu.entity.BiliPlan;
import com.oldwu.entity.BiliUser;

import java.util.List;

public interface BiliUserDao {
    int deleteByPrimaryKey(Integer id);

    int insert(BiliUser record);

    int insertSelective(BiliUser record);

    BiliUser selectByPrimaryKey(Integer id);

    List<BiliPlan> selectAll();

    List<BiliPlan> selectMine(Integer userid);

    BiliUser selectByMid(Long id);

    int updateByPrimaryKeySelective(BiliUser record);

    int updateByAutoIdSelective(BiliUser record);

    int updateByPrimaryKey(BiliUser record);
}