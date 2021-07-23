package com.cwh.mall.common.config;

import com.cwh.mall.common.domain.bo.BaseRedisProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.time.Duration;

/**
 * @author cwh
 * @date 2021/7/8 11:30
 */
@EnableCaching
@Slf4j
public abstract class BaseRedisCacheConfig extends CachingConfigurerSupport {
    /**
     *
     * @return
     */
    public abstract BaseRedisProperties redisProperties();

    /**
     * 通过redisProperties构建连接
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        BaseRedisProperties baseRedisProperties = redisProperties();

        //配置连接属性
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(baseRedisProperties.getIndex());
        redisStandaloneConfiguration.setHostName(baseRedisProperties.getHost());
        redisStandaloneConfiguration.setPort(baseRedisProperties.getPort());
        redisStandaloneConfiguration.setPassword(baseRedisProperties.getPassword());

        //jedis连接池设置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(baseRedisProperties.getMaxActive());
        jedisPoolConfig.setMaxIdle(baseRedisProperties.getMaxIdle());
        jedisPoolConfig.setMinIdle(baseRedisProperties.getMinIdle());
        jedisPoolConfig.setMaxWaitMillis(baseRedisProperties.getMaxWait());
        JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling().poolConfig(jedisPoolConfig).build();

        return new JedisConnectionFactory(redisStandaloneConfiguration,jedisClientConfiguration);
    }
    /**
     * 配置cacheManager
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory){
        //redisCacheManager需要构造redisCacheWriter和redisCacheConfigurer
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);

        //配置cache序列化为jsonSerializer
        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
        RedisSerializationContext.SerializationPair<Object> serializationPair = RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig().serializeValuesWith(serializationPair);
        //设置过期时间一天
        defaultCacheConfig.entryTtl(Duration.ofDays(1));

        return new RedisCacheManager(redisCacheWriter,defaultCacheConfig);
    }

    /**
     * 配置redisTemplate<String,Object>序列化
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        //设置连接工厂
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        //设置JackSon2JsonRedisSerialize 替代默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        //配置ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        //指定要序列化的域
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        //序列化时将对象全类名一起保存，方便反序列化
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        //设置redistemplate键值对的序列化
        RedisSerializer stringSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    /**
     * 自定义CacheErrorHandler应对redis宕机状态
     * 保证redis宕机状态下继续可以通过数据库连接
     * @return
     */
    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler(){

            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object o) {
                //判断是不是连接问题，若是连接问题则不抛出异常去数据库查询
                //不过会等待connectionTime
                if(e instanceof JedisConnectionException || e instanceof RedisConnectionFailureException){
                    handlerRedisCacheErrorException(e,cache,o);
                }else {
                    throw e;
                }
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object o, Object o1) {
                if(e instanceof JedisConnectionException || e instanceof RedisConnectionFailureException){
                    handlerRedisCacheErrorException(e,cache,o);
                }else {
                    throw e;
                }

            }
            //为保证一致性继续抛出异常
            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object o) {
                //throw e;
                throw new RedisConnectionFailureException("连接失败");
            }

            //为保证一致性继续抛出异常
            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                throw e;

            }
        };
    }

    protected void handlerRedisCacheErrorException(RuntimeException exception, Cache cache, Object key){
        log.error("redis异常：cacheName:[{}],key=[{}]",cache==null ? "unknown":cache.getName(),key);
    }



}
