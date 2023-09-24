package com.github.system.dao;

import com.github.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao {
    SysUser findByUserName(String username);

    int regUser(SysUser user);

    int setRole(int id);

    String getRole(Integer id);
}
