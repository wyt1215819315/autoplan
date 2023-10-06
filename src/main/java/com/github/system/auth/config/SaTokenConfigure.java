package com.github.system.auth.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.models.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

/**
 * 注册 Sa-Token 路由拦截器
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    @Autowired
    private SystemAuthConfig systemAuthConfig;

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 添加排除url，当然也可以使用注解@SaIgnore加在controller上以达到不拦截的目的
            SaRouter.match(systemAuthConfig.getExcludeUrl()).stop();
            // 登录拦截器，默认放行登录接口
            SaRouter.match("/**").check(r -> StpUtil.checkLogin());
            // 角色校验 -- 拦截以 admin 开头的路由，必须具备 admin 角色或者 super-admin 角色才可以通过认证
//            SaRouter.match("/admin/**", r -> StpUtil.checkRoleOr("ADMIN", "admin", "super-admin"));
        })).addPathPatterns("/**");
    }

//    /**
//     * 跨域规则配置
//     */
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        // 设置访问源地址
//        config.addAllowedOriginPattern("*");
//        // 设置访问源请求头
//        config.addAllowedHeader("*");
//        // 设置访问源请求方法
//        config.addAllowedMethod("*");
//        // 有效期 1800秒
//        config.setMaxAge(1800L);
//        // 添加映射路径，拦截一切请求
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        // 返回新的CorsFilter
//        return new CorsFilter(source);
//    }

    /**
     * 注册 [Sa-Token全局过滤器]
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .setBeforeAuth(r -> { // 前置函数，在认证函数每次执行前执行
                    // 获得客户端domain
                    SaRequest request = SaHolder.getRequest();
                    String origin = request.getHeader("Origin");
                    if (origin == null) {
                        origin = request.getHeader("Referer");
                    }
                    // 设置一些安全响应头之类的玩意
                    SaHolder.getResponse()
//                            .setHeader("Access-Control-Allow-Origin", "*")
//                            .setHeader("Access-Control-Allow-Methods", "*")
                            .setHeader("Access-Control-Max-Age", "3600")
//                            .setHeader("Access-Control-Allow-Headers", "*")
                            // 允许第三方 Cookie
                            .setHeader("Access-Control-Allow-Credentials", "true")
                            // 允许指定域访问跨域资源
                            .setHeader("Access-Control-Allow-Origin", origin)
                            // 允许所有请求方式
                            .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS")
                            // 允许的header参数
                            .setHeader("Access-Control-Allow-Headers", "Content-Type,XFILENAME,XFILECATEGORY,XFILESIZE,x-requested-with,token,x-token,auto_plan")
                            // 有效时间
                            .setHeader("Access-Control-Max-Age", "3600")
                            .setServer("Zeus");
                    // 跳过对 OPTIONS 请求的检查，否则这里会鉴权失败，导致 springboot 配置的 addCorsMappings 跨域不执行
                    if (SaHolder.getRequest().getMethod().equals(HttpMethod.OPTIONS.toString())) {
                        SaRouter.back();
                    }
                });
    }
}
