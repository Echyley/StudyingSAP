/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsombol.integrationtests.base;

import de.hybris.platform.sap.core.configuration.impl.DefaultSAPConfigurationService;
import de.hybris.platform.sap.core.jco.rec.JCoRecMode;
import de.hybris.platform.sap.core.jco.rec.JCoRecording;
import de.hybris.platform.util.Utilities;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration(locations =
{ "classpath:test/integration_test-sapcore-connection-spring.xml" })
@JCoRecording(mode = JCoRecMode.PLAYBACK, recordingExtensionName = "sapproductconfigsombol")
@SuppressWarnings("javadoc")
public class JCoIntegrationTestBase extends JCORecTestBase
{

	@Resource
	DefaultSAPConfigurationService defaultSAPConfigurationService;

	public final static String DATA_PATH_PREFIX = "resources/test/";

	@Override
	public void setUp()
	{
		super.setUp();

		defaultSAPConfigurationService.setRfcDestinationName("SAP_ERP_617");

	}

	public static String getCanonicalPathOfExtensionsapproductconfigsombolTest() throws IOException
	{
		return Utilities.getExtensionInfo("sapproductconfigsombol").getExtensionDirectory().getCanonicalPath() + File.separator;
	}

}
