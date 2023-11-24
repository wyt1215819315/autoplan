package com.github.system.base.dto.r;


public interface IErrorCode {

    /**
     * 错误编码 -1、失败 0、成功
     */
    long getCode();

    /**
     * 错误描述
     */
    String getMsg();

    Boolean getSuccess();
}