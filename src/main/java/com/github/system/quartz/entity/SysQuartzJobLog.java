package com.github.system.quartz.entity;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("t_sys_quartz_job_log")
@Data
@ApiModel("定时任务调度日志表")
public class SysQuartzJobLog implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 主键
     **/
    @ApiModelProperty("主键id")
    @TableId(type = IdType.AUTO)
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
     * 日志信息
     **/
    @ApiModelProperty("日志信息")
    private String jobMessage;

    /**
     * 执行状态（0正常 1失败 2超时）
     **/
    @ApiModelProperty("执行状态（0正常 1失败 2超时）")
    private Integer status;

    /**
     * 异常信息
     **/
    @ApiModelProperty("异常信息")
    private String exceptionInfo;

    /**
     * 开始时间
     **/
    @ApiModelProperty("开始时间")
    private Date startTime;

    /**
     * 结束时间
     **/
    @ApiModelProperty("结束时间")
    private Date endTime;

    public SysQuartzJobLog() {
        super();
    }


    public SysQuartzJobLog(Long id, String jobName, String invokeTarget, String jobMessage, Integer status, String exceptionInfo, Date startTime, Date endTime) {
        this.id = id;
        this.jobName = jobName;
        this.invokeTarget = invokeTarget;
        this.jobMessage = jobMessage;
        this.status = status;
        this.exceptionInfo = exceptionInfo;
        this.startTime = startTime;
        this.endTime = endTime;

    }

    /**
     * 格式化时间
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public String getdate(Date date) {
        return DateUtil.formatDateTime(date);
    }

}