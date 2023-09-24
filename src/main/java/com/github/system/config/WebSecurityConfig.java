package com.github.system.config;

import com.github.system.security.LoginAuthenticationFailureHandler;
import com.github.system.security.LoginAuthenticationSuccessHandler;
import com.github.system.security.validate.ValidateCodeFilter;
import com.github.system.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by yangyibo on 17/1/18.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private LoginAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private LoginAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    @Autowired
    private UserDetailsService customUserService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService).passwordEncoder(new PasswordEncoder() {

            @Override
            public String encode(CharSequence rawPassword) {
                return MD5Util.encode((String) rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(MD5Util.encode((String) rawPassword));
            }
        }); //user Details Service验证
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return hierarchy;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class) // 添加验证码校验过滤器
                .authorizeRequests()
                .antMatchers("/code/image").permitAll()
                .antMatchers("/static/**").permitAll()
                .antMatchers("/api/reg").permitAll()
                .antMatchers("/reg").permitAll()
                .antMatchers("/welcomePage").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/webhook-generate").permitAll()
                .antMatchers("/bili/index").permitAll()
                .antMatchers("/netmusic/index").permitAll()
                .antMatchers("/mihuyou/index").permitAll()
                .antMatchers("/xiaomi/index").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                //列表api请求
                .antMatchers("/api/index/**").permitAll()
                .antMatchers("/api/user/me").permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/user/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                //使用ajax方式返回登录结果
                .loginPage("/login")
//                .failureUrl("/login?error")
                .failureHandler(authenticationFailureHandler)
                .successHandler(authenticationSuccessHandler)
                .loginProcessingUrl("/loginProcessing")
                .usernameParameter("username").passwordParameter("password").permitAll()
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

}

