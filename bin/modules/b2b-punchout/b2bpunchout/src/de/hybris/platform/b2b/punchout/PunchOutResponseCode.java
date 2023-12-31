/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout;

/**
 * PunchOut protocol response codes.
 *
 * @deprecated Please use #org.springframework.http.HttpStatus instead
 */
@Deprecated(since = "2211", forRemoval = true)
public class PunchOutResponseCode
{

	/**
	 * Successful response.
	 */
	public static final String SUCCESS = "200";

	/**
	 * Authentication failed.
	 */
	public static final String ERROR_CODE_AUTH_FAILED = "401";

	/**
	 * Access to resource was forbidden.
	 */
	public static final String FORBIDDEN = "403";

	/**
	 * The current state of the server or its internal data prevented the (update) operation request. An identical
	 * Request is unlikely to succeed in the future, but only after another operation has executed, if at all.
	 */
	public static final String CONFLICT = "409";

	/**
	 * Request has invalid content.
	 */
	public static final String BAD_REQUEST = "400";

	/**
	 * Generic error. When other error codes does not apply.
	 */
	public static final String INTERNAL_SERVER_ERROR = "500";

	private PunchOutResponseCode()
	{
		throw new IllegalStateException("Cannot Instantiate an Constant Class");
	}


}
