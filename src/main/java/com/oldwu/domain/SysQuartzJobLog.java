package com.oldwu.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gitee.sunchenbin.mybatis.actable.annotation.Column;
import com.gitee.sunchenbin.mybatis.actable.annotation.ColumnComment;
import com.gitee.sunchenbin.mybatis.actable.annotation.IgnoreUpdate;
import com.gitee.sunchenbin.mybatis.actable.annotation.IsKey;
import com.gitee.sunchenbin.mybatis.actable.constants.MySqlTypeConstant;
import com.oldwu.util.DateUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务调度日志表 SysQuartzJobLog
 * @author fuce_自动生成
 * @email 115889198@qq.com
 * @date 2019-09-13 00:03:42
 */
@TableName("t_sys_quartz_job_log")
@Data
public class SysQuartzJobLog implements Serializable {

	private static final long serialVersionUID = 1L;


	/** 主键 **/
    @IsKey
    @ColumnComment("主键")
	private String id;

	/** 任务名称 **/
    @Column(comment = "任务名称")
	private String jobName;

	/** 任务组名 **/
    @Column(comment = "任务组名")
	private String jobGroup;

	/** 调用目标字符串 **/
    @Column(comment = "调用目标字符串")
	private String invokeTarget;

	/** 日志信息 **/
    @Column(comment = "日志信息")
	private String jobMessage;

	/** 执行状态（0正常 1失败） **/
    @Column(comment = "执行状态（0正常 1失败）")
    @IgnoreUpdate
	private Integer status;

	/** 异常信息 **/
    @Column(comment = "异常信息")
	private String exceptionInfo;

	/** 开始时间 **/
    @Column(type = MySqlTypeConstant.DATETIME, comment = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /** 结束时间 **/
    @Column(type = MySqlTypeConstant.DATETIME, comment = "结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private Date endTime;

	public SysQuartzJobLog() {
        super();
    }


	public SysQuartzJobLog(String id,String jobName,String jobGroup,String invokeTarget,String jobMessage,Integer status,String exceptionInfo,Date startTime,Date endTime) {

		this.id = id;
		this.jobName = jobName;
		this.jobGroup = jobGroup;
		this.invokeTarget = invokeTarget;
		this.jobMessage = jobMessage;
		this.status = status;
		this.exceptionInfo = exceptionInfo;
		this.startTime = startTime;
		this.endTime = endTime;

	}

	/**
     * 格式化时间
     * @return yyyy-MM-dd HH:mm:ss
     */
	public String getdate(Date date){
        return  DateUtils.parseDateToStr(DateUtils.DATE_TIME_PATTERN,date);
    }

}