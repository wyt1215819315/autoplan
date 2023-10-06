package com.github.system.auth.init;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.collection.ListUtil;
import com.github.system.auth.dao.SysRoleDao;
import com.github.system.auth.entity.SysRole;
import com.github.system.auth.service.SysRoleService;
import com.github.system.util.SpringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.*;


@Component
public class SystemInit implements CommandLineRunner {
    private final Log logger = LogFactory.getLog(SystemInit.class);

    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysRoleDao sysRoleDao;


    /**
     * 初始化角色
     */
    public void systemRoleInit() throws Exception {
        logger.info("初始化角色缓存...");
        Date date = new Date();
        List<SysRole> allRole = sysRoleService.getAllRole();
        List<SysRole> initRole = new ArrayList<>();
        //如果需要加入更多的初始角色在这边增加就行
        initRole.add(new SysRole("系统管理员", "ADMIN"));
        initRole.add(new SysRole("普通用户", "USER"));
        //包扫描获取spring注册的satoken中的所有角色code，并丢到初始化角色中去
        List<SaCheckRole> saList = SpringUtil.scanAllAnnotationByAnnotation(SaCheckRole.class);
        Set<SaCheckRole> saCheckRoles = new HashSet<>(saList);
        saCheckRoles.forEach(s -> ListUtil.toList(s.value()).forEach(ss -> initRole.add(new SysRole(ss, ss))));
        for (SysRole systemRole : allRole) {
            initRole.removeIf(initR -> initR.getCode().equals(systemRole.getCode()));
        }
        //初始化数据
        if (!initRole.isEmpty()) {
            for (SysRole systemRole : initRole) {
                sysRoleDao.insert(systemRole);
            }
        }
        logger.info("初始化角色缓存完毕！耗时" + (System.currentTimeMillis() - date.getTime()) + "ms");
    }


    @Override
    public void run(String... args) throws Exception {
        systemRoleInit();
//        long l = System.currentTimeMillis();
//        SystemDictUtil.refreshRedisDictCache();
//        logger.info("初始化字典缓存完成！耗时" + (System.currentTimeMillis() - l) + "ms");
    }
}
