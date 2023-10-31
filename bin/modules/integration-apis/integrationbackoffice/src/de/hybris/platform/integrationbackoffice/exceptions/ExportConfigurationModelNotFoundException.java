/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.exceptions;

import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

public class ExportConfigurationModelNotFoundException extends IntegrationBackofficeException
{
	private static final long serialVersionUID = 7966633559644191245L;
	private static final String MESSAGE_TEMPLATE = "An entity or an entity instance may have been deleted. Please refresh the list of instances.";

	public ExportConfigurationModelNotFoundException(final ModelNotFoundException e)
	{
		super(e, MESSAGE_TEMPLATE);
	}
}
