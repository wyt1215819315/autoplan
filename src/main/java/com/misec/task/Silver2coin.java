package com.misec.task;

import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.apiquery.OftenApi;
import com.misec.login.Verify;
import com.misec.utils.HttpUtil;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

import static com.misec.task.TaskInfoHolder.STATUS_CODE_STR;
import static com.misec.task.TaskInfoHolder.userInfo;

/**
 * 银瓜子换硬币
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:25
 */
@Log4j2
public class Silver2coin implements Task {

    @Override
    public void run() {
        JsonObject queryStatus = HttpUtil.doGet(ApiList.GET_SILVER_2_COIN_STATUS);
        if (queryStatus == null || Objects.isNull(queryStatus.get("data"))) {
            log.error("获取银瓜子状态失败");
            OldwuLog.error("获取银瓜子状态失败");
            return;
        }
        queryStatus = queryStatus.get("data").getAsJsonObject();
        //银瓜子兑换硬币汇率
        final int exchangeRate = 700;
        int silverNum = queryStatus.get("silver").getAsInt();

        if (silverNum < exchangeRate) {
            OldwuLog.log("当前银瓜子余额为: " + silverNum + ",不足700,不进行兑换");
            log.info("当前银瓜子余额为:{},不足700,不进行兑换", silverNum);
        } else {
            String requestBody = "csrf_token=" + Verify.getInstance().getBiliJct()
                    + "&csrf=" + Verify.getInstance().getBiliJct();
            JsonObject resultJson = HttpUtil.doPost(ApiList.SILVER_2_COIN, requestBody);

            int responseCode = resultJson.get(STATUS_CODE_STR).getAsInt();
            if (responseCode == 0) {
                OldwuLog.log("银瓜子兑换硬币成功");
                log.info("银瓜子兑换硬币成功");

                double coinMoneyAfterSilver2Coin = OftenApi.getCoinBalance();
                OldwuLog.log("当前银瓜子余额: " + (silverNum - exchangeRate));
                OldwuLog.log("兑换银瓜子后硬币余额: " + coinMoneyAfterSilver2Coin);
                log.info("当前银瓜子余额: {}", (silverNum - exchangeRate));
                log.info("兑换银瓜子后硬币余额: {}", coinMoneyAfterSilver2Coin);

                //兑换银瓜子后，更新userInfo中的硬币值
                if (userInfo != null) {
                    userInfo.setMoney(coinMoneyAfterSilver2Coin);
                }
            } else {
                OldwuLog.warning("银瓜子兑换硬币失败 原因是: " + resultJson.get("message").getAsString());
                log.info("银瓜子兑换硬币失败 原因是:{}", resultJson.get("message").getAsString());
            }
        }
    }

    @Override
    public String getName() {
        return "银瓜子换硬币";
    }
}