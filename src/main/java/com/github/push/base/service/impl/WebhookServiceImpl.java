package com.github.push.base.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.push.PushUtil;
import com.github.push.base.dto.PushResultDto;
import com.github.push.base.init.PushInit;
import com.github.push.base.model.PushData;
import com.github.push.base.service.WebhookService;
import com.github.system.auth.util.SessionUtils;
import com.github.system.base.configuration.SystemBean;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.dao.SysWebhookDao;
import com.github.system.base.dto.AjaxResult;
import com.github.system.base.entity.SysWebhook;
import com.github.system.base.service.SysConfigService;
import com.github.system.task.dto.TaskLog;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebhookServiceImpl extends ServiceImpl<SysWebhookDao, SysWebhook> implements WebhookService {

    @Resource
    private SystemBean systemBean;
    @Resource
    private SysConfigService configService;

    @Override
    public Map<String, Object> getColumn() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("list", PushInit.pushTypeList);
        resultMap.put("data", PushInit.pushConfigMap);
        return resultMap;
    }

    @Override
    public boolean saveOrUpdateCustom(SysWebhook entity) throws Exception {
        if (entity.getEnable() == null) {
            entity.setEnable(1);
        }
        Long currentUserId = SessionUtils.getUserId();
        entity.setUserId(currentUserId);
        if (entity.getId() == null) {
            // 检查下这货有多少webhook了，最多只能加n个限制一下
            long maxNum = Long.parseLong(configService.getValueByKeyOrDefault(SystemConstant.USER_WEBHOOK_MAX_LIMIT, "10"));
            long count = count(new LambdaQueryWrapper<SysWebhook>().eq(SysWebhook::getUserId, SessionUtils.getUserId()));
            if (count >= maxNum) {
                throw new Exception("最多只能添加" + maxNum + "个WebHook哦.");
            }
            return super.save(entity);
        } else {
            SysWebhook sysWebhook = getById(entity.getId());
            if (sysWebhook == null) {
                throw new Exception("无匹配实体");
            }
            if (!sysWebhook.getUserId().equals(currentUserId) && !SessionUtils.isAdmin()) {
                throw new Exception("无权限修改");
            }
            return super.updateById(entity);
        }
    }

    @Override
    public boolean changeStatus(SysWebhook sysWebhook) {
        SysWebhook query = getById(sysWebhook.getId());
        if (query == null) {
            return false;
        }
        query.setEnable(sysWebhook.getEnable());
        baseMapper.updateById(sysWebhook);
        return true;
    }

    @Override
    public List<SysWebhook> getUserWebhook(long userId) {
        LambdaQueryWrapper<SysWebhook> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysWebhook::getUserId, userId);
        queryWrapper.eq(SysWebhook::getEnable, 1);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public AjaxResult checkWebhook(PushData<?> pushData) {
        pushData.setUserId(SessionUtils.getUserId());
        pushData.setTitle(systemBean.getTitle() + "测试任务通知");
        TaskLog taskLog = new TaskLog();
        taskLog.info(SystemConstant.SYSTEM_WEBHOOK_TEST_MSG);
        pushData.setTaskLog(taskLog);
        PushResultDto pushResultDto = PushUtil.doPush(pushData);
        if (pushResultDto.isSuccess()) {
            return AjaxResult.doSuccess("推送成功，请检查是否正常收到推送！");
        }
        return AjaxResult.doError("<h3>推送失败！</h3><br/>" + pushResultDto.getMsg() + (StrUtil.isBlank(pushResultDto.getData()) ? "" : ("<br/>" + pushResultDto.getData())));
    }
}
