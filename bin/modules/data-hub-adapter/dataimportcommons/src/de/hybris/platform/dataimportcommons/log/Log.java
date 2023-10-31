/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.dataimportcommons.log;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * A logger that acts as a proxy to a real logger retrieved by calling {@link org.slf4j.LoggerFactory#getLogger(Class)}, which
 * enforces logging rules and limitations in a standard way that is applicable to all data import projects.
 */
public final class Log implements Logger
{
	private static final Map<String, Logger> REGISTERED_LOGGERS = new HashMap<>();

	private final String name;
	private Logger realLogger;

	/**
	 * Instantiates this logger
	 *
	 * @param n name of the logger to instantiate.
	 */
	private Log(final String n)
	{
		name = n;
	}

	/**
	 * Creates a logger for the specified class.
	 *
	 * @param cl a class used as a unique name of the logger.
	 * @return a new logger instance to be used by a class instance.
	 * @see org.slf4j.LoggerFactory#getLogger(Class)
	 * @see Logger#getName()
	 */
	public static Logger getLogger(final Class<?> cl)
	{
		return new Log(cl.getName());
	}

	private Logger realLogger()
	{
		if (realLogger == null)
		{
			realLogger = getRegisteredLogger(name).orElseGet(Log.this::createRealLogger);
		}
		return realLogger;
	}

	private Logger createRealLogger()
	{
		return LoggerFactory.getLogger(name);
	}

	static Optional<Logger> getRegisteredLogger(final String logName)
	{
		return Optional.ofNullable(REGISTERED_LOGGERS.get(logName));
	}

	/**
	 * Registers a logger instance to be used instead of creating a new logger when {@link Log#getLogger(Class)} is called with a
	 * class, for which {@code class.getName().equals(l.getName()}
	 *
	 * @param l a logger instance to use. If {@code null}, then it is ignored.
	 */
	static void register(final Logger l)
	{
		if (l != null)
		{
			REGISTERED_LOGGERS.put(l.getName(), l);
		}
	}

	public String getName()
	{
		return name;
	}

	public boolean isTraceEnabled()
	{
		return realLogger().isTraceEnabled();
	}

	public void trace(final String s)
	{
		realLogger().trace(s);
	}

	public void trace(final String s, final Object o)
	{
		realLogger().trace(s, o);
	}

	public void trace(final String s, final Object o, final Object o1)
	{
		realLogger().trace(s, o, o1);
	}

	public void trace(final String s, final Object... objects)
	{
		realLogger().trace(s, objects);
	}

	public void trace(final String s, final Throwable throwable)
	{
		realLogger().trace(s, throwable);
	}

	public boolean isTraceEnabled(final Marker marker)
	{
		return realLogger().isTraceEnabled(marker);
	}

	public void trace(final Marker marker, final String s)
	{
		realLogger().trace(marker, s);
	}

	public void trace(final Marker marker, final String s, final Object o)
	{
		realLogger().trace(marker, s, o);
	}

	public void trace(final Marker marker, final String s, final Object o, final Object o1)
	{
		realLogger().trace(marker, s, o, o1);
	}

	public void trace(final Marker marker, final String s, final Object... objects)
	{
		realLogger().trace(marker, s, objects);
	}

	public void trace(final Marker marker, final String s, final Throwable throwable)
	{
		realLogger().trace(marker, s, throwable);
	}

	public boolean isDebugEnabled()
	{
		return realLogger().isDebugEnabled();
	}

	public void debug(final String s)
	{
		realLogger().debug(s);
	}

	public void debug(final String s, final Object o)
	{
		realLogger().debug(s, o);
	}

	public void debug(final String s, final Object o, final Object o1)
	{
		realLogger().debug(s, o, o1);
	}

	public void debug(final String s, final Object... objects)
	{
		realLogger().debug(s, objects);
	}

	public void debug(final String s, final Throwable throwable)
	{
		realLogger().debug(s, throwable);
	}

	public boolean isDebugEnabled(final Marker marker)
	{
		return realLogger().isDebugEnabled(marker);
	}

	public void debug(final Marker marker, final String s)
	{
		realLogger().debug(marker, s);
	}

	public void debug(final Marker marker, final String s, final Object o)
	{
		realLogger().debug(marker, s, o);
	}

	public void debug(final Marker marker, final String s, final Object o, final Object o1)
	{
		realLogger().debug(marker, s, o, o1);
	}

	public void debug(final Marker marker, final String s, final Object... objects)
	{
		realLogger().debug(marker, s, objects);
	}

	public void debug(final Marker marker, final String s, final Throwable throwable)
	{
		realLogger().debug(marker, s, throwable);
	}

	public boolean isInfoEnabled()
	{
		return realLogger().isInfoEnabled();
	}

	public void info(final String s)
	{
		realLogger().info(s);
	}

	public void info(final String s, final Object o)
	{
		realLogger().info(s, o);
	}

	public void info(final String s, final Object o, final Object o1)
	{
		realLogger().info(s, o, o1);
	}

	public void info(final String s, final Object... objects)
	{
		realLogger().info(s, objects);
	}

	public void info(final String s, final Throwable throwable)
	{
		realLogger().info(s, throwable);
	}

	public boolean isInfoEnabled(final Marker marker)
	{
		return realLogger().isInfoEnabled(marker);
	}

	public void info(final Marker marker, final String s)
	{
		realLogger().info(marker, s);
	}

	public void info(final Marker marker, final String s, final Object o)
	{
		realLogger().info(marker, s, o);
	}

	public void info(final Marker marker, final String s, final Object o, final Object o1)
	{
		realLogger().info(marker, s, o, o1);
	}

	public void info(final Marker marker, final String s, final Object... objects)
	{
		realLogger().info(marker, s, objects);
	}

	public void info(final Marker marker, final String s, final Throwable throwable)
	{
		realLogger().info(marker, s, throwable);
	}

	public boolean isWarnEnabled()
	{
		return realLogger().isWarnEnabled();
	}

	public void warn(final String s)
	{
		realLogger().warn(s);
	}

	public void warn(final String s, final Object o)
	{
		realLogger().warn(s, o);
	}

	public void warn(final String s, final Object... objects)
	{
		realLogger().warn(s, objects);
	}

	public void warn(final String s, final Object o, final Object o1)
	{
		realLogger().warn(s, o, o1);
	}

	public void warn(final String s, final Throwable throwable)
	{
		realLogger().warn(s, throwable);
	}

	public boolean isWarnEnabled(final Marker marker)
	{
		return realLogger().isWarnEnabled(marker);
	}

	public void warn(final Marker marker, final String s)
	{
		realLogger().warn(marker, s);
	}

	public void warn(final Marker marker, final String s, final Object o)
	{
		realLogger().warn(marker, s, o);
	}

	public void warn(final Marker marker, final String s, final Object o, final Object o1)
	{
		realLogger().warn(marker, s, o, o1);
	}

	public void warn(final Marker marker, final String s, final Object... objects)
	{
		realLogger().warn(marker, s, objects);
	}

	public void warn(final Marker marker, final String s, final Throwable throwable)
	{
		realLogger().warn(marker, s, throwable);
	}

	public boolean isErrorEnabled()
	{
		return realLogger().isErrorEnabled();
	}

	public void error(final String s)
	{
		realLogger().error(s);
	}

	public void error(final String s, final Object o)
	{
		realLogger().error(s, o);
	}

	public void error(final String s, final Object o, final Object o1)
	{
		realLogger().error(s, o, o1);
	}

	public void error(final String s, final Object... objects)
	{
		realLogger().error(s, objects);
	}

	public void error(final String s, final Throwable throwable)
	{
		realLogger().error(s, throwable);
	}

	public boolean isErrorEnabled(final Marker marker)
	{
		return realLogger().isErrorEnabled(marker);
	}

	public void error(final Marker marker, final String s)
	{
		realLogger().error(marker, s);
	}

	public void error(final Marker marker, final String s, final Object o)
	{
		realLogger().error(marker, s, o);
	}

	public void error(final Marker marker, final String s, final Object o, final Object o1)
	{
		realLogger().error(marker, s, o, o1);
	}

	public void error(final Marker marker, final String s, final Object... objects)
	{
		realLogger().error(marker, s, objects);
	}

	public void error(final Marker marker, final String s, final Throwable throwable)
	{
		realLogger().error(marker, s, throwable);
	}
}
