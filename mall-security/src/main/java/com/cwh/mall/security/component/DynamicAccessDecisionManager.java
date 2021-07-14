package com.cwh.mall.security.component;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * 决策管理器，通过实现decide方法判断用户是否有访问该资源的权限
 * @author cwh
 * @date 2021/7/13 14:50
 */
@Component
public class DynamicAccessDecisionManager implements AccessDecisionManager {
    /**
     * 通过比较authentication中用户拥有的资源与当前访问所需资源进行比较
     * 若用户拥有资源则放行
     * @param authentication
     * @param object
     * @param configAttributes
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        //configAttributes为空说明还没配置权限，直接放行
        if(configAttributes.isEmpty()){
            return ;
        }

        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()){
            ConfigAttribute configAttribute = iterator.next();
            //对于每个可以访问该资源的请求，与用户所拥有权限进行比较，一旦匹配则说明用户可以访问该资源
            for(GrantedAuthority grantedAuthority : authentication.getAuthorities()){
                if(Objects.equals(grantedAuthority.getAuthority(),configAttribute.getAttribute())){
                    return;
                }
            }
        }
        //匹配失败抛出禁止访问异常
        throw new AccessDeniedException("ACCESS DENIED, YOU DON NOT HAVE THE PERMISSION");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return false;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }
}
