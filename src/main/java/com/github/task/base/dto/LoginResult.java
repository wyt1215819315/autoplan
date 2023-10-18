package com.github.task.base.dto;

import com.github.task.base.model.BaseUserInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginResult<R, D extends BaseUserInfo> extends TaskResult<R> {

    /**
     * 用于返回登录之后的用户信息，注意不要将敏感信息放到这个里面去
     */
    private D userInfo;

    public static <R, D extends BaseUserInfo> LoginResult<R, D> doSuccess(String msg, R data, D userInfo) {
        LoginResult<R, D> loginResult = new LoginResult<>();
        loginResult.setSuccess(true);
        loginResult.setMsg(msg);
        loginResult.setData(data);
        loginResult.setUserInfo(userInfo);
        return loginResult;
    }

}
