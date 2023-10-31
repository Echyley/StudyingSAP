/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.strategies.impl.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;


/**
 * Unit test for {@link MockConfigurationLifecycleStrategy}
 */
@UnitTest
public class MockConfigurationLifecycleStrategyTest
{

	private static final String NON_MOCK_CONFIG_ID = "1234-34567ef-2345";
	private static final String CONFIG_ID = "MOCK_72635A";
	private static final String PRODUCT_CODE = "CONF_CAMERA_BUNDLE";
	private static final String PRODUCT_CODE2 = "CONF_LAPTOP";
	private static final String OWNER_ID = "user123";
	MockConfigurationLifecycleStrategy classUnderTest;

	@Before
	public void initialize()
	{
		classUnderTest = new MockConfigurationLifecycleStrategy();
	}

	@Test
	public void testDefaultFallback()
	{
		assertTrue("Fallback behaviour should be active for mock by default", classUnderTest.isUseFallback());
	}
	
	@Test
	public void testRestoreUseFallbackDefault()
	{
		classUnderTest.setUseFallback(false);
		classUnderTest.restoreDefaultFallbackBehaviour();
		assertTrue("Fallback behaviour should be active for mock after restore of default value", classUnderTest.isUseFallback());
	}

	@Test
	public void testDeleteConfiguration()
	{
		classUnderTest.configurationMap.put(CONFIG_ID, "Configuration Structure");
		assertTrue(classUnderTest.deleteConfiguration(CONFIG_ID));
	}

	@Test
	public void testDeleteConfigurationNotExist()
	{
		classUnderTest.configurationMap.put("XYZ", "Configuration Structure");
		assertFalse(classUnderTest.deleteConfiguration(CONFIG_ID));
	}

	@Test
	public void testCloneConfiguration()
	{
		final String configIdAfterClone = classUnderTest.cloneConfiguration(CONFIG_ID, true);
		assertNotNull(configIdAfterClone);
		assertNotEquals(configIdAfterClone, CONFIG_ID);
		assertTrue(classUnderTest.pernamentSet.contains(configIdAfterClone));
	}

	@Test
	public void testCloneConfigurationNonPermanent()
	{
		final String configIdAfterClone = classUnderTest.cloneConfiguration(CONFIG_ID, false);
		assertNotNull(configIdAfterClone);
		assertNotEquals(configIdAfterClone, CONFIG_ID);
		assertFalse(classUnderTest.pernamentSet.contains(configIdAfterClone));
	}

	@Test(expected = IllegalStateException.class)
	public void testCloneConfigurationNonMock()
	{
		classUnderTest.cloneConfiguration(NON_MOCK_CONFIG_ID, true);
	}

	@Test
	public void testCloneConfigurationNotExist()
	{
		classUnderTest.configurationMap.put("XYZ", MockConfigurationLifecycleStrategy.SERIALIZED_CONFIG_FOR_UNIT_TEST);
		final String configIdAfterClone = classUnderTest.cloneConfiguration(CONFIG_ID, true);
		assertNotNull(configIdAfterClone);
		assertNotEquals (configIdAfterClone, CONFIG_ID);
	}

	@Test
	public void testCreateConfiguration()
	{
		final String configId = classUnderTest.createConfiguration(PRODUCT_CODE, OWNER_ID);
		assertNotNull(configId);
		assertTrue(configId.contains(MockConfigurationLifecycleStrategy.PREFIX_MOCK));
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateConfigurationWrongProductCode()
	{
		classUnderTest.createConfiguration("WRONG_PRODUCT_CODE", OWNER_ID);
	}

	@Test
	public void testGetConfigurationSummary()
	{
		classUnderTest.configurationMap.put(CONFIG_ID, MockConfigurationLifecycleStrategy.SERIALIZED_CONFIG_FOR_UNIT_TEST);
		final ConfigurationSummaryData configurationSummary = classUnderTest.getConfigurationSummary(CONFIG_ID);
		assertNotNull(configurationSummary);
		assertEquals(PRODUCT_CODE, configurationSummary.getConfiguration().getProductSystemId());
	}

	@Test
	public void testGetConfigurationSummaryFallBackMockConfig()
	{
		final ConfigurationSummaryData configurationSummary = classUnderTest.getConfigurationSummary(CONFIG_ID);
		assertNotNull(configurationSummary);
		assertEquals(MockConfigurationLifecycleStrategy.FALLBACK_PRODUCT,
				configurationSummary.getConfiguration().getProductSystemId());
		assertEquals(1, classUnderTest.configurationMap.values().size());
		assertTrue(classUnderTest.configurationMap.keySet().contains(CONFIG_ID));
	}

	@Test
	public void testGetConfigurationSummaryFallBackNonMockConfig()
	{
		final ConfigurationSummaryData configurationSummary = classUnderTest.getConfigurationSummary(NON_MOCK_CONFIG_ID);
		assertNotNull(configurationSummary);
		assertEquals(MockConfigurationLifecycleStrategy.FALLBACK_PRODUCT,
				configurationSummary.getConfiguration().getProductSystemId());
		assertTrue(classUnderTest.configurationMap.isEmpty());
	}

	@Test(expected = IllegalStateException.class)
	public void testGetConfigurationSummaryFallBackNotActive()
	{
		classUnderTest.setUseFallback(false);
		classUnderTest.getConfigurationSummary(CONFIG_ID);
	}

	@Test
	public void testExtractProductCodeEmpty()
	{
		final String result = classUnderTest.extractProductIdFromConfigId("");
		assertEquals(MockConfigurationLifecycleStrategy.FALLBACK_PRODUCT, result);
	}

	@Test
	public void testExtractProductCode()
	{
		final String result = classUnderTest.extractProductIdFromConfigId("MOCK123@" + PRODUCT_CODE2);
		assertEquals(PRODUCT_CODE2, result);
	}

	@Test
	public void testGetConfigIdForMockEncodesProduct()
	{
		final String configId = classUnderTest.getConfigIdForMock(PRODUCT_CODE);
		assertTrue("mock config ID is not suffixed with @productId", configId.endsWith("@" + PRODUCT_CODE));
	}

	@Test
	public void testGetConfigIdForMockIsPrefixedWithMock()
	{
		final String configId = classUnderTest.getConfigIdForMock(PRODUCT_CODE);
		assertTrue("mock config id should have mock prefix", configId.startsWith(MockConfigurationLifecycleStrategy.PREFIX_MOCK));
	}

	@Test
	public void testExtractProductIdFromConfigId()
	{
		final String productCode = classUnderTest.extractProductIdFromConfigId(classUnderTest.getConfigIdForMock(PRODUCT_CODE));
		assertEquals(PRODUCT_CODE, productCode);
	}

	@Test
	public void testExtractProductIdFromConfigIdNoProductId()
	{
		final String productCode = classUnderTest.extractProductIdFromConfigId(UUID.randomUUID().toString());
		assertEquals(MockConfigurationLifecycleStrategy.FALLBACK_PRODUCT, productCode);
	}

	@Test
	public void testMakeConfigurationPermanent()
	{
		final String configId = classUnderTest.createConfiguration(PRODUCT_CODE, OWNER_ID);
		classUnderTest.makeConfigurationPermanent(configId);
		assertTrue(classUnderTest.pernamentSet.contains(configId));
	}

	@Test
	public void testMakeConfigurationPermanentAndDelete()
	{
		final String configId = classUnderTest.createConfiguration(PRODUCT_CODE, OWNER_ID);
		classUnderTest.makeConfigurationPermanent(configId);
		classUnderTest.deleteConfiguration(configId);
		assertFalse(classUnderTest.pernamentSet.contains(configId));
	}

	@Test
	public void testMakeConfigurationPermanentDoesNotExist()
	{
		classUnderTest.makeConfigurationPermanent(CONFIG_ID);
		assertTrue(classUnderTest.pernamentSet.contains(CONFIG_ID));
	}

	@Test(expected = IllegalStateException.class)
	public void testMakeConfigurationPermanentNonMockConfig()
	{
		classUnderTest.makeConfigurationPermanent(NON_MOCK_CONFIG_ID);
	}

}
