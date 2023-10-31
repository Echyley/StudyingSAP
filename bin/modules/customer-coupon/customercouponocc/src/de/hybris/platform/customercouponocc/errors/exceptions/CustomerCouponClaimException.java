/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.customercouponocc.errors.exceptions;

import de.hybris.platform.webservicescommons.errors.exceptions.WebserviceException;


/**
 * Error exception to be thrown whenever the generic (non business-related) exception occurs
 */
public class CustomerCouponClaimException extends WebserviceException
{
	private static final String TYPE = "CustomerCouponError";
	private static final String DEFAULT_SUBJECT_TYPE = "customerCoupon";

	public CustomerCouponClaimException(final String message)
	{
		super(message);
	}

	public CustomerCouponClaimException(final String message, final String reason)
	{
		super(message, reason);
	}

	public CustomerCouponClaimException(final String message, final String reason, final Throwable cause)
	{
		super(message, reason, cause);
	}

	public CustomerCouponClaimException(final String message, final String reason, final String subject)
	{
		super(message, reason, subject);
	}

	public CustomerCouponClaimException(final String message, final String reason, final String subject, final Throwable cause)
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
		return DEFAULT_SUBJECT_TYPE;
	}
}
