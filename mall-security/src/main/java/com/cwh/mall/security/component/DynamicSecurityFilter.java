package com.cwh.mall.security.component;

import com.cwh.mall.security.config.IgnoreUrlsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.PathMatcher;

/**
 * 权限校验过滤器
 * 在dofilter中调用decide，进行权限校验
 * @author cwh
 * @date 2021/7/13 14:51
 */
public class DynamicSecurityFilter extends AbstractSecurityInterceptor implements Filter {

    @Autowired
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;

    /**
     * 注入自定义metadatasource
     * @return
     */
    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return dynamicSecurityMetadataSource;
    }

    /**
     * 注入我们自定义的accessDecisionManager
     * @autowired 表示该方法在启动时会先运行，并将返回值存放到容器中
     * 这里自动装配会自动在容器中获取AccessDecisionManager的实现类
     * @param accessDecisionManager
     */
    @Autowired
    @Override
    public void setAccessDecisionManager(AccessDecisionManager accessDecisionManager) {
        super.setAccessDecisionManager(accessDecisionManager);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * 核心操作
     * 调用AccessDecisionManager中的decide进行鉴权
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //封装
        FilterInvocation filterInvocation = new FilterInvocation(servletRequest,servletResponse,filterChain);

        //个人觉得SecurityConfig中已经设置放行OPTIONS请求和白名单这边没必要再设置
        //permitAll并非绕过过滤器，而是为其设置一个authentication使得过滤是直接过滤
        //对OPTIONS请求放行
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if(request.getMethod().equals(HttpMethod.OPTIONS.toString())){
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
            return;
        }

        //对白名单放行
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        for(String path : ignoreUrlsConfig.getUrls()){
            if(antPathMatcher.match(path,request.getRequestURI())){
                filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
                return;
            }
        }
        //其余请求调用decide进行鉴权
        InterceptorStatusToken token = super.beforeInvocation(filterInvocation);
        try{
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        }finally {
            super.afterInvocation(token,null);
        }
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }
}
