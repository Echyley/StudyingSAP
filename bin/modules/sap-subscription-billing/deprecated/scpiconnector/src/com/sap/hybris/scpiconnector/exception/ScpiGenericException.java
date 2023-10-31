/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.scpiconnector.exception;

/**
 *
 */
public class ScpiGenericException extends Exception
{

	private final String code;
	private final String reason;


	/**
	 * Constructor with errorCode and errorDescription
	 */
	public ScpiGenericException(final String code, final String reason)
	{
		super();
		this.code = code;
		this.reason = reason;
	}

	/**
	 * No argument constructor
	 */
	public ScpiGenericException()
	{
		super();
		this.code = null;
		this.reason = null;
	}




	/**
	 * @return the code
	 */
	public String getCode()
	{
		return code;
	}

	/**
	 * @return the reason
	 */
	public String getReason()
	{
		return reason;
	}

	@Override
	public String toString()
	{
		return String.format("SCPI generic exception. Error code [%s] and error reason [%s].", this.code, this.reason);
	}

}
