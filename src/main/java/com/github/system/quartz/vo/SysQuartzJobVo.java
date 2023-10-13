package com.github.system.quartz.vo;

import com.github.system.quartz.entity.SysQuartzJob;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysQuartzJobVo extends SysQuartzJob {

    @NotBlank
    private String jobName;

    @NotBlank(message = "调用目标字符串不能为空！")
    private String invokeTarget;

    private List<Long> ids;
}
