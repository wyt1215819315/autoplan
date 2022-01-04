package com.misec.task;

import com.google.gson.JsonObject;
import com.misec.apiquery.ApiList;
import com.misec.apiquery.OftenApi;
import com.misec.utils.HttpUtils;
import com.oldwu.log.OldwuLog;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.util.Calendar;
import java.util.TimeZone;

import static com.misec.task.TaskInfoHolder.STATUS_CODE_STR;
import static com.misec.task.TaskInfoHolder.queryVipStatusType;

/**
 * 漫画权益领取
 *
 * @author @JunzhouLiu @Kurenai @happy888888
 * @since 2020-11-22 5:48
 */
@Log4j2
@Data
public class GetVipPrivilege implements Task {

    /**
     * 权益号，由https://api.bilibili.com/x/vip/privilege/my.
     * 得到权益号数组，取值范围为数组中的整数.
     * 为方便直接取1，为领取漫读劵，暂时不取其他的值.
     */
    private int reasonId = 1;

    @Override
    public void run() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int day = cal.get(Calendar.DATE);


        /*
           根据userInfo.getVipStatus() ,如果是1 ，会员有效，0会员失效。
           @JunzhouLiu: fixed query_vipStatusType()现在可以查询会员状态，以及会员类型了 2020-10-15
         */
        int vipType = queryVipStatusType();

        if (vipType == 0) {
            OldwuLog.log("非大会员，跳过领取大会员权益");
            log.info("非大会员，跳过领取大会员权益");
            return;
        }

        if (vipType == 1 && day == 1 || vipType == 2 && day == 1) {
            OldwuLog.log("开始领取大会员漫画权益");
            log.info("开始领取大会员漫画权益");
            String requestBody = "{\"reason_id\":" + reasonId + "}";
            //注意参数构造格式为json，不知道需不需要重载下面的Post函数改请求头
            JsonObject jsonObject = HttpUtils.doPost(ApiList.MANGA_GET_VIP_REWARD, requestBody);
            if (jsonObject.get(STATUS_CODE_STR).getAsInt() == 0) {
                /*
                  @happy888888:好像也可以getAsString或,getAsShort
                  @JunzhouLiu:Int比较好判断
                 */
                int num = jsonObject.get("data").getAsJsonObject().get("amount").getAsInt();
                log.info("大会员成功领取{}张漫读劵", num);
                OldwuLog.log("大会员成功领取" + num + "张漫读劵");
            } else {
                log.info("大会员领取漫读劵失败，原因为:{}", jsonObject.get("msg").getAsString());
                OldwuLog.log("大会员领取漫读劵失败，原因为:" + jsonObject.get("msg").getAsString());
            }
        } else {
            OldwuLog.log("本日非领取大会员漫画执行日期");
            log.info("本日非领取大会员漫画执行日期");
        }

        if (day == 1 || day % 7 == 0) {
            if (vipType == 2) {
                log.info("开始领取年度大会员权益");
                OftenApi.getVipPrivilege(1);
                OftenApi.getVipPrivilege(2);
            }

        } else {
            OldwuLog.log("本日非领取年度大会员权益执行日期");
            log.info("本日非领取年度大会员权益执行日期");
        }
    }

    @Override
    public String getName() {
        return "漫画权益领取";
    }
}
