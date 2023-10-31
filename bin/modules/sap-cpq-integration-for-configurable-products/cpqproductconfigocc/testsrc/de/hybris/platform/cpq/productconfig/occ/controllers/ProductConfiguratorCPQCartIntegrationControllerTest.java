/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commercewebservicescommons.dto.order.CartModificationWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.product.ProductWsDTO;
import de.hybris.platform.cpq.productconfig.facades.CartIntegrationFacade;
import de.hybris.platform.cpq.productconfig.facades.data.ProductConfigData;
import de.hybris.platform.cpq.productconfig.occ.ProductConfigOrderEntryWsDTO;
import de.hybris.platform.cpq.productconfig.occ.ProductConfigWsDTO;
import de.hybris.platform.webservicescommons.mapping.DataMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link ProductConfiguratorCPQCartIntegrationController}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductConfiguratorCPQCartIntegrationControllerTest
{

	private static final int ENTRY_NUMBER = 3;
	private static final String CONFIG_ID = "c123";
	private static final String P_CODE = "p123";
	private static final String BASE_SITE = null;

	@InjectMocks
	private ProductConfiguratorCPQCartIntegrationController classUnderTest;

	@Mock
	private CartIntegrationFacade cartIntegrationFacade;

	@Mock
	private DataMapper dataMapper;

	private final ProductConfigOrderEntryWsDTO wsEntry = new ProductConfigOrderEntryWsDTO();
	private final ProductWsDTO wsProduct = new ProductWsDTO();
	private final ProductConfigWsDTO wsProductConfig = new ProductConfigWsDTO();
	private final CartModificationData cartModificationData = new CartModificationData();
	private final CartModificationWsDTO wsCartModification = new CartModificationWsDTO();
	private final ProductConfigData productConfigData = new ProductConfigData();


	@Before
	public void setUp() throws CommerceCartModificationException
	{
		given(cartIntegrationFacade.addConfigurationToCart(CONFIG_ID, 1L)).willReturn(cartModificationData);
		given(cartIntegrationFacade.updateCartEntryFromConfiguration(CONFIG_ID, ENTRY_NUMBER)).willReturn(cartModificationData);
		given(cartIntegrationFacade.getConfigIdForSessionCartEntry(ENTRY_NUMBER)).willReturn(productConfigData);

		given(dataMapper.map(cartModificationData, CartModificationWsDTO.class)).willReturn(wsCartModification);
		given(dataMapper.map(productConfigData, ProductConfigWsDTO.class)).willReturn(wsProductConfig);

		productConfigData.setConfigId(CONFIG_ID);

		cartModificationData.setEntry(new OrderEntryData());
		cartModificationData.getEntry().setProduct(new ProductData());
		cartModificationData.getEntry().getProduct().setCode(P_CODE);

		wsEntry.setConfigId(CONFIG_ID);
		wsEntry.setQuantity(1l);
		wsEntry.setProduct(wsProduct);
		wsProduct.setCode(P_CODE);
		wsCartModification.setEntry(wsEntry);

		wsProductConfig.setConfigId(CONFIG_ID);

	}

	@Test
	public void testAddCartEntryInternal() throws CommerceCartModificationException
	{
		assertSame(cartModificationData, classUnderTest.addCartEntryInternal(wsEntry));
	}

	@Test
	public void testAddCartEntry() throws CommerceCartModificationException
	{
		assertSame(wsCartModification, classUnderTest.addCartEntry(BASE_SITE, wsEntry));
	}

	@Test
	public void testUpdateCartEntry() throws CommerceCartModificationException
	{
		assertSame(wsCartModification, classUnderTest.updateCartEntry(BASE_SITE, ENTRY_NUMBER, wsEntry));
	}

	@Test
	public void testUpdateCartEntryInternal() throws CommerceCartModificationException
	{
		wsEntry.setEntryNumber(3);
		assertSame(cartModificationData, classUnderTest.updateCartEntryInternal(wsEntry));
	}

	@Test
	public void testGetCartEntryConfigurationIdInternal()
	{
		assertEquals(CONFIG_ID, classUnderTest.getCartEntryConfiguartionIdInternal(ENTRY_NUMBER).getConfigId());
	}

	@Test
	public void testGetCartEntryConfigurationId()
	{
		assertEquals(CONFIG_ID, classUnderTest.getCartEntryConfigurationId(BASE_SITE, ENTRY_NUMBER).getConfigId());
	}

}
