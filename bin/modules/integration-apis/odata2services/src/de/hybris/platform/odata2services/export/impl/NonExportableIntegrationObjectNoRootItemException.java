/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

/**
 * An exception that occurs when the exportable integration object does not have a root item.
 */
public class NonExportableIntegrationObjectNoRootItemException extends RuntimeException
{
	private static final String ERROR_MESSAGE = "The integration object [%s] is not exportable because it does not have a root item assigned.";
	private final String integrationObjectCode;

	/**
	 * Instantiates a non exportable integration object for missing the root item exception
	 *
	 * @param integrationObjectCode specifies the integration object code
	 */
	public NonExportableIntegrationObjectNoRootItemException(final String integrationObjectCode)
	{
		super(String.format(ERROR_MESSAGE, integrationObjectCode));
		this.integrationObjectCode = integrationObjectCode;
	}

	public String getIntegrationObjectCode()
	{
		return integrationObjectCode;
	}

}
