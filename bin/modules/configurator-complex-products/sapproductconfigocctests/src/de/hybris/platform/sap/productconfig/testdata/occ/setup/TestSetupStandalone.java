/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.testdata.occ.setup;

import de.hybris.platform.core.Initialization;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.jalo.JaloObjectNoLongerValidException;
import de.hybris.platform.payment.commands.factory.impl.DefaultCommandFactoryRegistryImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.SwitchableProviderFactory;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.webservicescommons.testsupport.server.EmbeddedServerController;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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


	public static void loadData(final boolean loadSolrData)
	{
		final CommonI18NService commonI18nService = Registry.getApplicationContext().getBean("commonI18NService",
				CommonI18NService.class);
		final List<CurrencyModel> allCurrencies = commonI18nService.getAllCurrencies();
		if (allCurrencies.isEmpty())
		{
			LOG.info("No currencies available");
		}
		else
		{
			final CurrencyModel currencyModel = allCurrencies.get(0);
			if (LOG.isInfoEnabled())
			{
				LOG.info(String.format("Using currency: %s", currencyModel.getIsocode()));
			}
			commonI18nService.setCurrentCurrency(currencyModel);
		}



		final ProductConfigOCCTestsSetup productConfigOCCTestSetup = Registry.getApplicationContext()
				.getBean("sapProductConfigOCCTestsSetup", ProductConfigOCCTestsSetup.class);
		productConfigOCCTestSetup.loadData(loadSolrData);

	}

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


	public static void cleanData() throws Exception
	{
		LOG.info("Clean data created for test");
		final DefaultCommandFactoryRegistryImpl commandFactoryReg = Registry.getApplicationContext()
				.getBean("commandFactoryRegistry", DefaultCommandFactoryRegistryImpl.class);
		commandFactoryReg.afterPropertiesSet();
		// This cleans up the test data created - if removed you will see failures in pipeline were more tests are executed.
		try
		{
			Initialization.initializeTestSystem();
		}
		catch (final JaloObjectNoLongerValidException ex)
		{
			// not sure why we see this, but this should not fail the test
			LOG.debug("JaloObjectNoLongerValidException", ex);
		}
	}

	public static void ensureMockProvider()
	{

		final SwitchableProviderFactory providerFactory = Registry.getApplicationContext()
				.getBean("sapProductConfigProviderFactory", SwitchableProviderFactory.class);
		providerFactory.switchProviderFactory("sapProductConfigMockProviderFactory");
	}

	public static void ensureSupportProductConfigMessagesActive()
	{
		final ProductConfigOCCTestsSetup productConfigOCCTestSetup = Registry.getApplicationContext()
				.getBean("sapProductConfigOCCTestsSetup", ProductConfigOCCTestsSetup.class);
		productConfigOCCTestSetup.ensureSupportProductConfigMessagesActive();
	}

	public static void ensureSupportProductConfigMessagesInactive()
	{
		final ProductConfigOCCTestsSetup productConfigOCCTestSetup = Registry.getApplicationContext()
				.getBean("sapProductConfigOCCTestsSetup", ProductConfigOCCTestsSetup.class);
		productConfigOCCTestSetup.ensureSupportProductConfigMessagesInactive();
	}

	public static void ensureGetDefaultConfigurationEnhancementsActive()
	{
		final ProductConfigOCCTestsSetup productConfigOCCTestSetup = Registry.getApplicationContext()
				.getBean("sapProductConfigOCCTestsSetup", ProductConfigOCCTestsSetup.class);
		productConfigOCCTestSetup.ensureGetDefaultConfigurationEnhancementsActive();
	}

	public static void ensureGetDefaultConfigurationEnhancementsInactive()
	{
		final ProductConfigOCCTestsSetup productConfigOCCTestSetup = Registry.getApplicationContext()
				.getBean("sapProductConfigOCCTestsSetup", ProductConfigOCCTestsSetup.class);
		productConfigOCCTestSetup.ensureGetDefaultConfigurationEnhancementsInactive();
	}

}
