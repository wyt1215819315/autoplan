package com.oldwu.task;

import com.miyoushe.mapper.AutoMihayouDao;
import com.miyoushe.model.AutoMihayou;
import com.miyoushe.service.MihayouService;
import com.miyoushe.sign.Constant;
import com.miyoushe.sign.DailyTask;
import com.miyoushe.sign.gs.GenshinHelperProperties;
import com.netmusic.dao.AutoNetmusicDao;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.netmusic.util.NeteaseMusicUtil;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.entity.AutoLog;
import com.push.ServerPush;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("mihuyouTask")
public class MiHuYouTask {

    private static AutoMihayouDao mihayouDao;
    private static AutoLogDao logDao;
    private static MihayouService mihayouService;

    @Autowired
    public void getMihuyouService(MihayouService service){
        MiHuYouTask.mihayouService = service;
    }

    @Autowired
    public void getLogDao(AutoLogDao logDao) {
        MiHuYouTask.logDao = logDao;
    }

    @Autowired
    public void getmihuyouDao(AutoMihayouDao mihayouDao) {
        MiHuYouTask.mihayouDao = mihayouDao;
    }

    /**
     * 用于每日0点重置状态
     */
    public void resetStatus(){
        //重置自动任务的标识
        List<AutoMihayou> autoMihayous = mihayouDao.selectAll();
        for (AutoMihayou autoMihayou : autoMihayous) {
            int autoId = autoMihayou.getId();
            AutoMihayou autoMihayou1 = new AutoMihayou();
            autoMihayou1.setId(autoId);
            autoMihayou1.setStatus("100");
            mihayouDao.updateByPrimaryKeySelective(autoMihayou1);
        }
    }


    /**
     * 米忽悠定时签到任务
     */
    public void doAutoCheck() {
        System.setProperty(Constant.GENSHIN_EXEC, System.getProperty("os.name"));
        int reconnect = 1;//最大重试次数
        List<AutoMihayou> autoMihayous = mihayouDao.selectAll();
        for (AutoMihayou autoMihayou : autoMihayous) {
            Integer autoId = autoMihayou.getId();
            Integer userid = autoMihayou.getUserId();
            //任务未开启，下一个
            if (!Boolean.parseBoolean(autoMihayou.getEnable())) {
                AutoMihayou autoMihayou1 = new AutoMihayou(autoId,"0",new Date());
                mihayouDao.updateByPrimaryKeySelective(autoMihayou1);
                continue;
            }
            //更新任务状态
            AutoMihayou autoMihayou1 = new AutoMihayou(autoId,"1",null);
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
                msg.append("\n开始第").append(i+1).append("次任务");
                Map<String, Object> maprun = dailyTask.doDailyTask();
                if (!(boolean)maprun.get("flag")){
                    if (i != reconnect-1){
                        msg.append(maprun.get("msg"));
                        msg.append("\n").append("用户登录失败！，尝试第").append(i + 1).append("次重试");
                        continue;
                    }
                    autoMihayou1.setStatus("500");
                    break;
                }else {
                    //任务成功完成
                    msg.append("\n").append(maprun.get("msg")).append("\n[SUCCESS]任务全部正常完成，进程退出");
                    autoMihayou1.setStatus("200");
                    break;
                }
            }
            //执行推送任务
            String s = ServerPush.doServerPush(msg.toString(), autoMihayou.getWebhook());
            msg.append("\n").append(s);
            //日志写入至数据库
            AutoLog netlog = new AutoLog(autoId, "mihuyou", autoMihayou1.getStatus(), userid, new Date(), msg.toString());
            logDao.insertSelective(netlog);
            //更新任务状态
            autoMihayou1.setEndate(new Date());
            mihayouDao.updateByPrimaryKeySelective(autoMihayou1);
        }
    }

}
