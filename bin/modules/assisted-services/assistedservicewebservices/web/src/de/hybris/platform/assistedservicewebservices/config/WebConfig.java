/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import de.hybris.platform.swagger.ApiDocInfo;

@Configuration
@ImportResource(value = "classpath*:/swagger/swaggerintegration/web/spring/*-web-spring.xml")
public class WebConfig
{
    @Bean("apiDocInfo")
    public ApiDocInfo apiDocInfo() {
        return () -> "assistedservicewebservices";
    }
}
