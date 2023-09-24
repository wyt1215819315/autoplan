package com.github.system.controller;

import com.github.task.bili.model.BiliPlan;
import com.github.task.bili.service.BiliService;
import com.github.task.miyoushe.model.AutoMihayou;
import com.github.task.miyoushe.service.MihayouService;
import com.github.task.netmusic.model.AutoNetmusic;
import com.github.task.netmusic.service.NetmusicService;
import com.github.system.entity.AjaxResult;
import com.github.system.service.SysService;
import com.github.system.vo.PageDataVO;
import com.github.task.xiaomi.model.entity.AutoXiaomiEntity;
import com.github.task.xiaomi.service.AutoXiaomiService;
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

    @Autowired
    private SysService sysService;

    @Autowired
    private AutoXiaomiService autoXiaomiService;

    /**
     * 获取系统首页公告
     *
     * @return AjaxResult 公告内容
     */
    @RequestMapping("/welcome-notice/list")
    public AjaxResult getSystemNoticeContent() {
        return sysService.getSystemNoticeContent();
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

    /**
     * 获取小米运动任务列表
     *
     * @param page  页码
     * @param limit 每页数
     * @return PageDataVO<BiliPlan>
     */
    @RequestMapping("/xiaomi/list")
    public PageDataVO<AutoXiaomiEntity> xiaoMiList(Integer page, Integer limit) {
        return autoXiaomiService.queryPageList(page, limit);
    }
}
