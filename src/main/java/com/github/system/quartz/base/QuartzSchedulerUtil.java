package com.github.system.quartz.base;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.system.base.constant.SystemConstant;
import com.github.system.base.util.SpringUtil;
import com.github.system.quartz.base.annotation.AutoJob;
import com.github.system.quartz.base.constant.QuartzConstant;
import com.github.system.quartz.dao.SysQuartzJobMapper;
import com.github.system.quartz.entity.SysQuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @CLASSNAME QuartzConfig
 * @Description Quartz配置类
 * @Auther Jan  橙寂
 * @DATE 2019/9/2 0002 15:21
 */
@Configuration
@Slf4j
public class QuartzSchedulerUtil {

    //这个东西可以放在配置文件中
    //cron表达式 一分钟执行一次
    private final String TEST_CRON = "0 0/1 * * * ?";
    @Autowired
    private Scheduler scheduler;
    @Autowired
    private SysQuartzJobMapper sysQuartzJobMapper;

    /**
     * 获取定时任务的具体执行类
     *
     */
    private static Class<? extends Job> getQuartzJobClass(SysQuartzJob sysJob) {
        boolean isConcurrent = sysJob.getConcurrent() == 0;
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 容器初始化时执行此方法
     * 也就是类初始化的时候
     */
    @PostConstruct
    public void init() throws SchedulerException {
        // 执行包扫描，扫描所有带@AutoJob注解的方法，然后插入数据库并注册到定时任务
        Set<Class<?>> classes = ClassUtil.scanPackageByAnnotation(SystemConstant.BASE_PACKAGE, AutoJob.class);
        List<SysQuartzJob> quartzJobs = sysQuartzJobMapper.selectList(new QueryWrapper<>());
        Set<String> regJobs = quartzJobs.stream().map(SysQuartzJob::getJobName).collect(Collectors.toSet());
        for (Class<?> aClass : classes) {
            // 获取spring bean，如果这玩意是spring类的话，就拿bean名称作为调用字符串，否则拿完整类路径
            String invokeTarget = aClass.getPackageName() + "." + aClass.getName();
            Object bean = null;
            try {
                bean = SpringUtil.getBean(aClass);
            } catch (Exception ignore) {}
            if (bean != null) {
                invokeTarget = SpringUtil.getBeanNamesForType(aClass)[0];
            }
            // 扫这个类上的包含注解的方法
            Method[] methods = ReflectUtil.getMethods(aClass, method -> AnnotationUtil.hasAnnotation(method, AutoJob.class));
            for (Method method : methods) {
                AutoJob annotation = AnnotationUtil.getAnnotation(aClass, AutoJob.class);
                if (regJobs.contains(annotation.value())) {
                    // 拿jobName来判断，数据库已经存在的job就不再进行下一步注册了
                    continue;
                }
                // 获取参数数量
                Class<?>[] parameterTypes = method.getParameterTypes();
                List<String> typeNameList = new ArrayList<>();
                for (Class<?> parameterType : parameterTypes) {
                    String typeName = parameterType.getTypeName();
                    typeNameList.add(typeName);
                }
                String methodName = method.getName() + "(" + StrUtil.join(",", typeNameList) + ")";
                // 往数据库里面塞一个新的job
                SysQuartzJob sysQuartzJob = new SysQuartzJob(annotation.value(), invokeTarget + "." + methodName, annotation.defaultCron(), 1, 1);
                sysQuartzJob.setTimeout(3600 * 24);
                sysQuartzJobMapper.insert(sysQuartzJob);
                quartzJobs.add(sysQuartzJob);
            }
        }

        for (SysQuartzJob job : quartzJobs) {
            try {
                //防止因为数据问题重复创建
                if (checkJobExists(job)) {
                    deleteJob(job);
                }
                createSchedule(job);
            } catch (SchedulerException e) {
                log.error("初始化Quartz定时任务失败",e);
            }
        }

        start();
    }

    /**
     * 启动定时器
     */
    public void start() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("定时任务执行失败", e);
        }
    }

    /**
     * 创建一个定时任务
     *
     * @param job
     * @throws SchedulerException
     */
    public void createSchedule(SysQuartzJob job) throws SchedulerException {
        if (!checkJobExists(job)) {
            //获取指定的job工作类
            Class<? extends Job> jobClass = getQuartzJobClass(job);
            // 通过JobBuilder构建JobDetail实例，JobDetail规定只能是实现Job接口的实例
            // JobDetail 是具体Job实例
            JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(ScheduleConstants.TASK_CLASS_NAME + job.getId(), QuartzConstant.DEFAULT_GROUP).build();
            // 基于表达式构建触发器
            CronScheduleBuilder cronScheduleBuilder = null;
            if (StrUtil.isNotEmpty(job.getCronExpression())) {
                cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            } else {
                cronScheduleBuilder = CronScheduleBuilder.cronSchedule(TEST_CRON);
            }

            // CronTrigger表达式触发器 继承于Trigger  //cronScheduleBuilder.withMisfireHandlingInstructionDoNothing()错过60分钟后不在补偿 拉下的执行次数
            // TriggerBuilder 用于构建触发器实例
            CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(ScheduleConstants.TASK_CLASS_NAME + job.getId(), QuartzConstant.DEFAULT_GROUP).withSchedule(cronScheduleBuilder.withMisfireHandlingInstructionDoNothing()).build();
            //放入参数，运行时的方法可以获取
            jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job);
            scheduler.scheduleJob(jobDetail, cronTrigger);

            //如果这个工作的状态为1
            if (job.getStatus().equals(1)) {
                pauseJob(job);
            }
        }
    }

    /**
     * 修改定时任务
     */
    public boolean modifyJob(SysQuartzJob job) {

        try {
            //先删除
            if (checkJobExists(job)) {
                deleteJob(job);
            }
            createSchedule(job);
        } catch (SchedulerException e) {
            log.error("修改定时任务失败", e);
            return false;
        }
        return true;
    }

    /**
     * 继续执行定时任务
     *
     * @param job
     * @return
     */
    public boolean resumeJob(SysQuartzJob job) {
        boolean bl = false;
        try {
            //JobKey定义了job的名称和组别
            JobKey jobKey = JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + job.getId(), QuartzConstant.DEFAULT_GROUP);
            if (jobKey != null) {
                //继续任务
                scheduler.resumeJob(jobKey);
                bl = true;
            }
        } catch (SchedulerException e) {
            log.error("继续调度任务异常:" + e);
        } catch (Exception e) {
            log.error("继续调度任务异常:" + e);
        }
        return bl;
    }

    /**
     * 删除定时任务
     *
     * @param job
     * @return
     */
    public boolean deleteJob(SysQuartzJob job) {
        boolean bl = false;
        try {
            //JobKey定义了job的名称和组别
            JobKey jobKey = JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + job.getId(), QuartzConstant.DEFAULT_GROUP);
            if (jobKey != null) {
                //删除定时任务
                scheduler.deleteJob(jobKey);
                bl = true;
            }
        } catch (SchedulerException e) {
            log.error("删除调度任务异常:" + e);
        } catch (Exception e) {
            log.error("删除调度任务异常:" + e);
        }
        return bl;
    }

    /**
     * 获取jobKey
     */
    public JobKey getJobKey(SysQuartzJob job) {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + job.getId(), QuartzConstant.DEFAULT_GROUP);
    }

    /**
     * 立即执行任务
     */
    public void run(SysQuartzJob job) throws SchedulerException {
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, job);
        scheduler.triggerJob(getJobKey(job), dataMap);
    }

    /**
     * 暂停任务
     *
     * @param job
     * @return
     */
    public boolean pauseJob(SysQuartzJob job) {
        boolean bl = false;
        try {
            //JobKey定义了job的名称和组别
            JobKey jobKey = JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + job.getId(), QuartzConstant.DEFAULT_GROUP);
            //暂停任务
            if (jobKey != null) {
                scheduler.pauseJob(jobKey);
                bl = true;
            }
        } catch (SchedulerException e) {
            log.error("暂停调度任务异常:" + e);
        } catch (Exception e) {
            log.error("暂停调度任务异常:" + e);
        }
        return bl;
    }

    /**
     * 判断定时任务是否已经存在
     *
     * @param job
     * @return
     * @throws SchedulerException
     */
    public boolean checkJobExists(SysQuartzJob job) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + job.getId(), QuartzConstant.DEFAULT_GROUP);
        return scheduler.checkExists(triggerKey);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void getquartzList() throws SchedulerException {

        List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
        for (String groupName : triggerGroupNames) {
            //组装group的匹配，为了模糊获取所有的triggerKey或者jobKey
            GroupMatcher groupMatcher = GroupMatcher.groupEquals(groupName);
            //获取所有的triggerKey
            Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(groupMatcher);
            for (TriggerKey triggerKey : triggerKeySet) {
                //通过triggerKey在scheduler中获取trigger对象
                CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                //获取trigger拥有的Job
                JobKey jobKey = trigger.getJobKey();
                JobDetailImpl jobDetail2 = (JobDetailImpl) scheduler.getJobDetail(jobKey);
                log.error(groupName);
                log.error(jobDetail2.getName());
                log.error(trigger.getCronExpression());
            }
        }
    }
}
