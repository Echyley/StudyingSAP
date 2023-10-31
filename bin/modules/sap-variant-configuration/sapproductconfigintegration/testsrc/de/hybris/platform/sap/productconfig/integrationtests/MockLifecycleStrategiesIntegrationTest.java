/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integrationtests;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.sap.productconfig.facades.integrationtests.LifecycleStrategiesIntegrationTestBase;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import javax.annotation.Resource;


@IntegrationTest
public class MockLifecycleStrategiesIntegrationTest extends LifecycleStrategiesIntegrationTestBase
{
	@Resource(name = "sapProductConfigSessionAccessService")
	private SessionAccessService sessionAccess;


	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCsv("/sapproductconfigintegration/test/sapProductConfig_sapconfiguration_testData.impex", "utf-8");
		importCsv("/sapproductconfigservices/test/sapProductConfig_changeableVariants_testData.impex", "utf-8");
		importCsv("/sapproductconfigintegration/test/sapProductConfig_SAPVariants_testData.impex", "utf-8"); //ERPVariantProduct
	}


	@Override
	protected void makeNewSessionByLoggingOutAndIn(final String userName) throws InvalidCredentialsException
	{
		// "rescue" configuration provider into new session, so we can test with persistent strategies
		final ConfigurationProvider configProvider = providerFactory.getConfigurationProvider();
		logout();
		login(userName, PASSWORD);
	}
}


