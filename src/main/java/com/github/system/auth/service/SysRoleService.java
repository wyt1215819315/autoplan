package com.github.system.auth.service;

import com.github.system.auth.entity.SysRole;
import com.github.system.base.vo.SysUserRoleVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SysRoleService {
    List<String> getAllRoleCode();

    List<SysRole> getAllRole();

    List<SysRole> getUserRoleInfo(Long userId);

    SysRole getRoleByCode(String code);

    boolean addUserRole(Long userId, String roleCode);

    boolean addUserRole(Long userId, Long roleId);

    List<String> getUserRole(Long userId);

    boolean editUserRole(SysUserRoleVo sysUserRoleVo);
}
