/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyab2bwebservices.v1.config;


import de.hybris.platform.swagger.ApiDocInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;

/**
 * Spring configuration which replace <mvc:annotation-driven> tag. It allows override default
 * RequestMappingHandlerMapping with our own mapping handler
 */

@Configuration
@PropertySource("classpath:/v1/swagger.properties")
@ImportResource(value = "classpath*:/swagger/swaggerintegration/web/spring/*-web-spring.xml")
public class WebConfig extends DelegatingWebMvcConfiguration
{
    @Bean("apiDocInfo")
    public ApiDocInfo apiDocInfo()
    {
        return () -> "gigyab2bwebservices";
    }

}
