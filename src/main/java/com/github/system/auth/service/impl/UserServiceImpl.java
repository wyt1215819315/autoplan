package com.github.system.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.system.auth.dao.SysUserDao;
import com.github.system.auth.entity.SysUser;
import com.github.system.auth.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements UserService {

    @Override
    public SysUser findUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUsername, username);
        return baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public boolean createUser(SysUser sysUser) {
        return save(sysUser);
    }

}
