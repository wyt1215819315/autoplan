package com.github.system.quartz.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.system.quartz.dao.SysQuartzJobMapper;
import com.github.system.quartz.entity.SysQuartzJob;
import com.github.system.quartz.base.QuartzSchedulerUtil;
import com.github.system.quartz.base.ScheduleConstants;
import org.quartz.SchedulerException;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Date;

@Service
public class SysQuartzJobService extends ServiceImpl<SysQuartzJobMapper,SysQuartzJob> {

    @Autowired
    private QuartzSchedulerUtil quartzSchedulerUtil;

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    @Transactional
    public int resumeJob(SysQuartzJob job) throws SchedulerException {
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        int rows = baseMapper.updateById(job);
        if (rows > 0) {
            quartzSchedulerUtil.resumeJob(job);
        }
        return rows;
    }

    /**
     * 暂停任务
     *
     * @param job 调度信息
     */
    @Transactional
    public int pauseJob(SysQuartzJob job) throws SchedulerException {
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        //job.setUpdateBy(ShiroUtils.getLoginName());
        int rows = baseMapper.updateById(job);
        if (rows > 0) {
            quartzSchedulerUtil.pauseJob(job);
        }
        return rows;
    }

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     */
    @Transactional
    public int changeStatus(SysQuartzJob job) throws SchedulerException {
        int rows = 0;
        Integer status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status)) {
            rows = resumeJob(job);
        } else if (ScheduleConstants.Status.PAUSE.getValue().equals(status)) {
            rows = pauseJob(job);
        }
        return rows;
    }

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     */
    @Transactional
    public void run(SysQuartzJob job) throws SchedulerException {
        quartzSchedulerUtil.run(job);
    }

    /**
     * 判断cron时间表达式正确性
     *
     */
    public boolean isValidExpression(String cronExpression) {
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(cronExpression);
            Date date = trigger.computeFirstFireTime(null);
            return date != null;
        } catch (ParseException e) {
            return false;
        }
    }


}
