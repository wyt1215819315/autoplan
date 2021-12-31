package com.netmusic.service;

import com.misec.utils.HelpUtil;
import com.netmusic.dao.AutoNetmusicDao;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.util.NeteaseMusicUtil;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.dao.UserDao;
import com.oldwu.entity.AutoLog;
import com.oldwu.task.NetMusicTask;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NetmusicService {

    @Autowired
    private AutoNetmusicDao netmusicDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AutoLogDao autoLogDao;


    public List<AutoNetmusic> getAllPlan() {
        List<AutoNetmusic> autoNetmusics = netmusicDao.selectAll();
        List<AutoNetmusic> result = new ArrayList<>();
        for (AutoNetmusic autoNetmusic : autoNetmusics) {
            autoNetmusic.setPassword(null);
            autoNetmusic.setNetmusicId(null);
            autoNetmusic.setPhone(null);
            autoNetmusic.setNetmusicName(HelpUtil.userNameEncode(autoNetmusic.getNetmusicName()));
            result.add(autoNetmusic);
        }
        return result;
    }


    public List<AutoNetmusic> getMyPlan(int userid) {
        List<AutoNetmusic> autoNetmusics = netmusicDao.selectMine(userid);
        List<AutoNetmusic> result = new ArrayList<>();
        for (AutoNetmusic autoNetmusic : autoNetmusics) {
            autoNetmusic.setPassword(null);
            result.add(autoNetmusic);
        }
        return result;
    }

    public Map<String, String> addNetMusicPlan(AutoNetmusic autoNetmusic) {
        Map<String, String> map = new HashMap<>();
        Map<String, Object> stringObjectMap = checkForm(autoNetmusic, false);
        if (!(boolean) stringObjectMap.get("flag")) {
            map.put("code", "-1");
            map.put("msg", (String) stringObjectMap.get("msg"));
            return map;
        }
        //信息检查完毕后，尝试登录账号，进行验证
        Map<String, String> infos = new HashMap<>();
        infos.put("phone", autoNetmusic.getPhone());
        infos.put("password", autoNetmusic.getPassword());
        infos.put("countrycode", autoNetmusic.getCountrycode());
        Map<String, String> usercheck = NeteaseMusicUtil.login(infos);
        if (usercheck.get("flag").equals("false")) {
            map.put("code", "-1");
            map.put("msg", usercheck.get("msg"));
            return map;
        }
        //账号验证成功,写入用户数据
        autoNetmusic.setNetmusicName(usercheck.get("nickname"));
        autoNetmusic.setNetmusicId(usercheck.get("uid"));
        autoNetmusic.setNetmusicNeedDay(usercheck.get("days"));
        autoNetmusic.setNetmusicNeedListen(usercheck.get("count"));
        autoNetmusic.setNetmusicLevel(usercheck.get("level"));
        autoNetmusic.setStatus("100");
        //判断数据是否存在，使用网易云uid进行检索
        AutoNetmusic autoNetmusic1 = netmusicDao.selectByUid(autoNetmusic.getNetmusicId());
        if (autoNetmusic1 == null || autoNetmusic1.getId() == null) {
            //insert
            netmusicDao.insertSelective(autoNetmusic);
        } else {
            //update
            autoNetmusic.setId(autoNetmusic1.getId());
            netmusicDao.updateByPrimaryKeySelective(autoNetmusic);
        }
        map.put("code", "200");
        map.put("msg", usercheck.get("msg"));
        return map;
    }

    public Map<String, Object> checkForm(AutoNetmusic autoNetmusic, boolean skipCheckCookie) {
        Map<String, Object> map = new HashMap<>();
        String enable = autoNetmusic.getEnable();
        String webhook = autoNetmusic.getWebhook();
        if (!skipCheckCookie) {
            String name = autoNetmusic.getName();
            String phone = autoNetmusic.getPhone();
            String password = autoNetmusic.getPassword();
            String countrycode = autoNetmusic.getCountrycode();
            if (StringUtils.isBlank(name) || StringUtils.isBlank(phone) || StringUtils.isBlank(password)) {
                map.put("flag", false);
                map.put("msg", "前三项不能为空！");
                return map;
            }
            if (StringUtils.isBlank(countrycode) || !StringUtils.isNumeric(countrycode)) {
                autoNetmusic.setCountrycode("86");
            }
        }
        if (StringUtils.isBlank(enable) || !enable.equals("true") && !enable.equals("false")) {
            autoNetmusic.setEnable("true");
        }
        if (StringUtils.isBlank(webhook)) {
            autoNetmusic.setWebhook("");
        }
        map.put("flag", true);
        map.put("msg", "check complete");
        return map;
    }

    public Map<String, Object> deleteNetMusicPlan(AutoNetmusic autoNetmusic) {
        Map<String, Object> map = new HashMap<>();
        //校验用户id
        Integer userid = autoNetmusic.getUserid();
        Integer autoid = autoNetmusic.getId();
        if (autoid == null || autoid == 0) {
            map.put("code", -1);
            map.put("msg", "传参不能为空！");
            return map;
        }
        List<AutoNetmusic> autoNetmusics = netmusicDao.selectMine(userid);
        boolean flag = false;
        for (AutoNetmusic netmusic : autoNetmusics) {
            int autoId = netmusic.getId();
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
        autoLog.setUserid(autoNetmusic.getUserid());
        autoLog.setAutoId(autoNetmusic.getId());
        autoLog.setType("netmusic");
        try {
            autoLogDao.deleteByAutoId(autoLog);
            int i = netmusicDao.deleteByPrimaryKey(autoid);
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

    public AutoNetmusic getMyEditPlan(AutoNetmusic autoNetmusic1) {
        AutoNetmusic autoNetmusic = netmusicDao.selectByPrimaryKey(autoNetmusic1.getId());
        if (autoNetmusic == null || autoNetmusic.getId() == null) {
            return null;
        }
        //放行管理员
        String role = userDao.getRole(autoNetmusic1.getUserid());
        if (!autoNetmusic.getUserid().equals(autoNetmusic1.getUserid()) && !role.equals("ROLE_ADMIN")) {
            return null;
        }
        return autoNetmusic;
    }

    public Map<String, Object> editNetMusicPlan(AutoNetmusic autoNetmusic1) {
        Map<String, Object> map = new HashMap<>();
        AutoNetmusic autoNetmusic = netmusicDao.selectByPrimaryKey(autoNetmusic1.getId());
        if (autoNetmusic == null || autoNetmusic.getId() == null) {
            map.put("code", -1);
            map.put("msg", "参数错误！");
            return map;
        }
        //放行管理员
        String role = userDao.getRole(autoNetmusic1.getUserid());
        if (!autoNetmusic.getUserid().equals(autoNetmusic1.getUserid()) && !role.equals("ROLE_ADMIN")) {
            map.put("code", 403);
            map.put("msg", "你没有权限修改！");
            return map;
        }
        checkForm(autoNetmusic1, true);
        int i = netmusicDao.updateByPrimaryKeySelective(autoNetmusic1);
        if (i > 0) {
            map.put("code", 200);
            map.put("msg", "操作成功！");
            return map;
        }
        map.put("code", 0);
        map.put("msg", "操作失败！");
        return map;
    }

    public Map<String, Object> doDailyTaskPersonal(Integer autoId,Integer userid) {
        Map<String, Object> map = new HashMap<>();
        AutoNetmusic autoNetmusic = netmusicDao.selectByPrimaryKey(autoId);
        if (autoNetmusic == null || autoNetmusic.getId() == null) {
            map.put("code", 500);
            map.put("msg", "参数错误！");
            return map;
        }
        String role = userDao.getRole(autoNetmusic.getUserid());
        if (!autoNetmusic.getUserid().equals(userid) && !role.equals("ROLE_ADMIN")) {
            map.put("code", 403);
            map.put("msg", "你没有权限执行！");
            return map;
        }
        if (autoNetmusic.getStatus().equals("1")) {
            map.put("code", 1);
            map.put("msg", "任务已经在运行啦~请不要重复执行");
            return map;
        }
        Thread t = new Thread(() -> {
            NetMusicTask netMusicTask = new NetMusicTask();
            netMusicTask.runTask(autoId, userid, autoNetmusic);
        });
        t.start();
        map.put("code", 200);
        map.put("msg", "运行指令已发送，请稍后查看运行状态");
        return map;
    }
}
