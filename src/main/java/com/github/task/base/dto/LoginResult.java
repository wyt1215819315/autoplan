package com.github.task.base.dto;

import com.github.task.base.model.BaseUserInfo;
import lombok.Data;

@Data
public class LoginResult<D extends BaseUserInfo> {

    /**
     * 任务是否执行成功
     */
    private boolean success;

    /**
     * 任务输出的信息
     */
    private String msg;

    /**
     * 用户信息返回，底层会将此字段持久化到数据库用于前端回显，请勿往里面塞敏感信息
     */
    private D userInfo;

    public static <D extends BaseUserInfo> LoginResult<D> doError(String msg) {
        LoginResult<D> taskResult = new LoginResult<>();
        taskResult.setSuccess(false);
        taskResult.setMsg(msg);
        return taskResult;
    }

    public static <D extends BaseUserInfo> LoginResult<D> doSuccess(String msg, D data) {
        LoginResult<D> taskResult = new LoginResult<>();
        taskResult.setSuccess(true);
        taskResult.setMsg(msg);
        taskResult.setUserInfo(data);
        return taskResult;
    }

    public static <D extends BaseUserInfo> LoginResult<D> doSuccess(String msg) {
        LoginResult<D> taskResult = new LoginResult<>();
        taskResult.setSuccess(true);
        taskResult.setMsg(msg);
        return taskResult;
    }

}
