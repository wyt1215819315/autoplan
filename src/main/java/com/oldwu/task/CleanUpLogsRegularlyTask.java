package com.oldwu.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.oldwu.dao.AutoLogDao;
import com.oldwu.dao.SysQuartzJobLogMapper;
import com.oldwu.domain.SysQuartzJobLog;
import com.oldwu.entity.AutoLog;
import com.oldwu.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.Date;

/**
 * 自动清理日志
 *
 * @author MoWei
 * @CreateDate 2022/9/13 16:00
 */
@Slf4j
@Configuration("cleanUpLogsRegularly")
@EnableScheduling
public class CleanUpLogsRegularlyTask {

    private static AutoLogDao logDao;

    private static SysQuartzJobLogMapper sysQuartzJobLogMapper;

    @Autowired
    public void getLogDao(AutoLogDao logDao) {
        CleanUpLogsRegularlyTask.logDao = logDao;
    }

    @Autowired
    public void getSysQuartzJobLogMapper(SysQuartzJobLogMapper sysQuartzJobLogMapper) {
        CleanUpLogsRegularlyTask.sysQuartzJobLogMapper = sysQuartzJobLogMapper;
    }

    /**
     * auto_log表
     * 每个月1号清理上个月的日志
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void cleanUpAutoLog() {
        System.out.println("定时任务1");
        Calendar calendar = Calendar.getInstance();
        //当前时间减去一个月，即一个月前的时间
        calendar.add(Calendar.MONTH, -1);
        Date time = calendar.getTime();
        String previousMonth = DateUtils.format(time) + DateUtils.ADD_H_LAST;
        int delete = logDao.delete(Wrappers.<AutoLog>lambdaQuery().le(AutoLog::getDate, previousMonth));
        log.info("auto_log任务日志表清理成功，总共清理了{}条日志", delete);
    }

    /**
     * t_sys_quartz_job_log表
     * 每个月1号清理上个月的日志
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void cleanUpScheduledTaskLogs() {
        System.out.println("定时任务2");
        Calendar calendar = Calendar.getInstance();
        //当前时间减去一个月，即一个月前的时间
        calendar.add(Calendar.MONTH, -1);
        Date time = calendar.getTime();
        String previousMonth = DateUtils.format(time) + DateUtils.ADD_H_LAST;
        int delete = sysQuartzJobLogMapper.delete(Wrappers.<SysQuartzJobLog>lambdaQuery().le(SysQuartzJobLog::getStartTime, previousMonth));
        log.info("t_sys_quartz_job_log定时任务日志表清理成功，总共清理了{}条日志", delete);
    }
}
