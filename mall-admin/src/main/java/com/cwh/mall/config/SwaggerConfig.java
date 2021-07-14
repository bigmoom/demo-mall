package com.cwh.mall.admin.config;

import com.cwh.mall.common.config.BaseSwaggerConfig;
import com.cwh.mall.common.domain.bo.SwaggerProperties;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author cwh
 * @date 2021/7/6 9:38
 */
@EnableOpenApi
@Configuration
public class SwaggerConfig extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties(){
        return SwaggerProperties.builder()
                .apiBasePackage("com.cwh.mall.malladmin.controller")
                .title("mall后台系统")
                .description("mall后台相关接口文档")
                .contactName("macro")
                .version("1.0")
                .enableSecurity(true)
                .build();
    }
}
