package com.github.system.auth.service;

import com.github.system.auth.entity.SysRole;

import java.util.List;

public interface SysRoleService {
    List<String> getAllRoleCode();

    List<SysRole> getAllRole();

    SysRole getRoleByCode(String code);

    boolean addUserRole(Long userId, String roleCode);

    boolean addUserRole(Long userId, Long roleId);

    List<String> getUserRole(Long userId);
}
