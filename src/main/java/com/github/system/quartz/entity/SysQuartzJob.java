package com.github.system.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@TableName("t_sys_quartz_job")
@Data
@ApiModel("定时任务调度表")
public class SysQuartzJob implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     **/
    @ApiModelProperty("id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务名称
     **/
    @ApiModelProperty("任务名称")
    private String jobName;

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
     * 是否并发执行（0允许 1禁止）
     **/
    @ApiModelProperty("是否并发执行（0允许 1禁止）")
    private Integer concurrent;

    /**
     * 任务执行超时 单位秒，0为永不超时
     **/
    @ApiModelProperty("任务执行超时，单位秒，0为永不超时")
    private Integer timeout;

    /**
     * 任务状态（0正常 1暂停）
     **/
    @ApiModelProperty("任务状态（0正常 1暂停）")
    private Integer status;

    public SysQuartzJob() {
        super();
    }


    public SysQuartzJob(String jobName, String invokeTarget, String cronExpression, Integer concurrent, Integer status) {
        this.jobName = jobName;
        this.invokeTarget = invokeTarget;
        this.cronExpression = cronExpression;
        this.concurrent = concurrent;
        this.status = status;
        this.timeout = 86400;
    }

}