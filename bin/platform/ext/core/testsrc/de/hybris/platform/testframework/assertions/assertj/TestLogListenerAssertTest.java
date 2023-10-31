/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.testframework.assertions.assertj;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.testframework.log.TestLogListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IntegrationTest
public class TestLogListenerAssertTest extends ServicelayerBaseTest
{
	private static final Logger LOG = LoggerFactory.getLogger(TestLogListenerAssertTest.class);
	private final TestLogListener testLogListener = new TestLogListener();

	@Before
	public void setUp()
	{
		testLogListener.attach();
	}

	@After
	public void tearDown()
	{
		testLogListener.detach();
	}

	@Test
	public void shouldAssertWithDecreasingAbstractionOfAssertions()
	{
		logWarnWithException();
		logWarn();
		logInfo();

		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .loggedFrom(TestLogListenerAssertTest.class)
		                     .occurrences(3);

		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .loggedFrom(TestLogListenerAssertTest.class)
		                     .withMessageContaining("logWarn")
		                     .occurrences(2);

		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .withMessageContaining("logWarn")
		                     .loggedFrom(TestLogListenerAssertTest.class)
		                     .withThrowable(CustomRuntimeException.class)
		                     .withLogLevel(TestLogListenerAssert.LogLevel.WARN)
		                     .occurrences(1);

		TestLogListenerAssert.assertThat(testLogListener)
		                     .hasLog()
		                     .withMessageContaining("logInfo")
		                     .loggedFrom(TestLogListenerAssertTest.class)
		                     .withLogLevel(TestLogListenerAssert.LogLevel.INFO)
		                     .occurrences(1);
	}

	private void logWarnWithException()
	{
		final CustomRuntimeException runtimeException = new CustomRuntimeException();
		LOG.warn("logWarn", runtimeException);
	}

	private void logWarn()
	{
		LOG.warn("logWarn");
	}

	private void logInfo()
	{
		LOG.info("logInfo");
	}


	private static class CustomRuntimeException extends RuntimeException
	{
	}
}
