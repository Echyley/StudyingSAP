/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.strategies.lifecycle.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.KBKeyImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.ConfigModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.InstanceModelImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.services.impl.ClassificationSystemCPQAttributesContainer;
import de.hybris.platform.sap.productconfig.services.cache.ProductConfigurationCacheAccessService;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Unit test for {@link DefaultConfigurationClassificationCacheStrategyImpl}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultConfigurationClassificationCacheStrategyImplTest
{

	private static final String PRODUCT_CODE = "productCode";

	@InjectMocks
	DefaultConfigurationClassificationCacheStrategyImpl classUnderTest;

	@Mock
	private ProductConfigurationCacheAccessService productConfigurationCacheAccessServiceMock;

	private final Map<String, ClassificationSystemCPQAttributesContainer> hybrisNames = new HashMap<>();
	private ConfigModel config;

	@Before
	public void setUp()
	{
		when(productConfigurationCacheAccessServiceMock.getCachedNameMap(PRODUCT_CODE)).thenReturn(hybrisNames);
		config = new ConfigModelImpl();
	}

	@Test
	public void testGetCachedNameMapByProductCode()
	{
		final Map<String, ClassificationSystemCPQAttributesContainer> result = classUnderTest.getCachedNameMap(PRODUCT_CODE);
		assertNotNull(result);
		assertEquals(hybrisNames, result);
	}

	@Test
	public void testGetCachedNameMapByConfigModel()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		config.setKbKey(kbKey);
		final Map<String, ClassificationSystemCPQAttributesContainer> result = classUnderTest.getCachedNameMap(config);
		assertNotNull(result);
		assertEquals(hybrisNames, result);
	}

	@Test
	public void testGetProductCodeByKbProductCode()
	{
		final KBKey kbKey = new KBKeyImpl(PRODUCT_CODE);
		config.setKbKey(kbKey);
		final String result = classUnderTest.getProductCode(config);
		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result);
	}

	@Test
	public void testGetProductCodeByRootInstanceName()
	{
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setName(PRODUCT_CODE);
		config.setRootInstance(rootInstance);
		final String result = classUnderTest.getProductCode(config);
		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result);
	}

	@Test
	public void testGetProductCodeKbProductCodeNull()
	{
		final KBKey kbKey = new KBKeyImpl(null);
		config.setKbKey(kbKey);
		final InstanceModel rootInstance = new InstanceModelImpl();
		rootInstance.setName(PRODUCT_CODE);
		config.setRootInstance(rootInstance);
		final String result = classUnderTest.getProductCode(config);
		assertNotNull(result);
		assertEquals(PRODUCT_CODE, result);
	}

}
