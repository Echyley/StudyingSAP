/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.textfieldconfiguratortemplateocctests;

import de.hybris.platform.core.Initialization;
import de.hybris.platform.core.Registry;
import de.hybris.platform.payment.commands.factory.impl.DefaultCommandFactoryRegistryImpl;
import de.hybris.platform.textfieldconfiguratortemplateocctests.setup.TextfieldConfiguratorOCCTestSetup;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.testsupport.server.EmbeddedServerController;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSetupStandalone
{
	private static boolean serverStarted = false;
	private static final Logger LOG = LoggerFactory.getLogger(TestSetupStandalone.class);
	private static final String[] EXTENSIONS_TO_START = new String[]
	{ "commercewebservices", "oauth2" };


	public static void loadData() throws Exception
	{
		LOG.info("Clean data from tenant to prepare test");
		// this cleans up any testdata created by other tests - if removed you may see failiures in pipeline were more tests are executed.
		Initialization.initializeTestSystem();

		final TextfieldConfiguratorOCCTestSetup productConfigOCCTestSetup = Registry.getApplicationContext()
				.getBean("textfieldConfiguratorOCCTestsSetup", TextfieldConfiguratorOCCTestSetup.class);
		productConfigOCCTestSetup.loadData();

	}

	public static void cleanData() throws Exception
	{
		LOG.info("Clean data created for test");
		final DefaultCommandFactoryRegistryImpl commandFactoryReg = Registry.getApplicationContext()
				.getBean("commandFactoryRegistry", DefaultCommandFactoryRegistryImpl.class);
		commandFactoryReg.afterPropertiesSet();
		// this cleans up the testdata created - if removed you may see failiures in pipeline were more tests are executed.
		Initialization.initializeTestSystem();
	}

	public static void startServer()
	{
		if (!serverStarted)
		{
			final String[] ext = EXTENSIONS_TO_START;
			LOG.info("Starting server with extensions: " + Arrays.deepToString(ext));
			if (!Config.getBoolean("ycommercewebservicestest.embedded.server.enabled", true))
			{
				LOG.info("Ignoring embedded server");
				return;
			}

			LOG.info("Starting embedded server");
			final EmbeddedServerController controller = Registry.getApplicationContext().getBean("embeddedServerController",
					EmbeddedServerController.class);
			controller.start(ext);
			LOG.info("embedded server is running");
			serverStarted = true;
		}
		else
		{
			LOG.info("embedded server already running");
		}

	}

	public static void stopServer()
	{
		if (!Config.getBoolean("ycommercewebservicestest.embedded.server.enabled", true))
		{
			LOG.info("Ignoring embedded server");
			return;
		}

		LOG.info("Stopping embedded server");
		final EmbeddedServerController controller = Registry.getApplicationContext().getBean("embeddedServerController",
				EmbeddedServerController.class);
		controller.stop();
		LOG.info("Stopped embedded server");
	}
}
