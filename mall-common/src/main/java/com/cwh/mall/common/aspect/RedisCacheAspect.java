package com.cwh.mall.common.aspect;

import com.cwh.mall.common.domain.bo.ResultCode;
import com.cwh.mall.common.domain.vo.ResultVO;
import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * redis缓存切面
 * 捕获redis连接错误，记录日志，返回null
 * 通过校验null直接去DB中拿数据
 * @author cwh
 * @date 2021/7/27 10:50
 */

@Aspect
@Component
@Slf4j
@Order(2)
public class RedisCacheAspect {
    //
    //@Pointcut("@annotation(com.cwh.mall.common.annotation.CheckRedisConnnection)")
    //public void redisCacheAspect(){
    //}

    /**
     * 切入点
     * 在以RedisService结尾的Service中切入所有的set和get方法
     */
    @Pointcut("(execution(public * com.cwh.mall.service.*RedisService.set*(..)) || execution(public * com.cwh.mall.service.*RedisService.get*(..))) " +
            "&& !execution(public * com.cwh.mall.service.RefreshTokenRedisService.*(..))")
    public void redisCacheAspect(){

    }

    @Around("redisCacheAspect()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("进入切面");
        Object result = null;
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        try{
            result = proceedingJoinPoint.proceed();
        }catch (RedisConnectionException| JedisConnectionException | RedisConnectionFailureException exception){
            //捕获redis连接异常，返回null,执行DB查询
            //保存操作日志，用于redis宕机重启更新缓存
            log.info("CLASS_METHOD:" + signature.getDeclaringTypeName() + ":" + signature.getName());
            log.info("参数:" + Arrays.toString(proceedingJoinPoint.getArgs()));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        }
        return result;
    }

}
