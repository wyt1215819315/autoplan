package com.github.system.base.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class AjaxResult {

    public Integer code = 200;
    public String msg;
    public Object data;
    public boolean success;

    public AjaxResult() {
    }

    public AjaxResult(Integer code) {
        this.code = code;
    }

    public static AjaxResult status(boolean status) {
        return status ? AjaxResult.doSuccess() : AjaxResult.doError();
    }
    public static AjaxResult doSuccess() {
        return doSuccess("操作成功！");
    }

    public static AjaxResult doSuccess(String msg) {
        AjaxResult ajaxResult = new AjaxResult();
        ajaxResult.setMsg(msg);
        ajaxResult.setSuccess(true);
        return ajaxResult;

    }

    public static AjaxResult doSuccess(Object data) {
        return doSuccess("操作成功！", data);
    }

    public static AjaxResult doSuccess(String msg, Object data) {
        return doSuccess(msg, data, 200);
    }

    public static AjaxResult doSuccess(String msg, Object data, Integer code) {
        AjaxResult ajaxResult = new AjaxResult(code);
        ajaxResult.setMsg(msg);
        ajaxResult.setData(data);
        ajaxResult.setSuccess(true);
        return ajaxResult;
    }

    public static AjaxResult doError() {
        return doError("操作失败！");
    }

    public static AjaxResult doError(String msg) {
        return doError(msg, null);
    }

    public static AjaxResult doError(String msg, Object data) {
        return doError(msg, data, -1);
    }

    public static AjaxResult doError(String msg, Object data, Integer code) {
        AjaxResult ajaxResult = new AjaxResult(code);
        ajaxResult.setMsg(msg);
        ajaxResult.setData(data);
        ajaxResult.setSuccess(false);
        return ajaxResult;
    }


}
