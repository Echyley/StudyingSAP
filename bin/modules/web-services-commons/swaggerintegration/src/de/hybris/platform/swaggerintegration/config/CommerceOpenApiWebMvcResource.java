/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springdoc.core.AbstractRequestService;
import org.springdoc.core.GenericResponseService;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.OperationService;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocProviders;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.filters.OpenApiMethodFilter;
import org.springdoc.webmvc.api.OpenApiResource;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;


/**
 * Commerce implementation of interface {@link OpenApiResource}. {@link OpenApiResource#isRestController(Map, HandlerMethod, String)}
 * is overridden such that the controller beans are searched from the parent context because controllers of OCC extensions  are
 * imported into the parent context of the servlet context.
 *
 * @since 2211
 */
public class CommerceOpenApiWebMvcResource extends OpenApiWebMvcResource implements InitializingBean, ApplicationContextAware
{

	private ApplicationContext applicationContext;

	private final Set<String> controllersInParentContext = new HashSet<>();

	public CommerceOpenApiWebMvcResource(final String groupName, final ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
			final AbstractRequestService requestBuilder, final GenericResponseService responseBuilder,
			final OperationService operationParser, final List<OperationCustomizer> operationCustomizers,
			final List<OpenApiCustomiser> openApiCustomisers, final List<OpenApiMethodFilter> methodFilters,
			final SpringDocConfigProperties springDocConfigProperties, final SpringDocProviders springDocProviders)
	{
		super(groupName, openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser,
				Optional.ofNullable(operationCustomizers), Optional.ofNullable(openApiCustomisers),
				Optional.ofNullable(methodFilters), springDocConfigProperties, springDocProviders);
	}

	public CommerceOpenApiWebMvcResource(final ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
			final AbstractRequestService requestBuilder, final GenericResponseService responseBuilder,
			final OperationService operationParser, final List<OperationCustomizer> operationCustomizers,
			final List<OpenApiCustomiser> openApiCustomisers, final List<OpenApiMethodFilter> methodFilters,
			final SpringDocConfigProperties springDocConfigProperties, final SpringDocProviders springDocProviders)
	{
		super(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser,
				Optional.ofNullable(operationCustomizers), Optional.ofNullable(openApiCustomisers),
				Optional.ofNullable(methodFilters), springDocConfigProperties, springDocProviders);
	}

	/**
	 * Is rest controller boolean.
	 *
	 * @param restControllers the rest controllers
	 * @param handlerMethod   the handler method
	 * @param operationPath   the operation path
	 * @return the boolean
	 */
	@Override
	protected boolean isRestController(final Map<String, Object> restControllers, final HandlerMethod handlerMethod,
			final String operationPath)
	{
		boolean isController = super.isRestController(restControllers, handlerMethod, operationPath);
		if (!isController)
		{
			isController = isInParentControllers(handlerMethod.getBean().toString());
		}
		return isController;
	}

	protected boolean isInParentControllers(final String beanName)
	{
		return this.controllersInParentContext.contains(beanName);
	}

	@Override
	public void afterPropertiesSet() throws Exception
	{
		Optional.ofNullable(applicationContext.getParent()).map(appCtx -> appCtx.getBeansWithAnnotation(Controller.class).keySet())
				.ifPresent(controllersInParentContext::addAll);
		Optional.ofNullable(applicationContext.getParent())
				.map(appCtx -> appCtx.getBeansWithAnnotation(RestController.class).keySet())
				.ifPresent(controllersInParentContext::addAll);
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}
}
