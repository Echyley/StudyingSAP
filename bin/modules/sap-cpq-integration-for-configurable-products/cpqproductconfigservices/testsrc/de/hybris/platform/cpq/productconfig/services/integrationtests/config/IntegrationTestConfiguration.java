/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.integrationtests.config;

import de.hybris.platform.cpq.productconfig.services.integrationtests.CPQEngineForTest;
import de.hybris.platform.cpq.productconfig.services.integrationtests.LoginData;

import java.util.List;


/**
 * Configuration container for integration test execution
 */
public interface IntegrationTestConfiguration
{
	/**
	 * @return list of impex files to import
	 */
	public List<String> getImpexToImport();

	/**
	 * @return {@link CPQEngineForTest} to be used, default is MOCK
	 */
	public default CPQEngineForTest getEngine()
	{
		return CPQEngineForTest.MOCK;
	}

	/**
	 * @return Login data to be used for test
	 */
	public default LoginData getLoginData()
	{
		return new LoginData("cpq01", "welcome");
	}

	/**
	 * @return base site to be used for test. default is 'testConfigureSite'
	 */
	public default String getBaseSite()
	{
		return "testConfigureSite";
	}

	/**
	 * @param impexPath
	 *           adds an additional impex to be imported
	 */
	public void addAdditionalImpex(final String impexPath);

}
