/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec.impl;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.RepositoryPlayback;
import de.hybris.platform.sap.core.jco.rec.constants.SapcorejcorecConstants;

import java.io.File;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.conn.jco.JCoFunction;


/**
 * Takes a recorded repository file and tries to execute functions from it.
 */
//@Ignore("GRIFFIN-2206")
public class PlaybackTest
{
	private static final String xmlFileName = SapcorejcorecConstants.getCanonicalPath() + "testsrc/de/hybris/platform/sap/core/jco/rec/impl/exampleRepository000.xml";
	private static File xmlFile = new File(xmlFileName);

	/**
	 * The actual test method.
	 * 
	 * @throws BackendException
	 *            if something went wrong during the test.
	 */
	@Test
	public void testPlayback() throws BackendException
	{
		final RepositoryPlayback repoPlay = new RepositoryPlaybackFactoryImpl(xmlFile).createRepositoryPlayback();
		final ConnectionDelegator connection = new ConnectionDelegator(null, JCoRecMode.PLAYBACK, repoPlay, null);

		final JCoFunction function = connection.getFunction("TEST_JCOREC_SIMPLE_CALL");

		Assert.assertNotNull(function);

		try
		{
			connection.getFunction("not_a_function");
			Assert.fail("function should not be found!");
		}
		catch (final BackendException e)
		{
			//okay
		}
	}
}
