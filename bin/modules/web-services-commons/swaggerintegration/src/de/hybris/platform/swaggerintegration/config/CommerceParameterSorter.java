/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import java.util.Collections;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.web.method.HandlerMethod;

import io.swagger.v3.oas.models.Operation;

/**
 * Commerce implementation of {@link OperationCustomizer} that sorts parameters by the name of each parameter.
 */
public class CommerceParameterSorter implements OperationCustomizer
{
	@Override
	public Operation customize(final Operation operation, final HandlerMethod handlerMethod)
	{
		if (operation != null && operation.getParameters() != null && operation.getParameters().size() > 1)
		{
			Collections.sort(operation.getParameters(), (o1, o2) -> o1.getName().compareTo(o2.getName()));
		}
		return operation;
	}
}
