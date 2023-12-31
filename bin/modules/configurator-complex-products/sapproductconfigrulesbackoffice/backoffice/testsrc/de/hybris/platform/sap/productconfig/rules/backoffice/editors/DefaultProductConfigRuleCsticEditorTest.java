/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.variants.model.VariantProductModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;

import com.hybris.cockpitng.editors.EditorContext;
import com.hybris.cockpitng.editors.EditorListener;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultProductConfigRuleCsticEditorTest extends ProductConfigRuleParameterEditorTestBase
{
	@InjectMocks
	private DefaultProductConfigRuleCsticEditor classUnderTest;

	@Test
	public void testGetPossibleValuesForConditionParameterNoProduct()
	{
		final List<Object> possibleValues = classUnderTest.getPossibleValuesForConditionParameter(null);
		assertEquals(0, possibleValues.size());

	}

	@Test
	public void testGetPossibleValuesForConditionParameter()
	{
		final List<Object> possibleValues = classUnderTest.getPossibleValuesForConditionParameter(productModel.getCode());
		assertEquals(3, possibleValues.size());
		assertTrue(possibleValues.contains(CSTIC_1));
		assertTrue(possibleValues.contains(CSTIC_2));
		assertTrue(possibleValues.contains(CSTIC_3));
	}

	@Test
	public void testGetPossibleValuesForActionParameterNoProduct()
	{
		final List<String> productCodeList = new ArrayList<>();
		productCodeList.add(null);
		final List<Object> possibleValues = classUnderTest.getPossibleValuesForActionParameter(productCodeList);
		assertEquals(0, possibleValues.size());

	}

	@Test
	public void testGetPossibleValuesForActionParameter()
	{
		final List<String> productCodeList = new ArrayList<>();
		productCodeList.add(PRODUCT_CODE);
		productCodeList.add(PRODUCT_CODE2);
		final List<Object> possibleValues = classUnderTest.getPossibleValuesForActionParameter(productCodeList);
		assertEquals(4, possibleValues.size());
		assertTrue(possibleValues.contains(CSTIC_1));
		assertTrue(possibleValues.contains(CSTIC_2));
		assertTrue(possibleValues.contains(CSTIC_3));
		assertTrue(possibleValues.contains(CSTIC_4));
	}

	@Test
	public void testGetPossibleValues_ProductModel()
	{
		final List<Object> possibleValues = classUnderTest.getPossibleValues(context);
		assertEquals(3, possibleValues.size());
	}

	@Test
	public void testGetPossibleValues_ProductCodeList()
	{
		final List<String> productCodeList = new ArrayList<>();
		productCodeList.add(PRODUCT_CODE);
		productCodeList.add(PRODUCT_CODE2);
		parameters.put(SapproductconfigrulesbackofficeConstants.REFERENCE_SEARCH_CONDITION_PRODUCT_CODE_LIST, productCodeList);

		final List<Object> possibleValues = classUnderTest.getPossibleValues(context);
		assertEquals(4, possibleValues.size());
	}

	@Test
	public void testAddValuesForProductCode()
	{
		final List<Object> values = new ArrayList<>();
		classUnderTest.addValuesForProductCode(values, PRODUCT_CODE);
		assertEquals(3, values.size());
		assertTrue(values.contains(CSTIC_1));
		assertTrue(values.contains(CSTIC_2));
		assertTrue(values.contains(CSTIC_3));

		classUnderTest.addValuesForProductCode(values, PRODUCT_CODE2);
		assertEquals(4, values.size());
		assertTrue(values.contains(CSTIC_1));
		assertTrue(values.contains(CSTIC_2));
		assertTrue(values.contains(CSTIC_3));
		assertTrue(values.contains(CSTIC_4));
	}

	@Test
	public void testPrepareModelWithoutInitialValue()
	{
		when(context.getInitialValue()).thenReturn(null);
		final ListModelList<Object> model = classUnderTest.prepareModel(context);

		assertEquals(3, model.size());
		assertEquals(0, model.getSelection().size());
	}

	@Test
	public void testPrepareModelWithInitialValueInList()
	{
		when(context.getInitialValue()).thenReturn(CSTIC_1);
		final ListModelList<Object> model = classUnderTest.prepareModel(context);

		assertEquals(3, model.size());
		assertEquals(CSTIC_1, model.get(0));
		assertEquals(1, model.getSelection().size());
		assertEquals(CSTIC_1, model.getSelection().iterator().next());
	}

	@Test
	public void testPrepareModelWithInitialValue()
	{
		when(context.getInitialValue()).thenReturn("Test");
		final ListModelList<Object> model = classUnderTest.prepareModel(context);

		assertEquals(4, model.size());
		assertEquals("Test", model.get(0));
		assertEquals(1, model.getSelection().size());
	}



	@Test
	public void testRenderDisabled()
	{
		when(context.getInitialValue()).thenReturn(CSTIC_1);

		final Component parent = Mockito.mock(Component.class);
		final EditorListener<Object> listener = Mockito.mock(EditorListener.class);
		// real class does a lot of constructor logic for zkoss framework, we do not want to test anyways
		final Combobox combobox = Mockito.mock(Combobox.class);
		final AbstractProductConfigRuleParameterEditor classUnderTestImpl = mockComboBox(combobox);

		classUnderTestImpl.render(parent, context, listener);

		verify(combobox, times(1)).setDisabled(true);
		verify(combobox, times(1)).setReadonly(false);
		verify(combobox, times(1)).setAutodrop(true);
	}


	protected AbstractProductConfigRuleParameterEditor mockComboBox(final Combobox combobox)
	{
		final AbstractProductConfigRuleParameterEditor classUnderTestImpl = new AbstractProductConfigRuleParameterEditor()
		{
			@Override
			protected List<Object> getPossibleValues(final EditorContext<Object> context)
			{
				return new ArrayList<>();
			}

			@Override
			protected Combobox createCombobox()
			{
				return combobox;
			}
		};
		return classUnderTestImpl;
	}

	@Test
	public void testRenderEnabled()
	{
		when(context.getInitialValue()).thenReturn(CSTIC_1);
		when(context.isEditable()).thenReturn(true);

		final Component parent = Mockito.mock(Component.class);
		final EditorListener<Object> listener = Mockito.mock(EditorListener.class);
		// real class does a lot of constructor logic for zkoss framework, we do not want to test anyways
		final Combobox combobox = Mockito.mock(Combobox.class);
		final AbstractProductConfigRuleParameterEditor classUnderTestImpl = mockComboBox(combobox);


		classUnderTestImpl.render(parent, context, listener);

		verify(combobox, times(1)).setDisabled(false);
		verify(combobox, times(1)).setReadonly(false);
		verify(combobox, times(1)).setAutodrop(true);
	}

	@Test
	public void testUseBaseProductCodeForChangeableVariantNotChangeableVariant()
	{
		final String productCode = "NOT_CHANGEABLE_VARIANT_PRODUCT_CODE";
		assertEquals(productCode, classUnderTest.useBaseProductCodeForChangeableVariant(productCode));
	}

	@Test
	public void testUseBaseProductCodeForChangeableVariantChangeableVariant()
	{
		final String baseProductCode = "BASE_PRODUCT_CODE";
		final String changeableVariantProductCode = "CHANGEABLE_VARIANT_PRODUCT_CODE";

		final ProductModel baseProduct = new ProductModel();
		baseProduct.setCode(baseProductCode);
		final VariantProductModel variantProduct = new VariantProductModel();
		variantProduct.setBaseProduct(baseProduct);
		final List<Object> resultList = new ArrayList<Object>();
		resultList.add(variantProduct);

		given(searchResult.getResult()).willReturn(resultList);
		given(configurationVariantUtil.isCPQChangeableVariantProduct(variantProduct)).willReturn(true);

		assertEquals(baseProductCode, classUnderTest.useBaseProductCodeForChangeableVariant(changeableVariantProductCode));
	}

}
