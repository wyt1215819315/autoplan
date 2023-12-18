package com.github.system.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.system.auth.dao.SysUserDao;
import com.github.system.auth.entity.SysUser;
import com.github.system.auth.service.SysRoleService;
import com.github.system.auth.service.UserService;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.configuration.SystemBean;
import com.github.system.auth.vo.SysUserVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<SysUserDao, SysUser> implements UserService {

    @Resource
    private SystemBean systemBean;
    @Resource
    private SysRoleService roleService;

    @Override
    public SysUser findUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysUser::getUsername, username);
        return baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public boolean updateUser(SysUserVo sysUserVo) {
        SysUser sysUser = getById(sysUserVo.getId());
        if (StrUtil.isNotBlank(sysUserVo.getPassword())) {
            sysUser.setPassword(encodePassword(sysUserVo.getPassword()));
        }
        if (StrUtil.isNotBlank(sysUserVo.getUsername())) {
            sysUser.setUsername(sysUserVo.getUsername());
        }
        return updateById(sysUser);
    }

    @Override
    public boolean saveUser(SysUserVo sysUserVo) {
        SysUser sysUser = BeanUtil.toBean(sysUserVo, SysUser.class);
        sysUser.setPassword(encodePassword(sysUserVo.getPassword()));
        return save(sysUser);
    }

    @Override
    public boolean editSelfPassword(SysUserVo sysUserVo) {
        Long userId = SessionUtils.getUserId();
        SysUser sysUser = getById(userId);
        sysUser.setPassword(encodePassword(sysUserVo.getPassword()));
        return updateById(sysUser);
    }

    @Override
    public String encodePassword(String password) {
        return SecureUtil.md5(password + systemBean.getPwdSalt());
    }

    @Override
    public boolean checkIfAdmin(Long userId) {
        List<String> userRole = roleService.getUserRole(userId);
        return userRole == null || !userRole.contains("ADMIN");
    }

}
