/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;


/**
 * Unit test for {@link DefaultAbstractOrderIntegrationService}
 */
@UnitTest
public class DefaultAbstractOrderIntegrationServiceTest
{
	@InjectMocks
	private DefaultAbstractOrderIntegrationService classUnderTest;
	@Mock
	private CartEntryModel cartItemMock;
	@Mock
	private ModelService mockModelService;
	@Mock
	private CloudCPQOrderEntryProductInfoModel productInfo;

	private final AbstractOrderEntryModel orderEntry = new QuoteEntryModel();
	private final List<AbstractOrderEntryProductInfoModel> productInfos = new ArrayList<>();

	private static final String CONFIG_ID = "1234";
	private static final String NEW_CONFIG_ID = "newConfigId";

	@Before
	public void initialize()
	{
		MockitoAnnotations.initMocks(this);
		when(cartItemMock.getProductInfos()).thenReturn(productInfos);
		when(productInfo.getConfigurationId()).thenReturn(CONFIG_ID);
		productInfos.add(productInfo);
	}

	@Test
	public void testGetConfigIdForCartEntryModel()
	{
		assertEquals(CONFIG_ID, classUnderTest.getConfigIdForAbstractOrderEntry(cartItemMock));
	}

	@Test(expected = NullPointerException.class)
	public void testGetConfigIdForCartEntryModelNull()
	{
		cartItemMock = null;
		classUnderTest.getConfigIdForAbstractOrderEntry(cartItemMock);
	}

	@Test(expected = NullPointerException.class)
	public void testGetConfigIdForCartEntryModelNoProductInfos()
	{
		when(cartItemMock.getProductInfos()).thenReturn(null);
		classUnderTest.getConfigIdForAbstractOrderEntry(cartItemMock);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetConfigIdForCartEntryModelNumberOfProductInfoDoesNotMatch()
	{
		productInfos.clear();
		classUnderTest.getConfigIdForAbstractOrderEntry(cartItemMock);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetConfigIdForCartEntryModelWrongConfiguratorType()
	{
		final AbstractOrderEntryProductInfoModel abstractOrderEntryProductInfo = new AbstractOrderEntryProductInfoModel();
		productInfos.add(abstractOrderEntryProductInfo);
		classUnderTest.getConfigIdForAbstractOrderEntry(cartItemMock);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetConfigIdForCartEntryModelConfigIdNull()
	{
		when(productInfo.getConfigurationId()).thenReturn(null);
		classUnderTest.getConfigIdForAbstractOrderEntry(cartItemMock);
	}

	@Test
	public void testSetConfigIdForAbstractOrderEntry()
	{
		CloudCPQOrderEntryProductInfoModel productInfo = new CloudCPQOrderEntryProductInfoModel();
		productInfo.setConfigurationId(CONFIG_ID);
		orderEntry.setProductInfos(Collections.singletonList(productInfo));

		classUnderTest.setConfigIdForAbstractOrderEntry(orderEntry, NEW_CONFIG_ID);
		productInfo = (CloudCPQOrderEntryProductInfoModel) orderEntry.getProductInfos()
				.get(0);
		assertEquals(NEW_CONFIG_ID, productInfo.getConfigurationId());
		assertEquals(ConfiguratorType.CLOUDCPQCONFIGURATOR, productInfo.getConfiguratorType());
		assertEquals(orderEntry, productInfo.getOrderEntry());
	}
}
