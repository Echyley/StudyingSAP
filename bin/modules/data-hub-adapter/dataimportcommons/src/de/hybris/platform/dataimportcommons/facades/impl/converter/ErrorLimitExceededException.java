/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.dataimportcommons.facades.impl.converter;

/**
 * An exception indicating that error limit configured has been exceeded.
 */
public class ErrorLimitExceededException extends RuntimeException
{
	private static final long serialVersionUID = -1223795822804219496L;

	public ErrorLimitExceededException()
	{
		super();
	}

	public ErrorLimitExceededException(final String message)
	{
		super(message);
	}
}
