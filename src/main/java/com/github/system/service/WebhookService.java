package com.github.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.push.PushUtil;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.model.PushData;
import com.github.system.configuration.SystemBean;
import com.github.system.dao.SysWebhookDao;
import com.github.system.entity.AjaxResult;
import com.github.system.entity.SysWebhook;
import com.github.system.security.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebhookService {

    @Autowired
    private SysWebhookDao sysWebhookDao;
    @Autowired
    private SystemBean systemBean;

    public List<SysWebhook> getUserWebhook(int userId) {
        LambdaQueryWrapper<SysWebhook> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysWebhook::getUserId, userId);
        return sysWebhookDao.selectList(queryWrapper);
    }

    public AjaxResult checkWebhook(PushData<?> pushData) {
        pushData.setUserId(SessionUtils.getUserId());
        pushData.setTitle(systemBean.getTitle() + "测试消息");
        PushResultDto pushResultDto = PushUtil.doPush(pushData);
        if (pushResultDto.isSuccess()) {
            return AjaxResult.doSuccess("推送成功，请检查是否正常收到推送！");
        }
        return AjaxResult.doError("推送失败！" + pushResultDto.getMsg());
    }
}
