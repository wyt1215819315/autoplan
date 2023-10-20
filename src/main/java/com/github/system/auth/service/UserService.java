package com.github.system.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.system.auth.entity.SysUser;

public interface UserService extends IService<SysUser> {
    SysUser findUserByUsername(String username);

    boolean createUser(SysUser sysUser);
}
