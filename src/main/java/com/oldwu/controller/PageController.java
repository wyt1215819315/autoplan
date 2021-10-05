package com.oldwu.controller;

import com.miyoushe.model.AutoMihayou;
import com.miyoushe.service.MihayouService;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.oldwu.dao.UserDao;
import com.oldwu.entity.AutoBilibili;
import com.oldwu.entity.BiliPlan;
import com.oldwu.service.BiliService;
import com.oldwu.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    private BiliService biliService;

    @Autowired
    private NetmusicService netmusicService;

    @Autowired
    private MihayouService mihayouService;

    @Autowired
    private UserService userService;

    @GetMapping("/webhook-generate")
    public String webhookGenerate(){
        return "webhook-generate";
    }

    @GetMapping("/netmusic/index")
    public String netmusicindex(Model model){
        List<AutoNetmusic> allPlan = netmusicService.getAllPlan();
        model.addAttribute("list",allPlan);
        return "netmusic-helper";
    }

    @GetMapping("/netmusic/add")
    public String netmusicaddPage(){
        return "netmusic-helper-add";
    }

    @GetMapping("/bili/index")
    public String index(Model model){
        List<BiliPlan> allPlan = biliService.getAllPlan();
        model.addAttribute("list",allPlan);
        return "bili-helper";
    }

    @GetMapping("/bili/edit")
    public String getMyBiliEditPage(@Param("id") Integer id, Principal principal,Model model){
        AutoBilibili autoBilibili = new AutoBilibili();
        autoBilibili.setUserid(userService.getUserId(principal.getName()));
        autoBilibili.setId(id);
        model.addAttribute("bili",biliService.getMyEditPlan(autoBilibili));
        return "bili-helper-edit";
    }

    @GetMapping("/netmusic/edit")
    public String getMyNetmusicEditPage(@Param("id") Integer id, Principal principal,Model model){
        AutoNetmusic autoNetmusic = new AutoNetmusic();
        autoNetmusic.setUserid(userService.getUserId(principal.getName()));
        autoNetmusic.setId(id);
        model.addAttribute("netmusic",netmusicService.getMyEditPlan(autoNetmusic));
        return "netmusic-helper-edit";
    }

    @GetMapping("/mihuyou/edit")
    public String getMyMihuYouEditPage(@Param("id") Integer id, Principal principal,Model model){
        AutoMihayou autoMihayou = new AutoMihayou();
        autoMihayou.setUserId(userService.getUserId(principal.getName()));
        autoMihayou.setId(id);
        model.addAttribute("mihuyou",mihayouService.getMyEditPlan(autoMihayou));
        return "miyoushe-helper-edit";
    }

    @GetMapping("/bili/add")
    public String addPage(){
        return "bili-helper-add";
    }

    @GetMapping("/mihuyou/index")
    public String miyousheindex(Model model){
        List<AutoMihayou> allPlan = mihayouService.getAllPlan();
        model.addAttribute("list",allPlan);
        return "miyoushe-helper";
    }

    @GetMapping("/mihuyou/add")
    public String miyousheaddPage(){
        return "miyoushe-helper-add";
    }

}
