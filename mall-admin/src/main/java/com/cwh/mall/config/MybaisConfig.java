package com.cwh.mall.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author cwh
 * @date 2021/7/8 17:16
 */
@Configuration
@MapperScan("com.cwh.mall.mallmbg.mapper")
public class MybaisConfig {
}
