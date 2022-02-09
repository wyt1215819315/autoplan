package com.bili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oldwu.entity.BiliPlan;
import com.oldwu.entity.BiliUser;
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
