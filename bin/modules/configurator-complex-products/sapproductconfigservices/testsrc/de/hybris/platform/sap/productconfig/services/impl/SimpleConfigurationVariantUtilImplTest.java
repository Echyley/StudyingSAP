/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.services.model.MockVariantProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link SimpleConfigurationVariantUtilImpl}
 */
@UnitTest
public class SimpleConfigurationVariantUtilImplTest
{
	private static final String BASE_PRODUCT_CODE = "baseProductCode";
	private static final String CHANGEABLE_VARIANT_BASE_PRODUCT_CODE = "CONF_M_PIPE";
	private final SimpleConfigurationVariantUtilImpl classUnderTest = new SimpleConfigurationVariantUtilImpl();

	Set<String> changeableVariantBaseProducts = new HashSet<>();

	@Mock
	private VariantTypeModel variantTypeModel;

	@Mock
	private ProductModel baseProductModel;

	@Mock
	private ProductModel standardProductModel;

	@Mock
	private ProductModel changeableVariantBaseProductModel;

	@Mock
	private VariantProductModel nonCPQVariantProductModel;

	@Mock
	private VariantProductModel variantProductModel;

	@Mock
	private VariantProductModel changeableVariantProductModel;


	@Before
	public void setup()
	{
		changeableVariantBaseProducts.add(CHANGEABLE_VARIANT_BASE_PRODUCT_CODE);
		classUnderTest.setChangeableVariantBaseProducts(changeableVariantBaseProducts);
		MockitoAnnotations.initMocks(this);
		given(variantProductModel.getBaseProduct()).willReturn(baseProductModel);
		given(nonCPQVariantProductModel.getBaseProduct()).willReturn(standardProductModel);
		given(changeableVariantProductModel.getBaseProduct()).willReturn(changeableVariantBaseProductModel);
		given(changeableVariantProductModel.getVariantType()).willReturn(variantTypeModel);

		given(baseProductModel.getCode()).willReturn(BASE_PRODUCT_CODE);
		given(baseProductModel.getVariantType()).willReturn(variantTypeModel);
		given(changeableVariantBaseProductModel.getCode()).willReturn(CHANGEABLE_VARIANT_BASE_PRODUCT_CODE);
		given(changeableVariantBaseProductModel.getVariantType()).willReturn(variantTypeModel);

		given(variantTypeModel.getCode()).willReturn(MockVariantProductModel._TYPECODE);
	}

	@Test
	public void testIsCPQBaseProductNotConfigurableBase()
	{
		assertFalse(classUnderTest.isCPQBaseProduct(standardProductModel));
	}

	@Test
	public void testIsCPQBaseProduct()
	{
		assertTrue(classUnderTest.isCPQBaseProduct(baseProductModel));
	}

	@Test
	public void isCPQVariantProduct()
	{
		assertTrue(classUnderTest.isCPQVariantProduct(variantProductModel));
	}

	@Test
	public void isCPQVariantProductChangeableVariant()
	{
		assertTrue(classUnderTest.isCPQVariantProduct(changeableVariantProductModel));
	}

	@Test
	public void isCPQVariantProductNoVariantAtAll()
	{
		assertFalse(classUnderTest.isCPQVariantProduct(standardProductModel));
	}

	@Test
	public void isCPQVariantProductNoCPQVariant()
	{
		assertFalse(classUnderTest.isCPQVariantProduct(nonCPQVariantProductModel));
	}

	@Test
	public void getBaseProductCode()
	{
		assertEquals(BASE_PRODUCT_CODE, classUnderTest.getBaseProductCode(variantProductModel));
	}

	@Test
	public void testIsCPQChangeableVariantProductForBaseProduct()
	{
		assertFalse(classUnderTest.isCPQChangeableVariantProduct(variantProductModel));
	}

	@Test
	public void testIsCPQChangeableVariantProductForVariant()
	{
		assertTrue(classUnderTest.isCPQChangeableVariantProduct(changeableVariantProductModel));
	}

	@Test
	public void testIsCPQNotChangeableVariantProduct()
	{
		assertTrue(classUnderTest.isCPQNotChangeableVariantProduct(variantProductModel));
	}

	@Test
	public void testChangeableVariantBaseProducts()
	{
		assertEquals(changeableVariantBaseProducts, classUnderTest.getChangeableVariantBaseProducts());
	}
}
