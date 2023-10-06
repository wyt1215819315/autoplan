package com.github.system.quartz.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@TableName("t_sys_quartz_job")
@Data
@ApiModel("定时任务调度表")
public class SysQuartzJob implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     **/
    @ApiModelProperty("日志id")
    private Long id;

    /**
     * 任务名称
     **/
    @ApiModelProperty("任务名称")
    private String jobName;

    /**
     * 任务组名
     **/
    @ApiModelProperty("任务组名")
    private String jobGroup;

    /**
     * 调用目标字符串
     **/
    @ApiModelProperty("调用目标字符串")
    private String invokeTarget;

    /**
     * cron执行表达式
     **/
    @ApiModelProperty("cron执行表达式")
    private String cronExpression;

    /**
     * cron计划策略
     **/
    @ApiModelProperty("cron计划策略")
    private String misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止）
     **/
    @ApiModelProperty("是否并发执行（0允许 1禁止）")
    private String concurrent;

    /**
     * 任务执行超时 单位秒，0为永不超时
     **/
    @ApiModelProperty("任务执行超时，单位秒，0为永不超时")
    private Integer timeout = 0;

    /**
     * 任务状态（0正常 1暂停）
     **/
    @ApiModelProperty("任务状态（0正常 1暂停）")
    private Integer status;

    public SysQuartzJob() {
        super();
    }


    public SysQuartzJob(Long id, String jobName, String jobGroup, String invokeTarget, String cronExpression, String misfirePolicy, String concurrent, Integer status) {
        this.id = id;
        this.jobName = jobName;
        this.jobGroup = jobGroup;
        this.invokeTarget = invokeTarget;
        this.cronExpression = cronExpression;
        this.misfirePolicy = misfirePolicy;
        this.concurrent = concurrent;
        this.status = status;

    }

}