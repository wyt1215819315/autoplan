package com.github.system.quartz.vo;

import com.github.system.quartz.entity.SysQuartzJobLog;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysQuartzJobLogVo extends SysQuartzJobLog {

    private List<Long> ids;

}
