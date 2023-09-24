package com.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.system.entity.SysUserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysUserInfoDao extends BaseMapper<SysUserInfo> {

    SysUserInfo selectByUserId(Integer id);

}