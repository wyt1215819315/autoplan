package com.github.task.base.service;

import com.github.task.base.annotation.TaskAction;
import com.github.task.base.dto.LoginResult;
import com.github.task.base.dto.TaskInfo;
import com.github.task.base.dto.TaskLog;
import com.github.task.base.dto.TaskResult;
import com.github.task.base.model.BaseTaskSettings;
import com.github.task.base.model.BaseUserInfo;

/**
 * 任务service
 * 新建的任务只需要实现这个service
 * T为你的配置bean类，用于传输用户增加的任务配置（通常为用户cookie等）
 * R为登录的结果集实体类，如果你有多个子任务的话，R一般就用来传输登录之后的凭证等，具体你们自己看着来
 * L为登录之后的用户信息，通常为uid，名称等用于前端展示的字段，此部分在添加任务或者任务完成之后会持久化到数据层
 * login方法在用户增加任务时用来校验他的用户状态，所以一般来说都需要实现，除非有特殊按理，那你直接返回success也没事
 * 你要是想把登录和任务都写在run里面也没事
 * 总之这只是一个规范 至于你想怎么实现随你
 */
public interface BaseTaskService<T extends BaseTaskSettings, R, L extends BaseUserInfo> {

    /**
     * 任务名称
     */
    TaskInfo getName();

    /**
     * 校验登录结果
     */
    LoginResult<R, L> login(T taskSettings, TaskLog log) throws Exception;

    /**
     * 执行主方法，当然也可以自行加上注解拓展@TaskAction来拓展实现的子任务，方法名自己定
     */
    @TaskAction(name = "主任务")
    TaskResult<R> run(T taskSettings, TaskLog log) throws Exception;

}
