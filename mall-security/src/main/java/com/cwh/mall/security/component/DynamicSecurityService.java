package com.cwh.mall.security.component;

import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

/**
 * @author cwh
 * @date 2021/7/13 14:50
 */
public interface DynamicSecurityService {
    /**
     * 加载
     * @return
     */
    Map<String, ConfigAttribute> loadDataSource();
}
