/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataProductContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.pricing.CPSMasterDataVariantPriceKey;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.Attribute;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingItemInput;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSVariantCondition;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingConfigurationParameter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Unit test for {@link PricingItemInputKBProductPopulator}
 */
@UnitTest
public class PricingItemInputKBProductPopulatorTest
{
	private static final String PRICING_PRODUCT = "PRICING_PRODUCT_ID";
	private static final String PRODUCT_ID = "THE_PRODUCT_ID";
	private static final String UOM_ST = "PCE";
	private PricingItemInputKBProductPopulator classUnderTest;
	private CPSMasterDataProductContainer productContainer;
	private PricingItemInput target;
	private MasterDataContext context;

	@Mock
	private ProductService productService;
	@Mock
	private PricingConfigurationParameter pricingConfigurationParameter;
	@Mock
	private CommonI18NService i18NService;

	Answer<String> isoCodeAnswer = new Answer<String>()
	{
		@Override
		public String answer(final InvocationOnMock invocation) throws Throwable
		{
			final UnitModel unitModel = (UnitModel) invocation.getArguments()[0];
			return unitModel.getCode();
		}
	};
	private final Map<String, List<CPSMasterDataVariantPriceKey>> requestedPrices = new HashMap<>();


	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new PricingItemInputKBProductPopulator();
		target = new PricingItemInput();
		classUnderTest.setProductService(productService);
		classUnderTest.setI18NService(i18NService);
		final UnitModel unitModel = new UnitModel();
		unitModel.setCode(UOM_ST);
		final ProductModel product = new ProductModel();
		product.setUnit(unitModel);
		Mockito.when(productService.getProductForCode(Mockito.anyString())).thenReturn(product);
		productContainer = new CPSMasterDataProductContainer();
		productContainer.setId(PRODUCT_ID);
		productContainer.setCstics(Collections.emptyMap());
		Mockito.when(pricingConfigurationParameter.retrieveUnitIsoCode(Mockito.any())).thenAnswer(isoCodeAnswer);
		classUnderTest.setPricingConfigurationParameter(pricingConfigurationParameter);

		requestedPrices.put(PRODUCT_ID, new ArrayList<>());
		context = new MasterDataContext();
		context.setPricingProduct(PRICING_PRODUCT);
		context.setRequestedPricesByProductMap(requestedPrices);

	}

	@Test
	public void testFillVariantConditions()
	{
		final List<CPSMasterDataVariantPriceKey> pricingKeys = context.getRequestedPricesByProductMap().get(PRODUCT_ID);
		pricingKeys.add(createPricingKey("vc1.1"));
		pricingKeys.add(createPricingKey("vc1.2"));
		pricingKeys.add(createPricingKey("vc1.3"));
		pricingKeys.add(createPricingKey("vc3.1"));

		classUnderTest.fillVariantConditions(productContainer, target, context);
		assertEquals(4, target.getVariantConditions().size());
		assertTrue(isConditionPresent(target.getVariantConditions(), "vc1.1"));
		assertTrue(isConditionPresent(target.getVariantConditions(), "vc1.2"));
		assertTrue(isConditionPresent(target.getVariantConditions(), "vc1.3"));
		assertTrue(isConditionPresent(target.getVariantConditions(), "vc3.1"));

	}

	@Test
	public void testCreateVariantCondition()
	{
		final CPSMasterDataVariantPriceKey pricingKey = new CPSMasterDataVariantPriceKey();
		pricingKey.setVariantConditionKey("variantConditionKey");
		final CPSVariantCondition condition = classUnderTest.createVariantCondition(pricingKey);
		assertEquals("1", condition.getFactor());
		assertEquals("variantConditionKey", condition.getKey());
	}

	private boolean isConditionPresent(final List<CPSVariantCondition> list, final String conditionKey)
	{
		return list.stream().filter(cond -> cond.getKey().equals(conditionKey)).findAny().isPresent();
	}

	private CPSMasterDataVariantPriceKey createPricingKey(final String varCondKey)
	{
		final CPSMasterDataVariantPriceKey pricingKey = new CPSMasterDataVariantPriceKey();
		pricingKey.setVariantConditionKey(varCondKey);
		pricingKey.setProductId(PRODUCT_ID);
		return pricingKey;
	}


	@Test
	public void testGetIsoUOM()
	{
		final String isoCodeUnit = classUnderTest.getIsoUOM(productContainer);
		assertEquals(UOM_ST, isoCodeUnit);
	}

	@Test
	public void testGetIsoUOMProductNotInHybris()
	{
		Mockito.when(productService.getProductForCode(Mockito.anyString())).thenThrow(new UnknownIdentifierException("NotFound"));
		productContainer.setUnitOfMeasure(UOM_ST);
		final String isoCodeUnit = classUnderTest.getIsoUOM(productContainer);
		assertEquals(UOM_ST, isoCodeUnit);
	}

	@Test
	public void testRetrievePricingProductNullContext()
	{
		final String productCode = classUnderTest.retrievePricingProduct(productContainer, null);
		assertEquals(PRODUCT_ID, productCode);
	}

	@Test
	public void testRetrievePricingProductEmptyContext()
	{
		final String productCode = classUnderTest.retrievePricingProduct(productContainer, new MasterDataContext());
		assertEquals(PRODUCT_ID, productCode);
	}

	@Test
	public void testRetrievePricingProductFromContext()
	{
		final String productCode = classUnderTest.retrievePricingProduct(productContainer, context);
		assertEquals(PRICING_PRODUCT, productCode);
	}

	@Test
	public void testPopulateFillsPricingProductAttribute()
	{
		classUnderTest.populate(productContainer, target, context);
		boolean found = false;
		for (final Attribute attr : target.getAttributes())
		{
			if (SapproductconfigruntimecpsConstants.PRICING_ATTRIBUTE_MATERIAL_NUMBER.equals(attr.getName()))
			{
				assertEquals(PRICING_PRODUCT, attr.getValues().get(0));
				found = true;
			}
		}
		assertTrue("Pricing Attribute with material number i mandatory", found);
	}



}
