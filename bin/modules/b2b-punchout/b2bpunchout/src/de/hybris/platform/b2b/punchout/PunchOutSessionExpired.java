/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout;

import org.springframework.http.HttpStatus;
/**
 *
 */
public class PunchOutSessionExpired extends PunchOutException
{

	/**
	 * Constructor.
	 * 
	 * @param message
	 *           the error message
	 */
	public PunchOutSessionExpired(final String message)
	{
		super(HttpStatus.CONFLICT, message);
	}

}
