/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.ymkt.segmentation.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

import de.hybris.platform.swagger.ApiDocInfo;

@Configuration
@PropertySource("classpath:/v1/swagger.properties")
@ImportResource(value = "classpath*:/swagger/swaggerintegration/web/spring/*-web-spring.xml")
public class WebConfig
{
    @Bean("apiDocInfo")
    public ApiDocInfo apiDocInfo() {
        return () -> "sapymktsegmentationwebservices";
    }
}
