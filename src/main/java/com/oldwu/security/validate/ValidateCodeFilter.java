package com.oldwu.security.validate;


import com.oldwu.security.LoginAuthenticationFailureHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: zy
 * @Description: 图片验证码过滤器，用于对图片验证码进行校验
 * @Date: 2020-2-9
 */
@Service
public class ValidateCodeFilter extends OncePerRequestFilter {

    @Autowired
    private LoginAuthenticationFailureHandler authenticationFailureHandler;

    //使用sessionStrategy将生成的验证码对象存储到Session中
    private final SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    /**
     * 如果请求是/login、对图片验证码进行校验
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //判断请求页面是否为/login、该路径对应登录form表单的action路径，请求的方法是否为POST，是的话进行验证码校验逻辑，否则直接执行filterChain.doFilter让代码往下走
        String requestURI = httpServletRequest.getRequestURI();
        //拦截登录和注册api请求
        String[] validateUrl = {"/loginProcessing", "/reg"};
        boolean b = StringUtils.equalsAnyIgnoreCase(requestURI, validateUrl);
        //只对post提交类型做出处理
        boolean post = StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "post");
        if (b && post) {
            try {
                //校验验证码 校验通过、继续向下执行   验证失败、抛出异常
                validateCode(new ServletWebRequest(httpServletRequest));
            } catch (ValidateCodeException e) {
                //校验失败 返回错误状态码及信息
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * 对图片验证码进行校验
     *
     * @param servletWebRequest：请求参数 包含表单提交的图片验证码信息
     * @throws ServletRequestBindingException
     * @throws ValidateCodeException:         验证码校验失败 抛出异常
     */
    private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException, ValidateCodeException {
        //从Session获取保存在服务器端的验证码
        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(servletWebRequest, ValidateCodeController.SESSION_KEY_IMAGE_CODE);

        //获取表单提交的图片验证码
        String codeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "imageCode");

        //验证码空校验
        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码不能为空！");
        }

        //验证码校验
        if (codeInSession == null) {
            throw new ValidateCodeException("验证码不存在，请重新发送！");
        }

        //验证码过期校验
        if (codeInSession.isExpire()) {
            sessionStrategy.removeAttribute(servletWebRequest, ValidateCodeController.SESSION_KEY_IMAGE_CODE);
            throw new ValidateCodeException("验证码已过期！");
        }

        //判断是否相等
        if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不正确！");
        }

        //从Session移除该字段信息
        sessionStrategy.removeAttribute(servletWebRequest, ValidateCodeController.SESSION_KEY_IMAGE_CODE);
    }
}
