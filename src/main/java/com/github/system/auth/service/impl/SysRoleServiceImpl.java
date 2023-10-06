package com.github.system.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.system.auth.dao.SysRoleDao;
import com.github.system.auth.entity.SysRole;
import com.github.system.auth.service.SysRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Resource
    private SysRoleDao sysRoleDao;

    @Override
    public List<String> getAllRoleCode() {
        return getAllRole().stream().map(SysRole::getCode).collect(Collectors.toList());
    }

    @Override
    public List<SysRole> getAllRole() {
        LambdaQueryWrapper<SysRole> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        return sysRoleDao.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<String> getUserRole(Integer userId) {
        return sysRoleDao.queryUserRole(userId);
    }

}
