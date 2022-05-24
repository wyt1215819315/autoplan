package com.oldwu.controller;

import com.oldwu.entity.AjaxResult;
import com.oldwu.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {
    @Autowired
    private SysService sysService;

    /**
     * 编辑系统首页公告
     * @return AjaxResult 公告内容
     */
    @PostMapping("/api/index/welcome-notice/edit")
    public AjaxResult editSystemNoticeContent(@RequestParam String text){
        return sysService.setSystemNoticeContent(text);
    }

    /**
     * 用于转换老版本的字段到json
     * @return AjaxResult
     */
    @PostMapping("/api/admin/turnbiliplan2json")
    public AjaxResult turnBiliPlan2Json(){
        return sysService.turnBiliPlan2Json();
    }

}
