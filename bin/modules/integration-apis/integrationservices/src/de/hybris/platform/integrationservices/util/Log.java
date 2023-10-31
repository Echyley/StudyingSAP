/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * A wrapper around {@link Logger} to avoid logging exception traces unless DEBUG logging level is active.
 */
public final class Log implements Logger
{
	private static final Map<String, Log> REGISTERED_LOGGERS = new HashMap<>();
	private static Boolean runningInTest;

	private final String name;
	private Logger logger;

	Log(final String n)
	{
		name = n;
	}

	/**
	 * Retrieves a logger for the specified class.
	 *
	 * @param cl a class for which the logger should be retrieved.
	 * @return a logger for the class
	 */
	public static Log getLogger(final Class<?> cl)
	{
		return getLogger(cl.getName());
	}

	/**
	 * Retrieves a logger with the specified name.
	 *
	 * @param name name of the logger to be retrieved.
	 * @return a logger corresponding to the name specified.
	 */
	public static Log getLogger(final String name)
	{
		return isCalledFromTest()
				? REGISTERED_LOGGERS.computeIfAbsent(name, Log::new)
				: new Log(name);
	}

	Logger getLogger()
	{
		if (logger == null)
		{
			logger = LoggerFactory.getLogger(name);
		}
		return logger;
	}

	void setLogger(final Logger l)
	{
		logger = l;
	}

	static void register(@NotNull final Log logger)
	{
		REGISTERED_LOGGERS.put(logger.getName(), logger);
	}

	private static boolean isCalledFromTest()
	{
		if (runningInTest == null)
		{
			runningInTest = Stream.of(new Exception().getStackTrace())
			                      .map(StackTraceElement::getClassName)
			                      .anyMatch(el -> el.endsWith("PlatformSpecRunner") || el.endsWith("JUnitTestRunner"));
		}
		return runningInTest;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isTraceEnabled()
	{
		return getLogger().isTraceEnabled();
	}

	@Override
	public void trace(final String s)
	{
		getLogger().trace(s);
	}

	@Override
	public void trace(final String s, final Object o)
	{
		getLogger().trace(s, o);
	}

	@Override
	public void trace(final String s, final Object o, final Object o1)
	{
		getLogger().trace(s, o, o1);
	}

	@Override
	public void trace(final String s, final Object... objects)
	{
		getLogger().trace(s, objects);
	}

	@Override
	public void trace(final String s, final Throwable throwable)
	{
		getLogger().trace(s, throwable);
	}

	@Override
	public boolean isTraceEnabled(final Marker marker)
	{
		return getLogger().isTraceEnabled(marker);
	}

	@Override
	public void trace(final Marker marker, final String s)
	{
		getLogger().trace(marker, s);
	}

	@Override
	public void trace(final Marker marker, final String s, final Object o)
	{
		getLogger().trace(marker, s, o);
	}

	@Override
	public void trace(final Marker marker, final String s, final Object o, final Object o1)
	{
		getLogger().trace(marker, s, o, o1);
	}

	@Override
	public void trace(final Marker marker, final String s, final Object... objects)
	{
		getLogger().trace(marker, s, objects);
	}

	@Override
	public void trace(final Marker marker, final String s, final Throwable throwable)
	{
		getLogger().trace(marker, s, throwable);
	}

	@Override
	public boolean isDebugEnabled()
	{
		return getLogger().isDebugEnabled();
	}

	@Override
	public void debug(final String s)
	{
		getLogger().debug(s);
	}

	@Override
	public void debug(final String s, final Object o)
	{
		getLogger().debug(s, o);
	}

	@Override
	public void debug(final String s, final Object o, final Object o1)
	{
		getLogger().debug(s, o, o1);
	}

	@Override
	public void debug(final String s, final Object... objects)
	{
		getLogger().debug(s, objects);
	}

	@Override
	public void debug(final String s, final Throwable throwable)
	{
		getLogger().debug(s, throwable);
	}

	@Override
	public boolean isDebugEnabled(final Marker marker)
	{
		return getLogger().isDebugEnabled(marker);
	}

	@Override
	public void debug(final Marker marker, final String s)
	{
		getLogger().debug(marker, s);
	}

	@Override
	public void debug(final Marker marker, final String s, final Object o)
	{
		getLogger().debug(marker, s, o);
	}

	@Override
	public void debug(final Marker marker, final String s, final Object o, final Object o1)
	{
		getLogger().debug(marker, s, o, o1);
	}

	@Override
	public void debug(final Marker marker, final String s, final Object... objects)
	{
		getLogger().debug(marker, s, objects);
	}

	@Override
	public void debug(final Marker marker, final String s, final Throwable throwable)
	{
		getLogger().debug(marker, s, throwable);
	}

	@Override
	public boolean isInfoEnabled()
	{
		return getLogger().isInfoEnabled();
	}

	@Override
	public void info(final String s)
	{
		getLogger().info(s);
	}

	@Override
	public void info(final String s, final Object o)
	{
		getLogger().info(s, o);
	}

	@Override
	public void info(final String s, final Object o, final Object o1)
	{
		final Object[] params = { o, o1 };
		info(s, params);
	}

	@Override
	public void info(final String s, final Object... objects)
	{
		final Object[] params = stackTrackSafeParams(objects);
		getLogger().info(s, params);
	}

	@Override
	public void info(final String s, final Throwable throwable)
	{
		final Object[] params = { throwable };
		info(s, params);
	}

	@Override
	public boolean isInfoEnabled(final Marker marker)
	{
		return getLogger().isInfoEnabled(marker);
	}

	@Override
	public void info(final Marker marker, final String s)
	{
		getLogger().info(marker, s);
	}

	@Override
	public void info(final Marker marker, final String s, final Object o)
	{
		getLogger().info(marker, s, o);
	}

	@Override
	public void info(final Marker marker, final String s, final Object o, final Object o1)
	{
		final Object[] params = { o, o1 };
		info(marker, s, params);
	}

	@Override
	public void info(final Marker marker, final String s, final Object... objects)
	{
		final Object[] params = stackTrackSafeParams(objects);
		getLogger().info(marker, s, params);
	}

	@Override
	public void info(final Marker marker, final String s, final Throwable throwable)
	{
		final Object[] params = { throwable };
		info(marker, s, params);
	}

	@Override
	public boolean isWarnEnabled()
	{
		return getLogger().isWarnEnabled();
	}

	@Override
	public void warn(final String s)
	{
		getLogger().warn(s);
	}

	@Override
	public void warn(final String s, final Object o)
	{
		getLogger().warn(s, o);
	}

	@Override
	public void warn(final String s, final Object... objects)
	{
		final Object[] params = stackTrackSafeParams(objects);
		getLogger().warn(s, params);
	}

	@Override
	public void warn(final String s, final Object o, final Object o1)
	{
		final Object[] params = { o, o1 };
		warn(s, params);
	}

	@Override
	public void warn(final String s, final Throwable throwable)
	{
		final Object[] params = { throwable };
		warn(s, params);
	}

	@Override
	public boolean isWarnEnabled(final Marker marker)
	{
		return getLogger().isWarnEnabled(marker);
	}

	@Override
	public void warn(final Marker marker, final String s)
	{
		getLogger().warn(marker, s);
	}

	@Override
	public void warn(final Marker marker, final String s, final Object o)
	{
		getLogger().warn(marker, s, o);
	}

	@Override
	public void warn(final Marker marker, final String s, final Object o, final Object o1)
	{
		final Object[] params = { o, o1 };
		warn(marker, s, params);
	}

	@Override
	public void warn(final Marker marker, final String s, final Object... objects)
	{
		final Object[] params = stackTrackSafeParams(objects);
		getLogger().warn(marker, s, params);
	}

	@Override
	public void warn(final Marker marker, final String s, final Throwable throwable)
	{
		final Object[] params = { throwable };
		warn(marker, s, params);
	}

	@Override
	public boolean isErrorEnabled()
	{
		return getLogger().isErrorEnabled();
	}

	@Override
	public void error(final String s)
	{
		getLogger().error(s);
	}

	@Override
	public void error(final String s, final Object o)
	{
		getLogger().error(s, o);
	}

	@Override
	public void error(final String s, final Object o, final Object o1)
	{
		final Object[] params = { o, o1 };
		error(s, params);
	}

	@Override
	public void error(final String s, final Object... objects)
	{
		final Object[] params = stackTrackSafeParams(objects);
		getLogger().error(s, params);
	}

	@Override
	public void error(final String s, final Throwable throwable)
	{
		final Object[] params = { throwable };
		error(s, params);
	}

	@Override
	public boolean isErrorEnabled(final Marker marker)
	{
		return getLogger().isErrorEnabled(marker);
	}

	@Override
	public void error(final Marker marker, final String s)
	{
		getLogger().error(marker, s);
	}

	@Override
	public void error(final Marker marker, final String s, final Object o)
	{
		getLogger().error(marker, s, o);
	}

	@Override
	public void error(final Marker marker, final String s, final Object o, final Object o1)
	{
		final Object[] params = { o, o1 };
		error(marker, s, params);
	}

	@Override
	public void error(final Marker marker, final String s, final Object... objects)
	{
		final Object[] params = stackTrackSafeParams(objects);
		getLogger().error(marker, s, params);
	}

	@Override
	public void error(final Marker marker, final String s, final Throwable throwable)
	{
		final Object[] params = { throwable };
		error(marker, s, params);
	}

	private Object[] stackTrackSafeParams(final Object[] objects)
	{
		return isDebugEnabled() || !isStackTraceLogged(objects)
				? objects
				: removeStackTraceFromBeingLogged(objects);
	}

	private Object[] removeStackTraceFromBeingLogged(final Object[] objects)
	{
		final Object[] objectsWithoutLastElement = Arrays.copyOf(objects, objects.length - 1);
		return stackTrackSafeParams(objectsWithoutLastElement);
	}

	private static boolean isStackTraceLogged(final Object[] params)
	{
		return params.length > 0 && params[params.length - 1] instanceof Throwable; // last parameter is a Throwable
	}
}
