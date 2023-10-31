/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicewebservices.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


public class AssistedServiceCustomer360Exception extends WebserviceException
{
	private static final String TYPE = "AssistedServiceCustomer360Error";
	private static final String SUBJECT_TYPE = "entry";

	public AssistedServiceCustomer360Exception(final String message)
	{
		super(message);
	}

	public AssistedServiceCustomer360Exception(final String message, final String reason)
	{
		super(message, reason);
	}

	public AssistedServiceCustomer360Exception(final String message, final String reason, final Throwable cause)
	{
		super(message, reason, cause);
	}

	public AssistedServiceCustomer360Exception(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	public AssistedServiceCustomer360Exception(final String message, final String reason, final String subject, final Throwable cause)
	{
		super(message, reason, subject, cause);
	}

	@Override
	public String getType()
	{
		return TYPE;
	}

	@Override
	public String getSubjectType()
	{
		return SUBJECT_TYPE;
	}

}
