package com.github.system.task.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("log_hi_task")
@ApiModel("历史任务日志")
public class HistoryTaskLog implements Serializable {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("任务id")
    private Integer autoId;

    @ApiModelProperty("任务类型")
    private String type;

    @ApiModelProperty("任务状态")
    private String status;

    @ApiModelProperty("任务所属的用户id")
    private Integer userid;

    @ApiModelProperty("记录时间")
    private Date date;

    @ApiModelProperty("日志内容")
    private String text;


    public HistoryTaskLog(Integer autoId, String type, String status, Integer userid, Date date, String text) {
        this.autoId = autoId;
        this.type = type;
        this.status = status;
        this.userid = userid;
        this.date = date;
        this.text = text;
    }

    public HistoryTaskLog() {
    }

}