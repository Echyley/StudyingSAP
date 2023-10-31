/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.actionseditor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.model.AbstractRuleTemplateModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleTemplateModel;
import de.hybris.platform.ruleengineservices.rule.services.RuleService;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductconfigActionsEditorSectionRendererTest
{
	protected static final String RULE_CODE = "TEST_RULE";

	@Mock
	ProductconfigProductCodeExtractor productCodeExtractor;

	@Mock
	ModelService modelService;

	@Mock
	RuleService ruleService;

	@InjectMocks
	private final ProductconfigActionsEditorSectionRenderer classUnderTest = new ProductconfigActionsEditorSectionRendererForTest();

	@Before
	public void setUp()
	{
		final List<String> productCodeList = new ArrayList<>();
		productCodeList.add("PRODUCT_1");
		productCodeList.add("PRODUCT_2");
		given(productCodeExtractor.retrieveProductCodeList(Mockito.any())).willReturn(productCodeList);
		when(ruleService.getRuleTypeFromTemplate(any())).thenReturn(null);
	}

	@Test
	public void testGetEditorId()
	{
		final String editorId = classUnderTest.getEditorId();
		assertEquals(ProductconfigActionsEditorSectionRenderer.PRODUCTCONFIG_ACTIONS_EDITOR_ID, editorId);
	}

	@Test
	public void testAddProductCodeListToParameters()
	{
		final Map<Object, Object> parameters = new HashMap<>();
		final ProductConfigSourceRuleModel model = new ProductConfigSourceRuleModel();
		model.setCode(RULE_CODE);

		classUnderTest.addProductCodeListToParameters(model, parameters);

		final List<String> retrievedProductCodeList = (List<String>) parameters
				.get(SapproductconfigrulesbackofficeConstants.PRODUCT_CODE_LIST);

		assertEquals(2, retrievedProductCodeList.size());
		assertEquals("PRODUCT_1", retrievedProductCodeList.get(0));
		assertEquals("PRODUCT_2", retrievedProductCodeList.get(1));
	}

	@Test
	public void testFillParametersWithNoProductConfigSourceRule()
	{
		final Map<Object, Object> parameters = new HashMap<>();
		final AbstractRuleTemplateModel model = new SourceRuleTemplateModel();

		classUnderTest.fillParameters(model, null, parameters);

		for (final Object paramName : parameters.keySet())
		{
			assertNotEquals("parameter productCodeList nor expected", SapproductconfigrulesbackofficeConstants.PRODUCT_CODE_LIST,
					paramName);
		}
	}

	private class ProductconfigActionsEditorSectionRendererForTest extends ProductconfigActionsEditorSectionRenderer
	{
		@Override
		protected boolean canChangeProperty(final DataAttribute attribute, final Object instance)
		{
			return false;
		}
	}
}
