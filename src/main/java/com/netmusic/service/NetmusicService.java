package com.netmusic.service;

import com.misec.utils.HelpUtil;
import com.netmusic.dao.AutoNetmusicDao;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.util.NeteaseMusicUtil;
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

    public List<AutoNetmusic> getAllPlan(){
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

    public List<AutoNetmusic> getMyPlan(){
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

    public Map<String, String> addBiliPlan(AutoNetmusic autoNetmusic) {
        Map<String, String> map = new HashMap<>();
        Map<String, Object> stringObjectMap = checkForm(autoNetmusic);
        if (!(boolean) stringObjectMap.get("flag")) {
            map.put("code", "-1");
            map.put("msg", (String) stringObjectMap.get("msg"));
            return map;
        }
        //信息检查完毕后，尝试登录账号，进行验证
        Map<String,String> infos = new HashMap<>();
        infos.put("phone",autoNetmusic.getPhone());
        infos.put("password",autoNetmusic.getPassword());
        infos.put("countrycode",autoNetmusic.getCountrycode());
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
        if (autoNetmusic1 == null || autoNetmusic1.getId() == null){
            //insert
            netmusicDao.insertSelective(autoNetmusic);
        }else {
            //update
            autoNetmusic.setId(autoNetmusic1.getId());
            netmusicDao.updateByPrimaryKeySelective(autoNetmusic);
        }
        map.put("code", "200");
        map.put("msg", usercheck.get("msg"));
        return map;
    }

    public Map<String,Object> checkForm(AutoNetmusic autoNetmusic){
        Map<String,Object> map = new HashMap<>();
        String name = autoNetmusic.getName();
        String phone = autoNetmusic.getPhone();
        String password = autoNetmusic.getPassword();
        String enable = autoNetmusic.getEnable();
        String other = autoNetmusic.getOther();
        String countrycode = autoNetmusic.getCountrycode();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(phone) || StringUtils.isBlank(password)){
            map.put("flag", false);
            map.put("msg", "前三项不能为空！");
            return map;
        }
        if (StringUtils.isBlank(countrycode) || !StringUtils.isNumeric(countrycode)){
            autoNetmusic.setCountrycode("86");
        }
        if (StringUtils.isBlank(enable) || !enable.equals("true") && !enable.equals("false")){
            autoNetmusic.setEnable("true");
        }
        if (StringUtils.isBlank(other)){
            autoNetmusic.setOther(null);
        }
        map.put("flag", true);
        map.put("msg", "check complete");
        return map;
    }

}
