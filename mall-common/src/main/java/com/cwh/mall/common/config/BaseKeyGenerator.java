package com.cwh.mall.common.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * 基础缓存key生成器
 * @author cwh
 * @date 2021/7/20 10:11
 */
@Component
public abstract class BaseKeyGenerator implements KeyGenerator {
    /**
     * 缓存前缀
     */
    private static String PREFIX = "BASE";

    protected void setPrefix(String prefix) {
        PREFIX = prefix;
    }

    /**
     * 生成策略
     * @param target 代理对象
     * @param method 代理方法
     * @param objects 参数
     * @return
     */
    @Override
    public Object generate(Object target, Method method, Object... objects) {
        //分隔符
        CharSequence seperator = ":";

        StringJoiner stringJoiner = new StringJoiner(seperator);
        //添加前缀
        stringJoiner.add(PREFIX);
        ////添加类名
        //stringJoiner.add(target.getClass().getSimpleName());
        ////添加方法名
        //stringJoiner.add(method.getName());
        //添加参数名
        Arrays.stream(objects).map(o -> stringJoiner.add(o.toString())).collect(Collectors.toList());

        return stringJoiner.toString();

        //return stringBuilder.toString();
    }
}
