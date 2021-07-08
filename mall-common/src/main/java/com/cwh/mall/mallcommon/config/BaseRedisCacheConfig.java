package com.cwh.mall.mallcommon.config;

import com.cwh.mall.mallcommon.domain.properties.BaseRedisProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import java.time.Duration;

/**
 * @author cwh
 * @date 2021/7/8 11:30
 */
@EnableCaching
@Configuration
@Slf4j
public abstract class BaseRedisCacheConfig extends CachingConfigurerSupport {
    //配置redis连接属性
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
}
