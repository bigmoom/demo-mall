package com.cwh.mall.security.config;

import com.cwh.mall.security.component.LoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * spring security的相关配置
 *
 * @author cwh
 * @date 2021/7/12 10:14
 */
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //放行白名单，不进行认证校验操作
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    //加密
    @Autowired
    private PasswordEncoder passwordEncoder;
    //自定义UserDetailsService
    @Autowired
    private UserDetailsService userDetailsService;
    //登录认证过滤器
    @Autowired
    private LoginFilter loginFilter;
    /**
     * 配置http请求相关策略
     * @param httpSecurity
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = httpSecurity
                .authorizeRequests();
        //放行白名单
        for(String url : ignoreUrlsConfig.getUrls()){
            registry.antMatchers(url).permitAll();
        }
        //允许跨域请求的OPTTIONS请求
        registry.antMatchers(HttpMethod.OPTIONS).
                permitAll()
                .and()
                //开启跨域
                .cors();
        //其他请求都需要身份认证
        registry.and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                //关闭csrf
                .csrf()
                .disable()
                //不使用session
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //认证失败处理器
                .exceptionHandling()
                .accessDeniedHandler()
                .authenticationEntryPoint()
                //登录认证过滤器
                .and()
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);

    }

    /**
     * 配置AuthenticationManager
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //添加自定义userDetailService和passEncoder
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    /**
     * 自定义编码器
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

}
