package com.github.system.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.system.auth.entity.SysUser;
import com.github.system.auth.vo.SysUserVo;

public interface UserService extends IService<SysUser> {
    SysUser findUserByUsername(String username);

    boolean updateUser(SysUserVo sysUserVo);

    boolean saveUser(SysUserVo sysUserVo);

    boolean editSelfPassword(SysUserVo sysUserVo);

    String encodePassword(String password);

    boolean checkIfAdmin(Long userId);
}
