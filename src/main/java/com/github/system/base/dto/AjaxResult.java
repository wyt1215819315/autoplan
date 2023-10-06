package com.github.system.base.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AjaxResult {

    public Integer code = 200;
    public String msg;
    public Object data;


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public AjaxResult() {
    }

    public AjaxResult(Integer code) {
        this.code = code;
    }

    public static AjaxResult doSuccess() {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMsg("操作成功！");
        return ajaxResult;
    }

    public static AjaxResult doSuccess(String msg) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMsg(msg);
        return ajaxResult;
    }

    public static AjaxResult doSuccess(String msg, Object data) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMsg(msg);
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static AjaxResult doSuccess(Object data) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMsg("操作成功！");
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static AjaxResult doSuccess(String msg, Object data, Integer code) {
        AjaxResult ajaxResult = new AjaxResult(code);
        ajaxResult.setMsg(msg);
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static AjaxResult doError() {
        AjaxResult ajaxResult = new AjaxResult(-1);
        ajaxResult.setMsg("操作失败！");
        return ajaxResult;
    }

    public static AjaxResult doError(String msg) {
        AjaxResult ajaxResult = new AjaxResult(-1);
        ajaxResult.setMsg(msg);
        return ajaxResult;
    }

    public static AjaxResult doError(String msg, Object data) {
        AjaxResult ajaxResult = new AjaxResult(-1);
        ajaxResult.setMsg(msg);
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static AjaxResult doError(String msg, Object data, Integer code) {
        AjaxResult ajaxResult = new AjaxResult(code);
        ajaxResult.setMsg(msg);
        ajaxResult.setData(data);
        return ajaxResult;
    }


}
