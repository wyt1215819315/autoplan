package com.github.system.task.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("自动任务列表")
@TableName("auto_index")
@Data
public class AutoIndex {

    @ApiModelProperty("主键id")
    private Long id;

    @ApiModelProperty("是否开启，0不开启 1开启")
    private Integer enable;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("代号")
    private String code;

    @ApiModelProperty("前端渲染的图标")
    private String icon;

    @ApiModelProperty("任务延时（通常用于控制多用户任务执行等待时间，防止风控），单位秒")
    private Integer delay;

    @ApiModelProperty("任务超时，当单个任务大于设定时间时回终止任务，单位秒")
    private Integer timeout;

    @ApiModelProperty("任务同时运行的并发数量，为了防止风控一般都是1")
    private Integer threadNum;

    public AutoIndex() {
    }

    public AutoIndex(Integer enable, String name, String code, Integer delay) {
        this.enable = enable;
        this.name = name;
        this.code = code;
        this.delay = delay;
    }

    public AutoIndex(Integer enable, String name, String code, Integer delay, Integer threadNum) {
        this.enable = enable;
        this.name = name;
        this.code = code;
        this.delay = delay;
        this.threadNum = threadNum;
    }

    public AutoIndex(Integer enable, String name, String code, Integer delay, Integer threadNum, Integer timeout) {
        this.enable = enable;
        this.name = name;
        this.code = code;
        this.delay = delay;
        this.threadNum = threadNum;
        this.timeout = timeout;
    }
}
