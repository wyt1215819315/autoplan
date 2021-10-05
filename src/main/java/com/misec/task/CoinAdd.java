package com.misec.task;

import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.apiquery.OftenApi;
import com.misec.config.ConfigLoader;
import com.misec.login.Verify;
import com.misec.utils.HttpUtils;
import com.misec.utils.SleepTime;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

import static com.misec.task.TaskInfoHolder.STATUS_CODE_STR;
import static com.misec.task.TaskInfoHolder.getVideoId;

/**
 * 投币任务
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:28
 */
@SuppressWarnings("StringConcatenationArgumentToLogCall")
@Log4j2
public class CoinAdd implements Task {

    /**
     * 检查是否投币.
     *
     * @param bvid av号
     * @return 返回是否投过硬币了.
     */
    static boolean isCoinAdded(String bvid) {
        String urlParam = "?bvid=" + bvid;
        JsonObject result = HttpUtils.doGet(ApiList.IS_COIN + urlParam);

        int multiply = result.getAsJsonObject("data").get("multiply").getAsInt();
        if (multiply > 0) {
            log.info("之前已经为av" + bvid + "投过" + multiply + "枚硬币啦");
            OldwuLog.log("之前已经为av" + bvid + "投过" + multiply + "枚硬币啦");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void run() {

        //投币最多操作数 解决csrf校验失败时死循环的问题
        int addCoinOperateCount = 0;
        //安全检查，最多投币数
        final int maxNumberOfCoins = 5;
        //获取自定义配置投币数 配置写在src/main/resources/taskConfig.json中
        int setCoin = ConfigLoader.getTaskConfig().getNumberOfCoins();
        // 预留硬币数
        int reserveCoins = ConfigLoader.getTaskConfig().getReserveCoins();

        //已投的硬币
        int useCoin = TaskInfoHolder.expConfirm();
        //投币策略
        int coinAddPriority = ConfigLoader.getTaskConfig().getCoinAddPriority();

        if (setCoin > maxNumberOfCoins) {
            OldwuLog.log("自定义投币数为: " + setCoin + "枚," + "为保护你的资产，自定义投币数重置为: " + maxNumberOfCoins + "枚");
            log.info("自定义投币数为: " + setCoin + "枚," + "为保护你的资产，自定义投币数重置为: " + maxNumberOfCoins + "枚");
            setCoin = maxNumberOfCoins;
        }
        OldwuLog.log("自定义投币数为: " + setCoin + "枚," + "程序执行前已投: " + useCoin + "枚");
        log.info("自定义投币数为: " + setCoin + "枚," + "程序执行前已投: " + useCoin + "枚");

        //调整投币数 设置投币数-已经投过的硬币数
        int needCoins = setCoin - useCoin;

        //投币前硬币余额
        Double beforeAddCoinBalance = OftenApi.getCoinBalance();
        int coinBalance = (int) Math.floor(beforeAddCoinBalance);


        if (needCoins <= 0) {
            OldwuLog.log("已完成设定的投币任务，今日无需再投币了");
            log.info("已完成设定的投币任务，今日无需再投币了");
            return;
        } else {
            OldwuLog.log("投币数调整为: " + needCoins + "枚");
            log.info("投币数调整为: " + needCoins + "枚");
            //投币数大于余额时，按余额投
            if (needCoins > coinBalance) {
                OldwuLog.log("完成今日设定投币任务还需要投: " + needCoins + "枚硬币，但是余额只有: " + beforeAddCoinBalance);
                OldwuLog.log("投币数调整为: " + coinBalance);
                log.info("完成今日设定投币任务还需要投: " + needCoins + "枚硬币，但是余额只有: " + beforeAddCoinBalance);
                log.info("投币数调整为: " + coinBalance);
                needCoins = coinBalance;
            }
        }

        if (coinBalance < reserveCoins) {
            OldwuLog.log("剩余硬币数为" + beforeAddCoinBalance + ",低于预留硬币数" + reserveCoins + ",今日不再投币");
            log.info("剩余硬币数为{},低于预留硬币数{},今日不再投币", beforeAddCoinBalance, reserveCoins);
            log.info("tips: 当硬币余额少于你配置的预留硬币数时，则会暂停当日投币任务");
            return;
        }
        OldwuLog.log("投币前余额为 : " + beforeAddCoinBalance);
        log.info("投币前余额为 : " + beforeAddCoinBalance);
        /*
         * 请勿修改 max_numberOfCoins 这里多判断一次保证投币数超过5时 不执行投币操作.
         * 最后一道安全判断，保证即使前面的判断逻辑错了，也不至于发生投币事故.
         */
        while (needCoins > 0 && needCoins <= maxNumberOfCoins) {
            String bvid;
            if (coinAddPriority == 1 && addCoinOperateCount < 7) {
                bvid = getVideoId.getFollowUpRandomVideoBvid();
            } else {
                bvid = getVideoId.getRegionRankingVideoBvid();
            }

            addCoinOperateCount++;
            new VideoWatch().watchVideo(bvid);
            new SleepTime().sleepDefault();
            boolean flag = coinAdd(bvid, 1, ConfigLoader.getTaskConfig().getSelectLike());
            if (flag) {
                needCoins--;
            }
            if (addCoinOperateCount > 15) {
                OldwuLog.error("尝试投币/投币失败次数太多");
                log.info("尝试投币/投币失败次数太多");
                break;
            }
        }
        log.info("投币任务完成后余额为: {}", OftenApi.getCoinBalance());
    }

    /**
     * 投币操作工具类.
     *
     * @param bvid       av号
     * @param multiply   投币数量
     * @param selectLike 是否同时点赞 1是
     * @return 是否投币成功
     */
    private boolean coinAdd(String bvid, int multiply, int selectLike) {
        String videoTitle = OftenApi.getVideoTitle(bvid);
        //判断曾经是否对此av投币过
        if (!isCoinAdded(bvid)) {
            Map<String, String> headers = new HashMap<>(10);
            headers.put("Referer", "https://www.bilibili.com/video/" + bvid);
            headers.put("Origin", "https://www.bilibili.com");
            String requestBody = "bvid=" + bvid
                    + "&multiply=" + multiply
                    + "&select_like=" + selectLike
                    + "&cross_domain=" + "true"
                    + "&csrf=" + Verify.getInstance().getBiliJct();
            JsonObject jsonObject = HttpUtils.doPost(ApiList.COIN_ADD, requestBody, headers);
            if (jsonObject.get(STATUS_CODE_STR).getAsInt() == 0) {
                log.info("为 " + videoTitle + " 投币成功");
                OldwuLog.log("为 " + videoTitle + " 投币成功");
                return true;
            } else {
                OldwuLog.log("投币失败" + jsonObject.get("message").getAsString());
                log.info("投币失败" + jsonObject.get("message").getAsString());
                return false;
            }
        } else {
            OldwuLog.log("已经为" + videoTitle + "投过币了");
            log.debug("已经为" + videoTitle + "投过币了");
            return false;
        }
    }

    @Override
    public String getName() {
        return "投币任务";
    }
}
