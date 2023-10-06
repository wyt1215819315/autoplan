package com.github.system.auth.config;

import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

@Component
public class MySaTokenListener extends SaTokenListenerForSimple {
    /*
     * SaTokenListenerForSimple 对所有事件提供了空实现，通过继承此类，你只需重写一部分方法即可实现一个可用的侦听器。
     */
    /** 每次登录时触发 */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        // 清除角色缓存和用户信息缓存
        StpUtil.getSession().delete("Role_List");
        StpUtil.getSession().delete("User_info");
    }

}