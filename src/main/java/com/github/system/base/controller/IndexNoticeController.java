package com.github.system.base.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.StrUtil;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.vo.IndexNoticeVo;
import com.github.system.base.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "首页公告")
@RestController
@RequestMapping("/index/notice")
public class IndexNoticeController {
    @Resource
    private SysConfigService sysConfigService;

    @ApiOperation("编辑")
    @PostMapping("/edit")
    @SaCheckRole("ADMIN")
    public AjaxResult edit(@RequestBody IndexNoticeVo indexNoticeVo) {
        return sysConfigService.saveOrUpdate(SystemConstant.SYSTEM_NOTICE_CONTENT, indexNoticeVo.getText()) ? AjaxResult.doSuccess() : AjaxResult.doError();
    }

    @ApiOperation("查看")
    @GetMapping("/view")
    public AjaxResult view() {
        String value = sysConfigService.getValueByKey(SystemConstant.SYSTEM_NOTICE_CONTENT);
        if (StrUtil.isEmpty(value)) {
            return AjaxResult.doSuccess("", "当前无公告！");
        }
        return AjaxResult.doSuccess("", value);
    }


}
