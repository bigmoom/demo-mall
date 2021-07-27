package com.cwh.mall.common.service.impl;

import com.cwh.mall.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

/**
 * redis的CRUD的封装
 * @author cwh
 * @date 2021/7/26 17:16
 */
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //ValueOperations<String,Object> operations = redisTemplate.opsForValue();


    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key,value);
    }

    @Override
    public void set(String key, Object value, long time) {
        redisTemplate.opsForValue().set(key, value, time);
    }

    @Override
    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, time, timeUnit);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);

    }

    @Override
    public Boolean del(String key) {
        return redisTemplate.opsForValue().getOperations().delete(key);
    }
}
