package com.oldwu.task;

import com.misec.BiliMain;
import com.oldwu.dao.AutoBilibiliDao;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.dao.BiliUserDao;
import com.oldwu.entity.AutoBilibili;
import com.oldwu.entity.AutoLog;
import com.oldwu.entity.BiliPlan;
import com.oldwu.entity.BiliUser;
import com.oldwu.log.OldwuLog;
import com.oldwu.service.BiliService;
import com.push.PushUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("biliTask")
public class BiliTask {

    private static AutoBilibiliDao bilibiliDao;
    private static BiliUserDao biliUserDao;
    private static AutoLogDao logDao;
    private static BiliService biliService;

    @Autowired
    public void getBiliService(BiliService service){
        BiliTask.biliService = service;
    }

    @Autowired
    public void getLogDao(AutoLogDao logDao) {
        BiliTask.logDao = logDao;
    }

    @Autowired
    public void getBiliDao(AutoBilibiliDao bilibiliDao) {
        BiliTask.bilibiliDao = bilibiliDao;
    }

    @Autowired
    public void getBiliUserDao(BiliUserDao biliUserDao) {
        BiliTask.biliUserDao = biliUserDao;
    }

    public void resetStatus(){
        //重置自动任务的标识
        //bili
        List<BiliPlan> biliPlans = biliUserDao.selectAll();
        for (BiliPlan biliPlan : biliPlans) {
            int autoId = biliPlan.getAutoId();
            BiliUser biliUser = new BiliUser();
            biliUser.setAutoId(autoId);
            biliUser.setStatus("100");
            biliUserDao.updateByAutoIdSelective(biliUser);
        }
    }

    /**
     * b站定时签到任务
     */
    public void doAutoCheck() {
        List<AutoBilibili> autoBilibilis = bilibiliDao.selectAll();
        for (AutoBilibili autoBilibili : autoBilibilis) {
            OldwuLog.clear();
            Integer auto_id = autoBilibili.getId();
            Integer userid = autoBilibili.getUserid();
            BiliUser userb = biliUserDao.selectByAutoId(auto_id);
            //任务未开启或已经完成，下一个
            if (Boolean.parseBoolean(autoBilibili.getSkipdailytask())) {
                BiliUser biliUser = new BiliUser(auto_id, "0", new Date());
                biliUserDao.updateByAutoIdSelective(biliUser);
                continue;
            }
            if (userb.getStatus().equals("200")){
                continue;
            }
            //更新任务状态
            BiliUser biliUser = new BiliUser(auto_id, "1", null);
            biliUserDao.updateByAutoIdSelective(biliUser);
            //执行任务
            String[] strings = null;
            if (!StringUtils.isBlank(autoBilibili.getWebhook())) {
                //有推送
                strings = new String[4];
                strings[3] = autoBilibili.getWebhook();
            } else {
                strings = new String[3];
            }
            strings[0] = autoBilibili.getDedeuserid();
            strings[1] = autoBilibili.getSessdata();
            strings[2] = autoBilibili.getBiliJct();
            String webhook = autoBilibili.getWebhook();
            //防止一个任务出错影响整体
            try {
                BiliMain.run(strings, auto_id);
            }catch (Exception e){
                OldwuLog.error("严重！！任务中断！！：：" + e.getMessage());
                PushUtil.doPush(OldwuLog.getLog(), webhook, userid);
                System.err.println(e.getMessage());
                biliUser.setStatus("-1");
                biliUser.setEnddate(new Date());
                biliUserDao.updateByAutoIdSelective(biliUser);
                OldwuLog.clear();
                continue;
            }
            //执行推送任务
            PushUtil.doPush(OldwuLog.getLog(), autoBilibili.getWebhook(), userid);
            //写入至数据库
            AutoLog bilibili = new AutoLog(auto_id, "bilibili", "200", userid, new Date(), OldwuLog.getLog());
            logDao.insertSelective(bilibili);
            //更新用户信息,一并检查cookie
            Map<String, String> map;
            try {
                map = biliService.checkUser(autoBilibili);
            }catch (Exception e){
                biliUser.setStatus("501");
                biliUser.setEnddate(new Date());
                biliUserDao.updateByAutoIdSelective(biliUser);
                OldwuLog.clear();
                continue;
            }
            if (map.get("flag").equals("false")){
                if (map.get("s") != null && map.get("s").equals("cookie")){
                    biliUser.setStatus("500");
                }
                biliUser.setStatus("-1");
                biliUser.setEnddate(new Date());
                biliUserDao.updateByAutoIdSelective(biliUser);
                OldwuLog.clear();
                continue;
            }
            //更新任务状态
            biliUser.setEnddate(new Date());
            biliUser.setStatus("200");
            biliUserDao.updateByAutoIdSelective(biliUser);
            OldwuLog.clear();
        }
    }

}
