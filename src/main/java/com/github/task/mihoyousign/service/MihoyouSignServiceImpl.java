//package com.github.task.mihoyousign.service;
//
//import cn.hutool.core.map.MapUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.crypto.SecureUtil;
//import cn.hutool.json.JSONArray;
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import com.github.system.base.util.HttpUtil;
//import com.github.system.task.annotation.TaskAction;
//import com.github.system.task.constant.AutoTaskStatus;
//import com.github.system.task.dto.*;
//import com.github.system.task.service.BaseTaskService;
//import com.github.task.mihoyousign.constant.MihoyouSignConstant;
//import com.github.task.mihoyousign.model.MihoyouSignSettings;
//import com.github.task.mihoyousign.model.MihoyouSignUserInfo;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//
//import static com.github.task.cloudgenshin.constant.CloudGenshinSignConstant.*;
//
//public class MihoyouSignServiceImpl extends BaseTaskService<MihoyouSignSettings, MihoyouSignUserInfo> {
//
//
//    @Override
//    public TaskInfo getTaskInfo() {
//        return new TaskInfo("米游社任务", "MihoyouSign", Duration.ofMinutes(1));
//    }
//
//    @Override
//    public TaskResult init(TaskLog log) throws Exception {
//
//    }
//
//    @Override
//    public ValidateResult validate() throws Exception {
//        String cookie = taskSettings.getCookie();
//        String lCookie = taskSettings.getLCookie();
//        StringBuilder sb = new StringBuilder();
//        //检查cookie字段
//        String account_id = HttpUtil.getCookieByName(cookie, "account_id");
//        String cookie_token = HttpUtil.getCookieByName(cookie, "cookie_token");
//        if (StrUtil.isBlank(account_id)) {
//            //备用方案
//            account_id = HttpUtil.getCookieByName(cookie, "ltuid");
//        }
//        if (StrUtil.isNotBlank(lCookie)) {
//            //校验两个cookie字段
//            String login_uid = HttpUtil.getCookieByName(lCookie, "login_uid");
//            if (StrUtil.isNotBlank(login_uid) && StrUtil.isNotBlank(account_id)) {
//                if (!login_uid.equals(account_id)) {
//                    return ValidateResult.doError("两项cookie中的账号信息不一致！");
//                }
//            }
//            if (StrUtil.isBlank(account_id)) {
//                //备用方案2
//                account_id = HttpUtil.getCookieByName(lCookie, "login_uid");
//            }
//            String login_ticket = HttpUtil.getCookieByName(lCookie, "login_ticket");
//            if (StrUtil.isBlank(account_id) || StrUtil.isBlank(login_ticket)) {
//                sb.append("cookie中没有login_ticket字段或login_ticket已过期，无法使用米游币任务，如果需要使用，请前往米哈游通行证处获取");
//            } else {
//                //获取stoken
//                JSONObject result = getCookieToken(login_ticket, account_id);
//                if (!"OK".equals(result.get("message"))) {
//                    sb.append("login_ticket已失效,请重新登录获取，现在你可能无法使用米游币任务！");
//                } else {
//                    taskSettings.setStoken(result.getJSONObject("data").getJSONArray("list").getJSONObject(0).getStr("token"));
//                }
//            }
//        } else {
//            sb.append("没有填写米哈游通行证cookie，如果您无需使用米游币任务，请忽略此消息");
//        }
//        taskSettings.setSuid(account_id);
//        if (StrUtil.isBlank(account_id) || StrUtil.isBlank(cookie_token)) {
//            return ValidateResult.doError("无效的cookie：米游社cookie中必须包含cookie_token和account_id字段！");
//        }
//        return ValidateResult.doSuccess(sb.toString());
//    }
//
//    public JSONObject getCookieToken(String loginTicket, String accountId) {
//        String token_url = String.format(MihoyouSignConstant.MYS_TOKEN_URL, loginTicket, accountId);
//        try {
//            return HttpUtil.requestJson(token_url, null, HttpUtil.RequestType.GET);
//        } catch (Exception e) {
//            log.error("米游社任务获取login_ticket请求服务端失败", e);
//            return new JSONObject();
//        }
//    }
//
//    @Override
//    public LoginResult<MihoyouSignUserInfo> checkUser() throws Exception {
//
//    }
//
//    @Override
//    public MihoyouSignUserInfo getUserInfo() throws Exception {
//        MihoyouSignUserInfo mihoyouSignUserInfo = new MihoyouSignUserInfo();
//        JSONObject data = this.userInfo.getJSONObject("data");
//        cloudGenshinUserInfo.setFreeTime(data.getJSONObject("free_time").getStr("free_time") + "/" + data.getJSONObject("free_time").getStr("free_time_limit"));
//        cloudGenshinUserInfo.setCoinNum(data.getJSONObject("coin").getStr("coin_num"));
//        cloudGenshinUserInfo.setPlayCard(data.getJSONObject("play_card").getStr("short_msg"));
//        cloudGenshinUserInfo.setOnlyId(SecureUtil.md5(taskSettings.toString()));
//        return cloudGenshinUserInfo;
//    }
//
//
//    @TaskAction(name = "校验登录状态", order = 0)
//    public TaskResult checkLoginStatus(TaskLog log) throws Exception {
//        if (checkLoginSuccess()) {
//            return TaskResult.doSuccess();
//        } else {
//            log.error("返回信息：" + this.userInfo);
//            return TaskResult.doError("登录校验失败", AutoTaskStatus.USER_CHECK_ERROR);
//        }
//    }
//
//    @TaskAction(name = "获取公告列表", order = 1)
//    public TaskResult getAnnouncement(TaskLog log) throws Exception {
//        JSONObject jsonObject = HttpUtil.requestJson(AnnouncementURL, null, this.header, HttpUtil.RequestType.GET);
//        log.info("获取到公告列表：{}", jsonObject.get("data"));
//        return TaskResult.doSuccess();
//    }
//
//    @TaskAction(name = "签到", order = 2)
//    public TaskResult sign(TaskLog log) throws Exception {
//        JSONObject jsonObject = HttpUtil.requestJson(ListNotificationURL, null, this.header, HttpUtil.RequestType.GET);
//        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
//        if (jsonArray.isEmpty()) {
//            log.info("奖励列表为空，应该是今天已经签到过了..");
//        } else {
//            for (int i = 0; i < jsonArray.size(); i++) {
//                JSONObject object = jsonArray.getJSONObject(i);
//                Long id = object.getLong("id");
//                // 请求接口获取奖励
//                JSONObject ackJsonObj = HttpUtil.requestJson(AckNotificationURL, MapUtil.of("id", id), this.header, HttpUtil.RequestType.JSON);
//                if (ackJsonObj.getInt("retcode") != 0) {
//                    log.error("领取奖励时发生错误：" + ackJsonObj.toJSONString(0));
//                }
//                JSONObject msgObj = JSONUtil.parseObj(object.getStr("msg"));
//                Integer overNum = msgObj.getInt("over_num");
//                if (msgObj.containsKey("msg") && overNum != null && msgObj.containsKey("num")) {
//                    log.info("领取到奖励内容【{}】,时长={},溢出时长={}", msgObj.get("msg"), msgObj.get("num"), overNum);
//                    if (overNum > 0) {
//                        log.warn("奖励时长已经溢出！");
//                    }
//                } else {
//                    log.info("领取到奖励内容：" + msgObj);
//                }
//            }
//        }
//        // 更新用户信息
//        initUserInfo();
//        return TaskResult.doSuccess();
//    }
//
//}
