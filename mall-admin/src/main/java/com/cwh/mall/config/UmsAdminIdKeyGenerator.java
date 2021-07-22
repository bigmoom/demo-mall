package com.cwh.mall.config;

import com.cwh.mall.common.config.BaseKeyGenerator;
import com.cwh.mall.dto.UmsAdminParam;
import com.cwh.mall.mbg.model.UmsAdmin;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author cwh
 * @date 2021/7/22 9:10
 */
@Component
public class UmsAdminIdKeyGenerator extends BaseKeyGenerator {

    private static final String PREFIX = "ID";
    private static final CharSequence SEPERATOR = ":";

    @Override
    public Object generate(Object target, Method method, Object... objects) {
        StringJoiner stringJoiner = new StringJoiner(SEPERATOR);
        ////添加类名
        stringJoiner.add(target.getClass().getSimpleName());
        //添加前缀
        stringJoiner.add(PREFIX);
        //添加参数名
        //Arrays.stream(objects).map(o -> stringJoiner.add(o.toString())).collect(Collectors.toList());

        Arrays.stream(objects).map(o -> connectKey(stringJoiner,o))
                .collect(Collectors.toList());

        return stringJoiner.toString();

        //return stringBuilder.toString();
    }

    private StringJoiner connectKey(StringJoiner stringJoiner,Object object){
        if(object instanceof UmsAdmin){
            stringJoiner.add(((UmsAdmin) object).getId().toString());
        }else {
            stringJoiner.add(object.toString());
        }
        return stringJoiner;
    }
}
