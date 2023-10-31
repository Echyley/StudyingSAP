/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util;

/**
 * Possible levels for writing into a log.
 */
public enum LogLevel
{
	NONE(0), INFO(1), ERROR(2), WARN(3), DEBUG(4), TRACE(5);

	private final int level;

	LogLevel(final int l)
	{
		level = l;
	}

	/**
	 * Determines whether this log level enables the specified level. For example, if this log level is {@code WARN}, then for
	 * {@code INFO} value of the {@code l} parameter, the return is {@code true}; but for {@code DEBUG} value - it's {@code false}.
	 *
	 * @param l a log level to check whether it's enabled.
	 * @return {@code true}, if the specified log level is enabled by this log level; {@code false} otherwise.<br/>For example, if
	 * there is a log level {@code var level = LogLevel.WARN}, then {@code level.isEnabled(LogLevel.INFO)} or
	 * {@code level.isEnabled(LogLevel.WARN)} will return {@code true}; but {@code level.isEnabled(LogLevel.DEBUG)} will return
	 * {@code false}
	 */
	public boolean isEnabled(final LogLevel l)
	{
		return l.level <= level;
	}
}
