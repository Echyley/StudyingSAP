/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;
import org.slf4j.Marker;

import com.google.common.base.MoreObjects;

/**
 * A test rule for retrieving SLF4J {@link org.slf4j.Logger}s with adjusted log level compared to the default logback configuration
 * and asserting that an output has happened.
 */
public final class LoggerProbe extends ExternalResource implements Logger
{
	private static final LogLevel DEFAULT_LOG_LEVEL = LogLevel.TRACE;

	private final Logger logger;
	private final List<LogRecord> loggedRecords;
	private LogLevel logLevel;
	private boolean active;

	private LoggerProbe(final Logger log)
	{
		logger = log;
		logLevel = DEFAULT_LOG_LEVEL;
		loggedRecords = new LinkedList<>();
		active = false;
	}

	/**
	 * Creates a probe for the specified logger.
	 * @param type type, for which a logger should be created.
	 * @return a logger probe to be used in tests
	 */
	public static LoggerProbe create(final Class<?> type)
	{
		final Log log = Log.getLogger(type);
		final LoggerProbe probe = probeFor(log);
		log.setLogger(probe);
		Log.register(log);
		return probe;
	}

	private static LoggerProbe probeFor(final Log l)
	{
		final Logger logger = l.getLogger();
		return logger instanceof LoggerProbe
				? (LoggerProbe) logger
				: new LoggerProbe(logger);
	}

	/**
	 * Sets log level for enabling/disabling output of certain messages.
	 * @param level level to apply for logging messages.
	 * @return a probe with log level applied.
	 */
	public LoggerProbe withLogLevel(final LogLevel level)
	{
		logLevel = level;
		return this;
	}

	@Override
	protected void before()
	{
		active = true;
	}

	@Override
	protected void after()
	{
		active = false;
		loggedRecords.clear();
	}

	/**
	 * Retrieves all records logged by this probe.
	 *
	 * @return a list of records in the order they were logged regardless of their log level
	 */
	public List<LogRecord> getAllLoggedRecords()
	{
		return List.copyOf(loggedRecords);
	}

	/**
	 * Retrieves all message logged by this probe.
	 * @return a list of message logged regardless of their log level
	 */
	public List<String> getAllLoggedMessages()
	{
		return loggedRecords.stream()
		                    .map(LogRecord::getMessage)
		                    .collect(Collectors.toList());
	}

	/**
	 * Retrieves all message logged at INFO level by this probe.
	 * @return a list of message logged at INFO level regardless of whether they had stack trace printed or not.
	 */
	public List<String> getMessagesLoggedAtInfo()
	{
		return getMessagesLoggedAt(LogLevel.INFO, record -> true);
	}

	/**
	 * Retrieves all message logged at INFO level with the stack trace.
	 * @return only messages logged by this probe at INFO level, which had also a stack trace included.
	 */
	public List<String> getMessagesLoggedAtInfoWithStackTrace()
	{
		return getMessagesLoggedAt(LogLevel.INFO, LogRecord::isExceptionLogged);
	}

	/**
	 * Retrieves all message logged at WARN level by this probe.
	 * @return a list of message logged at WARN level regardless of whether they had stack trace printed or not.
	 */
	public List<String> getMessagesLoggedAtWarn()
	{
		return getMessagesLoggedAt(LogLevel.WARN, record -> true);
	}

	/**
	 * Retrieves all message logged at WARN level with the stack trace.
	 * @return only messages logged by this probe at WARN level, which had also a stack trace included.
	 */
	public List<String> getMessagesLoggedAtWarnWithStackTrace()
	{
		return getMessagesLoggedAt(LogLevel.WARN, LogRecord::isExceptionLogged);
	}

	/**
	 * Retrieves all message logged at ERROR level by this probe.
	 * @return a list of message logged at ERROR level regardless of whether they had stack trace printed or not.
	 */
	public List<String> getMessagesLoggedAtError()
	{
		return getMessagesLoggedAt(LogLevel.ERROR, record -> true);
	}

	/**
	 * Retrieves all message logged at ERROR level with the stack trace.
	 * @return only messages logged by this probe at ERROR level, which had also a stack trace included.
	 */
	public List<String> getMessagesLoggedAtErrorWithStackTrace()
	{
		return getMessagesLoggedAt(LogLevel.ERROR, LogRecord::isExceptionLogged);
	}

	/**
	 * Retrieves all message logged at DEBUG level by this probe.
	 * @return a list of message logged at DEBUG level regardless of whether they had stack trace printed or not.
	 */
	public List<String> getMessagesLoggedAtDebug()
	{
		return getMessagesLoggedAt(LogLevel.DEBUG, record -> true);
	}

	/**
	 * Retrieves all message logged at DEBUG level with the stack trace.
	 * @return only messages logged by this probe at DEBUG level, which had also a stack trace included.
	 */
	public List<String> getMessagesLoggedAtDebugWithStackTrace()
	{
		return getMessagesLoggedAt(LogLevel.DEBUG, LogRecord::isExceptionLogged);
	}

	/**
	 * Retrieves all message logged at TRACE level by this probe.
	 * @return a list of message logged at TRACE level regardless of whether they had stack trace printed or not.
	 */
	public List<String> getMessagesLoggedAtTrace()
	{
		return getMessagesLoggedAt(LogLevel.TRACE, record -> true);
	}

	/**
	 * Retrieves all message logged at TRACE level with the stack trace.
	 * @return only messages logged by this probe at TRACE level, which had also a stack trace included.
	 */
	public List<String> getMessagesLoggedAtTraceWithStackTrace()
	{
		return getMessagesLoggedAt(LogLevel.TRACE, LogRecord::isExceptionLogged);
	}

	private List<String> getMessagesLoggedAt(final LogLevel level, final Predicate<LogRecord> condition)
	{
		return loggedRecords.stream()
		                    .filter(r -> r.getLevel() == level)
		                    .filter(condition)
		                    .map(LogRecord::getMessage)
		                    .collect(Collectors.toList());
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("logger", logger)
				.add("level", logLevel)
				.add("records", loggedRecords)
				.toString();
	}

	// implementation of the Logger interface

	@Override
	public String getName()
	{
		return logger.getName();
	}

	@Override
	public boolean isTraceEnabled()
	{
		return logLevel.isEnabled(LogLevel.TRACE);
	}

	@Override
	public void trace(final String s)
	{
		log(new LogRecord(LogLevel.TRACE, s));
		logger.trace(s);
	}

	@Override
	public void trace(final String s, final Object o)
	{
		log(new LogRecord(LogLevel.TRACE, s, params(o)));
		logger.trace(s, o);
	}

	@Override
	public void trace(final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.TRACE, s, params(o1, o2)));
		logger.trace(s, o1, o2);
	}

	@Override
	public void trace(final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.TRACE, s, objects));
		logger.trace(s, objects);
	}

	@Override
	public void trace(final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.TRACE, s, throwable));
		logger.trace(s, throwable);
	}

	@Override
	public boolean isTraceEnabled(final Marker marker)
	{
		return isTraceEnabled();
	}

	@Override
	public void trace(final Marker marker, final String s)
	{
		log(new LogRecord(LogLevel.TRACE, marker, s));
		logger.trace(marker, s);
	}

	@Override
	public void trace(final Marker marker, final String s, final Object o)
	{
		log(new LogRecord(LogLevel.TRACE, marker, s, params(o)));
		logger.trace(marker, s, o);
	}

	@Override
	public void trace(final Marker marker, final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.TRACE, marker, s, params(o1, o2)));
		logger.trace(marker, s, o1, o2);
	}

	@Override
	public void trace(final Marker marker, final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.TRACE, marker, s, objects));
		logger.trace(marker, s, objects);
	}

	@Override
	public void trace(final Marker marker, final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.TRACE, marker, s, throwable));
		logger.trace(marker, s, throwable);
	}

	@Override
	public boolean isDebugEnabled()
	{
		return logLevel.isEnabled(LogLevel.DEBUG);
	}

	@Override
	public void debug(final String s)
	{
		log(new LogRecord(LogLevel.DEBUG, s));
		logger.debug(s);
	}

	@Override
	public void debug(final String s, final Object o)
	{
		log(new LogRecord(LogLevel.DEBUG, s, params(o)));
		logger.debug(s, o);
	}

	@Override
	public void debug(final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.DEBUG, s, params(o1, o2)));
		logger.debug(s, o1, o2);
	}

	@Override
	public void debug(final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.DEBUG, s, objects));
		logger.debug(s, objects);
	}

	@Override
	public void debug(final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.DEBUG, s, throwable));
		logger.debug(s, throwable);
	}

	@Override
	public boolean isDebugEnabled(final Marker marker)
	{
		return isDebugEnabled();
	}

	@Override
	public void debug(final Marker marker, final String s)
	{
		log(new LogRecord(LogLevel.DEBUG, marker, s));
		logger.debug(marker, s);
	}

	@Override
	public void debug(final Marker marker, final String s, final Object o)
	{
		log(new LogRecord(LogLevel.DEBUG, marker, s, params(o)));
		logger.debug(marker, s, o);
	}

	@Override
	public void debug(final Marker marker, final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.DEBUG, marker, s, params(o1, o2)));
		logger.debug(marker, s, o1, o2);
	}

	@Override
	public void debug(final Marker marker, final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.DEBUG, marker, s, objects));
		logger.debug(marker, s, objects);
	}

	@Override
	public void debug(final Marker marker, final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.DEBUG, marker, s, throwable));
		logger.debug(marker, s, throwable);
	}

	@Override
	public boolean isInfoEnabled()
	{
		return logLevel.isEnabled(LogLevel.INFO);
	}

	@Override
	public void info(final String s)
	{
		log(new LogRecord(LogLevel.INFO, s));
		logger.info(s);
	}

	@Override
	public void info(final String s, final Object o)
	{
		log(new LogRecord(LogLevel.INFO, s, params(o)));
		logger.info(s, o);
	}

	@Override
	public void info(final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.INFO, s, params(o1, o2)));
		logger.info(s, o1, o2);
	}

	@Override
	public void info(final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.INFO, s, objects));
		logger.info(s, objects);
	}

	@Override
	public void info(final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.INFO, s, throwable));
		logger.info(s, throwable);
	}

	@Override
	public boolean isInfoEnabled(final Marker marker)
	{
		return isInfoEnabled();
	}

	@Override
	public void info(final Marker marker, final String s)
	{
		log(new LogRecord(LogLevel.INFO, marker, s));
		logger.info(marker, s);
	}

	@Override
	public void info(final Marker marker, final String s, final Object o)
	{
		log(new LogRecord(LogLevel.INFO, marker, s, params(o)));
		logger.info(marker, s, o);
	}

	@Override
	public void info(final Marker marker, final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.INFO, marker, s, params(o1, o2)));
		logger.info(marker, s, o1, o2);
	}

	@Override
	public void info(final Marker marker, final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.INFO, marker, s, objects));
		logger.info(marker, s, objects);
	}

	@Override
	public void info(final Marker marker, final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.INFO, marker, s, throwable));
		logger.info(marker, s, throwable);
	}

	@Override
	public boolean isWarnEnabled()
	{
		return logLevel.isEnabled(LogLevel.WARN);
	}

	@Override
	public void warn(final String s)
	{
		log(new LogRecord(LogLevel.WARN, s));
		logger.warn(s);
	}

	@Override
	public void warn(final String s, final Object o)
	{
		log(new LogRecord(LogLevel.WARN, s, params(o)));
		logger.warn(s, o);
	}

	@Override
	public void warn(final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.WARN, s, objects));
		logger.warn(s, objects);
	}

	@Override
	public void warn(final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.WARN, s, params(o1, o2)));
		logger.warn(s, o1, o2);
	}

	@Override
	public void warn(final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.WARN, s, throwable));
		logger.warn(s, throwable);
	}

	@Override
	public boolean isWarnEnabled(final Marker marker)
	{
		return isWarnEnabled();
	}

	@Override
	public void warn(final Marker marker, final String s)
	{
		log(new LogRecord(LogLevel.WARN, marker, s));
		logger.warn(marker, s);
	}

	@Override
	public void warn(final Marker marker, final String s, final Object o)
	{
		log(new LogRecord(LogLevel.WARN, marker, s, params(o)));
		logger.warn(marker, s, o);
	}

	@Override
	public void warn(final Marker marker, final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.WARN, marker, s, params(o1, o2)));
		logger.warn(marker, s, o1, o2);
	}

	@Override
	public void warn(final Marker marker, final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.WARN, marker, s, objects));
		logger.warn(marker, s, objects);
	}

	@Override
	public void warn(final Marker marker, final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.WARN, marker, s, throwable));
		logger.warn(marker, s, throwable);
	}

	@Override
	public boolean isErrorEnabled()
	{
		return logLevel.isEnabled(LogLevel.ERROR);
	}

	@Override
	public void error(final String s)
	{
		log(new LogRecord(LogLevel.ERROR, s));
		logger.error(s);
	}

	@Override
	public void error(final String s, final Object o)
	{
		log(new LogRecord(LogLevel.ERROR, s, params(o)));
		logger.error(s, o);
	}

	@Override
	public void error(final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.ERROR, s, params(o1, o2)));
		logger.error(s, o1, o2);
	}

	@Override
	public void error(final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.ERROR, s, objects));
		logger.error(s, objects);
	}

	@Override
	public void error(final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.ERROR, s, throwable));
		logger.error(s, throwable);
	}

	@Override
	public boolean isErrorEnabled(final Marker marker)
	{
		return isErrorEnabled();
	}

	@Override
	public void error(final Marker marker, final String s)
	{
		log(new LogRecord(LogLevel.ERROR, marker, s));
		logger.error(marker, s);
	}

	@Override
	public void error(final Marker marker, final String s, final Object o)
	{
		log(new LogRecord(LogLevel.ERROR, marker, s, params(o)));
		logger.error(marker, s, o);
	}

	@Override
	public void error(final Marker marker, final String s, final Object o1, final Object o2)
	{
		log(new LogRecord(LogLevel.ERROR, s, params(o1, o2)));
		logger.error(marker, s, o1, o2);
	}

	@Override
	public void error(final Marker marker, final String s, final Object... objects)
	{
		log(new LogRecord(LogLevel.ERROR, marker, s, objects));
		logger.error(marker, s, objects);
	}

	@Override
	public void error(final Marker marker, final String s, final Throwable throwable)
	{
		log(new LogRecord(LogLevel.ERROR, marker, s, throwable));
		logger.error(marker, s, throwable);
	}

	private void log(final LogRecord record)
	{
		if (active && logLevel.isEnabled(record.getLevel()))
		{
			loggedRecords.add(record);
		}
	}

	private static Object[] params(final Object o)
	{
		return new Object[]{o};
	}

	private static Object[] params(final Object o1, final Object o2)
	{
		return new Object[]{o1, o2};
	}
}
