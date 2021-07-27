package com.cwh.mall.common.service;

import java.util.concurrent.TimeUnit;

/**
 * @author cwh
 * @date 2021/7/26 17:15
 */
public interface RedisService {


    public void set(String key, Object value);

    public void set(String key, Object value, long time);

    public void set(String key, Object value, long time, TimeUnit timeUnit);

    public Object get(String key);

    public Boolean del(String key);
}
