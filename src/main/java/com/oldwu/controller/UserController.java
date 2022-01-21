package com.oldwu.controller;

import com.oldwu.entity.AjaxResult;
import com.oldwu.entity.AutoLog;
import com.oldwu.entity.SysUserInfo;
import com.oldwu.security.entity.SystemUser;
import com.oldwu.security.utils.SessionUtils;
import com.oldwu.service.LogService;
import com.oldwu.service.RegService;
import com.oldwu.service.UserService;
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

    @PostMapping("/reg")
    public AjaxResult regPost(@Param("username") String username, @Param("password") String password) {
        //TODO 加入验证码验证
        return regService.doReg(username, password);
    }

    /**
     * 获取执行日志，只能获取最新一条
     * @param params id=auto_id，type=mihuyou/netmusic/bili
     * @return AutoLog
     */
    @PostMapping("/api/user/getlog")
    public AjaxResult getLog(@RequestParam Map<String, String> params) {
        if (!params.containsKey("id") || StringUtils.isBlank(params.get("id"))){
            return AjaxResult.doError("ID不能为空！");
        }
        if (!params.containsKey("type") || StringUtils.isBlank(params.get("type"))){
            return AjaxResult.doError("TYPE不能为空！");
        }
        AutoLog log = logService.getLog(Integer.valueOf(params.get("id")), params.get("type"), SessionUtils.getPrincipal().getId());
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
