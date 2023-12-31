/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.prodconf.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.PriceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.PriceModelImpl;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;

import java.math.BigDecimal;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("javadoc")
@UnitTest
public class DefaultProductConfigurationServiceTest
{

	public static final String CONFIG_ID = "id";
	DefaultProductConfigurationService classUnderTest = new DefaultProductConfigurationService();
	de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService cpqConfigurationService = null;
	private final String itemKey = "A";
	private final String externalConfiguration = "XML";
	private ConfigModel configModel;
	final String productCode = "Product";
	Double price = new Double("123");
	private SessionAccessService sessionAccessService;


	@Before
	public void init()
	{
		configModel = new ConfigModelImpl();
		configModel.setId(CONFIG_ID);
		final PriceModel currentTotalPrice = new PriceModelImpl();
		currentTotalPrice.setPriceValue(new BigDecimal(price.intValue()));
		configModel.setCurrentTotalPrice(currentTotalPrice);
		sessionAccessService = EasyMock.createMock(SessionAccessService.class);
		EasyMock.expect(sessionAccessService.getConfigIdForCartEntry(itemKey)).andReturn(CONFIG_ID);
		final ConfigurationProvider configurationProvider = EasyMock.createMock(ConfigurationProvider.class);
		cpqConfigurationService = EasyMock.createMock(ProductConfigurationService.class);
		EasyMock.expect(cpqConfigurationService.retrieveExternalConfiguration(CONFIG_ID)).andReturn(externalConfiguration);
		EasyMock.expect(cpqConfigurationService.retrieveConfigurationOverview(CONFIG_ID)).andReturn(configModel);
		EasyMock.expect(cpqConfigurationService.createConfigurationFromExternal(EasyMock.anyObject(KBKey.class),
				EasyMock.anyObject(String.class))).andReturn(configModel);
		EasyMock.replay(sessionAccessService, configurationProvider, cpqConfigurationService);
		classUnderTest.setSessionAccessService(sessionAccessService);
		classUnderTest.setProductConfigurationService(cpqConfigurationService);

	}


	@Test
	public void testSetIntoSession()
	{
		EasyMock.reset(sessionAccessService);
		sessionAccessService.setConfigIdForCartEntry(itemKey, CONFIG_ID);
		EasyMock.replay(sessionAccessService);
		classUnderTest.setIntoSession(itemKey, configModel.getId());
		EasyMock.verify(sessionAccessService);
	}

	@Test
	public void testIsInSession()
	{
		Assert.assertTrue(classUnderTest.isInSession(itemKey));
	}

	@Test
	public void testGetExternalConfiguration()
	{
		assertEquals(externalConfiguration, classUnderTest.getExternalConfiguration(itemKey));
	}

	@Test
	public void testGetTotalPrice()
	{
		assertEquals(price, classUnderTest.getTotalPrice(itemKey));
	}

	@Test
	public void testGetConfigId()
	{
		assertEquals(CONFIG_ID, classUnderTest.getGetConfigId(itemKey));
	}

	@Test
	public void testGetConfigModel()
	{

		assertEquals(configModel, classUnderTest.getConfigModel(productCode, externalConfiguration));
	}
}
