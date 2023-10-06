package com.github.system.auth.service;

import com.github.system.auth.entity.SysRole;

import java.util.List;

public interface SysRoleService {
    List<String> getAllRoleCode();

    List<SysRole> getAllRole();

    List<String> getUserRole(Integer userId);
}
