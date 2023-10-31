/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.exception;

import de.hybris.platform.servicelayer.interceptor.Interceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

/**
 * An exception that occurs when attempting to save an integration object with an abstract attribute that is autoCreate or partOf.
 */
public class AbstractAttributeAutoCreateOrPartOfException extends InterceptorException
{
	private static final String ERROR_MESSAGE_TEMPLATE = "Attribute [%s.%s] cannot be autoCreate or partOf, because it references"
			+ " an abstract type in the integration object.";

	private final String typeCode;
	private final String attributeName;

	/**
	 * Default constructor.
	 *
	 * @param typeCode the type of the integration object item the attribute belongs to.
	 * @param attributeName the attribute's name.
	 * @param inter the interceptor that validated the attribute.
	 */
	public AbstractAttributeAutoCreateOrPartOfException(final String typeCode, final String attributeName, final Interceptor inter)
	{
		super(String.format(ERROR_MESSAGE_TEMPLATE, typeCode, attributeName), inter);
		this.typeCode = typeCode;
		this.attributeName = attributeName;
	}

	/**
	 * The type of the integration object item of the attribute causing the exception.
	 *
	 * @return the type of the integration object item.
	 */
	public String getTypeCode()
	{
		return typeCode;
	}

	/**
	 * The name of the attribute causing the exception.
	 *
	 * @return the name of the attribute.
	 */
	public String getAttributeName()
	{
		return attributeName;
	}
}
