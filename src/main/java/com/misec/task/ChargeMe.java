package com.misec.task;

import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.apiquery.OftenApi;
import com.misec.config.Config;
import com.misec.login.Verify;
import com.misec.utils.HelpUtil;
import com.misec.utils.HttpUtil;
import com.oldwu.log.OldwuLog;
import lombok.extern.log4j.Log4j2;

import java.util.Calendar;
import java.util.TimeZone;

import static com.misec.task.TaskInfoHolder.*;

/**
 * 给自己充电
 * <p/>
 * 月底自动给自己充电。仅充会到期的B币券，低于2的时候不会充
 *
 * @author @JunzhouLiu @Kurenai
 * @since 2020-11-22 5:43
 */
@Log4j2
public class ChargeMe implements Task {

    @Override
    public void run() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int day = cal.get(Calendar.DATE);
        //被充电用户的userID
        String userId = Verify.getInstance().getUserId();
        String configChargeUserId = Config.getInstance().getChargeForLove();

        //B币券余额
        double couponBalance;
        //大会员类型
        int vipType = queryVipStatusType();

        if (vipType == 0 || vipType == 1) {
            OldwuLog.log("普通会员和月度大会员每月不赠送B币券，所以没法给自己充电哦");
            log.info("普通会员和月度大会员每月不赠送B币券，所以没法给自己充电哦");
            return;
        }

        if (!Boolean.TRUE.equals(Config.getInstance().getMonthEndAutoCharge())) {
            OldwuLog.log("未开启月底给自己充电功能");
            log.info("未开启月底给自己充电功能");
            return;
        }

        if (!"0".equals(configChargeUserId)) {
            String userName = OftenApi.queryUserNameByUid(configChargeUserId);
            if ("1".equals(userName)) {
                userId = Verify.getInstance().getUserId();
                OldwuLog.log("充电对象已置为你本人");
                log.info("充电对象已置为你本人");
            } else {
                userId = Config.getInstance().getChargeForLove();
                OldwuLog.log("你配置的充电对象非本人而是:  " + HelpUtil.userNameEncode(userName));
                log.info("你配置的充电对象非本人而是: {}", HelpUtil.userNameEncode(userName));
            }
        } else {
            OldwuLog.log("你配置的充电对象是你本人没错了！");
            log.info("你配置的充电对象是你本人没错了！");
        }

        if (userInfo != null) {
            couponBalance = userInfo.getWallet().getCoupon_balance();
        } else {
            JsonObject queryJson = HttpUtil.doGet(ApiList.chargeQuery + "?mid=" + userId);
            couponBalance = queryJson.getAsJsonObject("data").getAsJsonObject("bp_wallet").get("coupon_balance").getAsDouble();
        }

        /*
          判断条件 是月底&&是年大会员&&b币券余额大于2&&配置项允许自动充电
         */
        if (day >= 28 && couponBalance >= 2 &&
                Boolean.TRUE.equals(Config.getInstance().getMonthEndAutoCharge())) {
            String requestBody = "bp_num=" + couponBalance
                    + "&is_bp_remains_prior=true"
                    + "&up_mid=" + userId
                    + "&otype=up"
                    + "&oid=" + userId
                    + "&csrf=" + Verify.getInstance().getBiliJct();

            JsonObject jsonObject = HttpUtil.doPost(ApiList.autoCharge, requestBody);

            int resultCode = jsonObject.get(STATUS_CODE_STR).getAsInt();
            if (resultCode == 0) {
                JsonObject dataJson = jsonObject.get("data").getAsJsonObject();
                int statusCode = dataJson.get("status").getAsInt();
                if (statusCode == 4) {
                    OldwuLog.log("月底了，给自己充电成功啦，送的B币券没有浪费哦");
                    OldwuLog.log("本次充值使用了: " + couponBalance + "个B币券");
                    log.info("月底了，给自己充电成功啦，送的B币券没有浪费哦");
                    log.info("本次充值使用了: " + couponBalance + "个B币券");
                    //获取充电留言token
                    String orderNo = dataJson.get("order_no").getAsString();
                    chargeComments(orderNo);
                } else {
                    OldwuLog.error("充电失败了啊 原因: " + jsonObject);
                    log.debug("充电失败了啊 原因: " + jsonObject);
                }

            } else {
                OldwuLog.error("充电失败了啊 原因: " + jsonObject);
                log.debug("充电失败了啊 原因: " + jsonObject);
            }
        } else {
            if (day < 28) {
                OldwuLog.log("今天是本月的第: " + day + "天，还没到充电日子呢");
                log.info("今天是本月的第: " + day + "天，还没到充电日子呢");
            } else {
                OldwuLog.log("本月已经充过电了，睿总送咱的B币券已经没有啦，下月再充啦");
                log.info("本月已经充过电了，睿总送咱的B币券已经没有啦，下月再充啦");
            }

        }
    }

    private void chargeComments(String token) {

        String requestBody = "order_id=" + token
                + "&message=" + "BILIBILI-HELPER自动充电"
                + "&csrf=" + Verify.getInstance().getBiliJct();
        JsonObject jsonObject = HttpUtil.doPost(ApiList.chargeComment, requestBody);

        if (jsonObject.get(STATUS_CODE_STR).getAsInt() == 0) {
            OldwuLog.log("充电留言成功");
            log.info("充电留言成功");
        } else {
            OldwuLog.error(jsonObject.get("message").getAsString());
            log.debug(jsonObject.get("message").getAsString());
        }

    }

    @Override
    public String getName() {
        return "大会员月底B币券充电和月初大会员权益领取";
    }
}
