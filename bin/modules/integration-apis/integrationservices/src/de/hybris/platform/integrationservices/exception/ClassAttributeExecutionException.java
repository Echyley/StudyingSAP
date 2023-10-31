/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.exception;

import de.hybris.platform.integrationservices.model.IntegrationObjectClassAttributeModel;

import org.apache.commons.lang.StringUtils;

/**
 * Indicates that a general error has occurred when trying to execute code pertaining to acquiring and processing class
 * attributes. For example, invoking methods Java Reflections, and the invocation could be trying to access methods it cannot,
 * thus, throwing an exception.
 */
public class ClassAttributeExecutionException extends RuntimeException
{
	/**
	 * Instantiates this exception.
	 *
	 * @param a an IOClassAttributeModel used to gather the information needed to retrieve the attribute value
	 */
	public ClassAttributeExecutionException(final IntegrationObjectClassAttributeModel a)
	{
		this(a, null);
	}

	/**
	 * Instantiates this exception.
	 *
	 * @param a an IOClassAttributeModel used to gather information about the method to be invoked
	 * @param e the specific exception that cause this general exception to be thrown
	 */
	public ClassAttributeExecutionException(final IntegrationObjectClassAttributeModel a,
	                                        final Exception e)
	{
		super(message(a), e);
	}

	private static String message(final IntegrationObjectClassAttributeModel attribute)
	{
		if (StringUtils.isNotEmpty(attribute.getReadMethod()))
		{
			return String.format(
					"ReadMethod %s from attribute %s does not exist.", attribute.getReadMethod(), attribute.getAttributeName());
		}
		else
		{
			final String className = attribute.getIntegrationObjectClass().getType().getName();
			return String.format(
					"Attribute %s for class %s does not exist.", attribute.getAttributeName(), className);
		}
	}
}
