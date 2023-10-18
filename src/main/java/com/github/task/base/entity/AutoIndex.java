package com.github.task.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("自动任务列表")
@TableName("auto_index")
@Data
public class AutoIndex {

    @ApiModelProperty("主键id")
    private Integer id;

    @ApiModelProperty("是否开启，0不开启 1开启")
    private Integer enable;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("代号")
    private String code;

    @ApiModelProperty("任务延时（通常用于控制多用户任务执行等待时间，防止风控）")
    private Integer delay;

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
}
