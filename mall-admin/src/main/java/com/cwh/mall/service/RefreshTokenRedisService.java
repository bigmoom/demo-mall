package com.cwh.mall.service;

import java.util.concurrent.TimeUnit;

/**
 * @author cwh
 * @date 2021/7/27 14:12
 */
public interface RefreshTokenRedisService {

    void setRefreshToken(String key, String refreshToken, long expiration, TimeUnit timeUnit);

    Boolean deleteRefreshToken(String key);
}
