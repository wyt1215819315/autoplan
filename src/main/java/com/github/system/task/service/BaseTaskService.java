package com.github.system.task.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.github.system.task.dto.TaskLog;
import com.github.system.task.annotation.TaskAction;
import com.github.system.task.dto.LoginResult;
import com.github.system.task.dto.TaskInfo;
import com.github.system.task.dto.TaskResult;
import com.github.system.task.model.BaseTaskSettings;
import com.github.system.task.model.BaseUserInfo;

/**
 * 任务service
 * 新建的任务只需要实现这个service
 * T为你的配置bean类，用于传输用户增加的任务配置（通常为用户cookie等）
 * L为登录之后的用户信息，通常为uid，名称等用于前端展示的字段，此部分在添加任务或者任务完成之后会持久化到数据层
 * login方法在用户增加任务时用来校验他的用户状态，所以一般来说都需要实现，除非有特殊按理，那你直接返回success也没事
 * 你要是想把登录和任务都写在run里面也没事
 * 总之这只是一个规范 至于你想怎么实现随你
 * 如果要在内部使用@Autowire等spring有关的东西，需要在实现类上加上@Compoent注解注册到spring容器即可
 */
public abstract class BaseTaskService<T extends BaseTaskSettings, L extends BaseUserInfo> {

    protected T taskSettings;
    protected TaskLog log;

    public void setThing(String taskSettingsJsonString, TaskLog taskLog) {
        this.log = taskLog;
        this.taskSettings = JSONUtil.toBean(taskSettingsJsonString, new TypeReference<>() {
        }, false);
    }

    /**
     * 任务名称，等信息，会自动注册到任务列表中
     */
    public abstract TaskInfo getName();

    /**
     * 初始化操作，底层会在新建对象之后确保调用一次初始化方法，此方法仅会调用一次
     * 可以将一些变量什么写到这边来初始化，如果有需要登录的，也可以将登录放到这里执行，后面的方法都可以拿到局部变量中的凭证
     */
    public abstract void init() throws Exception;

    /**
     * 仅用于在添加任务时，检查用户信息是否有效
     * 你可以在这个方法中把用户信息给返回了，这样底层就不会调用getUserInfo方法去再获取了
     */
    public abstract LoginResult<L> checkUser() throws Exception;

    /**
     * 获取用户信息
     * 请勿将敏感信息丢到这里来，比如cookie之类的，这里返回的信息会用于前端展示（当然也有脱敏选项）
     */
    public abstract L getUserInfo() throws Exception;

    /**
     * 执行主方法，当然也可以自行加上注解拓展@TaskAction来拓展实现的子任务，方法名自己定
     * 无参方法，返回结果为TaskResult
     */
    @TaskAction(name = "主任务")
    public abstract TaskResult run() throws Exception;

}
