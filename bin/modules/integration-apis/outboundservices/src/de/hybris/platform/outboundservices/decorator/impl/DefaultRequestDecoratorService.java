/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundservices.decorator.impl;

import de.hybris.platform.outboundservices.decorator.DecoratorContext;
import de.hybris.platform.outboundservices.decorator.DecoratorContextFactory;
import de.hybris.platform.outboundservices.decorator.DecoratorExecution;
import de.hybris.platform.outboundservices.decorator.DefaultDecoratorExecution;
import de.hybris.platform.outboundservices.decorator.OutboundRequestDecorator;
import de.hybris.platform.outboundservices.decorator.RequestDecoratorService;
import de.hybris.platform.outboundservices.facade.SyncParameters;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import com.google.common.base.Preconditions;

/**
 * Default implementation of {@link RequestDecoratorService}
 */
public class DefaultRequestDecoratorService implements RequestDecoratorService
{
	private final DecoratorContextFactory contextFactory;
	private List<OutboundRequestDecorator> decorators;

	/**
	 * Constructor that takes a required {@link DecoratorContextFactory} to be used in the service.
	 *
	 * @param factory that is used to create a {@link DecoratorContext}
	 */
	public DefaultRequestDecoratorService(@NotNull final DecoratorContextFactory factory)
	{
		Preconditions.checkState(factory != null, "DecoratorContextFactory cannot be null");
		contextFactory = factory;
	}

	@Override
	public HttpEntity<Map<String, Object>> createHttpEntity(final SyncParameters params)
	{
		final DecoratorContext context = contextFactory.createContext(params);

		final DecoratorExecution execution = new DefaultDecoratorExecution(filterNonApplicableDecorators(context));
		return execution.createHttpEntity(new HttpHeaders(), new HashMap<>(), context);
	}

	private List<OutboundRequestDecorator> filterNonApplicableDecorators(final DecoratorContext decoratorContext)
	{
		return decorators.stream()
		                 .filter(OutboundRequestDecorator::isEnabled)
		                 .filter(d -> d.isApplicable(decoratorContext))
		                 .toList();
	}
	
	public void setDecorators(final List<OutboundRequestDecorator> decorators)
	{
		this.decorators = decorators != null
				? Collections.unmodifiableList(decorators)
				: Collections.emptyList();
	}
}
