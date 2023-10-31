/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.b2b.integrationtests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.impl.DefaultQuoteFacade;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.integrationtests.CPQFacadeLayerTest;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.exceptions.ProductConfigurationAccessException;
import de.hybris.platform.servicelayer.security.auth.InvalidCredentialsException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Integration test for Product Configuration B2B Access Control Integration
 */
@IntegrationTest
public class ProductConfigurationB2BAccessControlIntegrationTest extends CPQFacadeLayerTest
{
	private static final String USER_NAME_B2B_1 = "cpq03";
	private static final String USER_NAME_B2B_2 = "cpq04";
	private static final String USER_NAME_B2B_DIFFERENT_ROOT_UNIT = "cpq10";

	@Resource(name = "sapProductConfigSessionAccessService")
	protected SessionAccessService sessionAccess;
	@Resource(name = "defaultQuoteFacade")
	protected DefaultQuoteFacade defaultQuoteFacade;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	protected boolean isPersistentLifecycle;

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();
		isPersistentLifecycle = isPersistentLifecycle();
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
		importCPQStockData();
		importCsvIfExist("/sapproductconfigintegration/test/sapProductConfig_sapconfiguration_testData.impex", "utf-8");
		importCsv("/sapproductconfigfacades/test/sapProductConfig_quote_testData.impex", "utf-8");
		importCsv("/sapproductconfigservices/test/sapProductConfig_b2b_userTestData.impex", "utf-8");
		importCsv("/sapproductconfigservices/test/sapProductConfig_b2b_distributionChannelMappingTestData.impex", "utf-8");
	}

	@Test
	public void testReadingQuoteConfigurationFromDifferentB2BUnitFails()
			throws InvalidCredentialsException, CommerceCartModificationException, InvalidCartException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME_B2B_1, PASSWORD);
		final ConfigurationData configOwnedByCPQ03 = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		final String configId = configOwnedByCPQ03.getConfigId();
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ03);
		final String quoteCode = defaultQuoteFacade.initiateQuote().getCode();
		final Integer orderEntryNum = defaultQuoteFacade.getQuoteForCode(quoteCode).getEntries().get(0).getEntryNumber();
		final ConfigurationOverviewData configOverview = configQuoteIntegrationFacade.getConfiguration(quoteCode, orderEntryNum);

		final ConfigurationData orderConfigOwnedByCPQ03 = new ConfigurationData();
		orderConfigOwnedByCPQ03.setKbKey(KB_KEY_CPQ_LAPTOP);
		orderConfigOwnedByCPQ03.setConfigId(configOverview.getId());
		logout();
		login(USER_NAME_B2B_DIFFERENT_ROOT_UNIT, PASSWORD);
		assertTrue(configurationLifecycleStrategy.isConfigKnown(configId));
		assertFalse(configurationLifecycleStrategy.isConfigForCurrentUser(configId));
		// this should fail!
		thrown.expect(ProductConfigurationAccessException.class);
		cpqFacade.getConfiguration(orderConfigOwnedByCPQ03);
	}

	@Test
	public void testReadingQuoteConfigurationFromSameRootB2BUnitWithDifferentUsers()
			throws InvalidCredentialsException, CommerceCartModificationException, InvalidCartException
	{
		assumeTrue("Test makes only sense in context of persistent lifecycle", isPersistentLifecycle);
		login(USER_NAME_B2B_1, PASSWORD);
		final ConfigurationData configOwnedByCPQ03 = cpqFacade.getConfiguration(KB_KEY_CPQ_LAPTOP);
		final String configId = configOwnedByCPQ03.getConfigId();
		cpqCartFacade.addConfigurationToCart(configOwnedByCPQ03);
		final String quoteCode = defaultQuoteFacade.initiateQuote().getCode();
		final Integer orderEntryNum = defaultQuoteFacade.getQuoteForCode(quoteCode).getEntries().get(0).getEntryNumber();
		final ConfigurationOverviewData configOverview = configQuoteIntegrationFacade.getConfiguration(quoteCode, orderEntryNum);

		final ConfigurationData orderConfigOwnedByCPQ03 = new ConfigurationData();
		orderConfigOwnedByCPQ03.setKbKey(KB_KEY_CPQ_LAPTOP);
		orderConfigOwnedByCPQ03.setConfigId(configOverview.getId());
		logout();
		login(USER_NAME_B2B_2, PASSWORD);
		assertTrue(configurationLifecycleStrategy.isConfigKnown(configId));
		assertTrue(configurationLifecycleStrategy.isConfigForCurrentUser(configId));
		// this should be ok as both users have the same root B2BUnit!
		cpqFacade.getConfiguration(orderConfigOwnedByCPQ03);
	}

}

