/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.mock.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.impl.CsticValueModelImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link ConfPipeMockImpl}
 */
@UnitTest
public class ConfPipeMockImplTest
{
	private static final String PRODUCT_CODE_VARIANT = "CONF_M_PIPE-1-1-T";
	private ConfPipeMockImpl classUnderTest;
	private ConfigModel model;
	private InstanceModel instance;
	private InstanceModel baseSubInstance;

	@Mock
	private ProductModel productModel;
	@Mock
	private PriceRowModel priceRowModel;

	private final Double priceFromProduct = 123d;
	private final BigDecimal priceFromProductDec = BigDecimal.valueOf(priceFromProduct);
	private final Collection<PriceRowModel> priceRowModels = new ArrayList<>();


	@Before
	public void setUp()
	{
		classUnderTest = (ConfPipeMockImpl) new RunTimeConfigMockFactory().createConfigMockForProductCode("CONF_M_PIPE");
		model = classUnderTest.createDefaultConfiguration();
		instance = model.getRootInstance();

		MockitoAnnotations.openMocks(this);
		given(productModel.getEurope1Prices()).willReturn(priceRowModels);
		priceRowModels.add(priceRowModel);
		given(priceRowModel.getPrice()).willReturn(priceFromProduct);

	}

	protected List<CsticValueModel> setAssignedValue(final String value)
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		final CsticValueModel csticValue = new CsticValueModelImpl();
		csticValue.setName(value);
		assignedValues.add(csticValue);

		return assignedValues;
	}

	protected void checkNullPrices()
	{
		assertNull(model.getCurrentTotalPrice());
		assertNull(model.getBasePrice());
		assertNull(model.getSelectedOptionsPrice());
	}

	@Test
	public void testCreateType()
	{
		final Map<String, String> variantParams = classUnderTest.createVariantParams(PRODUCT_CODE_VARIANT);

		final CsticModel csticModel = classUnderTest.createType(variantParams);
		assertNotNull(csticModel);
		assertEquals(ConfPipeMockImpl.TYPE_NAME, csticModel.getName());
	}

	@Test
	public void testCreateInnerDiameter()
	{
		final Map<String, String> variantParams = classUnderTest.createVariantParams(PRODUCT_CODE_VARIANT);

		final CsticModel csticModel = classUnderTest.createInnerDiameter(variantParams);
		assertNotNull(csticModel);
		assertEquals(ConfPipeMockImpl.INNER_DIA_NAME, csticModel.getName());
	}

	@Test
	public void testCreateLength()
	{
		final CsticModel csticModel = classUnderTest.createLength();
		assertNotNull(csticModel);
		assertEquals(ConfPipeMockImpl.LENGTH_NAME, csticModel.getName());
	}

	@Test
	public void testCreateOuterDiameter()
	{
		final Map<String, String> variantParams = classUnderTest.createVariantParams(PRODUCT_CODE_VARIANT);
		final CsticModel csticModel = classUnderTest.createOuterDiameter(variantParams);
		assertNotNull(csticModel);
		assertEquals(ConfPipeMockImpl.OUTER_DIA_NAME, csticModel.getName());
	}

	@Test
	public void testIsChangeableVariant()
	{
		assertTrue(classUnderTest.isChangeableVariant());
	}

	@Test
	public void testSetVariantCode()
	{
		final Map<String, String> variantParams = classUnderTest.createVariantParams(PRODUCT_CODE_VARIANT);
		assertEquals("1", variantParams.get(ConfPipeMockImpl.OUTER_DIA_NAME));
		assertEquals("1", variantParams.get(ConfPipeMockImpl.INNER_DIA_NAME));
		assertEquals("T", variantParams.get(ConfPipeMockImpl.TYPE_NAME));
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateVariantParamsWrongPattern()
	{
		classUnderTest.createVariantParams("NoVariant");
	}

	@Test(expected = IllegalStateException.class)
	public void testCreateVariantParamsWrongPatternToManyAttribs()
	{
		classUnderTest.createVariantParams(PRODUCT_CODE_VARIANT + "-2");
	}

	@Test
	public void testCreateVarianParamsFromNullCodeNull()
	{
		final Map<String, String> variantParams = classUnderTest.createVariantParams(null);
		assertNull(variantParams.get(ConfPipeMockImpl.OUTER_DIA_NAME));
	}

	@Test
	public void testAddProductAttributes()
	{
		classUnderTest.addProductAttributes(model, productModel);
		assertEquals(priceFromProductDec, model.getCurrentTotalPrice().getPriceValue());
		assertEquals(priceFromProductDec, model.getBasePrice().getPriceValue());
		assertEquals(BigDecimal.ZERO, model.getSelectedOptionsPrice().getPriceValue());
	}

	@Test
	public void testAddProductAttributesPriceRowsNull()
	{
		given(productModel.getEurope1Prices()).willReturn(null);
		classUnderTest.addProductAttributes(model, productModel);
		checkNullPrices();
	}

	@Test
	public void testAddProductAttributesPriceRowsEmpty()
	{
		given(productModel.getEurope1Prices()).willReturn(Collections.emptyList());
		classUnderTest.addProductAttributes(model, productModel);
		checkNullPrices();
	}

	@Test
	public void testAddProductAttributesNullProduct()
	{
		classUnderTest.addProductAttributes(model, null);
		checkNullPrices();
	}

}
