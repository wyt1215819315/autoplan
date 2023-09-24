package com.system.security.validate;

import org.springframework.security.core.AuthenticationException;


public class ValidateCodeException extends AuthenticationException {
    private static final long serialVersionUID = 5022575393500654458L;

    /**
     * 构造函数
     * @param message
     */
    public ValidateCodeException(String message) {
        super(message);
    }
}