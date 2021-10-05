package com.oldwu.dao;

import com.oldwu.entity.SysUserInfo;

public interface SysUserInfoDao {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUserInfo record);

    int insertSelective(SysUserInfo record);

    SysUserInfo selectByPrimaryKey(Integer id);

    SysUserInfo selectByUserId(Integer id);

    int updateByPrimaryKeySelective(SysUserInfo record);

    int updateByPrimaryKey(SysUserInfo record);
}