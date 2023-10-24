package com.github.system.task.dto;

import com.github.system.task.entity.AutoTask;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AutoTaskDto extends AutoTask {

    @ApiModelProperty("用户信息对象")
    private Object userInfo;



}
