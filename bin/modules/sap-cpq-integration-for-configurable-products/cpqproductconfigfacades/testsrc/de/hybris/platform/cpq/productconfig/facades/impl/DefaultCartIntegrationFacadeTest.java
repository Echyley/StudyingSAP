/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.order.CommerceCartService;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.cpq.productconfig.services.AbstractOrderIntegrationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryDataContent;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultCartIntegrationFacade}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCartIntegrationFacadeTest
{
	private static final String CLONED_CONFIG_ID = "cloned config id";
	private static final String PRODUCT_CODE = "PRODUCT_CODE";
	private static final String CONFIG_ID = "CONFIG_ID";
	private static final String UPDATED_CONFIG_ID = "UPDATED_CONFIG_ID";
	private static final String PK_STRING = "123";
	private static final Integer ENTRY_NUMBER = Integer.valueOf(1);

	@InjectMocks
	private DefaultCartIntegrationFacade classUnderTest;

	@Mock
	private CartModel cart;
	@Mock
	private CartService cartService;
	@Mock
	private ModelService modelService;
	@Mock
	private CommerceCartService commerceCartService;
	@Mock
	private ProductService productService;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private AbstractOrderIntegrationService abstractOrderIntegrationService;
	@Mock
	private CloudCPQOrderEntryProductInfoModel productInfo;
	@Mock
	private Converter<CommerceCartModification, CartModificationData> cartModificationConverter;
	@Mock
	private OrderEntryData entryData;
	@Mock
	private CartEntryModel entryModel;

	private final CommerceCartModification modificationModel = new CommerceCartModification();;
	private final CartModificationData modificationData = new CartModificationData();
	private final ProductModel product = new ProductModel();
	private final UnitModel unit = new UnitModel();
	private final ConfigurationSummaryData configurationSummary = new ConfigurationSummaryData();
	private final ConfigurationSummaryDataContent configurationSummaryContent = new ConfigurationSummaryDataContent();
	private final List<AbstractOrderEntryProductInfoModel> productInfos = new ArrayList<>();



	@Before
	public void setup() throws CommerceCartModificationException
	{
		classUnderTest = new DefaultCartIntegrationFacade(cartService, modelService, commerceCartService, productService,
				configurationService, abstractOrderIntegrationService, cartModificationConverter);

		when(configurationService.getConfigurationSummary(CONFIG_ID)).thenReturn(configurationSummary);
		when(entryModel.getProductInfos()).thenReturn(productInfos);
		when(productInfo.getConfigurationId()).thenReturn(CONFIG_ID);
		configurationSummary.setConfiguration(configurationSummaryContent);
		configurationSummaryContent.setProductSystemId(PRODUCT_CODE);

		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(product);
		given(cartService.getSessionCart()).willReturn(cart);
		given(cartService.getEntryForNumber(cart, ENTRY_NUMBER.intValue())).willReturn(entryModel);
		given(abstractOrderIntegrationService.getConfigIdForAbstractOrderEntry(entryModel)).willReturn(CONFIG_ID);
		given(commerceCartService.addToCart(Mockito.any(CommerceCartParameter.class))).willReturn(modificationModel);
		given(cartModificationConverter.convert(modificationModel)).willReturn(modificationData);
		given(entryModel.getProduct()).willReturn(product);

		productInfos.add(productInfo);

		product.setCode(PRODUCT_CODE);
		product.setUnit(unit);

		modificationModel.setEntry(entryModel);
		given(entryModel.getProductInfos()).willReturn(productInfos);

		modificationData.setEntry(entryData);
		given(entryData.getEntryNumber()).willReturn(ENTRY_NUMBER);
	}

	@Test
	public void testAddConfigurationToCart() throws Exception
	{
		final CartModificationData modData = classUnderTest.addConfigurationToCart(CONFIG_ID, 2L);
		assertEquals(ENTRY_NUMBER, modData.getEntry().getEntryNumber());
		verify(configurationService).makeConfigurationPermanent(CONFIG_ID);
	}

	@Test
	public void testAbstractOrderIntegrationService() throws Exception
	{
		assertEquals(abstractOrderIntegrationService, classUnderTest.getAbstractOrderIntegrationService());
	}

	@Test
	public void testFillCommerceCartParameterForAddToCart()
	{
		final CommerceCartParameter parameter = classUnderTest.prepareCommerceCartParameterForAddToCart(cart, product, 1, true,
				CONFIG_ID);
		assertTrue(parameter.isEnableHooks());
		assertSame(cart, parameter.getCart());
		assertSame(product, parameter.getProduct());
		assertEquals(1, parameter.getQuantity());
		assertSame(unit, parameter.getUnit());
		assertTrue(parameter.isCreateNewEntry());
		assertEquals(CONFIG_ID, parameter.getCpqConfigId());
	}

	@Test
	public void testCreateCartItem() throws Exception
	{
		assertSame(modificationModel, classUnderTest.createCartItem(product, cart, "config-id", 2L));
	}

	@Test
	public void testGetRootProduct()
	{
		assertEquals(PRODUCT_CODE, classUnderTest.getRootProduct(CONFIG_ID));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetRootProductNoConfigFound()
	{
		classUnderTest.getRootProduct("Not known");
	}

	@Test(expected = IllegalStateException.class)
	public void testGetRootProductNoRootProductPerConfigId()
	{
		configurationSummaryContent.setProductSystemId(null);
		classUnderTest.getRootProduct(CONFIG_ID);
	}

	@Test(expected = CommerceCartModificationException.class)
	public void testAddConfigurationToCartFails() throws Exception
	{
		given(productService.getProductForCode(PRODUCT_CODE)).willReturn(product);
		given(cartService.getSessionCart()).willReturn(cart);
		willThrow(CommerceCartModificationException.class).given(commerceCartService)
				.addToCart(Mockito.any(CommerceCartParameter.class));
		classUnderTest.addConfigurationToCart(CONFIG_ID, 1L);
	}

	@Test
	public void testGetConfigIdForCartEntry()
	{
		when(configurationService.cloneConfiguration(CONFIG_ID, false)).thenReturn(CLONED_CONFIG_ID);
		assertEquals(CLONED_CONFIG_ID, classUnderTest.getConfigIdForSessionCartEntry(ENTRY_NUMBER).getConfigId());
	}

	@Test
	public void testGetProductCodeForCartEntry()
	{
		assertEquals(PRODUCT_CODE, classUnderTest.getProductCodeForSessionCartEntry(ENTRY_NUMBER));
	}

	@Test
	public void testUpdateCartEntryFromConfiguration()
	{
		final ArgumentCaptor<CommerceCartModification> cartModification = ArgumentCaptor.forClass(CommerceCartModification.class);
		given(cartModificationConverter.convert(cartModification.capture())).willReturn(modificationData);
		given(entryModel.getQuantity()).willReturn(Long.valueOf(1l));
		final CartModificationData result = classUnderTest.updateCartEntryFromConfiguration(UPDATED_CONFIG_ID,
				ENTRY_NUMBER.intValue());
		assertNotNull(result);
		final CommerceCartModification ccm = cartModification.getValue();
		assertEquals(CommerceCartModificationStatus.SUCCESS, ccm.getStatusCode());
		assertEquals(entryModel, ccm.getEntry());
		assertEquals(1l, ccm.getQuantity());
		verify(entryModel).setCalculated(Boolean.FALSE);
		verify(modelService).save(cart);
		verify(modelService).save(productInfo);
		verify(configurationService).removeCachedConfigurationSummary(CONFIG_ID);
		verify(configurationService).deleteConfiguration(CONFIG_ID);
	}

	@Test
	public void testUpdateCartEntryFromConfigurationWithSame()
	{
		final ArgumentCaptor<CommerceCartModification> cartModification = ArgumentCaptor.forClass(CommerceCartModification.class);
		given(cartModificationConverter.convert(cartModification.capture())).willReturn(modificationData);
		given(entryModel.getQuantity()).willReturn(Long.valueOf(1l));
		final CartModificationData result = classUnderTest.updateCartEntryFromConfiguration(CONFIG_ID, ENTRY_NUMBER.intValue());
		assertNotNull(result);
		final CommerceCartModification ccm = cartModification.getValue();
		assertEquals(CommerceCartModificationStatus.SUCCESS, ccm.getStatusCode());
		assertEquals(entryModel, ccm.getEntry());
		assertEquals(1l, ccm.getQuantity());
		verify(entryModel).setCalculated(Boolean.FALSE);
		verify(modelService).save(cart);
		verify(modelService).save(productInfo);
		verify(configurationService).removeCachedConfigurationSummary(CONFIG_ID);
		verify(configurationService, times(0)).deleteConfiguration(CONFIG_ID);
	}

	@Test
	public void testRecalculateCart()
	{
		classUnderTest.recalculateCart(ENTRY_NUMBER.intValue());
		verify(entryModel).setCalculated(Boolean.FALSE);
		verify(modelService).save(cart);
		verify(commerceCartService).calculateCart(Mockito.any(CommerceCartParameter.class));
	}

	@Test
	public void testRemoveConfigurationCache()
	{
		classUnderTest.removeObsoleteConfiguration(CONFIG_ID);
		verify(configurationService).deleteConfiguration(CONFIG_ID);
	}

	@Test
	public void testInvalidateConfigurationSummaryCache()
	{
		classUnderTest.invalidateConfigurationSummaryCache(CONFIG_ID);
		verify(configurationService).removeCachedConfigurationSummary(CONFIG_ID);
	}
}
