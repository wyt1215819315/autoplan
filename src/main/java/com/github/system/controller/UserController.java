package com.github.system.controller;

import com.github.system.dao.UserDao;
import com.github.system.base.dto.AjaxResult;
import com.github.system.entity.AutoLog;
import com.github.system.entity.SysUserInfo;
import com.github.system.security.entity.SystemUser;
import com.github.system.security.utils.SessionUtils;
import com.github.system.service.LogService;
import com.github.system.service.RegService;
import com.github.system.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private RegService regService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private UserDao userDao;

    @PostMapping("/reg")
    public AjaxResult regPost(@Param("username") String username, @Param("password") String password) {
        return regService.doReg(username, password);
    }

    /**
     * 获取执行日志，只能获取最新一条
     * @param params id=auto_id，type=mihuyou/netmusic/bili
     * @return AutoLog
     */
    @PostMapping("/api/user/getlog")
    public AjaxResult getLog(@RequestParam Map<String, String> params) {
        if (!params.containsKey("autoId") || StringUtils.isBlank(params.get("autoId"))){
            return AjaxResult.doError("ID不能为空！");
        }
        if (!params.containsKey("type") || StringUtils.isBlank(params.get("type"))){
            return AjaxResult.doError("TYPE不能为空！");
        }

        Integer autoId = Integer.valueOf(params.get("autoId"));
        String type = params.get("type");

        Integer userId = SessionUtils.getPrincipal().getId();
        String role = userDao.getRole(userId);
        AutoLog log;
        if (role.equals("ROLE_ADMIN")) {
            if (StringUtils.isBlank(params.get("userId"))) {
                log = logService.getLog(null, autoId, type, userId);
            } else {
                log = logService.getLog(null, autoId, type, Integer.valueOf(params.get("userId")));
            }
        }else {
            log = logService.getLog(null, autoId, type, userId);
        }
        if (log == null || log.getId() == null) {
            AutoLog log1 = new AutoLog();
            log1.setText("当前无日志");
            log1.setDate(new Date());
            return AjaxResult.doSuccess(log1);
        }
        log.setText(log.getText().replaceAll("\n", "<br/>"));
        return AjaxResult.doSuccess(log);
    }

    @PostMapping("/api/user/me")
    public AjaxResult me(){
        if (SessionUtils.getPrincipal() == null){
            return AjaxResult.doError("未登录！");
        }
        SystemUser systemUser = SessionUtils.getPrincipal();
        systemUser.setPassword(null);
        return AjaxResult.doSuccess(systemUser);
    }

    @PostMapping("/api/user/userinfo/edit")
    public AjaxResult userInfoEdit(@RequestBody SysUserInfo userInfo) {
        return userService.editUserInfo(SessionUtils.getPrincipal().getId(), userInfo);
    }

    @PostMapping("/api/user/userinfo/list")
    public AjaxResult mySettingsList() {
        SysUserInfo userInfo = userService.getUserInfo(SessionUtils.getPrincipal().getId());
        return AjaxResult.doSuccess(userInfo);
    }

    @PostMapping("/api/user/checkwebhook")
    public AjaxResult checkWebhook(@RequestBody SysUserInfo userInfo) {
        return userService.checkWebhook(userInfo.getWebhook());
    }
}
