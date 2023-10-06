package com.github.system.auth.config;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.github.system.auth.service.SysRoleService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * 自定义权限验证接口扩展
 */
@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 角色缓存
        SaSession session = StpUtil.getSession();
        return session.get("Role_List", () -> {
            // 从数据库查询这个账号id拥有的角色列表
            List<String> roles = sysRoleService.getUserRole((Integer) loginId);
            if (roles.contains("ADMIN")) {
                return sysRoleService.getAllRoleCode();
            } else {
                return roles;
            }
        });
    }

}
