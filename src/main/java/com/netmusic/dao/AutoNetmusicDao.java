package com.netmusic.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.netmusic.model.AutoNetmusic;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AutoNetmusicDao extends BaseMapper<AutoNetmusic> {

    AutoNetmusic selectByUid(String uid);

    List<AutoNetmusic> selectMine(int userid);

}