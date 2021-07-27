package com.cwh.mall.service.impl;

import com.cwh.mall.common.service.RedisService;
import com.cwh.mall.service.RefreshTokenRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author cwh
 * @date 2021/7/27 14:12
 */
@Service
public class RefreshTokenRedisServiceImpl implements RefreshTokenRedisService {

    @Autowired
    private RedisService redisService;

    @Override
    public void setRefreshToken(String key, String refreshToken, long expiration, TimeUnit timeUnit) {
        redisService.set(key,refreshToken,expiration,timeUnit);
    }

    @Override
    public Boolean deleteRefreshToken(String key) {
        return redisService.del(key);
    }
}
