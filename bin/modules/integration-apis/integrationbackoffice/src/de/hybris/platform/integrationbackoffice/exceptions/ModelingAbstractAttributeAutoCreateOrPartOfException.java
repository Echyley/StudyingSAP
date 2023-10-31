/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.exceptions;

/**
 * An exception that occurs when attempting to save an integration object with an abstract attribute that is autoCreate or partOf.
 */
public class ModelingAbstractAttributeAutoCreateOrPartOfException extends IntegrationBackofficeException
{
	private static final long serialVersionUID = 2744320291395618673L;
	private static final String ERROR_MESSAGE_TEMPLATE = "Attribute [{0}.{1}] cannot be autoCreate or partOf, because it "
			+ "references an abstract type in the integration object.";

	/**
	 * Default constructor.
	 *
	 * @param cause the cause of the exception.
	 * @param typeCode the type of the integration object item the attribute belongs to.
	 * @param attributeName the attribute's name.
	 */
	public ModelingAbstractAttributeAutoCreateOrPartOfException(final Throwable cause, final String typeCode,
			final String attributeName)
	{
		super(cause, ERROR_MESSAGE_TEMPLATE, typeCode, attributeName);
	}
}
