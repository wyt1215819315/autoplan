package com.github.task.base.model;

import com.github.task.base.annotation.DataDesensitization;
import lombok.Data;

/**
 * 用户信息展示基类
 * BaseTaskService里的L类型
 * 需要继承这个
 */
@Data
public class BaseUserInfo {

    /**
     * 需要为一个能代表用户信息的唯一id，底层会拿这个来判断是否任务已经存在了
     */
    @DataDesensitization
    private String onlyId;

}
