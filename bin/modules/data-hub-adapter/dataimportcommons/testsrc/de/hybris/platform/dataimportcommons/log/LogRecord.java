/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.dataimportcommons.log;

import java.util.Arrays;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;

/**
 * Represents a record of the captured log output.
 */
@Immutable
public final class LogRecord
{
	private static final LogLevel DEFAULT_LEVEL = LogLevel.TRACE;
	private static final Object[] NO_PARAMS = {};
	private static final String LOG_PARAM_PLACEHOLDER = "{}";
	private static final String STRING_FORMAT_PARAM_PLACEHOLDER = "%s";

	private final LogLevel level;
	private final String message;
	private final Throwable exception;
	private final Marker marker;

	/**
	 * Instantiates this record.
	 *
	 * @param lev level at which the message was logged.
	 * @param msg message that was logged.
	 */
	LogRecord(final LogLevel lev, final String msg)
	{
		this(lev, null, msg, NO_PARAMS, null);
	}

	/**
	 * Instantiates this record.
	 *
	 * @param lev  level at which the message was logged.
	 * @param mark a marker for the logged message.
	 * @param msg  message that was logged.
	 */
	LogRecord(final LogLevel lev, final Marker mark, final String msg)
	{
		this(lev, mark, msg, NO_PARAMS, null);
	}

	/**
	 * Instantiates this record.
	 *
	 * @param lev level at which the message was logged.
	 * @param msg message that was logged.
	 * @param e   an exception that was logged with the message.
	 */
	LogRecord(final LogLevel lev, final String msg, final Throwable e)
	{
		this(lev, null, msg, NO_PARAMS, e);
	}

	/**
	 * Instantiates this record.
	 *
	 * @param lev  level at which the message was logged.
	 * @param mark a marker the message was marked with.
	 * @param msg  message that was logged.
	 * @param e    an exception that was logged with the message.
	 */
	LogRecord(final LogLevel lev, final Marker mark, final String msg, final Throwable e)
	{
		this(lev, mark, msg, NO_PARAMS, e);
	}

	/**
	 * Instantiates this record.
	 *
	 * @param lev    level at which the message was logged.
	 * @param msg    message that was logged.
	 * @param params parameters to be inserted into the message.
	 */
	LogRecord(final LogLevel lev, final String msg, final Object[] params)
	{
		this(lev, null, msg, params, null);
	}

	/**
	 * Instantiates this record.
	 *
	 * @param lev    level at which the message was logged.
	 * @param mark   a marker, with which the message is logged.
	 * @param msg    message that was logged.
	 * @param params parameters to be inserted into the message.
	 */
	LogRecord(final LogLevel lev, final Marker mark, final String msg, final Object[] params)
	{
		this(lev, mark, msg, params, null);
	}

	private LogRecord(final LogLevel lev,
	                  final Marker mark,
	                  final String msg,
	                  final Object[] params,
	                  final Throwable ex)
	{
		final var parameters = params == null ? NO_PARAMS : params;
		level = lev == null ? DEFAULT_LEVEL : lev;
		marker = mark;
		message = formatMessage(msg, parameters);
		exception = ex != null ? ex : deriveException(msg, parameters);
	}

	/**
	 * Retrieves log level, at which the message captured by this record was logged.
	 *
	 * @return log level for this record.
	 */
	@Nonnull
	public LogLevel getLevel()
	{
		return level;
	}

	/**
	 * Retrieves the message logged.
	 *
	 * @return message captured in this log record.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Retrieves exception logged. This means the log was called by passing the exception as a parameter for logging, e.g.
	 * {@link org.slf4j.Logger#debug(String, Throwable)}
	 *
	 * @return an exception that was logged with the message or {@code null}, if the message was logged without an exception.
	 */
	@Nullable
	public Throwable getException()
	{
		return exception;
	}

	/**
	 * Determines whether an exception stack trace was logged with the message by passing exception as a separate parameter into
	 * the logging call. For example, {@link org.slf4j.Logger#error(String, Throwable)}
	 *
	 * @return {@code true}, if the exception stack trace was logged with the message; {@code false} otherwise.
	 */
	public boolean isExceptionLogged()
	{
		return exception != null;
	}

	/**
	 * Retrieves marker used for logging the message.
	 *
	 * @return marker used when the message was logged or {@code null}, if the message was logged without a marker.
	 */
	@Nullable
	public Marker getMarker()
	{
		return marker;
	}

	@Override
	public String toString()
	{
		return exception == null
				? level + " " + message
				: level + " " + message + ": " + exception;
	}

	private static String formatMessage(final String message, final Object[] params)
	{
		if (message != null && params.length > 0)
		{
			final String template = message.replace(LOG_PARAM_PLACEHOLDER, STRING_FORMAT_PARAM_PLACEHOLDER);
			return String.format(template, params);
		}
		return message;
	}

	private static Throwable deriveException(final String msg, final Object[] params)
	{
		/*
		The exception is logged as stack trace only when there is no parameter placeholder for the Throwable in the message.
		For example, given ex is an istance of throwable:
		    logger.error("Something failed", ex) // no {} placeholder at all in the message
		    logger.error("Something failed while processing {}", obj, ex) // {} is consumed by obj and there is no {} for the ex
		    logger.error("Crashed {} while applying {}", obj1, obj2, ex) // both {} are consumed by obj1 and obj2
		When there is a parameter placeholder for the Throwable parameter, e.g:
		    logger.error("Something failed: {}", ex)
		    logger.error("{} processing crashed with {}", obj, ex)
		    logger.error("{} applied {} and threw {}", obj1, obj2, ex)
		 The exception is not logged as the stack trace.
		 Also, only last Throwable parameter outside the parameter placeholders is logged with the stack trace. For example,
		     logger.warn("Processed {} and handled error", obj, ex1, ex2)
		 Will ignore the ex1 parameter completely. It will inject obj parameter into the message and print stack trace of ex2.
		 Therefore, in order to determine whether an exception was logged or not we need to see whether there is no a placeholder
		 for the last Throwable parameter passed into the logger method. See LogUnitTest for the scenarios.
		 */
		final int paramPlaceholdersCnt = StringUtils.countMatches(msg, LOG_PARAM_PLACEHOLDER);
		final Object[] paramsWithoutPlaceholdersInTheMessage = params.length > paramPlaceholdersCnt
				? Arrays.copyOfRange(params, paramPlaceholdersCnt, params.length)
				: NO_PARAMS;
		return isStackTraceLogged(paramsWithoutPlaceholdersInTheMessage)
				? (Throwable) paramsWithoutPlaceholdersInTheMessage[paramsWithoutPlaceholdersInTheMessage.length - 1]
				: null;
	}

	private static boolean isStackTraceLogged(final Object[] params)
	{
		return params.length > 0 && params[params.length - 1] instanceof Throwable; // last parameter is a Throwable
	}

}
