package com.cwh.mall.security.component;

import com.cwh.mall.mbg.mapper.UmsResourceMapper;
import com.cwh.mall.mbg.model.UmsResource;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author cwh
 * @date 2021/7/13 14:49
 */
public class DynamicSecurityMetadataSource implements SecurityMetadataSource {

    @Autowired
    private DynamicSecurityService dynamicSecurityService;

    private static Map<String, ConfigAttribute> configAttributeMap = null;

    /**
     * 加载所有url配置Map
     * @PostConstruct 优先执行,但是晚于autowired
     */
    @PostConstruct
    public void loadDataSource(){
        configAttributeMap = dynamicSecurityService.loadDataSource();
    }
    /**
     * 获取当前请求所需权限信息
     * @param object
     * @return
     * @throws IllegalArgumentException
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        if(configAttributeMap == null){
            loadDataSource();
        }
        List<ConfigAttribute> configAttributes = new ArrayList<>();

        //使用filterInvocation封装httpRequest
        FilterInvocation filterInvocation = (FilterInvocation) object;
        HttpServletRequest request = filterInvocation.getRequest();
        Iterator<String> iterator = configAttributeMap.keySet().iterator();
        //获取当前路径所需资源id
        while(iterator.hasNext()){
            //这里没有为了直接使用源数据库，没有对resource进行修改
            //建议添加请求方式验证，更加细粒化
            //String resourceUrl = iterator.next();
            //String[] url = resourceUrl.split(":");
            //AntPathRequestMatcher ant = new AntPathRequestMatcher(url[1]);
            //if(request.getMethod().equals(url[0]) && ant.matches(request)){
            //    configAttributes.add(configAttributeMap.get(resourceUrl));
            String pattern = iterator.next();
            AntPathRequestMatcher ant = new AntPathRequestMatcher(pattern);
            if(ant.matches(request)){
                configAttributes.add(configAttributeMap.get(pattern));
            }
        }
        return configAttributes;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

}
