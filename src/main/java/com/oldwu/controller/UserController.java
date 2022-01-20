package com.oldwu.controller;

import com.miyoushe.model.AutoMihayou;
import com.miyoushe.service.MihayouService;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.oldwu.entity.AjaxResult;
import com.oldwu.entity.AutoLog;
import com.oldwu.entity.BiliPlan;
import com.oldwu.entity.SysUserInfo;
import com.oldwu.security.entity.SystemUser;
import com.oldwu.security.utils.SessionUtils;
import com.oldwu.service.BiliService;
import com.oldwu.service.LogService;
import com.oldwu.service.RegService;
import com.oldwu.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by yangyibo on 17/1/18.
 */
@Controller
public class UserController {

    @Autowired
    private RegService regService;

    @Autowired
    private BiliService biliService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private NetmusicService netmusicService;

    @Autowired
    private MihayouService mihayouService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/welcomePage")
    public String welcomePage() {
        return "welcomePage";
    }

    @GetMapping("/login")
    public String login() {
        return "login2";
    }

    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }

    @GetMapping("/reg")
    public String reg() {
        return "reg2";
    }

    @PostMapping("/reg")
    @ResponseBody
    public AjaxResult regPost(@Param("username") String username, @Param("password") String password, Model model) {
        //TODO 加入验证码验证
        return regService.doReg(username, password);
    }

    @GetMapping("/getlog")
    public String getLog(Model model, Principal principal, @Param("id") Integer id, @Param("type") String type) {
        AutoLog log = logService.getLog(id, type, userService.getUserId(principal.getName()));
        if (log == null || log.getId() == null) {
            AutoLog log1 = new AutoLog();
            log1.setText("当前无日志");
            log1.setDate(new Date());
            model.addAttribute("log", log1);
            return "getlog";
        }
        String logText = log.getText();
        log.setText(logText.substring(1, logText.length()).replace("\n", "<br/>"));
        model.addAttribute("log", log);
        return "getlog";
    }

    @GetMapping("/my")
    public String myIndex(Model model, Principal principal) {
        List<BiliPlan> biliPlans = biliService.getMyPlan(userService.getUserId(principal.getName()));
        List<AutoNetmusic> netplans = netmusicService.getMyPlan(userService.getUserId(principal.getName()));
        List<AutoMihayou> mihuyouplans = mihayouService.getMyPlan(userService.getUserId(principal.getName()));
        model.addAttribute("bililist", biliPlans);
        model.addAttribute("netlist", netplans);
        model.addAttribute("milist", mihuyouplans);
        return "my-helper2";
    }

    @GetMapping("/my-edit")
    public String myIndexEdit(Model model, Principal principal) {
        int userId = userService.getUserId(principal.getName());
        SysUserInfo userInfo = userService.getUserInfo(userId);
        model.addAttribute("info",userInfo);
        return "my-helper-edit";
    }

    /**
     * 获取执行日志，只能获取最新一条
     * @param params id=auto_id，type=mihuyou/netmusic/bilibili
     * @return AutoLog
     */
    @PostMapping("/api/user/getlog")
    @ResponseBody
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

    @ResponseBody
    @PostMapping("/api/user/me")
    public AjaxResult me(){
        if (SessionUtils.getPrincipal() == null){
            return AjaxResult.doError("未登录！");
        }
        SystemUser systemUser = SessionUtils.getPrincipal();
        systemUser.setPassword(null);
        return AjaxResult.doSuccess(systemUser);
    }

    @ResponseBody
    @PostMapping("/api/user/userinfo/edit")
    public AjaxResult userInfoEdit(Principal principal, @RequestBody SysUserInfo userInfo) {
        int userId = userService.getUserId(principal.getName());
        return userService.editUserInfo(userId,userInfo);
    }

    @ResponseBody
    @PostMapping("/api/user/checkwebhook")
    public AjaxResult checkWebhook(@RequestBody SysUserInfo userInfo) {
        return userService.checkWebhook(userInfo.getWebhook());
    }
}
