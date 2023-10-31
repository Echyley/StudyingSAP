/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.exceptions;

/**
 * Thrown if an action on a product configuration runtime instance is forbidden
 */
public class ProductConfigurationAccessException extends IllegalStateException
{
	/**
	 * Constructor
	 * 
	 * @param message
	 *           Message text
	 */
	public ProductConfigurationAccessException(final String message)
	{
		super(message);
	}
}
