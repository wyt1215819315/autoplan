package com.github.task.xiaomi.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.system.dao.AutoLogDao;
import com.github.system.entity.AutoLog;
import com.github.system.util.DateUtils;
import com.github.push.PushUtil;
import com.github.task.xiaomi.dao.AutoXiaomiDao;
import com.github.task.xiaomi.model.entity.AutoXiaomiEntity;
import com.github.task.xiaomi.util.XiaomiApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component("xiaomiTask")
public class XiaoMiTask {
    private static AutoXiaomiDao autoXiaomiDao;
    private static AutoLogDao logDao;

    @Autowired
    public void getLogDao(AutoLogDao logDao) {
        XiaoMiTask.logDao = logDao;
    }

    @Autowired
    public void getAutoXiaomiDao(AutoXiaomiDao autoXiaomiDao) {
        XiaoMiTask.autoXiaomiDao = autoXiaomiDao;
    }

    /**
     * 用于每日0点重置状态
     */
    public void resetStatus() {
        //重置自动任务的标识
        List<AutoXiaomiEntity> autoXiaomis = autoXiaomiDao.selectList(Wrappers.<AutoXiaomiEntity>lambdaQuery().eq(AutoXiaomiEntity::getEnable, "true"));
        for (AutoXiaomiEntity autoXiaomi : autoXiaomis) {
            AutoXiaomiEntity xiaomi = new AutoXiaomiEntity();
            xiaomi.setId(autoXiaomi.getId());
            xiaomi.setStatus("100");
            autoXiaomiDao.updateById(xiaomi);
        }
    }

    /**
     * 小米定时任务
     */
    public void doAutoCheck() throws Exception {
        List<AutoXiaomiEntity> autoXiaomis = autoXiaomiDao.selectList(Wrappers.<AutoXiaomiEntity>lambdaQuery().eq(AutoXiaomiEntity::getEnable, "true"));
        for (AutoXiaomiEntity autoXiaomi : autoXiaomis) {
            Integer autoId = autoXiaomi.getId();
            Integer userid = autoXiaomi.getUserId();
            runTask(autoId, userid, autoXiaomi);
            //每一个任务执行完之后，休眠一会，防止步数冲突
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error("线程错误：{}", e.getMessage());
            }
        }
    }

    public void runTask(Integer autoId, Integer userid, AutoXiaomiEntity autoXiaomi) {
        //更新任务状态
        AutoXiaomiEntity xiaomi = new AutoXiaomiEntity();
        xiaomi.setId(autoId);
        xiaomi.setStatus("1");
        xiaomi.setName(autoXiaomi.getName());
        xiaomi.setWebhook(autoXiaomi.getWebhook());
        autoXiaomiDao.updateById(xiaomi);
        StringBuilder msg = new StringBuilder();

        int steps = 0;

        if ("1".equals(autoXiaomi.getRandomOrNot()) || StrUtil.isBlank(autoXiaomi.getSteps()) || "0".equals(autoXiaomi.getSteps())) {
            int weekOfDate = DateUtil.thisDayOfWeek();
            switch (weekOfDate) {
                case 1:
                    steps = ThreadLocalRandom.current().nextInt(10000, 35001);
                    break;
                case 2:
                    steps = ThreadLocalRandom.current().nextInt(10000, 35001);
                    break;
                case 3:
                    steps = ThreadLocalRandom.current().nextInt(10000, 35001);
                    break;
                case 4:
                    steps = ThreadLocalRandom.current().nextInt(10000, 35001);
                    break;
                case 5:
                    steps = ThreadLocalRandom.current().nextInt(10000, 35001);
                    break;
                case 6:
                    steps = ThreadLocalRandom.current().nextInt(35001, 98801);
                    break;
                case 7:
                    steps = ThreadLocalRandom.current().nextInt(35001, 98801);
                    break;
                default:
                    steps = ThreadLocalRandom.current().nextInt(10000, 35001);
            }
        } else {
            steps = Integer.parseInt(autoXiaomi.getSteps());
        }

        Map<String, Object> run = XiaomiApi.mainHandler(autoXiaomi.getPhone(), autoXiaomi.getPassword(), autoXiaomi.getName(), steps);
        if (!(boolean) run.get("flag")) {
            if ("200".equals(run.get("code"))) {
                msg.append(run.get("msg"));
                msg.append("\n").append("任务名称：" + xiaomi.getName() + "\n登录失败！请检查下手机号或者密码是否已修改");
                xiaomi.setStatus("500");
                //如果账号密码是错误的，把这个任务关闭。
                xiaomi.setEnable("false");
            } else {
                msg.append("任务名称：" + xiaomi.getName() + "\n登录失败！");
                msg.append(run.get("msg"));
                xiaomi.setStatus("-1");
            }

        }

        if ((boolean) run.get("flag")) {
            msg.append(run.get("msg")).append("\n-----------------\n").append("[SUCCESS] 任务全部正常完成，进程退出");
            xiaomi.setStatus("200");
        }

        //执行推送任务
        if (!StringUtils.isEmpty(xiaomi.getWebhook())) {
            try {
                PushUtil.doPush(msg.toString(), xiaomi.getWebhook(), userid);
            } catch (Exception e) {
                log.error("用户id：{},推送失败：{}", userid, e.getMessage());
            }
        }
        //日志写入至数据库
        AutoLog netlog = new AutoLog(autoId, "xiaomi", xiaomi.getStatus(), userid, new Date(), msg.toString());
        logDao.insert(netlog);
        //更新任务
        xiaomi.setPreviousOccasion(String.valueOf(steps));
        xiaomi.setEnddate(new Date());
        autoXiaomiDao.updateById(xiaomi);
    }
}
