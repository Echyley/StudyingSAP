/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.integrationtests.config;

import de.hybris.platform.cpq.productconfig.services.integrationtests.LoginData;

public class DefaultB2BIntegrationTestConfiguration extends DefaultIntegrationTestConfiguration
		implements IntegrationTestConfiguration
{
	@Override
	public LoginData getLoginData()
	{
		return new LoginData("cpq03", "welcome");
	}

	@Override
	protected void addStandardImpxes()
	{
		super.addStandardImpxes();
		impexToImport.add("/cpqproductconfigservices/test/testDataB2BUsers.impex");
		impexToImport.add("/cpqproductconfigservices/test/testDataSapConfiguration.impex");
	}
}
