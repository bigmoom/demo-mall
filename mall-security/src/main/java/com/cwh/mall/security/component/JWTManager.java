package com.cwh.mall.security.component;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT的相关操作
 * 包括解密，生成，判断过期等等
 *
 * @author cwh
 * @date 2021/7/12 10:15
 */
@Slf4j
@Component
public class JWTManager {

    @Value("${security.jwt.securityKey}")
    private String securityKey ;
    @Value("${security.jwt.expiration}")
    private Long expiration;
    @Value("${security.jwt.tokenHead}")
    private String tokenHead;

    /**
     * 根据用户名和签发时间等信息生成token
     * @param name
     * @return
     */
    public String generateToken(String name){
        //设置过期时间
        Date expireDate = new Date(System.currentTimeMillis()+expiration*1000);
        return Jwts.builder()
                //设置 sub: 用户名
                .setSubject(name)
                //设置签发时间 iat: new Date()
                .setIssuedAt(new Date())
                //设置过期时间 exp: expireDate
                .setExpiration(expireDate)
                //设置加密算法和秘钥
                .signWith(SignatureAlgorithm.HS512, securityKey)
                .compact();
    }

    /**
     * 解密
     * 成功返回claims对象
     * 失败返回null
     * @param token
     * @return
     */
    public Claims parse(String token){
        //空字符返回null
        if(token.isEmpty()){
            return null;
        }
        //非空则进行解析
        Claims claims = null;
        try{
            claims = Jwts.parser()
                    //设置秘钥
                    .setSigningKey(securityKey)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (JwtException e){
            log.info("JWT格式解析失败：{}",token);
        }
        return claims;
    }

    /**
     * 验证token是否过期
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token){
        Date expireDate = this.parse(token).getExpiration();
        return expireDate.before(new Date());
    }
}
