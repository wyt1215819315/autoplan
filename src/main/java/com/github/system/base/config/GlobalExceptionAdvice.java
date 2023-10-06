package com.github.system.base.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.hutool.core.util.ReUtil;
import com.github.system.base.dto.AjaxResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionAdvice {


    @ExceptionHandler(Exception.class)
    public AjaxResult ExceptionHandler(Exception ex) {
        if (ex.getMessage() != null && ReUtil.contains("[\u4e00-\u9fa5]", ex.getMessage())) {
            return AjaxResult.doError(ex.getMessage(), "", 9);
        } else if (ex.getCause() != null && ex.getCause().getMessage() != null && ReUtil.contains("[\u4e00-\u9fa5]", ex.getCause().getMessage())) {
            return AjaxResult.doError(ex.getCause().getMessage(), "", 9);
        } else {
            ex.printStackTrace();
        }
        return AjaxResult.doError(ex.getMessage(), "", 9);
    }

    @ExceptionHandler
    public AjaxResult handlerException(NotLoginException e) {
        return AjaxResult.doError("", "鉴权失败！", 7);
    }

    @ExceptionHandler
    public AjaxResult handlerException(NotPermissionException e) {
        return AjaxResult.doError("", "无权限访问！" + e.getPermission(), 8);
    }

    @ExceptionHandler
    public AjaxResult handlerException(NotRoleException e) {
        return AjaxResult.doError("", "角色认证失败！" + e.getRole(), 8);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult handleBindException(BindException exception) {
        List<FieldError> allErrors = exception.getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError errorMessage : allErrors) {
            sb.append(errorMessage.getField()).append(": ").append(errorMessage.getDefaultMessage()).append(", ");
        }
        return AjaxResult.doError("参数校验失败：" + sb);
    }


}
