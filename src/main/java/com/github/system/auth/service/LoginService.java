package com.github.system.auth.service;

import com.github.system.auth.vo.LoginModel;
import com.github.system.base.dto.AjaxResult;

public interface LoginService {


    AjaxResult me();


    AjaxResult formLogin(LoginModel loginModel);
}
