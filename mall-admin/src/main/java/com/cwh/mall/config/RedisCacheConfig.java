package com.cwh.mall.admin.config;

import com.cwh.mall.common.config.BaseRedisCacheConfig;
import com.cwh.mall.common.domain.bo.BaseRedisProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 根据application.yml中的属性配置redis连接和连接池属性
 * @author cwh
 * @date 2021/7/8 15:16
 */
@Configuration
public class RedisCacheConfig extends BaseRedisCacheConfig {

    @Value("${spring.redis.database}")
    private Integer index;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private Integer port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.timeout}")
    private Long timeout;
    @Value("${spring.redis.jedis.pool.max-active}")
    private Integer maxActive;
    @Value("${spring.redis.jedis.pool.max-wait}")
    private Long maxWait;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private Integer maxIdle;
    @Value("${spring.redis.jedis.pool.max-idle}")
    private Integer minIdle;

    @Override
    public BaseRedisProperties redisProperties() {

        return BaseRedisProperties.builder()
                .index(index)
                .host(host)
                .port(port)
                .password(password)
                .timeout(timeout)
                .maxActive(maxActive)
                .maxIdle(maxIdle)
                .maxWait(maxWait)
                .minIdle(minIdle)
                .build();

    }
}
