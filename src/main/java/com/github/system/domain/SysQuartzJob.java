package com.github.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.gitee.sunchenbin.mybatis.actable.annotation.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 定时任务调度表 SysQuartzJob 
 * @author fuce_自动生成
 * @email 115889198@qq.com
 * @date 2019-09-13 00:03:35
 */
@TableName("t_sys_quartz_job")
@Data
public class SysQuartzJob implements Serializable {

	private static final long serialVersionUID = 1L;
	
		
	/** id **/
    @IsKey
    @ColumnComment("日志id")
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
		
	/** cron执行表达式 **/
    @Column(comment = "cron执行表达式")
	private String cronExpression;
		
	/** cron计划策略 **/
    @Column(comment = "cron计划策略")
	private String misfirePolicy;
		
	/** 是否并发执行（0允许 1禁止） **/
    @Column(comment = "是否并发执行（0允许 1禁止）")
	private String concurrent;
		
	/** 任务状态（0正常 1暂停） **/
    @Column(comment = "任务状态（0正常 1暂停）")
    @IgnoreUpdate
	private Integer status;
			
	public SysQuartzJob() {
        super();
    }
    
																																										
	public SysQuartzJob(String id,String jobName,String jobGroup,String invokeTarget,String cronExpression,String misfirePolicy,String concurrent,Integer status) {
	
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