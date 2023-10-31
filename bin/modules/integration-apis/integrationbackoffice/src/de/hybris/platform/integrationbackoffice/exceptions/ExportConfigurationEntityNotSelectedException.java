/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.exceptions;

/**
 * Exception that occurs when dealing with entity instances while no entity is selected in the export configuration editor.
 */
public class ExportConfigurationEntityNotSelectedException extends IntegrationBackofficeException
{
	private static final long serialVersionUID = 1615344687137265201L;
	private static final String MESSAGE_TEMPLATE = "No entity is currently selected in the editor.";

	public ExportConfigurationEntityNotSelectedException()
	{
		super(MESSAGE_TEMPLATE);
	}
}
