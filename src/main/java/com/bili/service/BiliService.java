package com.bili.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bili.dao.AutoBilibiliDao;
import com.bili.dao.BiliUserDao;
import com.bili.model.AutoBilibili;
import com.bili.model.BiliPlan;
import com.bili.model.BiliUser;
import com.bili.model.task.BiliData;
import com.bili.model.task.BiliTaskInfo;
import com.bili.model.task.config.*;
import com.bili.util.BiliHelpUtil;
import com.bili.util.BiliTaskUtil;
import com.oldwu.constant.URLConstant;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.dao.UserDao;
import com.oldwu.entity.AjaxResult;
import com.oldwu.entity.AutoLog;
import com.oldwu.security.utils.SessionUtils;
import com.oldwu.task.BiliTask;
import com.oldwu.util.HttpUtils;
import com.oldwu.vo.PageDataVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class BiliService {
    private static final Log logger = LogFactory.getLog(BiliService.class);

    @Autowired
    private AutoBilibiliDao autoBilibiliDao;

    @Autowired
    private BiliUserDao biliUserDao;

    @Autowired
    private AutoLogDao autoLogDao;

    @Autowired
    private UserDao userDao;

    public AjaxResult view(Integer id) {
        Integer userId = SessionUtils.getPrincipal().getId();
        AutoBilibili autoBilibili = autoBilibiliDao.selectById(id);
        if (autoBilibili == null) {
            return AjaxResult.doError();
        } else {
            //放行管理员
            String role = userDao.getRole(userId);
            if (!autoBilibili.getUserid().equals(userId) && !role.equals("ROLE_ADMIN")) {
                return AjaxResult.doError("你无权访问！");
            }
        }
        //移除cookie
        autoBilibili.setSessdata(null);
        autoBilibili.setBiliJct(null);
        autoBilibili.setDedeuserid(null);
        return AjaxResult.doSuccess(autoBilibili);
    }

    public Map<String, Object> getQrcodeStatus(String oauthKey) {
        Map<String, String> headers = new HashMap<>();
        Map<String, Object> result = new HashMap<>();
        String body = "oauthKey=" + oauthKey;
        headers.put("Accept-Language", "zh-cn");
        headers.put("Connection", "keep-alive");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        try {
            HttpResponse httpResponse = HttpUtils.doPost(URLConstant.BILI_QRCODE_STATUS_URL, null, headers, null, body);
            JSONObject json = HttpUtils.getJson(httpResponse);
            if (json == null || json.getBoolean("status") == null) {
                result.put("code", "-100");
                result.put("msg", "获取状态失败！");
                return result;
            }
            Boolean status = json.getBoolean("status");
            if (!status) {
                Integer data = json.getInteger("data");
                String message = json.getString("message");
                result.put("code", data);
                result.put("msg", message);
                return result;
            } else {
                //登陆成功
                Map<String, String> cookies = HttpUtils.getCookies(httpResponse);
                result.put("code", 200);
                result.put("msg", "校验成功！已自动填充");
                result.put("dedeuserid", cookies.get("DedeUserID"));
                result.put("sessdata", cookies.get("SESSDATA"));
                result.put("bilijct", cookies.get("bili_jct"));
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", "-101");
            result.put("msg", "获取状态失败！" + e);
            return result;
        }
    }

    public AjaxResult getQrcodeAuth() {
        try {
            HttpResponse httpResponse = HttpUtils.doGet(URLConstant.BILI_QRCODE_URL, null, HttpUtils.getHeaders(), null);
            JSONObject json = HttpUtils.getJson(httpResponse);
            if (json != null && json.getInteger("code") == 0) {
                return AjaxResult.doSuccess("success", json.getJSONObject("data").getString("oauthKey"));
            } else {
                return AjaxResult.doError("二维码获取失败！");
            }
        } catch (Exception e) {
            return AjaxResult.doError("二维码获取失败！");
        }
    }

    public PageDataVO<BiliPlan> queryPageList(Integer page, Integer limit) {
        List<BiliPlan> biliPlans = biliUserDao.selectPageList((page - 1) * limit, limit);

        for (BiliPlan biliPlan : biliPlans) {
            biliPlan.setBiliName(com.oldwu.util.StringUtils.userNameEncode(biliPlan.getBiliName()));
        }

        QueryWrapper<BiliUser> queryWrapper = new QueryWrapper<>();

        Long count = biliUserDao.selectCount(queryWrapper);

        return new PageDataVO<>(count, biliPlans);
    }

    public AjaxResult addBiliPlan(String json) {
        Map<String, Object> stringObjectMap = checkForm(json, false);
        if (!(boolean) stringObjectMap.get("flag")) {
            return AjaxResult.doError((String) stringObjectMap.get("msg"));
        }
        AutoBilibili autoBilibili = (AutoBilibili) stringObjectMap.get("data");
        autoBilibili.setUserid(SessionUtils.getPrincipal().getId());
        //信息检查完毕后，使用cookie尝试登录账号，进行验证
        try {
            return checkUser(autoBilibili);
        } catch (Exception e) {
            return AjaxResult.doError("用户校验失败！" + e.getMessage());
        }
    }

    public boolean userCheck(AutoBilibili autoBilibili) {
        BiliTaskInfo taskInfo = new BiliTaskInfo(autoBilibili.getDedeuserid(), autoBilibili.getSessdata(), autoBilibili.getBiliJct());
        BiliTaskUtil biliTaskUtil = new BiliTaskUtil(taskInfo);
        //首先需要执行登录任务以及硬币检查任务
        try {
            biliTaskUtil.userCheck();
            return true;
        } catch (Exception e) {
            //登录检查失败，直接返回失败
            return false;
        }
    }

    /**
     * 校验用户，成功写入任务数据，失败返回msg
     *
     * @param autoBilibili
     * @return
     */
    @Transactional
    public AjaxResult checkUser(AutoBilibili autoBilibili) throws Exception {
        BiliTaskInfo taskInfo = new BiliTaskInfo(autoBilibili.getDedeuserid(), autoBilibili.getSessdata(), autoBilibili.getBiliJct());
        BiliTaskUtil biliTaskUtil = new BiliTaskUtil(taskInfo);
        //首先需要执行登录任务以及硬币检查任务
        BiliData biliData;
        try {
            biliData = biliTaskUtil.userCheck();
        } catch (Exception e) {
            //登录检查失败，直接返回失败
            return AjaxResult.doError(e.getMessage());
        }
        String s = biliData.getUname();
        long mid = biliData.getMid();
        //判断用户表中是否存在该用户
        BiliUser biliUser = biliUserDao.selectByMid(mid);
        if (biliUser == null || biliUser.getId() == null) {
            //将数据储存到任务表以及获取用户信息储存到biliuser表和bili任务表
            autoBilibiliDao.insert(autoBilibili);
            if (autoBilibili.getId() <= 0) {
                return AjaxResult.doError("数据库错误！添加任务信息失败！");
            }
            boolean b = updateUserInfo(autoBilibili.getId(), biliData, false);
            if (!b) {
                //此处出错需要事务回滚，防止数据库中内容被污染
                throw new Exception("数据库错误！添加用户信息失败！");
            }
            return AjaxResult.doSuccess("检查登录信息成功：" + s);
        } else {
            //更新用户信息
            Integer autoId = biliUser.getAutoId();
            autoBilibili.setId(autoId);
            int i = autoBilibiliDao.updateById(autoBilibili);
            if (i <= 0) {
                return AjaxResult.doError("");
            }
            boolean b = updateUserInfo(autoBilibili.getId(), biliData, true);
            if (b) {
                return AjaxResult.doSuccess("更新原有登录信息成功：" + s);
            }
            throw new Exception("数据库错误！更新任务信息失败！");
        }
    }

    public boolean updateUserInfo(Integer autoId, BiliData userInfo, boolean update) {
        BiliUser biliUser1 = new BiliUser();
        biliUser1.setAutoId(autoId);
        biliUser1.setBiliCoin(userInfo.getMoney());
        biliUser1.setUid(userInfo.getMid());
        biliUser1.setBiliName(userInfo.getUname());
        biliUser1.setBiliLevel(userInfo.getLevel_info().getCurrent_level());
        biliUser1.setBiliExp((long) userInfo.getLevel_info().getCurrent_exp());
        biliUser1.setBiliUpexp((long) userInfo.getLevel_info().getNext_exp_asInt());
        biliUser1.setFaceImg(userInfo.getFace());
        biliUser1.setIsVip((int) BiliHelpUtil.queryVipStatusType(userInfo).get("data") == 0 ? "false" : "true");
        biliUser1.setVipDueDate(new Date(userInfo.getVipDueDate()));
        if (!update) {
            //增加用户信息
            return biliUserDao.insert(biliUser1) > 0;
        } else {
            //update
            biliUser1.setAutoId(autoId);
            return biliUserDao.updateByAutoIdSelective(biliUser1) > 0;
        }
    }

    /**
     * 校验表单，失败返回《flag=false，msg=msg》
     *
     * @param json 任务信息json字符串
     * @return
     */
    public Map<String, Object> checkForm(String json, boolean skipCookieCheck) {
        Map<String, Object> map = new HashMap<>();
        JSONObject jsonObject = null;
        if (!StringUtils.isBlank(json) && json.startsWith("{")) {
            try {
                jsonObject = JSON.parseObject(json);
            } catch (Exception e) {
                map.put("flag", false);
                map.put("msg", "非法json！");
                return map;
            }
        } else {
            map.put("flag", false);
            map.put("msg", "非法json！");
            return map;
        }
        AutoBilibili autoBilibili = new AutoBilibili();
        if (!skipCookieCheck) {
            String biliJct = jsonObject.getString("biliJct");
            String dedeuserid = jsonObject.getString("dedeuserid");
            String sessdata = jsonObject.getString("sessdata");
            if (StringUtils.isBlank(biliJct) || StringUtils.isBlank(dedeuserid) || StringUtils.isBlank(sessdata)) {
                map.put("flag", false);
                map.put("msg", "cookie的三项都不能为空！");
                return map;
            }
            if (StringUtils.isBlank(jsonObject.getString("name"))) {
                map.put("flag", false);
                map.put("msg", "任务名不能为空！");
                return map;
            }
            autoBilibili.setBiliJct(biliJct);
            autoBilibili.setDedeuserid(dedeuserid);
            autoBilibili.setSessdata(sessdata);
        }
        boolean enableAutoPlan = Boolean.parseBoolean(jsonObject.getString("enable"));
        autoBilibili.setEnable(Boolean.toString(enableAutoPlan));
        String webhook = autoBilibili.getWebhook();
        if (StringUtils.isBlank(webhook)) {
            autoBilibili.setWebhook("");
        }
        //改为json传参，校验成功之后转为json字符串保存
        BiliTaskConfig taskConfig = new BiliTaskConfig();
        BiliPreConfig biliPreConfig = new BiliPreConfig();
        BiliCoinConfig biliCoinConfig = new BiliCoinConfig();
        BiliChargeConfig biliChargeConfig = new BiliChargeConfig();
        BiliGiveGiftConfig biliGiveGiftConfig = new BiliGiveGiftConfig();
        Integer dailyCoin = jsonObject.getInteger("dailyCoin"); //每日投币数量
        if (dailyCoin != null && dailyCoin <= 5 && dailyCoin >= 0) {
            biliCoinConfig.setDailyCoin(dailyCoin);
        }
        Integer reserveCoins = jsonObject.getInteger("reserveCoins");
        if (reserveCoins != null && reserveCoins >= 0) {
            biliCoinConfig.setReserveCoins(reserveCoins);
        }
        boolean enableClickLike = Boolean.parseBoolean(jsonObject.getString("enableClickLike"));
        biliCoinConfig.setEnableClickLike(enableClickLike);
        Integer coinRules = jsonObject.getInteger("coinRules"); //投币规则
        if (coinRules != null && coinRules >= 0 && coinRules <= 1) {
            biliCoinConfig.setCoinRules(coinRules);
        }

        boolean enableAutoCharge = Boolean.parseBoolean(jsonObject.getString("enableAutoCharge"));
        biliChargeConfig.setEnableAutoCharge(enableAutoCharge);
        String chargeObject = jsonObject.getString("chargeObject");
        if (!StringUtils.isBlank(chargeObject) && StringUtils.isNumeric(chargeObject)) {
            biliChargeConfig.setChargeObject(chargeObject);
        }
        Integer chargeDay = jsonObject.getInteger("chargeDay"); //自动充电时间
        if (chargeDay != null && chargeDay >= 1 && chargeDay <= 31) {
            biliChargeConfig.setChargeDay(chargeDay);
        }

        boolean enableGiveGift = Boolean.parseBoolean(jsonObject.getString("enableGiveGift"));
        biliGiveGiftConfig.setEnableGiveGift(enableGiveGift);
        String giveGiftRoomID = jsonObject.getString("giveGiftRoomID");
        if (!StringUtils.isBlank(giveGiftRoomID)) {
            biliGiveGiftConfig.setGiveGiftRoomID(giveGiftRoomID);
        }

        boolean enablePre = Boolean.parseBoolean(jsonObject.getString("enablePre"));
        biliPreConfig.setEnablePre(enablePre);
        boolean enableReversePre = Boolean.parseBoolean(jsonObject.getString("enableReversePre"));
        biliPreConfig.setEnableReversePre(enableReversePre);
        Integer preCoin = jsonObject.getInteger("preCoin");
        if (preCoin != null && preCoin >= 0) {
            biliPreConfig.setPreCoin(preCoin);
        }
        Integer preKeepCoin = jsonObject.getInteger("preKeepCoin");
        if (preKeepCoin != null && preKeepCoin >= 0) {
            biliPreConfig.setKeepCoin(preKeepCoin);
        }

        String cartoonSignOS = jsonObject.getString("cartoonSignOS");
        if (!StringUtils.isNumeric(cartoonSignOS) && (cartoonSignOS.equals("ios") || cartoonSignOS.equals("android"))) {
            taskConfig.setCartoonSignOS(cartoonSignOS);
        }

        //任务信息转换部分
        if (jsonObject.containsKey("id")) {
            autoBilibili.setId(jsonObject.getInteger("id"));
        }
        if (jsonObject.containsKey("name")) {
            autoBilibili.setName(jsonObject.getString("name"));
        }

        taskConfig.setBiliCoinConfig(biliCoinConfig);
        taskConfig.setBiliChargeConfig(biliChargeConfig);
        taskConfig.setBiliPreConfig(biliPreConfig);
        taskConfig.setBiliGiveGiftConfig(biliGiveGiftConfig);
        //转换为json字符串
        String taskInfoJsonStr = JSON.toJSONString(taskConfig);
        autoBilibili.setTaskConfig(taskInfoJsonStr);

        map.put("flag", true);
        map.put("msg", "check complete");
        map.put("data", autoBilibili);
        return map;
    }

    /**
     * 删除b站任务，开启事务
     *
     * @param id 传入要删除的autoId
     * @return AjaxResult 删除结果
     */
    @Transactional
    public AjaxResult deleteBiliPlan(Integer id) throws Exception {
        //校验用户id
        Integer userid = SessionUtils.getPrincipal().getId();
        if (id == null || id == 0) {
            return AjaxResult.doError("传参不能为空！");
        }
        List<BiliPlan> biliPlans = biliUserDao.selectMine(userid);
        boolean flag = false;
        for (BiliPlan biliPlan : biliPlans) {
            int autoId = biliPlan.getAutoId();
            if (autoId == id) {
                flag = true;
                break;
            }
        }
        if (userDao.getRole(userid).equals("ROLE_ADMIN")) {  //忽略管理
            flag = true;
        }
        if (!flag) {
            return AjaxResult.doError("你没有权限删除这条或数据不存在！");
        }
        //首先删除日志
        AutoLog autoLog = new AutoLog();
        autoLog.setUserid(userid);
        autoLog.setAutoId(id);
        autoLog.setType("bili");
        autoLogDao.deleteByAutoId(autoLog);
        //然后删除b站用户数据
        biliUserDao.deleteByAutoId(id);
        //最后删除主要数据
        int i = autoBilibiliDao.deleteById(id);
        if (i > 0) {
            return AjaxResult.doSuccess("删除成功");
        }
        //删除失败后回滚
        throw new Exception("删除失败！");
    }

    public AjaxResult editBiliPlan(String json) {
        Map<String, Object> stringObjectMap = checkForm(json, true);
        if (stringObjectMap.get("flag").equals("false")) {
            return AjaxResult.doError((String) stringObjectMap.get("msg"));
        }
        AutoBilibili autoBilibili1 = (AutoBilibili) stringObjectMap.get("data");
        Map<String, Object> map = new HashMap<>();
        AutoBilibili autoBilibili = autoBilibiliDao.selectById(autoBilibili1.getId());
        if (autoBilibili == null || autoBilibili.getId() == null) {
            return AjaxResult.doError("参数错误！");
        }
        //放行管理员
        String role = userDao.getRole(SessionUtils.getPrincipal().getId());
        if (!autoBilibili.getUserid().equals(SessionUtils.getPrincipal().getId()) && !role.equals("ROLE_ADMIN")) {
            return AjaxResult.doError("你没有权限修改！");
        }
        int i = autoBilibiliDao.updateById(autoBilibili1);
        if (i > 0) {
            return AjaxResult.doSuccess("操作成功！");
        }
        return AjaxResult.doError("操作失败！");
    }

    /**
     * 根据用户id查询这个用户的b站任务信息
     *
     * @param id
     * @return
     */
    public AjaxResult listMine(Integer id) {
        List<BiliPlan> biliPlans = biliUserDao.selectMine(id);
        return AjaxResult.doSuccess(biliPlans);
    }

    public AjaxResult doDailyTaskPersonal(Integer autoId) {
        Integer userId = SessionUtils.getPrincipal().getId();

        AutoBilibili autoBilibili = autoBilibiliDao.selectById(autoId);
        if (autoBilibili == null || autoBilibili.getId() == null) {
            return AjaxResult.doError("参数错误！");
        }
        String role = userDao.getRole(autoBilibili.getUserid());
        if (!autoBilibili.getUserid().equals(userId) && !role.equals("ROLE_ADMIN")) {
            return AjaxResult.doError("你没有权限执行！");
        }
        BiliUser biliUser = biliUserDao.selectByAutoId(autoBilibili.getId());
        if (biliUser.getStatus().equals("1")) {
            return AjaxResult.doError("任务已经在运行啦~请不要重复执行");
        }
        Thread t = new Thread(() -> {
            BiliTask biliTask = new BiliTask();
            biliTask.runTask(autoBilibili);
        });
        t.start();
        return AjaxResult.doSuccess("运行指令已发送，请稍后查看运行状态");
    }
}
