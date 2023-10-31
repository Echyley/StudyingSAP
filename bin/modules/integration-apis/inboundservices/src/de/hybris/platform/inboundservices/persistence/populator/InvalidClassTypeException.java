/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.populator;

import de.hybris.platform.integrationservices.exception.IntegrationAttributeProcessingException;
import de.hybris.platform.integrationservices.model.TypeAttributeDescriptor;

/**
 * An exception indicating that a path is invalid for the given class type attribute.
 */
public class InvalidClassTypeException extends IntegrationAttributeProcessingException
{
	private static final String MSG = "The IntegrationObjectClass [%s] does not exist in the specified classpath.";

	/**
	 * Instantiates the exception.
	 *
	 * @param attrDescriptor The {@link TypeAttributeDescriptor} that formulates the exception
	 * @param value          The classpath value that is invalid
	 */
	public InvalidClassTypeException(final TypeAttributeDescriptor attrDescriptor, final Object value)
	{
		super(String.format(MSG, value), attrDescriptor);
	}
}
