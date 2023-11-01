package com.github.task.bili.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.system.desensitized.DataDesensitizationUtil;
import com.github.system.task.dto.TaskLog;
import com.github.task.bili.constant.BiliUrlConstant;
import com.github.task.bili.model.task.BiliData;
import com.github.task.bili.model.task.BiliTaskInfo;
import com.github.task.bili.model.task.config.BiliTaskConfig;
import com.github.task.bili.util.task.GetVideoId;
import com.github.task.bili.util.task.GiveGift;
import com.github.task.bili.util.task.MatchGame;
import com.github.task.bili.util.task.VideoWatch;
import lombok.Getter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;


/**
 * @author oldwu
 * 警告，此类必须使用new来新建一个对象进行初始化后才能调用！
 */
public class BiliTaskUtil {
    private final Log logger = LogFactory.getLog(BiliTaskUtil.class);
    private final BiliTaskInfo taskInfo;
    private final BiliWebUtil biliWebUtil;
    private final BiliTaskConfig taskConfig;
    @Getter
    private BiliData data;

    public BiliTaskUtil(BiliTaskInfo taskInfo) {
        this.taskInfo = taskInfo;
        this.taskConfig = taskInfo.getTaskConfig();
        biliWebUtil = new BiliWebUtil(taskInfo.getCookie());
    }

    /**
     * 第一步 校验用户信息
     *
     * @throws Exception exception
     */
    public BiliData userCheck(TaskLog log) throws Exception {
        JSONObject userJson = biliWebUtil.doGet(BiliUrlConstant.BILI_LOGIN);
        if (userJson == null) {
            logger.error("b站用户信息校验失败：" + taskInfo.toString());
            throw new Exception("用户信息请求失败！");
        } else {
            //判断Cookies是否有效
            if (userJson.getInt("code") == 0 && userJson.getJSONObject("data").getBool("isLogin")) {
                log.info("Cookie有效，登录成功");
            } else {
                log.error("Cookies可能失效了,请仔细检查配置中的DEDEUSERID SESSDATA BILI_JCT三项的值是否正确、过期");
                throw new Exception("用户信息校验失败！");
            }
            data = JSONUtil.toBean(userJson.getJSONObject("data"), BiliData.class);
            log.info("硬币余额: " + data.getMoney());
            log.info("用户名称: " + DataDesensitizationUtil.normal(data.getUname()));
        }
        return data;
    }

    /**
     * 第二步，统计硬币情况
     */
    public void coinLogs(TaskLog log) throws Exception {
        JSONObject jsonObject = biliWebUtil.doGet(BiliUrlConstant.BILI_GET_COIN_LOG);
        if (jsonObject.getInt("code") == 0) {
            JSONObject data = jsonObject.getJSONObject("data");
            log.info("最近一周共计{}条硬币记录", data.getInt("count"));
            JSONArray coinList = data.getJSONArray("list");

            double income = 0.0;
            double expend = 0.0;
            for (int i = 0; i < coinList.size(); i++) {
                double delta = coinList.getJSONObject(i).getDouble("delta");
                //  String reason = jsonElement.getAsJsonObject().get("reason").getAsString();
                if (delta > 0) {
                    income += delta;
                } else {
                    expend += delta;
                }
            }
            log.info("最近一周收入{}个硬币", income);
            log.info("最近一周支出{}个硬币", expend);
        }
    }

    /**
     * 充电功能
     */
    public void chargeMe(TaskLog log) throws Exception {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int day = cal.get(Calendar.DATE);
        //被充电用户的userID
        String userId = taskConfig.getBiliChargeConfig().getChargeObject();

        //0为给自己充电
        if (userId.equals("0")) {
            userId = String.valueOf(data.getMid());
        }

        Map<String, String> map = BiliHelpUtil.queryUserNameByUid(userId, biliWebUtil);
        if (map.containsKey("msg")) {
            log.info(map.get("msg"));
        }
        String userName = map.get("data");

        //B币券余额
        double couponBalance;
        //大会员类型
        Map<String, Object> queryVipStatusTypeMap = BiliHelpUtil.queryVipStatusType(data);
        if (queryVipStatusTypeMap.containsKey("msg")) {
            log.info(map.get("msg"));
        }
        int vipType = (int) queryVipStatusTypeMap.get("data");

        if (vipType == 0 || vipType == 1) {
            log.info("普通会员和月度大会员每月不赠送B币券，所以没法给自己充电哦");
            return;
        }
        if (!Boolean.TRUE.equals(taskConfig.getBiliChargeConfig().getEnableAutoCharge())) {
            log.info("未开启月底给自己充电功能");
            return;
        }
        if ("0".equals(userId) || "".equals(userId)) {
            log.error("充电对象uid配置错误，请参考最新的文档");
            return;
        }
        if (day < taskConfig.getBiliChargeConfig().getChargeDay()) {
            log.info("今天是 {}号，还没到充电日子呢", day);
            return;
        }

        log.info("月底自动充电对象是: {}", DataDesensitizationUtil.normal(userName));

        couponBalance = data.getWallet().getCoupon_balance();

        //判断条件 是月底 &&是年大会员 && b币券余额大于2 && 配置项允许自动充电
        if (day == taskConfig.getBiliChargeConfig().getChargeDay() && couponBalance >= 2) {
            Map<String, Object> postMap = new HashMap<>();
            postMap.put("bp_num", String.valueOf(couponBalance));
            postMap.put("is_bp_remains_prior", "true");
            postMap.put("up_mid", userId);
            postMap.put("otype", "up");
            postMap.put("oid", userId);
            postMap.put("csrf", taskInfo.getBiliJct());

            JSONObject jsonObject = biliWebUtil.doPost(BiliUrlConstant.BILI_AUTO_CHARGE, postMap);
            int resultCode = jsonObject.getInt("code");
            if (resultCode == 0) {
                JSONObject dataJson = jsonObject.getJSONObject("data");
                int statusCode = dataJson.getInt("status");
                if (statusCode == 4) {
                    log.info("月底了，给自己充电成功啦，送的B币券没有浪费哦");
                    log.info("本次充值使用了: {} 个B币券", couponBalance);
                    //获取充电留言token
                    String orderNo = dataJson.getStr("order_no");
                    log.info(BiliHelpUtil.chargeComments(orderNo, biliWebUtil, taskInfo.getBiliJct()));
                    return;
                }
            }
            log.error("充电失败了啊 原因: " + jsonObject);
            throw new Exception("充电失败！");
        }
    }

    /**
     * 投币功能
     */
    public void coinAdd(TaskLog log) throws Exception {
        //投币最多操作数 解决csrf校验失败时死循环的问题
        int addCoinOperateCount = 0;
        //安全检查，最多投币数
        final int maxNumberOfCoins = 5;
        //获取自定义配置投币数 配置写在src/main/resources/taskConfig.json中
        int setCoin = taskConfig.getBiliCoinConfig().getDailyCoin();
        // 预留硬币数
        int reserveCoins = taskConfig.getBiliCoinConfig().getReserveCoins();

        //已投的硬币
        int useCoin = BiliHelpUtil.expConfirm(biliWebUtil);
        //投币策略
        int coinAddPriority = taskConfig.getBiliCoinConfig().getCoinRules();

        if (setCoin > maxNumberOfCoins) {
            log.info("自定义投币数为: {}枚, 为保护你的资产，自定义投币数重置为: {}枚", setCoin, maxNumberOfCoins);
            setCoin = maxNumberOfCoins;
        }
        log.info("自定义投币数为: {}枚, 程序执行前已投: {}枚", setCoin, useCoin);

        //调整投币数 设置投币数-已经投过的硬币数
        int needCoins = setCoin - useCoin;

        //投币前硬币余额
        double beforeAddCoinBalance = getCoinBalance(log);
        int coinBalance = (int) Math.floor(beforeAddCoinBalance);

        if (needCoins <= 0) {
            log.info("已完成设定的投币任务，今日无需再投币了");
            return;
        } else {
            log.info("投币数调整为: {}枚", needCoins);
            //投币数大于余额时，按余额投
            if (needCoins > coinBalance) {
                log.info("完成今日设定投币任务还需要投: {}枚硬币，但是余额只有: {}枚", needCoins, beforeAddCoinBalance);
                log.info("投币数调整为: " + coinBalance);
                needCoins = coinBalance;
            }
        }

        if (coinBalance < reserveCoins) {
            log.info("剩余硬币数为{},低于预留硬币数{},今日不再投币", beforeAddCoinBalance, reserveCoins);
            log.info("tips: 当硬币余额少于你配置的预留硬币数时，则会暂停当日投币任务");
            return;
        }
        log.info("投币前余额为 : " + beforeAddCoinBalance);
        /*
         * 请勿修改 max_numberOfCoins 这里多判断一次保证投币数超过5时 不执行投币操作.
         * 最后一道安全判断，保证即使前面的判断逻辑错了，也不至于发生投币事故.
         */
        while (needCoins > 0 && needCoins <= maxNumberOfCoins) {
            String bvid;
            GetVideoId taskGetVID = new GetVideoId(biliWebUtil, taskInfo);
            if (coinAddPriority == 1 && addCoinOperateCount < 7) {
                bvid = taskGetVID.getFollowUpRandomVideoBvid();
            } else {
                bvid = taskGetVID.getRegionRankingVideoBvid();
            }

            addCoinOperateCount++;
            //观看视频
            try {
                String msg = new VideoWatch(biliWebUtil, taskInfo).watchVideo(bvid);
                log.info(msg);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
//            new SleepTime().sleepDefault();
            Map<String, Object> coinAddMap = BiliHelpUtil.coinAdd(bvid, 1, taskConfig.getBiliCoinConfig().getEnableClickLike(), biliWebUtil, taskInfo.getBiliJct());
            log.info((String) coinAddMap.get("msg"));
            if ((boolean) coinAddMap.get("data")) {
                needCoins--;
            }
            if (addCoinOperateCount > 15) {
                log.error("尝试投币/投币失败次数太多");
                log.info("投币任务完成后余额为: {}", getCoinBalance(log));
                throw new Exception("尝试投币/投币失败次数太多");
            }
        }
        log.info("投币任务完成后余额为: {}", getCoinBalance(log));
    }

    /**
     * 观看视频功能
     */
    public void watchVideo(TaskLog log) throws Exception {
        String flag = "";
        VideoWatch videoWatch = new VideoWatch(biliWebUtil, taskInfo);
        GetVideoId taskGetVID = new GetVideoId(biliWebUtil, taskInfo);
        JSONObject dailyTaskStatus = BiliHelpUtil.getDailyTaskStatus(biliWebUtil);
        String bvid = taskGetVID.getRegionRankingVideoBvid();
        if (!dailyTaskStatus.getBool("watch")) {
            try {
                String msg = videoWatch.watchVideo(bvid);
                log.info(msg);
            } catch (Exception e) {
                log.error(e.getMessage());
                flag += e.getMessage();
            }
        } else {
            log.info("本日观看视频任务已经完成了，不需要再观看视频了");
        }

        if (!dailyTaskStatus.getBool("share")) {
            try {
                String msg = videoWatch.dailyAvShare(bvid);
                log.info(msg);
            } catch (Exception e) {
                log.error(e.getMessage());
                flag += e.getMessage();
            }
        } else {
            log.info("本日分享视频任务已经完成了，不需要再分享视频了");
        }
        if (StrUtil.isNotBlank(flag)) {
            throw new Exception(flag);
        }
    }

    /**
     * bili漫画签到
     */
    public void cartoonSign(TaskLog log) throws Exception {
        String platform = taskConfig.getCartoonSignOS();
        Map<String, Object> params = new HashMap<>();
        params.put("platform", platform);
        JSONObject result = biliWebUtil.doPost(BiliUrlConstant.BILI_MANGA, params);

        if (result == null) {
            log.info("哔哩哔哩漫画已经签到过了");
        } else {
            log.info("完成漫画签到");
        }
    }

    /**
     * 银瓜子换硬币
     */
    public void silver2Coin(TaskLog log) throws Exception {
        JSONObject queryStatus = biliWebUtil.doGet(BiliUrlConstant.BILI_GET_SILVER_2_COIN_STATUS);
        if (queryStatus == null || Objects.isNull(queryStatus.get("data"))) {
            log.error("获取银瓜子状态失败");
            throw new Exception("获取银瓜子状态失败");
        }
        queryStatus = queryStatus.getJSONObject("data");
        //银瓜子兑换硬币汇率
        final int exchangeRate = 700;
        int silverNum = queryStatus.getInt("silver");

        if (silverNum < exchangeRate) {
            log.info("当前银瓜子余额为:{},不足700,不进行兑换", silverNum);
        } else {
            Map<String, Object> params = new HashMap<>();
            params.put("csrf_token", taskInfo.getBiliJct());
            params.put("csrf", taskInfo.getBiliJct());
            JSONObject resultJson = biliWebUtil.doPost(BiliUrlConstant.BILI_SILVER_2_COIN, params);

            int responseCode = resultJson.getInt("code");
            if (responseCode == 0) {
                log.info("银瓜子兑换硬币成功");
                double coinMoneyAfterSilver2Coin = getCoinBalance(log);
                log.info("当前银瓜子余额: {}", (silverNum - exchangeRate));
                log.info("兑换银瓜子后硬币余额: {}", coinMoneyAfterSilver2Coin);

                //兑换银瓜子后，更新biliData中的硬币值
                if (data != null) {
                    data.setMoney(coinMoneyAfterSilver2Coin);
                }
            } else {
                String message = StrUtil.format("银瓜子兑换硬币失败 原因是:{}", resultJson.getStr("message"));
                log.error(message);
                throw new Exception(message);
            }
        }
    }

    /**
     * 直播签到
     */
    public void liveSign(TaskLog log) throws Exception {
        JSONObject liveCheckInResponse = biliWebUtil.doGet(BiliUrlConstant.BILI_LIVE_CHECKING);
        int code = liveCheckInResponse.getInt("code");
        if (code == 0) {
            JSONObject data = liveCheckInResponse.getJSONObject("data");
            log.info("直播签到成功，本次签到获得{},{}", data.getStr("text"), data.getStr("specialText"));
        } else {
            String message = liveCheckInResponse.getStr("message");
            if (message.contains("已签到")) {
                log.info("今日已经进行过直播签到");
                return;
            }
            message = StrUtil.format("直播签到失败: {}", message);
            log.error(message);
            throw new Exception(message);
        }
    }

    /**
     * 直播送礼
     */
    public void liveGift(TaskLog log) throws Exception {
        /* 从配置类中读取是否需要执行赠送礼物 */
        if (!Boolean.TRUE.equals(taskConfig.getBiliGiveGiftConfig().getEnableGiveGift())) {
            log.info("未开启自动送出即将过期礼物功能");
            return;
        }
        GiveGift taskGiveGift = new GiveGift(biliWebUtil, taskInfo);
        /* 直播间 id */
        String roomId = "";
        /* 直播间 uid 即 up 的 id*/
        String uid = "";
        /* B站后台时间戳为10位 */
        long nowTime = System.currentTimeMillis() / 1000;
        /* 获得礼物列表 */
        JSONArray jsonArray = taskGiveGift.xliveGiftBagList();
        /* 判断是否有过期礼物出现 */
        boolean flag = true;

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            long expireAt = Long.parseLong(json.getStr("expire_at"));
            /* 礼物还剩 1 天送出 */
            /* 永久礼物到期时间为 0 */
            if ((expireAt - nowTime) < 60 * 60 * 25 && expireAt != 0) {
                /* 如果有未送出的礼物，则获取一个直播间 */
                if ("".equals(roomId)) {
                    Map<String, String> resultMap = taskGiveGift.getuidAndRid(taskConfig.getBiliGiveGiftConfig().getGiveGiftRoomID());
                    log.info(resultMap.get("msg"));
                    uid = resultMap.get("uid");
                    roomId = resultMap.get("roomid");
                }

                Map<String, Object> params = new HashMap<>();
                params.put("biz_id", roomId);
                params.put("ruid", uid);
                params.put("bag_id", json.getStr("bag_id"));
                params.put("gift_id", json.getStr("gift_id"));
                params.put("gift_num", json.getStr("gift_num"));

                JSONObject jsonObject3 = taskGiveGift.xliveBagSend(params);
                if ("0".equals(jsonObject3.getStr("code"))) {
                    /* 礼物的名字 */
                    String giftName = jsonObject3.getJSONObject("data").getStr("gift_name");
                    /* 礼物的数量 */
                    String giftNum = jsonObject3.getJSONObject("data").getStr("gift_num");
                    log.info("给直播间 - {} - {} - 数量: {}✔", roomId, giftName, giftNum);
                    flag = false;
                } else {
                    String message = StrUtil.format("送礼失败, 原因 : {}❌", jsonObject3);
                    log.error(message);
                    throw new Exception(message);
                }
            }
        }
        if (flag) {
            log.info("当前无即将过期礼物❌");
        }
//        log.error("💔赠送礼物异常 : ", e);
    }

    /**
     * 大会员漫画权益领取
     */
    public void vipCartoonRec(TaskLog log) throws Exception {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int day = cal.get(Calendar.DATE);

        /*
           根据userInfo.getVipStatus() ,如果是1 ，会员有效，0会员失效。
           @JunzhouLiu: fixed query_vipStatusType()现在可以查询会员状态，以及会员类型了 2020-10-15
         */
        Map<String, Object> map = BiliHelpUtil.queryVipStatusType(data);
        if (map.containsKey("msg")) {
            log.info((String) map.get("msg"));
        }
        int vipType = (int) map.get("data");

        if (vipType == 0) {
            log.info("非大会员，跳过领取大会员权益");
            return;
        }

        if (vipType == 1 || vipType == 2 && (day == 1 || day % 7 == 0)) {
            log.info("开始领取大会员漫画权益");
            String requestBody = "{\"reason_id\":" + BiliUrlConstant.reasonId + "}";
            //注意参数构造格式为json，不知道需不需要重载下面的Post函数改请求头
            JSONObject jsonObject = biliWebUtil.doPost(BiliUrlConstant.BILI_MANGA_GET_VIP_REWARD, requestBody);
            if (jsonObject.getInt("code") == 0) {
                int num = jsonObject.getJSONObject("data").getInt("amount");
                log.info("大会员成功领取{}张漫读劵", num);
            } else {
                if ("不能重复领取".equals(jsonObject.getStr("msg"))){
                    log.info("大会员领取漫读劵失败，不能重复领取");
                    return;
                }
                String msg = StrUtil.format("大会员领取漫读劵失败，原因为:{}", jsonObject.getStr("msg"));
                log.error(msg);
                throw new Exception(msg);
            }
        } else {
            log.info("本日非领取大会员漫画执行日期");
        }

        if (day == 1 || day % 7 == 0) {
            if (vipType == 2) {
                String flag = "";
                log.info("开始领取年度大会员权益");
                try {
                    String vipPrivilege = BiliHelpUtil.getVipPrivilege(1, taskInfo.getBiliJct(), biliWebUtil);
                    log.info(vipPrivilege);
                } catch (Exception e) {
                    log.info(e.getMessage());
                    flag += e.getMessage();
                }
                try {
                    String vipPrivilege1 = BiliHelpUtil.getVipPrivilege(2, taskInfo.getBiliJct(), biliWebUtil);
                    log.info(vipPrivilege1);
                } catch (Exception e) {
                    log.info(e.getMessage());
                    flag += e.getMessage();
                }
                if (StrUtil.isNotBlank(flag)) {
                    throw new Exception(flag);
                }

            }

        } else {
            log.info("本日非领取年度大会员权益执行日期");
        }
    }

    /**
     * 每日漫画阅读，祖传堀与宫村（我也不知道这是啥漫画）
     */
    public void readCartoon(TaskLog log) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("device", "pc");
        map.put("platform", "web");
        map.put("comic_id", "27355");
        map.put("ep_id", "381662");

        JSONObject result = biliWebUtil.doPost(BiliUrlConstant.BILI_MANGA_READ, JSONUtil.toJsonStr(map));
        String code = result.getStr("code");

        if (code.equals("0")) {
            log.info("本日漫画自动阅读1章节成功！，阅读漫画为：堀与宫村");
        } else {
            String msg = StrUtil.format("阅读失败,错误信息为\n```json\n{}\n```", result);
            log.error(msg);
            throw new Exception(msg);
        }
    }

    /**
     * 赛事预测
     */
    public void matchGame(TaskLog log) throws Exception {
        if (!taskConfig.getBiliPreConfig().getEnablePre()) {
            log.info("赛事预测未开启");
            return;
        }

        double currentCoin = getCoinBalance(log);

        if (currentCoin < taskConfig.getBiliPreConfig().getKeepCoin()) {
            log.info("{}个硬币都没有，参加\uD83D\uDC34预测呢？任务结束", taskConfig.getBiliPreConfig().getKeepCoin());
            return;
        }

        MatchGame matchGame = new MatchGame(biliWebUtil);
        JSONObject resultJson = matchGame.queryContestQuestion(matchGame.getTime(), 1, 50);
        JSONObject jsonObject = resultJson.getJSONObject("data");
        if (resultJson.getInt("code") == 0) {
            JSONArray list = jsonObject.getJSONArray("list");
            JSONObject pageinfo = jsonObject.getJSONObject("page");
            if (pageinfo.getInt("total") == 0) {
                log.info("今日无赛事或者本日赛事已经截止预测");
                return;
            }
            if (list != null) {
                int coinNumber = taskConfig.getBiliPreConfig().getPreCoin();
                int contestId;
                String contestName;
                int questionId;
                String questionTitle;
                int teamId;
                String teamName;
                //   int seasonId;
                String seasonName;

                for (int i = 0; i < list.size(); i++) {
                    JSONObject listInfo = list.getJSONObject(i);
                    log.info("-----预测开始-----");
                    if (currentCoin < taskConfig.getBiliPreConfig().getKeepCoin()) {
                        log.info("仅剩{}个硬币，低于最低保留硬币数量，后续预测不再执行", currentCoin);
                        break;
                    }
                    JSONObject contestJson = listInfo.getJSONObject("contest");
                    JSONObject questionJson = listInfo.getJSONArray("questions").getJSONObject(0);
                    contestId = contestJson.getInt("id");
                    contestName = contestJson.getStr("game_stage");
                    questionId = questionJson.getInt("id");
                    questionTitle = questionJson.getStr("title");
                    //seasonId = contestJson.get("season").getAsJsonObject().get("id").getAsInt();
                    seasonName = contestJson.getJSONObject("season").getStr("title");
                    log.info("{} {}:{}", seasonName, contestName, questionTitle);

                    if (questionJson.getInt("is_guess") == 1) {
                        log.info("此问题已经参与过预测了，无需再次预测");
                        continue;
                    }
                    JSONObject teamA = questionJson.getJSONArray("details").getJSONObject(0);
                    JSONObject teamB = questionJson.getJSONArray("details").getJSONObject(1);
                    log.info("当前赔率为:  {}:{}", teamA.getDouble("odds"), teamB.getDouble("odds"));

                    if (taskConfig.getBiliPreConfig().getEnableReversePre()) {
                        if (teamA.getDouble("odds") <= teamB.getDouble("odds")) {
                            teamId = teamB.getInt("detail_id");
                            teamName = teamB.getStr("option");
                        } else {
                            teamId = teamA.getInt("detail_id");
                            teamName = teamA.getStr("option");
                        }
                    } else {
                        if (teamA.getDouble("odds") >= teamB.getDouble("odds")) {
                            teamId = teamB.getInt("detail_id");
                            teamName = teamB.getStr("option");
                        } else {
                            teamId = teamA.getInt("detail_id");
                            teamName = teamA.getStr("option");
                        }
                    }
                    log.info("拟预测的队伍是:{},预测硬币数为:{}", teamName, coinNumber);
                    currentCoin -= coinNumber;
                    String s = matchGame.doPrediction(contestId, questionId, teamId, coinNumber, taskInfo.getBiliJct());
                    log.info(s);
//                    new SleepTime().sleepDefault();
                }
            }
        } else {
            log.error("获取赛事信息失败");
            throw new Exception("获取赛事信息失败");
        }
    }

    /**
     * 最后步骤：统计数值
     */
    public BiliData calculateUpgradeDays(TaskLog log) throws Exception {
//        if (data == null) {
//            appendLog("未请求到用户信息，暂无法计算等级相关数据");
//            throw new Exception("未请求到用户信息，暂无法计算等级相关数据");
//        }

        int todayExp = 15;
        todayExp += BiliHelpUtil.expConfirm(biliWebUtil) * 10;
        log.info("今日获得的总经验值为: {}", todayExp);

        int needExp = data.getLevel_info().getNext_exp_asInt() - data.getLevel_info().getCurrent_exp();

        if (data.getLevel_info().getCurrent_level() < 6) {
            log.info("按照当前进度，升级到Lv{}还需要: {}天", data.getLevel_info().getCurrent_level() + 1, needExp / todayExp);
        } else {
            log.info("当前等级Lv6，经验值为：{}", data.getLevel_info().getCurrent_exp());
        }
        return data;
    }
    
    private double getCoinBalance(TaskLog log) {
        try {
            Map<String, Object> coinBalanceMap = BiliHelpUtil.getCoinBalance(biliWebUtil);
            if (coinBalanceMap.containsKey("msg")) {
                log.info((String) coinBalanceMap.get("msg"));
            }
            return (double) coinBalanceMap.get("data");
        } catch (Exception e) {
            log.info("b站任务请求硬币余额接口错误：" + e.getMessage());
            return 0.0;
        }
    }

}
