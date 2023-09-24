package com.system.security.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.system.security.entity.SystemUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;


/**
 * session工具类
 */
public class SessionUtils {

    public static String getSessionId() {
        HttpServletRequest request = RequestUtils.getCurrentRequest();
        if (request == null)
            return null;
        HttpSession session = request.getSession(false);
        if (session != null)
            return session.getId();
        return null;
    }

    public static ServletWebRequest getServletWebRequest() {
        return new ServletWebRequest(RequestUtils.getCurrentRequest());
    }

    public static void setAttribute(RequestAttributes request, String name, Object value) {
        request.setAttribute(name, value, RequestAttributes.SCOPE_SESSION);
    }

    public static Object getAttribute(RequestAttributes request, String name) {
        return request.getAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    public static void removeAttribute(RequestAttributes request, String name) {
        request.removeAttribute(name, RequestAttributes.SCOPE_SESSION);
    }

    public static Object getAttribute(String name) {
        return getAttribute(getServletWebRequest(), name);
    }

    public static void setAttribute(String name, Object value) {
        setAttribute(getServletWebRequest(), name, value);
    }

    public static void removeAttribute(String name) {
        removeAttribute(getServletWebRequest(), name);
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static SecurityContext getContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * 当前登录用户：用户信息, 类型为抽象实体AbstractUser
     *
     * @return
     */
    public static SystemUser getPrincipal() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }

        Object user = authentication.getPrincipal();
        if (user instanceof SystemUser) {
            return (SystemUser) user;
        }
        return null;
    }

    /**
     * 当前登录用户：用户信息  类型为Object 自行转换为具体实体
     *
     * @return
     */
    public static Object getSystemPrincipal() {
        Authentication authentication = getAuthentication();
        if (authentication == null)
            return null;
        Object user = authentication.getPrincipal();
        if (user instanceof SystemUser) {
            return user;
        }
        return null;
    }

}
