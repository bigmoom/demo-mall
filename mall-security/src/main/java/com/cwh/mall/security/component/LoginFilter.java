package com.cwh.mall.security.component;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author cwh
 * @date 2021/7/12 16:44
 */
@Slf4j
@Component
public class LoginFilter extends OncePerRequestFilter {

    @Autowired
    private JWTManager jwtManager;
    @Value("${security.jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${security.jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 校验是否已登录
     * token为null或者解析失败 标明未登录
     * token解析成功则为登录,设置authentication放到上下文中
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取authorization
        String authorization = request.getHeader(this.tokenHeader);
        //验证是否为null 以及token 类型
        //authorization格式 Bear eyJhbG....
        if(authorization != null && authorization.startsWith(this.tokenHead)){
            //切分token
            String token = authorization.substring(this.tokenHead.length());
            //解析token
            Claims claims = jwtManager.parse(token);
            if(claims != null && SecurityContextHolder.getContext().getAuthentication() == null){
                String userName = claims.getSubject();
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                log.info("checking token success && userName:{}",userName);
                //判断token是否过期
                if(!jwtManager.isTokenExpired(token)){
                    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,userDetails.getPassword(),userDetails.getAuthorities());
                    //设置authentication
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
    }
}
