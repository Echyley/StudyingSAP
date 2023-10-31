/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.exception;

/**
 * An exception indicating that an Integration Object has been modeled containing both Integration Object Items and Integration
 * Object Classes, which are not compatible and will cause the schema generation for the integration Object to fail
 */
public class IntegrationObjectItemAndClassConflictException extends RuntimeException
{
	private static final String MSG = "Integration Object '%s' has both IntegrationObjectItem(s) and IntegrationObjectClass(es) " +
			"associated. This combination is incompatible and one of the types must be removed.";

	/**
	 * Instantiates the exception
	 *
	 * @param io The integration object name
	 */
	public IntegrationObjectItemAndClassConflictException(final String io)
	{
		super(String.format(MSG, io));
	}
}
