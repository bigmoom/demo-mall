package com.cwh.mall.common.config;

import com.cwh.mall.common.domain.bo.SwaggerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author cwh
 * @date 2021/7/6 9:32
 */
public abstract class BaseSwaggerConfig{
    /**
     * 自定义Swagger配置
     */
    public abstract SwaggerProperties swaggerProperties();

    @Bean
    public Docket createRestApi() {
        SwaggerProperties swaggerProperties = swaggerProperties();
        Docket docket = new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo(swaggerProperties))
                .select()
                .apis(RequestHandlerSelectors.basePackage(swaggerProperties.getApiBasePackage()))
                .paths(PathSelectors.any())
                .build();
        //if (swaggerProperties.isEnableSecurity()) {
        //    docket.securitySchemes(securitySchemes()).securityContexts(securityContexts());
        //}
        return docket;
    }

    private ApiInfo apiInfo(SwaggerProperties swaggerProperties) {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getTitle())
                .description(swaggerProperties.getDescription())
                .contact(new Contact(swaggerProperties.getContactName(), swaggerProperties.getContactUrl(), swaggerProperties.getContactEmail()))
                .version(swaggerProperties.getVersion())
                .build();
    }


    //配置授权信息用以查看接口信息
    //private List<SecurityScheme> securitySchemes() {
    //    ApiKey apiKey = new ApiKey("BASE_TOKEN", "token", io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER.toString());
    //    return Collections.singletonList(apiKey);
    //}
    //
    //
    //private List<SecurityContext> securityContexts() {
    //    return Collections.singletonList(
    //            SecurityContext.builder()
    //                    .securityReferences(Collections.singletonList(new SecurityReference("BASE_TOKEN", new AuthorizationScope[]{new AuthorizationScope("global", "")})))
    //                    .build()
    //    );
    //}



}
