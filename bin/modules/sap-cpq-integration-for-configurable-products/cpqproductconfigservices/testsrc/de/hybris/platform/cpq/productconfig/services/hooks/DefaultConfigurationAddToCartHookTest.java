/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.hooks;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurableChecker;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultConfigurationAddToCartHook}
 */
@UnitTest
public class DefaultConfigurationAddToCartHookTest
{
	private static final String CONFIG_ID = "123";
	private ProductModel productModel;
	private CommerceCartParameter parameter;
	private CommerceCartModification result;

	@Mock
	private CartEntryModel cartEntryModel;
	@Mock
	private ConfigurableChecker configurableChecker;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private ModelService modelService;

	private DefaultConfigurationAddToCartHook classUnderTest;
	private final CloudCPQOrderEntryProductInfoModel cpqProductInfo = new CloudCPQOrderEntryProductInfoModel();


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		classUnderTest = new DefaultConfigurationAddToCartHook(configurationService, configurableChecker, modelService);

		productModel = new ProductModel();
		parameter = new CommerceCartParameter();
		parameter.setProduct(productModel);

		when(cartEntryModel.getPk()).thenReturn(PK.fromLong(123));
		when(cartEntryModel.getProduct()).thenReturn(productModel);

		result = new CommerceCartModification();
		result.setEntry(cartEntryModel);

		cpqProductInfo.setConfigurationId(CONFIG_ID);

		when(configurationService.createConfiguration(anyString())).thenReturn(CONFIG_ID);
	}

	@Test
	public void testBeforeAddToCartCpqProduct() throws Exception
	{
		when(configurableChecker.isCloudCPQConfigurableProduct(productModel)).thenReturn(true);
		classUnderTest.beforeAddToCart(parameter);

		assertTrue(parameter.isCreateNewEntry());
	}

	@Test
	public void testBeforeAddToCartNonCpqProduct() throws Exception
	{
		when(configurableChecker.isCloudCPQConfigurableProduct(productModel)).thenReturn(false);
		classUnderTest.beforeAddToCart(parameter);

		assertFalse(parameter.isCreateNewEntry());
	}

	@Test
	public void testAfterAddToCartNewCpqProduct() throws Exception
	{
		when(configurableChecker.isCloudCPQConfigurableProduct(productModel)).thenReturn(true);

		final List<AbstractOrderEntryProductInfoModel> cpqOrderEntryProductInfoModelList = new ArrayList<>();
		final CloudCPQOrderEntryProductInfoModel cpqOrderEntryProductInfoModel = new CloudCPQOrderEntryProductInfoModel();
		cpqOrderEntryProductInfoModel.setConfigurationId(CONFIG_ID);
		cpqOrderEntryProductInfoModel.setProductInfoStatus(ProductInfoStatus.SUCCESS);
		cpqOrderEntryProductInfoModelList.add(cpqOrderEntryProductInfoModel);
		when(cartEntryModel.getProductInfos()).thenReturn(cpqOrderEntryProductInfoModelList);

		classUnderTest.afterAddToCart(new CommerceCartParameter(), result);

		assertNotNull(cpqOrderEntryProductInfoModel.getConfigurationId());
		assertEquals(CONFIG_ID, cpqOrderEntryProductInfoModel.getConfigurationId());
		assertEquals(ProductInfoStatus.SUCCESS, cpqOrderEntryProductInfoModel.getProductInfoStatus());
	}

	@Test
	public void testAfterAddToCartModelNotSaved() throws Exception
	{
		when(configurableChecker.isCloudCPQConfigurableProduct(productModel)).thenReturn(true);
		when(cartEntryModel.getProductInfos()).thenReturn(null);
		when(cartEntryModel.getPk()).thenReturn(null);

		classUnderTest.afterAddToCart(new CommerceCartParameter(), result);

		verify(modelService, times(0)).save(any());
	}


	@Test
	public void testAfterAddToCartConfiguredCpqProduct() throws Exception
	{
		when(configurableChecker.isCloudCPQConfigurableProduct(productModel)).thenReturn(true);

		final List<AbstractOrderEntryProductInfoModel> cpqOrderEntryProductInfoModelList = new ArrayList<>();
		final CloudCPQOrderEntryProductInfoModel cpqOrderEntryProductInfoModel = new CloudCPQOrderEntryProductInfoModel();
		cpqOrderEntryProductInfoModel.setConfigurationId("456");
		cpqOrderEntryProductInfoModelList.add(cpqOrderEntryProductInfoModel);
		when(cartEntryModel.getProductInfos()).thenReturn(cpqOrderEntryProductInfoModelList);

		classUnderTest.afterAddToCart(null, result);

		verify(modelService, times(0)).save(cpqOrderEntryProductInfoModel);
		assertNotNull(cpqOrderEntryProductInfoModel.getConfigurationId());
		assertEquals("456", cpqOrderEntryProductInfoModel.getConfigurationId());
	}

	@Test(expected = IllegalStateException.class)
	public void testAfterAddToCartConfiguredCpqProductTwoProductInfos() throws Exception
	{
		when(configurableChecker.isCloudCPQConfigurableProduct(productModel)).thenReturn(true);

		final List<AbstractOrderEntryProductInfoModel> cpqOrderEntryProductInfoModelList = new ArrayList<>();
		cpqOrderEntryProductInfoModelList.add(new CloudCPQOrderEntryProductInfoModel());
		cpqOrderEntryProductInfoModelList.add(new CloudCPQOrderEntryProductInfoModel());
		cartEntryModel.setProductInfos(cpqOrderEntryProductInfoModelList);

		classUnderTest.afterAddToCart(null, result);
	}

	@Test
	public void testAfterAddToCartNonCpqProduct() throws Exception
	{
		when(configurableChecker.isCloudCPQConfigurableProduct(productModel)).thenReturn(false);

		classUnderTest.afterAddToCart(null, result);

		verify(modelService, times(0)).save(any());
	}

	@Test
	public void testAfterAddToCartConfiguredCpqProductConfigIdAsParameter() throws Exception
	{
		when(configurableChecker.isCloudCPQConfigurableProduct(productModel)).thenReturn(true);

		final List<AbstractOrderEntryProductInfoModel> cpqOrderEntryProductInfoModelList = new ArrayList<>();
		final CloudCPQOrderEntryProductInfoModel cpqOrderEntryProductInfoModel = new CloudCPQOrderEntryProductInfoModel();
		cpqOrderEntryProductInfoModel.setConfigurationId(null);
		cpqOrderEntryProductInfoModelList.add(cpqOrderEntryProductInfoModel);
		when(cartEntryModel.getProductInfos()).thenReturn(cpqOrderEntryProductInfoModelList);

		final CommerceCartParameter commerceCartParameter = new CommerceCartParameter();
		commerceCartParameter.setCpqConfigId("123");

		classUnderTest.afterAddToCart(commerceCartParameter, result);

		verify(modelService).save(cpqOrderEntryProductInfoModel);
		assertNotNull(cpqOrderEntryProductInfoModel.getConfigurationId());
		assertEquals("123", cpqOrderEntryProductInfoModel.getConfigurationId());
	}

}
