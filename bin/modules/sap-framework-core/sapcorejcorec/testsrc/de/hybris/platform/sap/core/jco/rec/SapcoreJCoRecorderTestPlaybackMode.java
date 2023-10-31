/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.rec;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.sap.conn.jco.JCoFunction;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.impl.DefaultSAPConfigurationService;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.connection.JCoManagedConnectionContainer;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Makes a simple backend call.
 * Prerequisites : SAP_DEFAULT.jcodestination should exist
 */
//@Ignore("GRIFFIN-2206")
@UnitTest
@JCoRecording(mode = JCoRecMode.PLAYBACK, recordingExtensionName = "sapcorejcorec")
public class SapcoreJCoRecorderTestPlaybackMode extends SapcoreJCoRecJUnitTest
{

	/**
	 * Class under test.
	 */
	@Resource
	private JCoManagedConnectionContainer managedConnectionContainer;

	/**
	 * Configuration service.
	 */
	@Resource
	private DefaultSAPConfigurationService defaultSAPConfigurationService;


	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.sap.core.test.SapcoreSpringJUnitTest#setUp()
	 */
	@Override
	public void setUp()
	{
		super.setUp();
		defaultSAPConfigurationService.setRfcDestinationName("SAP_DEFAULT"); //SAP_CRM_714
	}

	/**
	 * Test if recording functionality is working.
	 * 
	 * @throws BackendException
	 *            BackendException
	 */
	@Test
	public void testRecording() throws BackendException
	{
		final JCoConnection managedConnection = managedConnectionContainer.getManagedConnection("JCoStateless");

		final JCoFunction function = managedConnection.getFunction("STFC_CONNECTION");
		function.getImportParameterList().setValue("REQUTEXT", "Hello SAP");

		managedConnection.execute(function);

		final String result = function.getExportParameterList().getString("RESPTEXT");

		Assert.assertNotNull(result);
		Assert.assertTrue(!result.isEmpty());
	}
}
