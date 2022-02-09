package com.oldwu.controller;

import com.bili.model.BiliPlan;
import com.miyoushe.model.AutoMihayou;
import com.miyoushe.service.MihayouService;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.oldwu.entity.AjaxResult;
import com.bili.service.BiliService;
import com.oldwu.service.SysService;
import com.oldwu.vo.PageDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private SysService sysService;

    /**
     * 获取系统首页公告
     * @return AjaxResult 公告内容
     */
    @RequestMapping("/welcome-notice/list")
    public AjaxResult getSystemNoticeContent(){
        return sysService.getSystemNoticeContent();
    }

    /**
     * 编辑系统首页公告
     * @return AjaxResult 公告内容
     */
    @PostMapping("/welcome-notice/edit")
    public AjaxResult editSystemNoticeContent(@RequestParam String text){
        return sysService.setSystemNoticeContent(text);
    }


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
