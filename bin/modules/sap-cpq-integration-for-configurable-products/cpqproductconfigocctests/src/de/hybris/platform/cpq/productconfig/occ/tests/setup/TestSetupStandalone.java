/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.tests.setup;


import static de.hybris.platform.testframework.runlistener.ItemCreationListener.ITEM_CREATION_LIFECYCLE_LISTENER;

import de.hybris.platform.core.Initialization;
import de.hybris.platform.core.Registry;
import de.hybris.platform.cpq.productconfig.services.impl.DefaultEngineDeterminationService;
import de.hybris.platform.testframework.ItemCreationLifecycleListener;
import de.hybris.platform.webservicescommons.testsupport.server.EmbeddedServerController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;


/**
 * helper class to tun OCC tests
 */
public final class TestSetupStandalone
{
	private static boolean serverStarted = false;
	private static final Logger LOG = LoggerFactory.getLogger(TestSetupStandalone.class);
	private static final String[] EXTENSIONS_TO_START = new String[]
	{ "commercewebservices", "oauth2" };

	private TestSetupStandalone()
	{
		//private constructor to prohibit instantiation
	}

	/**
	 * loads data required for test
	 */
	public static void loadData()
	{
		final CpqProductConfigOCCTestsSetup cpqProductConfigOCCTestSetup = Registry.getApplicationContext()
				.getBean("cpqProductConfigOCCTestSetup", CpqProductConfigOCCTestsSetup.class);
		cpqProductConfigOCCTestSetup.loadData();

	}

	/**
	 * starts the embedded server
	 */
	public static void startServer()
	{
		if (!serverStarted)
		{
			final String[] ext = EXTENSIONS_TO_START;


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


	/**
	 * makes sure that data is cleaned after tests have been executed
	 *
	 * @throws Exception
	 */
	public static void cleanData() throws Exception
	{
		LOG.info("CPQ Clean data created for test");
		Initialization.initializeTestSystem();
		afterInitializeTestSystem();
	}

	/**
	 * Re-register the ItemCreationLifecycleListener in the persistence pool after test system initialization
	 */
	public static void afterInitializeTestSystem()
	{
		final ApplicationContext ctx = Registry.getCoreApplicationContext();
		if (ctx.containsBean(ITEM_CREATION_LIFECYCLE_LISTENER))
		{
			final ItemCreationLifecycleListener itemCreationLifecycleListener = (ItemCreationLifecycleListener) ctx
					.getBean(ITEM_CREATION_LIFECYCLE_LISTENER);
			// re-register listener, unregister first to prevent duplication
			Registry.getCurrentTenant().getPersistencePool().unregisterPersistenceListener(itemCreationLifecycleListener);
			Registry.getCurrentTenant().getPersistencePool().registerPersistenceListener(itemCreationLifecycleListener);
		}
	}

	/**
	 * activates the mock provider, so test can be executed without connection to a real CPQ system necessary
	 */
	public static void ensureMockProvider()
	{
		final DefaultEngineDeterminationService engineDeterminationService = Registry.getApplicationContext()
				.getBean("cpqProductConfigEngineDeterminationService", DefaultEngineDeterminationService.class);
		engineDeterminationService.setMockEngineActive(true);
	}

	public static boolean isGetCPQConfigurationIdForQuoteEntryEndpointDisabled()
	{
		final CpqProductConfigOCCTestsSetup cpqProductConfigOCCTestSetup = Registry.getApplicationContext()
				.getBean("cpqProductConfigOCCTestSetup", CpqProductConfigOCCTestsSetup.class);
		return cpqProductConfigOCCTestSetup.isGetCPQConfigurationIdForQuoteEntryEndpointDisabled();
	}
}
