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
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.variants.model.VariantProductModel;
import de.hybris.platform.variants.model.VariantTypeModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 *
 */
@UnitTest
public class ConfigurationVariantUtilImplTest
{
	private static final String BASE_PRODUCT_CODE = "baseProductCode";
	private final ConfigurationVariantUtilImpl classUnderTest = new ConfigurationVariantUtilImpl();
	@Mock
	private ProductModel baseProductModel;

	@Mock
	private VariantProductModel variantProductModel;

	@Mock
	private ERPVariantProductModel changeableVariantProductModel;

	@Mock
	private ERPVariantProductModel notChangeableVariantProductModel;

	@Mock
	private VariantTypeModel variantTypeModel;

	@Mock
	private ProductModel mockProductModel;

	@Mock
	private VariantTypeModel mockVariantTypeModel;

	@Mock
	private MockVariantProductModel mockVariantProductModel;

	Set<String> changeableVariantBaseProducts = new HashSet<>();

	@Before
	public void setup()
	{
		changeableVariantBaseProducts.add(BASE_PRODUCT_CODE);
		classUnderTest.setChangeableVariantBaseProducts(changeableVariantBaseProducts);
		MockitoAnnotations.initMocks(this);
		given(baseProductModel.getVariantType()).willReturn(variantTypeModel);
		given(variantTypeModel.getCode()).willReturn(ERPVariantProductModel._TYPECODE);
		given(variantProductModel.getBaseProduct()).willReturn(baseProductModel);
		given(changeableVariantProductModel.getBaseProduct()).willReturn(baseProductModel);
		given(changeableVariantProductModel.isChangeable()).willReturn(true);
		given(notChangeableVariantProductModel.getBaseProduct()).willReturn(baseProductModel);
		given(notChangeableVariantProductModel.isChangeable()).willReturn(false);
		given(baseProductModel.getCode()).willReturn(BASE_PRODUCT_CODE);

		given(mockProductModel.getVariantType()).willReturn(mockVariantTypeModel);
		given(mockVariantTypeModel.getCode()).willReturn(MockVariantProductModel._TYPECODE);
		given(mockVariantProductModel.getBaseProduct()).willReturn(mockProductModel);
	}

	@Test
	public void testIsCPQBaseProduct()
	{
		assertTrue(classUnderTest.isCPQBaseProduct(baseProductModel));
	}

	@Test
	public void testIsCPQBaseProductNoVariant()
	{
		given(baseProductModel.getVariantType()).willReturn(null);
		assertFalse(classUnderTest.isCPQBaseProduct(baseProductModel));
	}

	@Test
	public void testIsCPQBaseProductWrongType()
	{
		given(variantTypeModel.getCode()).willReturn("UNKNOWN");
		assertFalse(classUnderTest.isCPQBaseProduct(baseProductModel));
	}

	@Test
	public void isCPQVariantProduct()
	{
		assertTrue(classUnderTest.isCPQVariantProduct(variantProductModel));
	}

	@Test
	public void isCPQVariantProductNoVariant()
	{
		assertFalse(classUnderTest.isCPQVariantProduct(baseProductModel));
	}

	@Test
	public void isCPQVariantProductNoVariantBaseProductNull()
	{
		given(variantProductModel.getBaseProduct()).willReturn(null);
		assertFalse(classUnderTest.isCPQVariantProduct(baseProductModel));
	}

	@Test
	public void getBaseProductCode()
	{
		assertEquals(BASE_PRODUCT_CODE, classUnderTest.getBaseProductCode(variantProductModel));
	}

	@Test
	public void testIsCPQChangeableVariantProductFalseBaseProduct()
	{
		assertFalse(classUnderTest.isCPQChangeableVariantProduct(baseProductModel));
	}

	@Test
	public void testIsCPQChangeableVariantProductFalseNotChangeableVariantProduct()
	{
		assertFalse(classUnderTest.isCPQChangeableVariantProduct(notChangeableVariantProductModel));
	}

	@Test
	public void testIsCPQChangeableVariantProductTrueChangeableVariantProduct()
	{
		assertTrue(classUnderTest.isCPQChangeableVariantProduct(changeableVariantProductModel));
	}

	@Test
	public void testIsCPQNotChangeableVariantProductFalseBaseProduct()
	{
		assertFalse(classUnderTest.isCPQNotChangeableVariantProduct(baseProductModel));
	}

	@Test
	public void testIsCPQNotChangeableVariantProductTrueNotChangeableVariantProduct()
	{
		assertTrue(classUnderTest.isCPQNotChangeableVariantProduct(notChangeableVariantProductModel));
	}

	@Test
	public void testIsCPQNotChangeableVariantProductFalseChangeableVariantProduct()
	{
		assertFalse(classUnderTest.isCPQNotChangeableVariantProduct(changeableVariantProductModel));
	}

	@Test
	public void testIsCPQNotChangeableVariantProductForMockVariantProduct()
	{
		assertTrue(classUnderTest.isCPQNotChangeableVariantProduct(mockVariantProductModel));
	}

	@Test
	public void testIsCPQChangeableVariantProductForMockVariantProduct()
	{
		assertFalse(classUnderTest.isCPQChangeableVariantProduct(mockVariantProductModel));
	}

	@Test
	public void testChangeableVariantBaseProducts()
	{
		assertEquals(changeableVariantBaseProducts, classUnderTest.getChangeableVariantBaseProducts());
	}
}
