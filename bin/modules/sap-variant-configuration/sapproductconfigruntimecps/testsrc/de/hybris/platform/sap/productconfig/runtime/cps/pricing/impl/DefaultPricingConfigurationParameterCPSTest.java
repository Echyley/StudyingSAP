/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.pricing.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.runtime.cps.enums.SAPProductConfigPricingDetailsMode;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultPricingConfigurationParameterCPS}
 */
@UnitTest
public class DefaultPricingConfigurationParameterCPSTest
{

	private static final String SUBTOTAL_FOR_BASEPRICE = "1";
	private static final String SUBTOTAL_FOR_SELRCTEDOPTIONS = "2";
	private static final Collection<String> CONDITIONTYPES_FOR_BASEPRICE = Arrays.asList("PR00", "PR01");
	private static final Collection<String> CONDITIONTYPES_FOR_SELECTEDOPTIONS = Arrays.asList("VA00", "VA01");
	private static final String CONDITIONFUNCTION_FOR_BASEPRICE = "BASE";
	private static final String CONDITIONFUNCTION_FOR_SELECTEDOPTIONS = "SOPT";

	private DefaultPricingConfigurationParameterCPS classUnderTest;


	@Mock
	private BaseStoreService baseStoreService;

	@Mock
	private BaseStoreModel baseStore;

	@Mock
	private SAPConfigurationModel sapConfiguration;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		Mockito.when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		Mockito.when(baseStore.getSAPConfiguration()).thenReturn(sapConfiguration);
		classUnderTest = new DefaultPricingConfigurationParameterCPS();
		classUnderTest.setBaseStoreService(baseStoreService);
	}

	@Test
	public void testGetPricingDetailsMode()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_pricingdetailsmode())
				.thenReturn(SAPProductConfigPricingDetailsMode.CONDITIONFUNCTION);
		assertEquals(SAPProductConfigPricingDetailsMode.CONDITIONFUNCTION, classUnderTest.getPricingDetailsMode());
	}

	@Test
	public void testGetSubTotalForBasePrice()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_subtotal_baseprice_cps()).thenReturn(SUBTOTAL_FOR_BASEPRICE);
		assertEquals(SUBTOTAL_FOR_BASEPRICE, classUnderTest.getSubTotalForBasePrice());
	}

	@Test
	public void testGetSubTotalForSelectedOptions()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_subtotal_selectedoptions_cps()).thenReturn(SUBTOTAL_FOR_SELRCTEDOPTIONS);
		assertEquals(SUBTOTAL_FOR_SELRCTEDOPTIONS, classUnderTest.getSubTotalForSelectedOptions());
	}

	@Test
	public void tstGetConditionTypesForBasePrice()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_conditiontypes_baseprice_cps()).thenReturn(CONDITIONTYPES_FOR_BASEPRICE);
		assertEquals(CONDITIONTYPES_FOR_BASEPRICE, classUnderTest.getConditionTypesForBasePrice());
	}

	@Test
	public void testGetConditionTypesForSelectedOptions()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_conditiontypes_selectedoptions_cps())
				.thenReturn(CONDITIONTYPES_FOR_SELECTEDOPTIONS);
		assertEquals(CONDITIONTYPES_FOR_SELECTEDOPTIONS, classUnderTest.getConditionTypesForSelectedOptions());
	}

	@Test
	public void testGetTargetForBasePrice()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_condfunc_baseprice_cps()).thenReturn(CONDITIONFUNCTION_FOR_BASEPRICE);
		assertEquals(CONDITIONFUNCTION_FOR_BASEPRICE, classUnderTest.getTargetForBasePrice());
	}

	@Test
	public void testGetTargetForSelectedOptions()
	{
		Mockito.when(sapConfiguration.getSapproductconfig_condfunc_selectedoptions_cps())
				.thenReturn(CONDITIONFUNCTION_FOR_SELECTEDOPTIONS);
		assertEquals(CONDITIONFUNCTION_FOR_SELECTEDOPTIONS, classUnderTest.getTargetForSelectedOptions());
	}

}
