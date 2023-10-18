package com.github.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.task.miyoushe.mapper.AutoMihayouDao;
import com.github.task.miyoushe.model.AutoMihayou;
import com.github.task.miyoushe.service.MihayouService;
import com.github.task.miyoushe.sign.Constant;
import com.github.task.miyoushe.sign.DailyTask;
import com.github.task.miyoushe.sign.gs.GenshinHelperProperties;
import com.github.task.base.dao.HistoryTaskLogDao;
import com.github.task.base.entity.HistoryTaskLog;
import com.github.push.PushUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component("mihuyouTask")
public class MiHuYouTask {

    private static AutoMihayouDao mihayouDao;
    private static HistoryTaskLogDao logDao;
    private static MihayouService mihayouService;
    private final Log logger = LogFactory.getLog(MiHuYouTask.class);

    @Autowired
    public void getMihuyouService(MihayouService service){
        MiHuYouTask.mihayouService = service;
    }

    @Autowired
    public void getLogDao(HistoryTaskLogDao logDao) {
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
        List<AutoMihayou> autoMihayous = mihayouDao.selectList(new QueryWrapper<>());
        for (AutoMihayou autoMihayou : autoMihayous) {
            int autoId = autoMihayou.getId();
            AutoMihayou autoMihayou1 = new AutoMihayou();
            autoMihayou1.setId(autoId);
            autoMihayou1.setStatus("100");
            mihayouDao.updateById(autoMihayou1);
        }
    }

    /**
     * 更新头像逻辑，因为数据量可能比较大，所以不用每天调用
     */
    public void updateAvatar(){
        List<AutoMihayou> autoMihayous = mihayouDao.selectList(new QueryWrapper<>());
        for (AutoMihayou mihayous : autoMihayous) {
            mihayouService.setPersonInfo(mihayous.getId(),mihayous.getCookie());
        }
    }

    /**
     * 米忽悠定时签到任务
     */
    public void doAutoCheck() {
        System.setProperty(Constant.GENSHIN_EXEC, System.getProperty("os.name"));

        List<AutoMihayou> autoMihayous = mihayouDao.selectList(new QueryWrapper<>());

        for (AutoMihayou autoMihayou : autoMihayous) {
            Integer autoId = autoMihayou.getId();
            Integer userid = autoMihayou.getUserId();

            //任务未开启，下一个
            if (!Boolean.parseBoolean(autoMihayou.getEnable())) {
                AutoMihayou autoMihayou1 = new AutoMihayou(autoId,"0",new Date());
                mihayouDao.updateById(autoMihayou1);
                continue;
            }

            runTask(autoId, userid, autoMihayou);
        }
    }

    /**
     * 米忽悠定时签到任务 执行部分
     * @param autoId id
     * @param userid userId
     * @param autoMihayou autoMihayou
     */
    public void runTask(Integer autoId, Integer userid, AutoMihayou autoMihayou){
        int reconnect = 1;//最大重试次数

        //更新任务状态
        AutoMihayou autoMihayou1 = new AutoMihayou(autoId,"1",null);
        mihayouDao.updateById(autoMihayou1);

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
            Map<String, Object> maprun;
            try {
                maprun = dailyTask.doDailyTask();
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
                    msg.append("\n").append(maprun.get("msg")).append("\n-----------------\n").append("[SUCCESS] 任务全部正常完成，进程退出");
                    autoMihayou1.setStatus("200");
                    break;
                }
            }catch (Exception e){
                logger.error("！米游社任务运行出错" + e.getMessage());
                msg.append("！米游社任务运行出错：").append(e.getMessage());
                autoMihayou1.setStatus("-1");
            }
        }
        //执行推送任务
        PushUtil.doPush(msg.toString(), autoMihayou.getWebhook(), userid);
        //日志写入至数据库
        HistoryTaskLog netlog = new HistoryTaskLog(autoId, "mihuyou", autoMihayou1.getStatus(), userid, new Date(), msg.toString());
        logDao.insert(netlog);
        //更新任务状态
        autoMihayou1.setEndate(new Date());
        mihayouDao.updateById(autoMihayou1);
    }

}
