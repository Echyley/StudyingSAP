/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.interceptor;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.Objects;

import javax.validation.constraints.NotNull;

/**
 * Validates that any atomic type {@link IntegrationObjectClassModel} is not set as the root.
 */
public class IntegrationObjectClassRootValidateInterceptor
		implements ValidateInterceptor<IntegrationObjectClassModel>
{
	private static final String ROOT_CANNOT_BE_ATOMIC =
			"Class [%s] of atomic type [%s] cannot have root set to true for IntegrationObject [%s].";
	private final TypeService typeService;

	/**
	 * Instantiate the {@link IntegrationObjectClassRootValidateInterceptor}
	 *
	 * @param typeService type service to search for atomic types
	 */
	public IntegrationObjectClassRootValidateInterceptor(@NotNull final TypeService typeService)
	{
		this.typeService = Objects.requireNonNull(typeService, "TypeService cannot be null");
	}

	/**
	 * Validates the context class model and throws exceptions when an atomic type is root.
	 *
	 * @throws InterceptorException when configuration problems found
	 */
	@Override
	public void onValidate(final IntegrationObjectClassModel ioClass, final InterceptorContext interceptorContext)
			throws InterceptorException
	{
		if (ioClass.getType() != null && Boolean.TRUE.equals(ioClass.getRoot()))
		{
			validateTypeIsNotAnAtomicType(ioClass);
		}
	}

	private void validateTypeIsNotAnAtomicType(final IntegrationObjectClassModel ioClass)
			throws InterceptorException
	{
		if (isAnAtomicType(ioClass.getType()))
		{
			throw interceptorException(ioClass);
		}
	}

	private boolean isAnAtomicType(final Class<?> type)
	{
		try
		{
			return typeService.getAtomicTypeForJavaClass(type) != null;
		}
		catch (final UnknownIdentifierException e)
		{
			return false;
		}
	}

	private InterceptorException interceptorException(final IntegrationObjectClassModel contextClass)
	{
		return new InterceptorException(ROOT_CANNOT_BE_ATOMIC.formatted(contextClass.getCode(),
				contextClass.getType().getName(),
				contextClass.getIntegrationObject().getCode()), this);
	}
}
