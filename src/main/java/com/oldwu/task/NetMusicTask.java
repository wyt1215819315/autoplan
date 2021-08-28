package com.oldwu.task;

import com.misec.BiliMain;
import com.netmusic.dao.AutoNetmusicDao;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.netmusic.util.NeteaseMusicUtil;
import com.oldwu.dao.AutoBilibiliDao;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.dao.BiliUserDao;
import com.oldwu.entity.AutoBilibili;
import com.oldwu.entity.AutoLog;
import com.oldwu.entity.BiliUser;
import com.oldwu.log.OldwuLog;
import com.oldwu.service.BiliService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("netTask")
public class NetMusicTask {

    private static AutoNetmusicDao netmusicDao;
    private static AutoLogDao logDao;
    private static NetmusicService netmusicService;

    @Autowired
    public void getNetService(NetmusicService service){
        NetMusicTask.netmusicService = service;
    }

    @Autowired
    public void getLogDao(AutoLogDao logDao) {
        NetMusicTask.logDao = logDao;
    }

    @Autowired
    public void getBiliDao(AutoNetmusicDao netmusicDao) {
        NetMusicTask.netmusicDao = netmusicDao;
    }


    /**
     * 网易云定时签到任务
     */
    public void doAutoCheck() {
        List<AutoNetmusic> autoNetmusics = netmusicDao.selectAll();
        for (AutoNetmusic autoNetmusic : autoNetmusics) {
            Integer autoId = autoNetmusic.getId();
            Integer userid = autoNetmusic.getUserid();
            //任务未开启，下一个
            if (!Boolean.parseBoolean(autoNetmusic.getEnable())) {
                AutoNetmusic autoNetmusic1 = new AutoNetmusic(autoId,"0",new Date());
                netmusicDao.updateByPrimaryKeySelective(autoNetmusic1);
                continue;
            }
            //更新任务状态
            AutoNetmusic autoNetmusic1 = new AutoNetmusic(autoId,"1",null);
            netmusicDao.updateByPrimaryKeySelective(autoNetmusic1);
            //执行任务
            String phone = autoNetmusic.getPhone();
            String password = autoNetmusic.getPassword();
            String countrycode = autoNetmusic.getCountrycode();
            Map<String,String> infos = new HashMap<>();
            infos.put("phone",phone);
            infos.put("password",password);
            infos.put("countrycode",countrycode);
            Map<String, Object> run = NeteaseMusicUtil.run(infos);
            if (!(boolean)run.get("flag")){
                autoNetmusic1.setStatus("500");
                autoNetmusic1.setEnddate(new Date());
                netmusicDao.updateByPrimaryKeySelective(autoNetmusic1);
                AutoLog netlog = new AutoLog(null, autoId, "netmusic", "500", userid, new Date(), (String) run.get("msg"));
                logDao.insertSelective(netlog);
                continue;
            }
            //写入至数据库
            AutoLog netlog = new AutoLog(null, autoId, "netmusic", "200", userid, new Date(), (String) run.get("msg"));
            logDao.insertSelective(netlog);
            //更新任务状态
            autoNetmusic1.setEnddate(new Date());
            autoNetmusic1.setStatus("200");
            netmusicDao.updateByPrimaryKeySelective(autoNetmusic1);
        }
    }

}
