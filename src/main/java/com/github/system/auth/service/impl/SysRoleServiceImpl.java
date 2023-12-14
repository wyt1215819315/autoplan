package com.github.system.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.system.auth.dao.SysRoleDao;
import com.github.system.auth.dao.SysRoleUserDao;
import com.github.system.auth.entity.SysRole;
import com.github.system.auth.entity.SysRoleUser;
import com.github.system.auth.service.SysRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Resource
    private SysRoleDao sysRoleDao;
    @Resource
    private SysRoleUserDao sysRoleUserDao;

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
    public List<String> getUserRole(Long userId) {
        return sysRoleDao.queryUserRole(userId);
    }

    @Override
    public SysRole getRoleByCode(String code) {
        return sysRoleDao.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getCode, code));
    }

    @Override
    public boolean addUserRole(Long userId, String roleCode) {
        SysRole roleByCode = getRoleByCode(roleCode);
        if (roleByCode == null) {
            return false;
        }
        return addUserRole(userId, roleByCode.getId());
    }

    @Override
    public boolean addUserRole(Long userId, Long roleId) {
        SysRoleUser sysRoleUser = new SysRoleUser();
        sysRoleUser.setUserId(userId);
        sysRoleUser.setRoleId(roleId);
        return sysRoleUserDao.insert(sysRoleUser) > 0;
    }
}
