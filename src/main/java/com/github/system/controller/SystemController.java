package com.github.system.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.github.system.base.dto.AjaxResult;
import com.github.system.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SaCheckRole("ADMIN")
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


}
