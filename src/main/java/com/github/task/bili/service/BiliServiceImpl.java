package com.github.task.bili.service;


import cn.hutool.core.date.DateUtil;
import com.github.system.task.annotation.TaskAction;
import com.github.system.task.constant.AutoTaskStatus;
import com.github.system.task.dto.LoginResult;
import com.github.system.task.dto.TaskInfo;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.dto.TaskResult;
import com.github.system.task.service.BaseTaskService;
import com.github.task.bili.model.BiliSettings;
import com.github.task.bili.model.BiliUserInfo;
import com.github.task.bili.model.task.BiliData;
import com.github.task.bili.model.task.BiliTaskInfo;
import com.github.task.bili.model.task.config.*;
import com.github.task.bili.util.BiliTaskUtil;

import java.time.Duration;
import java.util.Date;

public class BiliServiceImpl extends BaseTaskService<BiliSettings, BiliUserInfo> {
    private BiliTaskUtil biliTaskUtil = null;

    @Override
    public TaskInfo getTaskInfo() {
        return new TaskInfo("哔哩哔哩自动化", "bili", Duration.ofMinutes(1));
    }

    @Override
    public TaskResult init(TaskLog log) throws Exception {
        this.biliTaskUtil = new BiliTaskUtil(getBiliTaskInfo(), log);
        try {
            biliTaskUtil.userCheck();
            return TaskResult.doSuccess("登录校验成功！");
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage(), AutoTaskStatus.USER_CHECK_ERROR);
        }
    }

    @Override
    public LoginResult<BiliUserInfo> checkUser() throws Exception {
        if (biliTaskUtil != null && biliTaskUtil.getData() != null) {
            return LoginResult.doSuccess("登录校验成功！", turnUserInfo(biliTaskUtil.getData()));
        }
        return LoginResult.doError("登录校验失败，data为null");
    }

    @Override
    public BiliUserInfo getUserInfo() throws Exception {
        if (biliTaskUtil.getData() == null) {
            biliTaskUtil.userCheck();
        }
        return turnUserInfo(biliTaskUtil.getData());
    }

    @TaskAction(name = "漫画签到")
    public TaskResult cartoonSign(TaskLog log) throws Exception {
        try {
            biliTaskUtil.cartoonSign();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "充电功能")
    public TaskResult chargeMe(TaskLog log) throws Exception {
        try {
            biliTaskUtil.chargeMe();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "投币功能")
    public TaskResult coinAdd(TaskLog log) throws Exception {
        try {
            biliTaskUtil.coinAdd();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "直播送礼")
    public TaskResult liveGift(TaskLog log) throws Exception {
        try {
            biliTaskUtil.liveGift();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "直播签到")
    public TaskResult liveSign(TaskLog log) throws Exception {
        try {
            biliTaskUtil.liveSign();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "赛事预测")
    public TaskResult matchGame(TaskLog log) throws Exception {
        try {
            biliTaskUtil.matchGame();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "每日漫画阅读")
    public TaskResult readCartoon(TaskLog log) throws Exception {
        try {
            biliTaskUtil.readCartoon();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "银瓜子换硬币")
    public TaskResult silver2Coin(TaskLog log) throws Exception {
        try {
            biliTaskUtil.silver2Coin();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "大会员漫画权益领取")
    public TaskResult vipCartoonRec(TaskLog log) throws Exception {
        try {
            biliTaskUtil.vipCartoonRec();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "观看视频功能")
    public TaskResult watchVideo(TaskLog log) throws Exception {
        try {
            biliTaskUtil.watchVideo();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            log.error(e);
            return TaskResult.doError(e.getMessage());
        }
    }

    @TaskAction(name = "统计数值", order = 1, delay = 0)
    public TaskResult calculateUpgradeDays(TaskLog log) throws Exception {
        try {
            biliTaskUtil.calculateUpgradeDays();
            return TaskResult.doSuccess();
        } catch (Exception e) {
            return TaskResult.doError(e.getMessage());
        }
    }


    private BiliUserInfo turnUserInfo(BiliData biliData) {
        BiliUserInfo biliUserInfo = new BiliUserInfo();
        biliUserInfo.setBiliCoin(biliData.getMoney());
        biliUserInfo.setBiliName(biliData.getUname());
        biliUserInfo.setBiliExp(biliData.getLevel_info().getCurrent_exp());
        biliUserInfo.setBiliLevel(biliData.getLevel_info().getCurrent_level());
        if (biliData.getVipStatus() == 1) {
            biliUserInfo.setIsVip(biliData.getVipType());
        } else {
            biliUserInfo.setIsVip(0);
        }
        biliUserInfo.setVipDueDate(DateUtil.formatDate(new Date(biliData.getVipDueDate())));
        biliUserInfo.setBiliUpExp(biliData.getLevel_info().getNext_exp_asInt());
        biliUserInfo.setOnlyId(String.valueOf(biliData.getMid()));
        biliUserInfo.setHeadImg(biliData.getFace());
        return biliUserInfo;
    }

    private BiliTaskInfo getBiliTaskInfo() {
        BiliTaskConfig taskConfig = new BiliTaskConfig();
        taskConfig.setCartoonSignOS(taskConfig.getCartoonSignOS());
        taskConfig.setBiliCoinConfig(new BiliCoinConfig(taskSettings.getDailyCoin(), taskSettings.getReserveCoins(), taskSettings.getEnableClickLike() == 1, taskSettings.getCoinRules()));
        taskConfig.setBiliChargeConfig(new BiliChargeConfig(taskSettings.getChargeObject(), taskSettings.getChargeDay(), taskSettings.getEnableAutoCharge() == 1));
        taskConfig.setBiliPreConfig(new BiliPreConfig(taskSettings.getEnablePre() == 1, taskSettings.getEnableReversePre() == 1, taskSettings.getPreCoin(), taskSettings.getKeepCoin()));
        taskConfig.setBiliGiveGiftConfig(new BiliGiveGiftConfig(taskSettings.getEnableGiveGift() == 1, taskSettings.getGiveGiftRoomID()));
        BiliTaskInfo taskInfo = new BiliTaskInfo(taskSettings.getDedeuserid(), taskSettings.getSessdata(), taskSettings.getBiliJct());
        taskInfo.setTaskConfig(taskConfig);
        return taskInfo;
    }

}
