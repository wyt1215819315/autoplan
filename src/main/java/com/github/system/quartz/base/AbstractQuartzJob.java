package com.github.system.quartz.base;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.system.quartz.entity.SysQuartzJob;
import com.github.system.quartz.entity.SysQuartzJobLog;
import com.github.system.quartz.service.SysQuartzJobLogService;
import com.github.system.base.util.SpringUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @CLASSNAME AbstractQuartzJob
 * @Description
 * @Auther Jan  橙寂
 * @DATE 2019/9/5 0005 15:05
 */

public abstract class AbstractQuartzJob implements Job {

    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static final ThreadLocal<Date> threadLocal = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        SysQuartzJob sysJob = new SysQuartzJob();
        BeanUtils.copyProperties(context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES), sysJob);
        try {
            before(context, sysJob);
            // 增加超时机制，如果任务执行超时直接将其kill掉
            if (sysJob.getTimeout() == null || sysJob.getTimeout() <= 0) {
                doExecute(context, sysJob);
            } else {
                FutureTask<Boolean> task = new FutureTask<>(() -> {
                    doExecute(context, sysJob);
                    return true;
                });
                Thread thread = new Thread(task);
                thread.start();
                task.get(sysJob.getTimeout(), TimeUnit.SECONDS);
            }
            after(context, sysJob, null);
        } catch (TimeoutException e) {
            log.error(sysJob.getJobName() + "任务执行超时");
            after(context, sysJob, e);
        } catch (Exception e) {
            log.error("任务执行异常  - ：", e);
            after(context, sysJob, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void before(JobExecutionContext context, SysQuartzJob sysJob) {
        threadLocal.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param sysJob  系统计划任务
     */
    protected void after(JobExecutionContext context, SysQuartzJob sysJob, Exception e) {
        Date startTime = threadLocal.get();
        threadLocal.remove();

        final SysQuartzJobLog sysJobLog = new SysQuartzJobLog();
        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setStartTime(startTime);
        sysJobLog.setEndTime(new Date());
        long runMs = sysJobLog.getEndTime().getTime() - sysJobLog.getStartTime().getTime();
        sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null) {
            if (e instanceof TimeoutException) {
                sysJobLog.setStatus(ScheduleConstants.TIMEOUT_STATUS);
            } else {
                sysJobLog.setStatus(ScheduleConstants.FAIL_STATUS);
            }
            String errorMsg = StrUtil.sub(ExceptionUtil.getMessage(e), 0, 2000);
            sysJobLog.setExceptionInfo(errorMsg);
        } else {
            sysJobLog.setStatus(ScheduleConstants.SUCCESS_STATUS);
        }
        //  这里获取service然后插入库中
        SpringUtil.getBean(SysQuartzJobLogService.class).save(sysJobLog);
    }

    /**
     * 子类去实现
     *
     * @param jobExecutionContext
     * @param sysJob
     */
    protected abstract void doExecute(JobExecutionContext jobExecutionContext, SysQuartzJob sysJob) throws Exception;
}
