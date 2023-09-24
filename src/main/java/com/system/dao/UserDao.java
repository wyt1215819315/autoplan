package com.system.dao;

import com.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    SysUser findByUserName(String username);

    int regUser(SysUser user);

    int setRole(int id);

    String getRole(Integer id);
}
