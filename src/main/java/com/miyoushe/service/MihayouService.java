package com.miyoushe.service;

import com.misec.utils.HelpUtil;
import com.miyoushe.mapper.AutoMihayouDao;
import com.miyoushe.model.AutoMihayou;
import com.miyoushe.sign.gs.GenShinSignMiHoYo;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.util.NeteaseMusicUtil;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.dao.UserDao;
import com.oldwu.entity.AutoLog;
import com.oldwu.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String,Object> map = new HashMap<>();
        //校验用户id
        Integer userid = autoMihayou.getUserId();
        Integer autoid = autoMihayou.getId();
        if (autoid == null || autoid == 0){
            map.put("code",-1);
            map.put("msg","传参不能为空！");
            return map;
        }
        List<AutoMihayou> autoMihayous = mihayouDao.selectMine(userid);
        boolean flag = false;
        for (AutoMihayou mihayou : autoMihayous) {
            int autoId = mihayou.getId();
            if (autoId == autoid){
                flag = true;
                break;
            }
        }
        if (userDao.getRole(userid).equals("ROLE_ADMIN")){  //忽略管理
            flag = true;
        }
        if (!flag){
            map.put("code",403);
            map.put("msg","你没有权限删除这条或数据不存在！");
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
            if (i > 0){
                map.put("code",200);
                map.put("msg","删除成功");
                return map;
            }
        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        map.put("code",-1);
        map.put("msg","删除失败！");
        return map;
    }

    public Map<String, String> addMiHuYouPlan(AutoMihayou autoMihayou) {
        Map<String, String> map = new HashMap<>();
        Map<String, Object> stringObjectMap = checkForm(autoMihayou);
        if (!(boolean) stringObjectMap.get("flag")) {
            map.put("code", "-1");
            map.put("msg", (String) stringObjectMap.get("msg"));
            return map;
        }
        //信息检查完毕后，尝试登录账号，进行验证
        GenShinSignMiHoYo signMiHoYo = new GenShinSignMiHoYo(autoMihayou.getCookie());
        Map<String, Object> uidInfo = signMiHoYo.getUid();
        if (!(boolean)uidInfo.get("flag")) {
            map.put("code", "-1");
            map.put("msg", (String) uidInfo.get("msg"));
            return map;
        }
        //账号验证成功,写入用户数据
        autoMihayou.setGenshinUid((String) uidInfo.get("uid"));
        autoMihayou.setMiName((String) uidInfo.get("nickname"));
        autoMihayou.setSuid((String) stringObjectMap.get("stuid"));
        autoMihayou.setStoken((String) stringObjectMap.get("stoken"));
        autoMihayou.setStatus("100");
        //判断数据是否存在，使用stuid进行检索
        AutoMihayou autoMihayou1 = mihayouDao.selectBystuid(autoMihayou.getSuid());
        if (autoMihayou1 == null || autoMihayou1.getId() == null){
            //insert
            mihayouDao.insertSelective(autoMihayou);
        }else {
            //update
            autoMihayou.setId(autoMihayou1.getId());
            mihayouDao.updateByPrimaryKeySelective(autoMihayou);
        }
        map.put("code", "200");
        map.put("msg", (String) uidInfo.get("msg"));
        return map;

    }

    public Map<String,Object> checkForm(AutoMihayou autoMihayou){
        Map<String,Object> map = new HashMap<>();
        String name = autoMihayou.getName();
        String cookie = autoMihayou.getCookie();
        String enable = autoMihayou.getEnable();
        String webhook = autoMihayou.getWebhook();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(cookie)){
            map.put("flag", false);
            map.put("msg", "前两项不能为空！");
            return map;
        }
        //检查cookie字段
        String account_id = HttpUtils.getCookieByName(autoMihayou.getCookie(), "account_id");
        if (StringUtils.isBlank(account_id)){
            //备用方案
            account_id = HttpUtils.getCookieByName(autoMihayou.getCookie(), "ltuid");
        }
        String cookie_token = HttpUtils.getCookieByName(autoMihayou.getCookie(), "cookie_token");
        if (StringUtils.isBlank(account_id) || StringUtils.isBlank(cookie_token)){
            map.put("flag", false);
            map.put("msg", "cookie中必须包含account_id/ltuid 和 cookie_token 字段，请尝试重新登录米游社获取！");
            return map;
        }
        if (StringUtils.isBlank(enable) || !enable.equals("true") && !enable.equals("false")){
            autoMihayou.setEnable("true");
        }
        if (StringUtils.isBlank(webhook)){
            autoMihayou.setWebhook(null);
        }
        //返回ltuid和token
        map.put("stuid", account_id);
        map.put("stoken", cookie_token);
        map.put("flag", true);
        map.put("msg", "check complete");
        return map;
    }
}
