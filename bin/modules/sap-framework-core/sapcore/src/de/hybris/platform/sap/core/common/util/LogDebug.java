/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.common.util;

import java.text.MessageFormat;

import org.apache.log4j.Logger;


/**
 * LogDebug utility.
 */
public class LogDebug
{
	private LogDebug() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Logs debug information with arguments.
	 * 
	 * @param log
	 *           Logger
	 * @param msg
	 *           Message to be logged. See {@link java.text.MessageFormat} for pattern description
	 * @param objects
	 *           Set of objects to be logged
	 */
	public static void debug(final Logger log, final String msg, final Object... objects)
	{
		if (log.isDebugEnabled())
		{
			log.debug(MessageFormat.format(msg, objects));
		}

	}
}
