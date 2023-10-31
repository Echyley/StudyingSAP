/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.config;

import static com.hybris.merchandising.constants.MerchandisingcmswebservicesConstants.EXTENSIONNAME;

import de.hybris.platform.swagger.ApiDocInfo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
@ImportResource(value = "classpath*:/swagger/swaggerintegration/web/spring/*-web-spring.xml")
public class WebConfig
{
	@Bean("apiDocInfo")
	public ApiDocInfo apiDocInfo()
	{
		return () -> EXTENSIONNAME;
	}
}
