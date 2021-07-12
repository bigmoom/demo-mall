package com.cwh.mall.common.domain.properties;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * redis连接属性，以及连接池属性
 * @author cwh
 * @date 2021/7/8 11:52
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
public class BaseRedisProperties {
    //==================redis数据库属性================
    //数据库索引
    private Integer index;
    //host ip
    private String host;
    //端口
    private Integer port;
    //服务器连接密码（默认为空）
    private String password;
    //连接超时时间
    private Long timeout;

    //===================连接池属性======================
    //最大连接数
    private Integer maxActive;
    //最大阻塞等待时间（负值表示没有显示）
    private Long maxWait;
    //最大空闲连接
    private Integer maxIdle;
    //最小空闲连接
    private Integer minIdle;

    public BaseRedisProperties(Integer index, String host, Integer port, String password, Long timeout, Integer maxActive, Long maxWait, Integer maxIdle, Integer minIdle) {
        this.index = index;
        this.host = host;
        this.port = port;
        this.password = password;
        this.timeout = timeout;
        this.maxActive = maxActive;
        this.maxWait = maxWait;
        this.maxIdle = maxIdle;
        this.minIdle = minIdle;
    }
}
