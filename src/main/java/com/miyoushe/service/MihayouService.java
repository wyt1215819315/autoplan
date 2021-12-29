package com.miyoushe.service;

import com.alibaba.fastjson.JSONObject;
import com.misec.utils.HelpUtil;
import com.miyoushe.mapper.AutoMihayouDao;
import com.miyoushe.model.AutoMihayou;
import com.miyoushe.sign.DailyTask;
import com.miyoushe.sign.gs.GenShinSignMiHoYo;
import com.miyoushe.sign.gs.GenshinHelperProperties;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.dao.UserDao;
import com.oldwu.entity.AutoLog;
import com.oldwu.util.HttpUtils;
import com.push.PushUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class MihayouService {

    @Autowired
    private AutoMihayouDao mihayouDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AutoLogDao autoLogDao;


    public List<AutoMihayou> getAllPlan() {
        List<AutoMihayou> autoMihayous = mihayouDao.selectAll();
        List<AutoMihayou> result = new ArrayList<>();
        for (AutoMihayou mihayous : autoMihayous) {
            mihayous.setCookie(null);
            mihayous.setStoken(null);
            mihayous.setSuid(null);
            mihayous.setGenshinUid(HelpUtil.userNameEncode(mihayous.getGenshinUid()));
            mihayous.setMiName(HelpUtil.userNameEncode(mihayous.getMiName()));
            result.add(mihayous);
        }
        return result;
    }

    public List<AutoMihayou> getMyPlan(Integer userId) {
        List<AutoMihayou> autoMihayous = mihayouDao.selectMine(userId);
        List<AutoMihayou> result = new ArrayList<>();
        for (AutoMihayou mihayous : autoMihayous) {
            mihayous.setCookie(null);
            mihayous.setStoken(null);
            mihayous.setSuid(null);
            result.add(mihayous);
        }
        return result;
    }

    public Map<String, Object> deleteMiHuYouPlan(AutoMihayou autoMihayou) {
        Map<String, Object> map = new HashMap<>();
        //校验用户id
        Integer userid = autoMihayou.getUserId();
        Integer autoid = autoMihayou.getId();
        if (autoid == null || autoid == 0) {
            map.put("code", -1);
            map.put("msg", "传参不能为空！");
            return map;
        }
        List<AutoMihayou> autoMihayous = mihayouDao.selectMine(userid);
        boolean flag = false;
        for (AutoMihayou mihayou : autoMihayous) {
            int autoId = mihayou.getId();
            if (autoId == autoid) {
                flag = true;
                break;
            }
        }
        if (userDao.getRole(userid).equals("ROLE_ADMIN")) {  //忽略管理
            flag = true;
        }
        if (!flag) {
            map.put("code", 403);
            map.put("msg", "你没有权限删除这条或数据不存在！");
            return map;
        }
        //首先删除日志
        AutoLog autoLog = new AutoLog();
        autoLog.setUserid(userid);
        autoLog.setAutoId(autoid);
        autoLog.setType("mihuyou");
        try {
            autoLogDao.deleteByAutoId(autoLog);
            int i = mihayouDao.deleteByPrimaryKey(autoid);
            if (i > 0) {
                map.put("code", 200);
                map.put("msg", "删除成功");
                return map;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        map.put("code", -1);
        map.put("msg", "删除失败！");
        return map;
    }

    public List<Map<String, String>> addMiHuYouPlan(AutoMihayou autoMihayou) {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = new HashMap<>();

        Map<String, Object> stringObjectMap = checkForm(autoMihayou, false);
        if (!(boolean) stringObjectMap.get("flag")) {
            map.put("code", "-1");
            map.put("msg", (String) stringObjectMap.get("msg"));
            list.add(map);
            return list;
        }

        //信息检查完毕后，尝试登录账号，进行验证
        GenShinSignMiHoYo signMiHoYo = new GenShinSignMiHoYo(autoMihayou.getCookie());
        List<Map<String, Object>> uidInfo = signMiHoYo.getUid();

        //账号都是同一个，如果要错一起错，一般不会出现不一致的情况
        if (!(boolean) uidInfo.get(0).get("flag")) {
            map.put("code", "-1");
            map.put("msg", (String) uidInfo.get(0).get("msg"));
            list.add(map);
            return list;
        }
        //账号验证成功,写入用户数据，如果有多个数据则拿逗号分隔
        String uid = "";
        String nickname = "";
        for (int i = 0; i < uidInfo.size(); i++) {
            Map<String, Object> map1 = uidInfo.get(i);
            if (i == 0) {
                uid = (String) map1.get("uid");
                nickname = (String) map1.get("nickname");
            } else {
                uid = uid + "," + map1.get("uid");
                nickname = nickname + "," + map1.get("nickname");
            }

        }
        autoMihayou.setGenshinUid(uid);
        autoMihayou.setMiName(nickname);
        autoMihayou.setSuid((String) stringObjectMap.get("stuid"));
        autoMihayou.setStoken((String) stringObjectMap.get("stoken"));
        autoMihayou.setOtherKey((String) stringObjectMap.get("login_ticket_str"));
        autoMihayou.setStatus("100");

        //判断数据是否存在，使用stuid进行检索
        AutoMihayou autoMihayou1 = mihayouDao.selectBystuid(autoMihayou.getSuid());
//            AutoMihayou autoMihayou1 = mihayouDao.selectByGenshinUid(autoMihayou.getGenshinUid());
        if (autoMihayou1 == null || autoMihayou1.getId() == null) {
            //insert
            mihayouDao.insertSelective(autoMihayou);
        } else {
            //update
            autoMihayou.setId(autoMihayou1.getId());
            mihayouDao.updateByPrimaryKeySelective(autoMihayou);
        }
        for (Map<String, Object> uidInfoMap : uidInfo) {
            Map<String, String> map1 = new HashMap<>();
            map1.put("code", "200");
            map1.put("msg", (String) uidInfoMap.get("msg"));
            if (!stringObjectMap.containsKey("login_ticket_str") || stringObjectMap.containsKey("login_ticket") && !(Boolean) stringObjectMap.get("login_ticket")) {
                map1.put("code", "201");
                map1.put("msg", map1.get("msg") + "<br>");
                map1.put("msg1", (String) stringObjectMap.get("msg"));
            }
            list.add(map1);
        }
        return list;
    }

    public Map<String, Object> checkForm(AutoMihayou autoMihayou, boolean skipCookieCheck) {
        Map<String, Object> map = new HashMap<>();
        String lcookie = autoMihayou.getLcookie();
        String enable = autoMihayou.getEnable();
        String webhook = autoMihayou.getWebhook();
        if (!skipCookieCheck) {
            String name = autoMihayou.getName();
            String cookie = autoMihayou.getCookie();
            if (StringUtils.isBlank(name) || StringUtils.isBlank(cookie)) {
                map.put("flag", false);
                map.put("msg", "前两项不能为空！");
                return map;
            }
            //检查cookie字段
            String account_id = HttpUtils.getCookieByName(autoMihayou.getCookie(), "account_id");
            String cookie_token = HttpUtils.getCookieByName(autoMihayou.getCookie(), "cookie_token");
            if (StringUtils.isBlank(account_id)) {
                //备用方案
                account_id = HttpUtils.getCookieByName(autoMihayou.getCookie(), "ltuid");
            }
            if (!StringUtils.isBlank(lcookie)) {
                //校验两个cookie字段
                String login_uid = HttpUtils.getCookieByName(lcookie, "login_uid");
                if (!StringUtils.isBlank(login_uid) && !StringUtils.isBlank(account_id)) {
                    if (!login_uid.equals(account_id)) {
                        map.put("flag", false);
                        map.put("msg", "两项cookie中的账号信息不一致！");
                        return map;
                    }
                }
                if (StringUtils.isBlank(account_id)) {
                    //备用方案2
                    account_id = HttpUtils.getCookieByName(lcookie, "login_uid");
                }
                String login_ticket = HttpUtils.getCookieByName(lcookie, "login_ticket");
                if (StringUtils.isBlank(account_id) || StringUtils.isBlank(login_ticket)) {
                    map.put("login_ticket", false);
                    map.put("msg", "cookie中没有login_ticket字段或login_ticket已过期，无法使用米游币任务，如果需要使用，请前往米哈游通行证处获取");
                } else {
                    //获取stoken
                    Map<String, Object> cookieToken = getCookieToken(login_ticket, account_id);
                    if (!(boolean) cookieToken.get("flag")) {
                        map.put("login_ticket", false);
                        map.put("msg", "login_ticket校验失败：" + cookieToken.get("msg") + "你可能无法使用米游币任务！");
                    } else {
                        map.put("login_ticket_str", login_ticket);
                        map.put("stoken", cookieToken.get("stoken"));
                    }
                }
            } else {
                map.put("msg", "没有填写米哈游通行证cookie，如果您无需使用米游币任务，请忽略此消息");
            }
            map.put("stuid", account_id);
            if (StringUtils.isBlank(account_id) || StringUtils.isBlank(cookie_token)) {
                map.put("flag", false);
                map.put("msg", "无效的cookie：米游社cookie中必须包含cookie_token和account_id字段！");
                return map;
            }
        } else {
            if (!StringUtils.isBlank(lcookie) && autoMihayou.getId() != null) {
                String login_uid = HttpUtils.getCookieByName(lcookie, "login_uid");
                String suid = mihayouDao.selectByPrimaryKey(autoMihayou.getId()).getSuid();
                if (!StringUtils.isBlank(login_uid) && !StringUtils.isBlank(suid)) {
                    if (!login_uid.equals(suid)) {
                        map.put("flag", false);
                        map.put("msg", "与之前添加的账号信息不一致！");
                        return map;
                    }
                }
                String login_ticket = HttpUtils.getCookieByName(lcookie, "login_ticket");
                if (!StringUtils.isBlank(suid) && !StringUtils.isBlank(login_ticket)) {
                    //获取stoken
                    Map<String, Object> cookieToken = getCookieToken(login_ticket, suid);
                    if ((boolean) cookieToken.get("flag")) {
                        autoMihayou.setOtherKey(login_ticket);
                        autoMihayou.setStoken((String) cookieToken.get("stoken"));
                    } else {
                        map.put("flag", false);
                        map.put("msg", "login_ticket校验失败：" + cookieToken.get("msg"));
                    }
                } else {
                    map.put("flag", false);
                    map.put("msg", "没有发现login_ticket字段！");
                }
            }
        }
        if (StringUtils.isBlank(enable) || !enable.equals("true") && !enable.equals("false")) {
            autoMihayou.setEnable("true");
        }
        if (StringUtils.isBlank(webhook)) {
            autoMihayou.setWebhook("");
        }
        //返回ltuid和token
        if (map.containsKey("flag") && !(boolean) map.get("flag")) {
            return map;
        }
        map.put("flag", true);
        return map;
    }

    public Map<String, Object> getCookieToken(String login_ticket, String accountId) {
        Map<String, Object> map = new HashMap<>();
        String token_url = "https://api-takumi.mihoyo.com/auth/api/getMultiTokenByLoginTicket?login_ticket=%s&token_types=3&uid=%s";
        token_url = String.format(token_url, login_ticket, accountId);
        HttpResponse httpResponse = null;
        try {
            httpResponse = HttpUtils.doGet(token_url, null, HttpUtils.getHeaders(), null);
            JSONObject result = HttpUtils.getJson(httpResponse);
            if (!"OK".equals(result.get("message"))) {
                map.put("flag", false);
                map.put("msg", "login_ticket已失效,请重新登录获取");
//                System.err.println("login_ticket已失效,请重新登录获取");
                return map;
            }
            map.put("flag", true);
            map.put("msg", "OK");
            map.put("stoken", result.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("token"));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("flag", false);
            map.put("msg", "服务端请求失败！" + e.getMessage());
            return map;
        }
    }

    public AutoMihayou getMyEditPlan(AutoMihayou autoMihayou1) {
        AutoMihayou autoMihayou = mihayouDao.selectByPrimaryKey(autoMihayou1.getId());
        if (autoMihayou == null || autoMihayou.getId() == null) {
            return null;
        }
        //放行管理员
        String role = userDao.getRole(autoMihayou1.getUserId());
        if (!autoMihayou.getUserId().equals(autoMihayou1.getUserId()) && !role.equals("ROLE_ADMIN")) {
            return null;
        }
        return autoMihayou;
    }

    public Map<String, Object> editMiHuYouPlan(AutoMihayou autoMihayou1) {
        Map<String, Object> map = new HashMap<>();
        AutoMihayou autoMihayou = mihayouDao.selectByPrimaryKey(autoMihayou1.getId());
        if (autoMihayou == null || autoMihayou.getId() == null) {
            map.put("code", -1);
            map.put("msg", "参数错误！");
            return map;
        }
        //放行管理员
        String role = userDao.getRole(autoMihayou1.getUserId());
        if (!autoMihayou.getUserId().equals(autoMihayou1.getUserId()) && !role.equals("ROLE_ADMIN")) {
            map.put("code", 403);
            map.put("msg", "你没有权限修改！");
            return map;
        }
        Map<String, Object> formResult = checkForm(autoMihayou1, true);
        if (!(boolean) formResult.get("flag")) {
            map.put("code", 201);
            map.put("msg", formResult.get("msg"));
            return map;
        }
        int i = mihayouDao.updateByPrimaryKeySelective(autoMihayou1);
        if (i > 0) {
            map.put("code", 200);
            map.put("msg", "操作成功！");
            return map;
        }
        map.put("code", 0);
        map.put("msg", "操作失败！");
        return map;
    }

    public Map<String, Object> doDailyTaskPersonal(Integer autoId, Integer userId) {
        Map<String, Object> map = new HashMap<>();
        AutoMihayou autoMihayou = mihayouDao.selectByPrimaryKey(autoId);
        if (autoMihayou == null || autoMihayou.getId() == null) {
            map.put("code", 500);
            map.put("msg", "参数错误！");
            return map;
        }
        String role = userDao.getRole(autoMihayou.getUserId());
        if (!autoMihayou.getUserId().equals(userId) && !role.equals("ROLE_ADMIN")) {
            map.put("code", 403);
            map.put("msg", "你没有权限执行！");
            return map;
        }
        if (autoMihayou.getStatus().equals("1")) {
            map.put("code", 1);
            map.put("msg", "任务已经在运行啦~请不要重复执行");
            return map;
        }
        Thread t = new Thread(() -> {
            int reconnect = 1;//最大重试次数
            //更新任务状态
            AutoMihayou autoMihayou1 = new AutoMihayou(autoId, "1", null);
            mihayouDao.updateByPrimaryKeySelective(autoMihayou1);

            //执行任务
            String suid = autoMihayou.getSuid();
            String stoken = autoMihayou.getStoken();
            String cookie = autoMihayou.getCookie();

            GenshinHelperProperties.Account account = new GenshinHelperProperties.Account();
            account.setCookie(cookie);
            account.setStuid(suid);
            account.setStoken(stoken);

            DailyTask dailyTask = new DailyTask(account);
            StringBuilder msg = new StringBuilder();
            for (int i = 0; i < reconnect; i++) {
                msg.append("\n开始第").append(i + 1).append("次任务");
                Map<String, Object> maprun = dailyTask.doDailyTask();
                if (!(boolean) maprun.get("flag")) {
                    if (i != reconnect - 1) {
                        msg.append(maprun.get("msg"));
                        msg.append("\n").append("用户登录失败！，尝试第").append(i + 1).append("次重试");
                        continue;
                    }
                    autoMihayou1.setStatus("500");
                    break;
                } else {
                    //任务成功完成
                    msg.append("\n").append(maprun.get("msg")).append("\n[SUCCESS]任务全部正常完成，进程退出");
                    autoMihayou1.setStatus("200");
                    break;
                }
            }
            //执行推送任务
            PushUtil.doPush(msg.toString(), autoMihayou.getWebhook(), userId);
            //日志写入至数据库
            AutoLog netlog = new AutoLog(autoId, "mihuyou", autoMihayou1.getStatus(), userId, new Date(), msg.toString());
            autoLogDao.insertSelective(netlog);
            //更新任务状态
            autoMihayou1.setEndate(new Date());
            mihayouDao.updateByPrimaryKeySelective(autoMihayou1);
        });
        t.start();
        map.put("code", 200);
        map.put("msg", "运行指令已发送，请稍后查看运行状态");
        return map;
    }
}
