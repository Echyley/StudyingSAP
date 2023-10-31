/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.common.util;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;


/**
 * Test class for LogDebug.
 */
@UnitTest
public class LogDebugTest
{

	@Test
	@SuppressWarnings("javadoc")
	public void testLogDebug()
	{

		final TestLogger testLog = new TestLogger("");


		final String msg = "The quick brown {0} jumps over the lazy {1} {2} times";
		LogDebug.debug(testLog, msg, "fox", "dogs", 6);

		Assert.assertNotNull(testLog.message);
		Assert.assertEquals("The quick brown fox jumps over the lazy dogs 6 times", testLog.message);
	}


	/**
	 * Test logger.
	 */
	public static class TestLogger extends Logger
	{
		/**
		 * Flag indicating if debug mode is set.
		 */
		public boolean isDebugEnabled = true; //NOPMD
		/**
		 * Receives a message.
		 */
		public String message = null;//NOPMD

		/**
		 * Constructor.
		 * 
		 * @param name
		 *           name
		 */
		public TestLogger(final String name)
		{
			super(name);
		}

		@Override
		public boolean isDebugEnabled()
		{
			return isDebugEnabled;
		}

		@Override
		public void debug(final Object message)
		{
			this.message = message.toString();
		}


	}
}
