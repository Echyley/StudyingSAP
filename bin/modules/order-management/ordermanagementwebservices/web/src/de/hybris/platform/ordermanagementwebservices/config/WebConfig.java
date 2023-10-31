/*
 * [y] hybris Platform
 *
 * Copyright (c) 2022 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.ordermanagementwebservices.config;

import de.hybris.platform.swagger.ApiDocInfo;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;



@EnableWebMvc
@Configuration
@ImportResource(value = "classpath*:/swagger/swaggerintegration/web/spring/*-web-spring.xml")
public class WebConfig extends DelegatingWebMvcConfiguration
{
	private ApplicationContext applicationContext;

	@Resource(name = "restHandlerExceptionResolver")
	private HandlerExceptionResolver exceptionResolver;

	@Bean("apiDocInfo")
	public ApiDocInfo apiDocInfo()
	{
		return () -> "ordermanagementwebservices";
	}


	@Override
	protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers)
	{
		final ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
		exceptionHandlerExceptionResolver.setApplicationContext(applicationContext);
		exceptionHandlerExceptionResolver.setContentNegotiationManager(mvcContentNegotiationManager());
		exceptionHandlerExceptionResolver.setMessageConverters(getMessageConverters());
		exceptionHandlerExceptionResolver.afterPropertiesSet();

		exceptionResolvers.add(exceptionHandlerExceptionResolver);
		exceptionResolvers.add(exceptionResolver);
		exceptionResolvers.add(new ResponseStatusExceptionResolver());
		exceptionResolvers.add(new DefaultHandlerExceptionResolver());
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		super.setApplicationContext(applicationContext);
		this.applicationContext = applicationContext;
	}

}
