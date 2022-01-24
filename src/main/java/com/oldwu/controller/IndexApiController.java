package com.oldwu.controller;

import com.miyoushe.model.AutoMihayou;
import com.miyoushe.service.MihayouService;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.oldwu.entity.BiliPlan;
import com.oldwu.service.BiliService;
import com.oldwu.vo.PageDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/index")
public class IndexApiController {

    @Autowired
    private BiliService biliService;

    @Autowired
    private NetmusicService netmusicService;

    @Autowired
    private MihayouService mihayouService;

    /**
     * 获取b站任务列表
     * @param page 页码
     * @param limit 每页数
     * @return PageDataVO<BiliPlan>
     */
    @RequestMapping("/bili/list")
    public PageDataVO<BiliPlan> biliList(Integer page, Integer limit){
        return biliService.queryPageList(page, limit);
    }

    /**
     * 获取网易云任务列表
     * @param page 页码
     * @param limit 每页数
     * @return PageDataVO<BiliPlan>
     */
    @RequestMapping("/netmusic/list")
    public PageDataVO<AutoNetmusic> netmusicList(Integer page, Integer limit){
        return netmusicService.queryPageList(page, limit);
    }

    /**
     * 获取米游社任务列表
     * @param page 页码
     * @param limit 每页数
     * @return PageDataVO<BiliPlan>
     */
    @RequestMapping("/mihuyou/list")
    public PageDataVO<AutoMihayou> mihuyouList(Integer page, Integer limit){
        return mihayouService.queryPageList(page, limit);
    }

}
