package com.system.security;

import com.system.entity.AjaxResult;
import com.system.security.validate.ValidateCodeException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class LoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        PrintWriter out = response.getWriter();

        //判断错误类型
        if (exception.getClass() == BadCredentialsException.class){
            out.write(AjaxResult.toJson(AjaxResult.doError("用户名或密码错误，请检查！")));
        }else if (exception.getClass() == ValidateCodeException.class){
            out.write(AjaxResult.toJson(AjaxResult.doError(exception.getMessage())));
        }else {
            out.write(AjaxResult.toJson(AjaxResult.doError(exception.getMessage())));
        }

        out.flush();
        out.close();
    }

}
