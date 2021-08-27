package com.oldwu.dao;

import com.oldwu.domain.SysUser;
import org.apache.ibatis.annotations.Param;


public interface UserDao {
    public SysUser findByUserName(String username);
    int regUser(SysUser user);
    int setRole(int id);
}
