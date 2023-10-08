package com.github.system.auth.service;

import com.github.system.auth.vo.LoginModel;
import com.github.system.base.dto.AjaxResult;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface LoginService {


    AjaxResult me();


    AjaxResult formLogin(LoginModel loginModel);

    void getValidCode(HttpServletResponse response) throws IOException;
}
