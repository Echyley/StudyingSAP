/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.actionseditor;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionsRegistry;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionsService;
import de.hybris.platform.sap.productconfig.rules.backoffice.constants.SapproductconfigrulesbackofficeConstants;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;

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


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProductconfigProductCodeExtractorImplTest
{
	private static final String CONDITIONS_STRING = "conditionsString";

	@InjectMocks
	private ProductconfigProductCodeExtractorImpl classUnderTest;

	@Mock
	private RuleConditionsRegistry ruleConditionsRegistry;

	@Mock
	private RuleConditionsService ruleConditionsService;

	ProductConfigSourceRuleModel ruleModel;

	@Before
	public void setUp()
	{
		ruleModel = new ProductConfigSourceRuleModel();
		ruleModel.setConditions(CONDITIONS_STRING);

		final Map<String, RuleConditionDefinitionData> conditionDefinitions = new HashMap<String, RuleConditionDefinitionData>();
		final List<RuleConditionData> conditionDataList = creteConditionDataList();

		given(ruleConditionsRegistry.getConditionDefinitionsForRuleTypeAsMap(Mockito.any())).willReturn(conditionDefinitions);
		given(ruleConditionsService.convertConditionsFromString(CONDITIONS_STRING, conditionDefinitions))
				.willReturn(conditionDataList);
	}

	@Test
	public void testRetrieveProductCodeList()
	{
		final List<String> productCodeList = classUnderTest.retrieveProductCodeList(ruleModel);
		assertEquals(2, productCodeList.size());
		assertEquals("PRODUCT_1", productCodeList.get(0));
		assertEquals("PRODUCT_3", productCodeList.get(1));
	}

	private List<RuleConditionData> creteConditionDataList()
	{
		final List<RuleConditionData> conditionDataList = new ArrayList<RuleConditionData>();

		RuleConditionData conditionData = new RuleConditionData();
		conditionData.setDefinitionId(SapproductconfigrulesbackofficeConstants.CONDITION_DEFINITION_ID_CURRENT_CONFIG_PRODUCT);
		Map<String, RuleParameterData> parameters = new HashMap<String, RuleParameterData>();
		RuleParameterData productParameter = new RuleParameterData();
		productParameter.setValue("PRODUCT_1");
		parameters.put(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT, productParameter);
		conditionData.setParameters(parameters);
		conditionDataList.add(conditionData);

		conditionData = new RuleConditionData();
		conditionData.setDefinitionId("y_configurable_product_in_cart");
		parameters = new HashMap<String, RuleParameterData>();
		productParameter = new RuleParameterData();
		productParameter.setValue("PRODUCT_2");
		parameters.put(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT, productParameter);
		conditionData.setParameters(parameters);
		conditionDataList.add(conditionData);

		conditionData = new RuleConditionData();
		conditionData.setDefinitionId(SapproductconfigrulesbackofficeConstants.CONDITION_DEFINITION_ID_CURRENT_CONFIG_PRODUCT);
		parameters = new HashMap<String, RuleParameterData>();
		productParameter = new RuleParameterData();
		productParameter.setValue("PRODUCT_3::CATALOGVERSION");
		parameters.put(SapproductconfigrulesbackofficeConstants.PARAMETER_PRODUCT, productParameter);
		conditionData.setParameters(parameters);
		conditionDataList.add(conditionData);

		return conditionDataList;
	}
}

