package com.oldwu.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.netmusic.dao.AutoNetmusicDao;
import com.netmusic.model.AutoNetmusic;
import com.netmusic.service.NetmusicService;
import com.netmusic.util.NeteaseMusicUtil;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.entity.AutoLog;
import com.push.PushUtil;
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
    public void getNetService(NetmusicService service) {
        NetMusicTask.netmusicService = service;
    }

    @Autowired
    public void getLogDao(AutoLogDao logDao) {
        NetMusicTask.logDao = logDao;
    }

    @Autowired
    public void getNetMusicDao(AutoNetmusicDao netmusicDao) {
        NetMusicTask.netmusicDao = netmusicDao;
    }

    /**
     * 用于每日0点重置状态
     */
    public void resetStatus() {
        //重置自动任务的标识
        List<AutoNetmusic> autoNetmusics = netmusicDao.selectList(new QueryWrapper<>());
        for (AutoNetmusic autoNetmusic : autoNetmusics) {
            int autoId = autoNetmusic.getId();
            AutoNetmusic autoNetmusic1 = new AutoNetmusic();
            autoNetmusic1.setId(autoId);
            autoNetmusic1.setStatus("100");
            netmusicDao.updateById(autoNetmusic1);
        }
    }

    /**
     * 刷新用户的等级等信息
     */
    public void refreshUserInfo() {
        //获取数据库中所有用户
        List<AutoNetmusic> autoNetmusics = netmusicDao.selectList(new QueryWrapper<>());
        for (AutoNetmusic autoNetmusic : autoNetmusics) {
            Map<String, String> infos = new HashMap<>();
            infos.put("phone", autoNetmusic.getPhone());
            infos.put("password", autoNetmusic.getPassword());
            infos.put("countrycode", autoNetmusic.getCountrycode());
            Map<String, String> login = NeteaseMusicUtil.login(infos);
            if (login.get("flag").equals("false")) {
                continue;
            }
            autoNetmusic.setNetmusicName(login.get("nickname"));
            autoNetmusic.setNetmusicId(login.get("uid"));
            autoNetmusic.setNetmusicNeedDay(login.get("days"));
            autoNetmusic.setNetmusicNeedListen(login.get("count"));
            autoNetmusic.setAvatar(login.get("avatarUrl"));
            autoNetmusic.setNetmusicLevel(login.get("level"));
            netmusicDao.updateById(autoNetmusic);
        }
    }


    /**
     * 网易云定时签到任务
     */
    public void doAutoCheck() {

        List<AutoNetmusic> autoNetmusics = netmusicDao.selectList(new QueryWrapper<>());

        for (AutoNetmusic autoNetmusic : autoNetmusics) {
            Integer autoId = autoNetmusic.getId();
            Integer userid = autoNetmusic.getUserid();

            //任务未开启，下一个
            if (!Boolean.parseBoolean(autoNetmusic.getEnable())) {
                AutoNetmusic autoNetmusic1 = new AutoNetmusic(autoId, "0", new Date());
                netmusicDao.updateById(autoNetmusic1);
                continue;
            }

            runTask(autoId, userid, autoNetmusic);
        }
        refreshUserInfo();
    }

    public void runTask(Integer autoId, Integer userid, AutoNetmusic autoNetmusic){
        int reconnect = 2;//最大重试次数

        //更新任务状态
        AutoNetmusic autoNetmusic1 = new AutoNetmusic(autoId, "1", null);
        netmusicDao.updateById(autoNetmusic1);

        String phone = autoNetmusic.getPhone();
        String password = autoNetmusic.getPassword();
        String countrycode = autoNetmusic.getCountrycode();

        Map<String, String> infos = new HashMap<>();
        infos.put("phone", phone);
        infos.put("password", password);
        infos.put("countrycode", countrycode);

        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < reconnect; i++) {

            Map<String, Object> run = NeteaseMusicUtil.run(infos);

            if (!(boolean) run.get("flag")) {
                if (i != reconnect - 1) {
                    msg.append(run.get("msg"));
                    msg.append("\n").append("用户登录失败！，尝试第").append(i + 1).append("次重试");
                    continue;
                }
                autoNetmusic1.setStatus("500");
                break;
            }
            if (run.get("complete") != null && (boolean) run.get("complete")) {
                //任务成功完成
                msg.append("\n").append(run.get("msg")).append("\n-----------------\n").append("[SUCCESS] 任务全部正常完成，进程退出");
                autoNetmusic1.setStatus("200");
                break;
            } else {
                if (i == reconnect - 1) {
                    //任务重试失败
                    msg.append("\n").append(run.get("msg")).append("[!!FAILED!!]").append("任务异常！请查看日志！");
                    autoNetmusic1.setStatus("-1");
                    break;
                }
                msg.append("\n[!WARNING!]任务运行出现异常\n").append(run.get("msg"));
            }
        }
        //执行推送任务
        PushUtil.doPush(msg.toString(), autoNetmusic.getWebhook(), userid);
        //日志写入至数据库
        AutoLog netlog = new AutoLog(autoId, "netmusic", autoNetmusic1.getStatus(), userid, new Date(), msg.toString());
        logDao.insert(netlog);
        //更新任务状态
        autoNetmusic1.setEnddate(new Date());
        netmusicDao.updateById(autoNetmusic1);
    }
}
