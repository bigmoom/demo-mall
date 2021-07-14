package com.cwh.mall.config;

import com.cwh.mall.mbg.model.UmsResource;
import com.cwh.mall.security.component.DynamicSecurityService;
import com.cwh.mall.service.UmsAdminService;
import com.cwh.mall.security.config.SecurityConfig;
import com.cwh.mall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cwh
 * @date 2021/7/13 10:57
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AdminSecurityConfig extends SecurityConfig {

    @Autowired
    private UmsAdminService umsAdminService;

    @Autowired
    private UmsResourceService umsResourceService;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> umsAdminService.loadUserByUsername(username);
    }

    /**
     * 获取所有的url以及对应配置
     * @return
     */
    @Bean
    public DynamicSecurityService dynamicSecurityService(){
        return ()->{
            Map<String, ConfigAttribute> urlAttribute = new ConcurrentHashMap<>();
            List<UmsResource> resourceList = umsResourceService.listAll();
            for(UmsResource resource : resourceList){
                urlAttribute.put(resource.getUrl(),new org.springframework.security.access.SecurityConfig(resource.getId() + ":" + resource.getName()));
            }
            return urlAttribute;
        };
    }



}
