/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl;

/**
 * An exception that occurs when the integration object is not supported to be exported.
 */
public class NonExportableIntegrationObjectException extends RuntimeException
{
	private static final String ERROR_MESSAGE = "The integration object [%s] is not exportable. " +
			"Please check the configuration property odata2services.exportable.integration.objects.";
	private final String integrationObjectCode;

	/**
	 * Instantiates a non exportable integration object exception.
	 *
	 * @param integrationObjectCode specifies the integration object code
	 */
	public NonExportableIntegrationObjectException(final String integrationObjectCode)
	{
		super(String.format(ERROR_MESSAGE, integrationObjectCode));
		this.integrationObjectCode = integrationObjectCode;
	}

	public String getIntegrationObjectCode()
	{
		return integrationObjectCode;
	}

}
