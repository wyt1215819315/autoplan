package com.oldwu.controller;

import com.miyoushe.model.AutoMihayou;
import com.miyoushe.service.MihayouService;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.oldwu.entity.BiliPlan;
import com.oldwu.service.BiliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private BiliService biliService;

    @Autowired
    private NetmusicService netmusicService;

    @Autowired
    private MihayouService mihayouService;


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

    @GetMapping("/bili/add")
    public String addPage(){
        return "bili-helper-add";
    }

    @GetMapping("/miyoushe/index")
    public String miyousheindex(Model model){
        List<AutoMihayou> allPlan = mihayouService.getAllPlan();
        model.addAttribute("list",allPlan);
        return "miyoushe-helper";
    }

    @GetMapping("/miyoushe/add")
    public String miyousheaddPage(){
        return "miyoushe-helper-add";
    }

}
