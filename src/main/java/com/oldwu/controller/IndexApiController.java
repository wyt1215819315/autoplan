package com.oldwu.controller;

import com.miyoushe.model.AutoMihayou;
import com.miyoushe.service.MihayouService;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.oldwu.entity.AjaxResult;
import com.oldwu.entity.BiliPlan;
import com.oldwu.service.BiliService;
import com.oldwu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/index")
public class IndexApiController {

    @Autowired
    private BiliService biliService;

    @Autowired
    private NetmusicService netmusicService;

    @Autowired
    private MihayouService mihayouService;

    @Autowired
    private UserService userService;

    /**
     * 获取b站任务列表
     * @return
     */
    @RequestMapping("/bili/list")
    public AjaxResult biliList(){
        List<BiliPlan> allPlan = biliService.getAllPlan();
        return AjaxResult.doSuccess(allPlan);
    }

    /**
     * 获取网易云任务列表
     * @return
     */
    @RequestMapping("/netmusic/list")
    public AjaxResult netmusicList(){
        List<AutoNetmusic> allPlan = netmusicService.getAllPlan();
        return AjaxResult.doSuccess(allPlan);
    }

    /**
     * 获取米游社任务列表
     * @return
     */
    @RequestMapping("/mihuyou/list")
    public AjaxResult mihuyouList(){
        List<AutoMihayou> allPlan = mihayouService.getAllPlan();
        return AjaxResult.doSuccess(allPlan);
    }

}
