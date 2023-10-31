/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ConfiguratorSettingsService;
import de.hybris.platform.product.model.AbstractConfiguratorSettingModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultConfigurableChecker}
 */
@UnitTest
public class DefaultConfigurableCheckerTest
{
	private DefaultConfigurableChecker classUnderTest;
	@Mock
	private ProductModel product;
	@Mock
	private ConfiguratorSettingsService configuratorSettingsService;
	@Mock
	private AbstractConfiguratorSettingModel settingModel;
	private List<AbstractConfiguratorSettingModel> settingModelList;


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new DefaultConfigurableChecker(configuratorSettingsService);
		when(settingModel.getConfiguratorType()).thenReturn(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		settingModelList = new ArrayList();
		settingModelList.add(settingModel);
		when(configuratorSettingsService.getConfiguratorSettingsForProduct(product)).thenReturn(settingModelList);
	}

	@Test
	public void testIsCloudCPQConfigurableProduct()
	{
		final boolean result = classUnderTest.isCloudCPQConfigurableProduct(product);
		assertTrue(result);
	}

	@Test
	public void testIsCloudCPQConfigurableProductFalseNoInfoModel()
	{
		settingModelList.clear();
		final boolean result = classUnderTest.isCloudCPQConfigurableProduct(product);
		assertFalse(result);
	}

	@Test
	public void testIsCloudCPQConfigurableProductFalseOtherType()
	{
		when(settingModel.getConfiguratorType()).thenReturn(null);
		final boolean result = classUnderTest.isCloudCPQConfigurableProduct(product);
		assertFalse(result);
	}

}
