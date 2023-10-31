/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.integrationtests.config;

import java.util.ArrayList;
import java.util.List;


/**
 * Default Config for integration testing with CPQ. CPQ System is mocked.
 */
public class DefaultIntegrationTestConfiguration implements IntegrationTestConfiguration
{

	protected final List<String> impexToImport = new ArrayList<>();

	public DefaultIntegrationTestConfiguration()
	{
		initImpexList();
	}

	@Override
	public List<String> getImpexToImport()
	{
		return impexToImport;
	}

	protected void initImpexList()
	{
		addStandardImpxes();
	}

	protected void addStandardImpxes()
	{
		impexToImport.add("/cpqproductconfigservices/test/testDataUsers.impex");
		impexToImport.add("/cpqproductconfigservices/test/testDataProducts.impex");
		impexToImport.add("/cpqproductconfigservices/test/testDataStock.impex");
	}

	public void addAdditionalImpex(final String impexPath)
	{
		impexToImport.add(impexPath);
	}


}
