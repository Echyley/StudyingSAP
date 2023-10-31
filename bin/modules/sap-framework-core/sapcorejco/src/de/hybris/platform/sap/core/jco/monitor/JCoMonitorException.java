/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.monitor;

import de.hybris.platform.sap.core.common.exceptions.CoreBaseException;


/**
 * Exception which occurs during JCo monitoring.
 * 
 */
public class JCoMonitorException extends CoreBaseException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1344124319839369351L;


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public JCoMonitorException(final String msg)
	{
		super(msg);
	}


	/**
	 * Constructor.
	 * 
	 * @param msg
	 *           Message for the Exception
	 * @param ex
	 *           root cause
	 */
	public JCoMonitorException(final String msg, final Throwable ex)
	{
		super(msg, ex);
	}


}
