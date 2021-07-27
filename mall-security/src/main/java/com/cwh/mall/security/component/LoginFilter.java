package com.cwh.mall.security.component;

import com.cwh.mall.common.domain.bo.ResultCode;
import com.cwh.mall.common.domain.vo.ResultVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import redis.clients.jedis.exceptions.JedisConnectionException;

import javax.security.sasl.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
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
        log.info("启动登录验证");
        //获取authorization
        String authorization = request.getHeader(this.tokenHeader);
        //验证是否为null 以及token 类型
        //authorization格式 Bear eyJhbG....
        if(authorization != null && authorization.startsWith(this.tokenHead)){
            //切分token
            String token = authorization.substring(this.tokenHead.length()+1);
            //解析token
            Claims claims = jwtManager.parse(token);
            if(claims != null && SecurityContextHolder.getContext().getAuthentication() == null){
                String userName = claims.getSubject();
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                log.info("checking token success && userName:{}",userName);
                //密码不明文放在上下文中，所以设置为Null
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                //设置authentication
                SecurityContextHolder.getContext().setAuthentication(authentication);
                //添加判断，refreshtoken是否在redis中,以实现登出是使jwt无效
                String key = "refreshToken:" + userName;
                String refreshToken = redisTemplate.opsForValue().get(key);
                if(refreshToken == null){
                    throw new AccountExpiredException("token已过期，请重新登录");
                }
            }
        }
        filterChain.doFilter(request,response);
    }

}
