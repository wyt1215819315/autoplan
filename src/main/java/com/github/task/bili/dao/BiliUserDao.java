package com.task.bili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.task.bili.model.BiliPlan;
import com.task.bili.model.BiliUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BiliUserDao extends BaseMapper<BiliUser> {

    int deleteByAutoId(Integer id);

    BiliUser selectByAutoId(Integer autoId);

    List<BiliPlan> selectAll();

    List<BiliPlan> selectPageList(@Param("page") Integer page, @Param("limit") Integer limit);

    List<BiliPlan> selectMine(Integer userid);

    BiliUser selectByMid(Long id);

    int updateByAutoIdSelective(BiliUser record);

}
