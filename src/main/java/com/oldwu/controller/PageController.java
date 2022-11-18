package com.oldwu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController {

    /**
     * 首页
     * @return ModelAndView
     */
    @RequestMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    /**
     * 欢迎页
     * @return ModelAndView
     */
    @RequestMapping("/welcomePage")
    public ModelAndView welcomePage() {
        return new ModelAndView("welcomePage");
    }

    /**
     * 公告编辑页
     * @return ModelAndView
     */
    @RequestMapping("/system-notice-edit")
    public ModelAndView editSystemNotice() {
        return new ModelAndView("system-notice-edit");
    }

    /**
     * 登录页
     * @return ModelAndView
     */
    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    /**
     * 注册页
     * @return ModelAndView
     */
    @GetMapping("/reg")
    public ModelAndView reg() {
        return new ModelAndView("reg");
    }

    /**
     * 日志查看页
     * @return ModelAndView
     */
    @GetMapping("/getlog")
    public ModelAndView getLog() {
        return new ModelAndView("myHelper/getlog");
    }

    /**
     * 我的任务页
     * @return ModelAndView
     */
    @GetMapping("/my")
    public ModelAndView myPage() {
        return new ModelAndView("myHelper/my-helper");
    }

    /**
     * 全局推送配置页
     * @return ModelAndView
     */
    @GetMapping("/my-edit")
    public ModelAndView myEditPage() {
        return new ModelAndView("myHelper/my-helper-edit");
    }

    /**
     * 推送地址生成器页面
     * @return ModelAndView
     */
    @GetMapping("/webhook-generate")
    public ModelAndView webhookGeneratePage(){
        return new ModelAndView("myHelper/webhook-generate");
    }

    /**
     * 哔哩哔哩任务表格显示页
     * @return ModelAndView
     */
    @GetMapping("/bili/index")
    public ModelAndView biliIndexPage(){
        return new ModelAndView("bili/bili-helper");
    }

    /**
     * 哔哩哔哩任务修改页
     * @return ModelAndView
     */
    @GetMapping("/bili/edit")
    public ModelAndView biliEditPage(){
        return new ModelAndView("bili/bili-helper-edit");
    }

    /**
     * 哔哩哔哩任务添加页
     * @return ModelAndView
     */
    @GetMapping("/bili/add")
    public ModelAndView biliAddPage(){
        return new ModelAndView("bili/bili-helper-add");
    }

    /**
     * 网易云任务表格显示页
     * @return ModelAndView
     */
    @GetMapping("/netmusic/index")
    public ModelAndView netmusicIndexPage(){
        return new ModelAndView("netmusic/netmusic-helper");
    }

    /**
     * 网易云任务添加页
     * @return ModelAndView
     */
    @GetMapping("/netmusic/add")
    public ModelAndView netmusicAddPage(){
        return new ModelAndView("netmusic/netmusic-helper-add");
    }

    /**
     * 网易云任务修改页
     * @return ModelAndView
     */
    @GetMapping("/netmusic/edit")
    public ModelAndView netmusicEditPage(){
        return new ModelAndView("netmusic/netmusic-helper-edit");
    }

    /**
     * 米哈游任务表格显示页
     * @return ModelAndView
     */
    @GetMapping("/mihuyou/index")
    public ModelAndView miyousheIndexPage(){
        return new ModelAndView("miyoushe/miyoushe-helper");
    }

    /**
     * 米哈游任务添加页
     * @return ModelAndView
     */
    @GetMapping("/mihuyou/add")
    public ModelAndView miyousheAddPage(){
        return new ModelAndView("miyoushe/miyoushe-helper-add");
    }

    /**
     * 米哈游任务修改页
     * @return ModelAndView
     */
    @GetMapping("/mihuyou/edit")
    public ModelAndView mihuYouEditPage(){
        return new ModelAndView("miyoushe/miyoushe-helper-edit");
    }

    /**
     * 小米运动任务展示首页
     *
     * @return
     */
    @GetMapping("/xiaomi/index")
    public ModelAndView xiaoMiIndexPage() {
        return new ModelAndView("xiaomi/xiaomi-helper.html");
    }

    /**
     * 小米运动任务添加页
     *
     * @return ModelAndView
     */
    @GetMapping("/xiaomi/add")
    public ModelAndView xiaoMiAddPage() {
        return new ModelAndView("xiaomi/xiaomi-helper-add.html");
    }

    /**
     * 小米运动任务修改页
     *
     * @return ModelAndView
     */
    @GetMapping("/xiaomi/edit")
    public ModelAndView xiaoMiEditPage() {
        return new ModelAndView("xiaomi/xiaomi-helper-edit.html");
    }

}
