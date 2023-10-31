/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modals.builders;

public class ItemModelNotSelectedForReportException extends RuntimeException
{
	private static final String MESSAGE_TEMPLATE = "No itemModel of type: %s is selected when generating audit report.";

	public ItemModelNotSelectedForReportException(String message)
	{
		super(String.format(MESSAGE_TEMPLATE, message));
	}
}
