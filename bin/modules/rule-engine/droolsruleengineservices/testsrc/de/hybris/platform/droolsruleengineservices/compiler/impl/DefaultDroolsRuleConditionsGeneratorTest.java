/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.droolsruleengineservices.compiler.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.droolsruleengineservices.compiler.DroolsRuleGeneratorContext;
import de.hybris.platform.droolsruleengineservices.compiler.DroolsRuleValueFormatter;
import de.hybris.platform.ruleengineservices.compiler.RuleIr;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrAttributeRelCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrExistsCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrLocalVariablesContainer;
import de.hybris.platform.ruleengineservices.compiler.RuleIrNotCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrTypeCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrVariable;
import de.hybris.platform.ruleengineservices.rao.AbstractRuleActionRAO;
import de.hybris.platform.ruleengineservices.rao.CartRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryGroupRAO;
import de.hybris.platform.ruleengineservices.rao.OrderEntryRAO;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.ruleengineservices.rao.WebsiteGroupRAO;
import de.hybris.platform.ruleengineservices.util.DroolsStringUtils;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDroolsRuleConditionsGeneratorTest extends AbstractGeneratorTest
{
	private static final String ORDER_ENTRY_CLASS_NAME = "OrderEntryRAO";
	private static final String ORDER_ENTRY_VARIABLE_NAME = "orderEntry";
	private static final String INDENTATION = "  ";
	private static final String VARIABLE_PREFIX = "$";
	private static final String ATTRIBUTE_DELIMITER = ".";

	private static final String CART_VARIABLE_NAME = "cart";
	private static final String CART_VARIABLE_CLASS_NAME = "CartRAO";

	private static final String ENTRYGROUP_VARIABLE_NAME = "entryGroup";
	private static final String ENTRYGROUP_VARIABLE_CLASS_NAME = "OrderEntryGroupRAO";

	private static final String PRODUCT_VARIABLE_NAME = "productCode";

	private static final String RESULT_VARIABLE_NAME = "result";
	private static final String RESULT_VARIABLE_CLASS_NAME = "RuleEngineResultRAO";

	private static final String WEBSITE_VARIABLE_NAME = "website";
	private static final String WEBSITE_VARIABLE_CLASS_NAME = "WebsiteGroupRAO";

	private static final String ACTION_VARIABLE_NAME = "action";
	private static final String ACTION_VARIABLE_CLASS_NAME = "AbstractRuleActionRAO";


	@Mock
	private DroolsRuleGeneratorContext droolsRuleGeneratorContext;

	@Mock
	private DroolsRuleValueFormatter droolsRuleValueFormatter;

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	private RuleIrVariable cartVariable;
	private RuleIrVariable resultVariable;
	private RuleIrVariable orderEntryVariable;
	private RuleIrVariable entryGroupVariable;
	private RuleIrVariable websiteGrpVariable;
	private RuleIrVariable actionVariable;

	private RuleIr ruleIr;
	private Map<String, RuleIrVariable> ruleIrVariables;
	private RuleIrTypeCondition ruleIrResultCondition;

	private DefaultDroolsRuleConditionsGenerator conditionsGenerator;

	private DroolsStringUtils droolsStringUtils;

	@Before
	public void setUp()
	{

		cartVariable = new RuleIrVariable();
		cartVariable.setName(CART_VARIABLE_NAME);
		cartVariable.setType(CartRAO.class);

		orderEntryVariable = new RuleIrVariable();
		orderEntryVariable.setName(ORDER_ENTRY_VARIABLE_NAME);
		orderEntryVariable.setType(OrderEntryRAO.class);

		entryGroupVariable = new RuleIrVariable();
		entryGroupVariable.setName(ENTRYGROUP_VARIABLE_NAME);
		entryGroupVariable.setType(OrderEntryGroupRAO.class);

		resultVariable = new RuleIrVariable();
		resultVariable.setName(RESULT_VARIABLE_NAME);
		resultVariable.setType(RuleEngineResultRAO.class);

		websiteGrpVariable = new RuleIrVariable();
		websiteGrpVariable.setName(WEBSITE_VARIABLE_NAME);
		websiteGrpVariable.setType(WebsiteGroupRAO.class);

		actionVariable = new RuleIrVariable();
		actionVariable.setName(ACTION_VARIABLE_NAME);
		actionVariable.setType(AbstractRuleActionRAO.class);

		ruleIr = new RuleIr();
		ruleIrVariables = new LinkedHashMap<>();

		ruleIrResultCondition = new RuleIrTypeCondition();
		ruleIrResultCondition.setVariable(RESULT_VARIABLE_NAME);

		Mockito.lenient().when(droolsRuleGeneratorContext.getIndentationSize()).thenReturn(INDENTATION);
		Mockito.lenient().when(droolsRuleGeneratorContext.getVariablePrefix()).thenReturn(VARIABLE_PREFIX);
		Mockito.lenient().when(droolsRuleGeneratorContext.getAttributeDelimiter()).thenReturn(ATTRIBUTE_DELIMITER);
		Mockito.lenient().when(droolsRuleGeneratorContext.getRuleIr()).thenReturn(ruleIr);
		Mockito.lenient().when(droolsRuleGeneratorContext.getVariables()).thenReturn(ruleIrVariables);
		Mockito.lenient().when(droolsRuleGeneratorContext.getLocalVariables()).thenReturn(new ArrayDeque<>());
		Mockito.lenient().when(droolsRuleGeneratorContext.generateClassName(CartRAO.class)).thenReturn(CART_VARIABLE_CLASS_NAME);
		Mockito.lenient().when(droolsRuleGeneratorContext.generateClassName(RuleEngineResultRAO.class)).thenReturn(RESULT_VARIABLE_CLASS_NAME);
		Mockito.lenient().when(droolsRuleGeneratorContext.generateClassName(OrderEntryRAO.class)).thenReturn(ORDER_ENTRY_CLASS_NAME);
		Mockito.lenient().when(droolsRuleGeneratorContext.generateClassName(OrderEntryGroupRAO.class)).thenReturn(ENTRYGROUP_VARIABLE_CLASS_NAME);
		Mockito.lenient().when(droolsRuleGeneratorContext.generateClassName(WebsiteGroupRAO.class)).thenReturn(WEBSITE_VARIABLE_CLASS_NAME);
		Mockito.lenient().when(droolsRuleGeneratorContext.generateClassName(AbstractRuleActionRAO.class)).thenReturn(ACTION_VARIABLE_CLASS_NAME);

		Mockito.lenient().when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.lenient().when(configuration.getBoolean("droolsruleengineservices.generate.unique.fact.matching.condition", true)).thenReturn(true);

		droolsStringUtils = new DroolsStringUtils();

		conditionsGenerator = new DefaultDroolsRuleConditionsGenerator();
		conditionsGenerator.setDroolsRuleValueFormatter(droolsRuleValueFormatter);
		conditionsGenerator.setExcludedQueryVariableClassNames(Collections.emptyList());
		conditionsGenerator.setTypesToSkipOperatorEvaluation(List.of(AbstractList.class));
		conditionsGenerator.setConfigurationService(configurationService);
		conditionsGenerator.setDroolsStringUtils(droolsStringUtils);
	}

	@Test
	public void testSingleNotCondition() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsForSingleNotCondition.bin");

		final RuleIrLocalVariablesContainer varContainer = new RuleIrLocalVariablesContainer();
		varContainer.setVariables(ruleIrVariables);
		ruleIrVariables.put(orderEntryVariable.getName(), orderEntryVariable);

		final RuleIrAttributeCondition entry = createRuleIrAttributeCondition(PRODUCT_VARIABLE_NAME,
				RuleIrAttributeOperator.EQUAL, "1234567", orderEntryVariable.getName());

		final RuleIrExistsCondition exists = new RuleIrExistsCondition();
		exists.setVariablesContainer(varContainer);
		exists.setChildren(Collections.singletonList(entry));

		final RuleIrNotCondition irNot = new RuleIrNotCondition();
		irNot.setChildren(Collections.singletonList(exists));

		ruleIr.setConditions(Collections.singletonList(irNot));
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(entry)))
				.thenReturn("\"1234567\"");
		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateConditions(droolsRuleGeneratorContext, INDENTATION);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);

	}

	@Test
	public void testSingleCondition() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsForSingleCondition.bin");

		final BigDecimal totalValue = BigDecimal.valueOf(100);

		final RuleIrAttributeCondition amountCondition = createRuleIrAttributeCondition("total",
				RuleIrAttributeOperator.GREATER_THAN, totalValue, CART_VARIABLE_NAME);

		ruleIrVariables.put(CART_VARIABLE_NAME, cartVariable);
		ruleIrVariables.put(RESULT_VARIABLE_NAME, resultVariable);
		ruleIr.setConditions(Arrays.asList(ruleIrResultCondition, amountCondition));

		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(amountCondition))).thenReturn(
				"new java.math.BigDecimal(\"100\")");

		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateConditions(droolsRuleGeneratorContext, INDENTATION);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	@Test
	public void testSingleConditionWithExcludedVariableClassNames() throws IOException
	{
		conditionsGenerator.setExcludedQueryVariableClassNames(Collections.singletonList("CartRAO"));

		// given
		final BigDecimal totalValue = BigDecimal.valueOf(100);

		final RuleIrAttributeCondition amountCondition = createRuleIrAttributeCondition("total",
				RuleIrAttributeOperator.GREATER_THAN, totalValue, CART_VARIABLE_NAME);

		ruleIrVariables.put(CART_VARIABLE_NAME, cartVariable);
		ruleIrVariables.put(RESULT_VARIABLE_NAME, resultVariable);
		ruleIr.setConditions(Arrays.asList(ruleIrResultCondition, amountCondition));

		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(droolsRuleGeneratorContext, amountCondition)).thenReturn(
				"new java.math.BigDecimal(\"100\")");

		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateConditions(droolsRuleGeneratorContext, INDENTATION);

		// then
		assertEquals("", generatedDroolsCode);
	}

	@Test
	public void testSingleRelConditionWithTargetAttribute() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsForSingleRelConditionWithTargetAttribute.bin");

		final Integer quantity = Integer.valueOf(1);

		final RuleIrAttributeCondition qtyCondition = createRuleIrAttributeCondition("quantity",
				RuleIrAttributeOperator.GREATER_THAN_OR_EQUAL, quantity, ORDER_ENTRY_VARIABLE_NAME);

		final RuleIrAttributeRelCondition relationCondition = createRuleIrAttributeRelCondition("entryGroupId",
				RuleIrAttributeOperator.MEMBER_OF, ENTRYGROUP_VARIABLE_NAME, ORDER_ENTRY_VARIABLE_NAME);
		relationCondition.setTargetAttribute("entryGroupNumbers");

		ruleIrVariables.put(ORDER_ENTRY_VARIABLE_NAME, orderEntryVariable);
		ruleIrVariables.put(ENTRYGROUP_VARIABLE_NAME, entryGroupVariable);
		ruleIr.setConditions(Arrays.asList(qtyCondition, relationCondition));

		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(qtyCondition))).thenReturn("new Integer(1)");

		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateConditions(droolsRuleGeneratorContext, INDENTATION);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	@Test
	public void testMultipleConditions() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsForMultipleConditions.bin");


		final String colorValue = "blue";
		final RuleIrAttributeCondition colorCondition = createRuleIrAttributeCondition("color",
				RuleIrAttributeOperator.EQUAL, colorValue, ORDER_ENTRY_VARIABLE_NAME);

		final BigDecimal totalValue = BigDecimal.valueOf(100);
		final RuleIrAttributeCondition amountCondition = createRuleIrAttributeCondition("total",
				RuleIrAttributeOperator.GREATER_THAN, totalValue, CART_VARIABLE_NAME);

		final RuleIrGroupCondition blueOrCartCondition = createIrGroupCondition(RuleIrGroupOperator.AND, Arrays.asList(colorCondition, amountCondition));

		final List<String> codeValues = Arrays.asList("123", "456", "789");
		final RuleIrAttributeCondition code = createRuleIrAttributeCondition("productCode",
				RuleIrAttributeOperator.IN, codeValues, ORDER_ENTRY_VARIABLE_NAME);

		final RuleIrGroupCondition group = createIrGroupCondition(RuleIrGroupOperator.OR, Arrays.asList(code, blueOrCartCondition));

		ruleIrVariables.put(ORDER_ENTRY_VARIABLE_NAME, orderEntryVariable);
		ruleIrVariables.put(CART_VARIABLE_NAME, cartVariable);
		ruleIrVariables.put(RESULT_VARIABLE_NAME, resultVariable);
		ruleIr.setConditions(Arrays.asList(ruleIrResultCondition, group));

		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(colorCondition))).thenReturn("\"blue\"");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(amountCondition))).thenReturn(
				"new java.math.BigDecimal(\"100\")");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(code)))
				.thenReturn("productCode == \"123\" || productCode == \"456\" || productCode == \"789\"");

		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateConditions(droolsRuleGeneratorContext, INDENTATION);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	@Test
	public void testMultipleConditionsWithSameTypeVariables() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsForMultipleConditionsWithSameTypeVariables.bin");

		final RuleIrVariable orderEntryVariable2 = new RuleIrVariable();
		orderEntryVariable2.setName(ORDER_ENTRY_VARIABLE_NAME + "_2");
		orderEntryVariable2.setType(OrderEntryRAO.class);

		final String colorValue = "blue";
		final RuleIrAttributeCondition colorCondition = createRuleIrAttributeCondition("color",
				RuleIrAttributeOperator.EQUAL, colorValue, ORDER_ENTRY_VARIABLE_NAME);

		final RuleIrAttributeCondition colorConditionForProduct2 = createRuleIrAttributeCondition("color",
				RuleIrAttributeOperator.EQUAL, "red", orderEntryVariable2.getName());

		final BigDecimal totalValue = BigDecimal.valueOf(100);
		final RuleIrAttributeCondition amountCondition = createRuleIrAttributeCondition("total",
				RuleIrAttributeOperator.GREATER_THAN, totalValue, CART_VARIABLE_NAME);

		final RuleIrGroupCondition blueOrCartCondition = createIrGroupCondition(RuleIrGroupOperator.AND, Arrays.asList(colorCondition, amountCondition));

		final List<String> codeValues = Arrays.asList("123", "456", "789");
		final RuleIrAttributeCondition code = createRuleIrAttributeCondition("productCode",
				RuleIrAttributeOperator.IN, codeValues, ORDER_ENTRY_VARIABLE_NAME);

		final RuleIrGroupCondition group = createIrGroupCondition(RuleIrGroupOperator.OR, Arrays.asList(code, blueOrCartCondition,
				colorConditionForProduct2));

		ruleIrVariables.put(ORDER_ENTRY_VARIABLE_NAME, orderEntryVariable);
		ruleIrVariables.put(ORDER_ENTRY_VARIABLE_NAME + "_2", orderEntryVariable2);
		ruleIrVariables.put(CART_VARIABLE_NAME, cartVariable);
		ruleIrVariables.put(RESULT_VARIABLE_NAME, resultVariable);
		ruleIr.setConditions(Arrays.asList(ruleIrResultCondition, group));

		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(colorCondition))).thenReturn("\"blue\"");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(colorConditionForProduct2))).thenReturn("\"red\"");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(amountCondition))).thenReturn(
				"new java.math.BigDecimal(\"100\")");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(code)))
				.thenReturn("productCode == \"123\" || productCode == \"456\" || productCode == \"789\"");

		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateConditions(droolsRuleGeneratorContext, INDENTATION);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	@Test
	public void testDisableUniqueFactMatchingCondition() throws IOException
	{

		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsUniqueFactPatternDisabled.bin");

		final RuleIrVariable orderEntryVariable2 = new RuleIrVariable();
		orderEntryVariable2.setName(ORDER_ENTRY_VARIABLE_NAME + "_2");
		orderEntryVariable2.setType(OrderEntryRAO.class);

		final String colorValue = "blue";
		final RuleIrAttributeCondition colorCondition = createRuleIrAttributeCondition("color", RuleIrAttributeOperator.EQUAL,
				colorValue, ORDER_ENTRY_VARIABLE_NAME);

		final RuleIrAttributeCondition colorConditionForProduct2 = createRuleIrAttributeCondition("color",
				RuleIrAttributeOperator.EQUAL, "red", orderEntryVariable2.getName());

		final BigDecimal totalValue = BigDecimal.valueOf(100);
		final RuleIrAttributeCondition amountCondition = createRuleIrAttributeCondition("total",
				RuleIrAttributeOperator.GREATER_THAN, totalValue, CART_VARIABLE_NAME);

		final RuleIrGroupCondition blueOrCartCondition = createIrGroupCondition(RuleIrGroupOperator.AND,
				Arrays.asList(colorCondition, amountCondition));

		final List<String> codeValues = Arrays.asList("123", "456", "789");
		final RuleIrAttributeCondition code = createRuleIrAttributeCondition("productCode", RuleIrAttributeOperator.IN, codeValues,
				ORDER_ENTRY_VARIABLE_NAME);

		final RuleIrGroupCondition group = createIrGroupCondition(RuleIrGroupOperator.OR,
				Arrays.asList(code, blueOrCartCondition, colorConditionForProduct2));

		ruleIrVariables.put(ORDER_ENTRY_VARIABLE_NAME, orderEntryVariable);
		ruleIrVariables.put(ORDER_ENTRY_VARIABLE_NAME + "_2", orderEntryVariable2);
		ruleIrVariables.put(CART_VARIABLE_NAME, cartVariable);
		ruleIrVariables.put(RESULT_VARIABLE_NAME, resultVariable);
		ruleIr.setConditions(Arrays.asList(ruleIrResultCondition, group));

		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(colorCondition))).thenReturn("\"blue\"");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(colorConditionForProduct2))).thenReturn("\"red\"");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(amountCondition))).thenReturn("new java.math.BigDecimal(\"100\")");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(code)))
				.thenReturn("productCode == \"123\" || productCode == \"456\" || productCode == \"789\"");

		Mockito.lenient().when(configuration.getBoolean("droolsruleengineservices.generate.unique.fact.matching.condition", true)).thenReturn(false);
		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateConditions(droolsRuleGeneratorContext, INDENTATION);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	@Test
	public void testSinglePatternWhenCondition() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsForPatternWhenCondition.bin");
		final List<RuleIrCondition> patternConditions = createPatternConditions();
		final List<RuleIrCondition> conditions = ruleIr.getConditions();
		if (CollectionUtils.isNotEmpty(conditions))
		{
			conditions.addAll(patternConditions);
		}
		else
		{
			ruleIr.setConditions(patternConditions);
		}

		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateRequiredFactsCheckPattern(droolsRuleGeneratorContext);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	@Test
	public void testPatternAndGroupWhenConditions() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsForPatternAndGroupWhenConditions.bin");
		final List<RuleIrCondition> patternConditions = createPatternConditions();
		final List<RuleIrCondition> groupConditions = createGroupConditions();
		final List<RuleIrCondition> conditions = ruleIr.getConditions();
		if (CollectionUtils.isNotEmpty(conditions))
		{
			conditions.addAll(patternConditions);
		}
		else
		{
			ruleIr.setConditions(patternConditions);
		}
		ruleIr.getConditions().addAll(groupConditions);

		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateRequiredFactsCheckPattern(droolsRuleGeneratorContext);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	@Test
	public void testGroupWhenConditions() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsForGroupWhenConditions.bin");
		final List<RuleIrCondition> conditions = ruleIr.getConditions();
		final List<RuleIrCondition> groupConditions = createGroupConditions();
		if (CollectionUtils.isNotEmpty(conditions))
		{
			conditions.addAll(groupConditions);
		}
		else
		{
			ruleIr.setConditions(groupConditions);
		}

		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateRequiredFactsCheckPattern(droolsRuleGeneratorContext);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	@Test
	public void testNotWhenConditions() throws IOException
	{
		// given
		final String expectedDroolsCode = getResourceAsString(
				"/droolsruleengineservices/test/compiler/generatedConditionsForGroupNotWhenConditions.bin");

		final List<RuleIrCondition> conditions = ruleIr.getConditions();
		final List<RuleIrCondition> groupConditions = createGroupNotConditions();
		if (CollectionUtils.isNotEmpty(conditions))
		{
			conditions.addAll(groupConditions);
		}
		else
		{
			ruleIr.setConditions(groupConditions);
		}

		// Mockito.lenient().when
		final String generatedDroolsCode = conditionsGenerator.generateRequiredFactsCheckPattern(droolsRuleGeneratorContext);

		// then
		assertEquals(expectedDroolsCode, generatedDroolsCode);
	}

	protected List<RuleIrCondition> createPatternConditions()
	{
		final String websiteGrpName = "electronicsPromoGroup";

		final RuleIrTypeCondition typeCondition1 = new RuleIrTypeCondition();
		typeCondition1.setVariable(CART_VARIABLE_NAME);
		final RuleIrTypeCondition typeCondition2 = new RuleIrTypeCondition();
		typeCondition2.setVariable(RESULT_VARIABLE_NAME);
		final RuleIrAttributeCondition attributeCondition = createRuleIrAttributeCondition("id",
				RuleIrAttributeOperator.EQUAL, websiteGrpName, WEBSITE_VARIABLE_NAME);

		ruleIrVariables.put(CART_VARIABLE_NAME, cartVariable);
		ruleIrVariables.put(RESULT_VARIABLE_NAME, resultVariable);
		ruleIrVariables.put(WEBSITE_VARIABLE_NAME, websiteGrpVariable);


		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(attributeCondition)))
				.thenReturn("\"electronicsPromoGroup\"");

		return Lists.newArrayList(typeCondition1, typeCondition2, attributeCondition);
	}

	private List<RuleIrCondition> createGroupConditions()
	{
		final RuleIrGroupCondition groupCondition1 = createIrGroupCondition(RuleIrGroupOperator.AND);
		final RuleIrGroupCondition groupCondition11 = createIrGroupCondition(RuleIrGroupOperator.AND);
		groupCondition1.setChildren(Collections.singletonList(groupCondition11));

		final RuleIrAttributeCondition attributeCondition111 = createRuleIrAttributeCondition("productCode",
				RuleIrAttributeOperator.EQUAL, "1234567", "orderEntry");

		final RuleIrAttributeCondition attributeCondition113 = createRuleIrAttributeCondition("quantity",
				RuleIrAttributeOperator.GREATER_THAN_OR_EQUAL, Integer.valueOf(1), "orderEntry");
		final RuleIrAttributeRelCondition attributeRelCondition114 = createRuleIrAttributeRelCondition("entries",
				RuleIrAttributeOperator.CONTAINS, "cart", "orderEntry");

		groupCondition11.setChildren(
				Lists.newArrayList(attributeCondition111, attributeCondition113, attributeRelCondition114));

		final RuleIrGroupCondition groupCondition21 = createIrGroupCondition(RuleIrGroupOperator.AND);
		final RuleIrGroupCondition groupCondition2 = createIrGroupCondition(RuleIrGroupOperator.AND, Collections.singletonList(groupCondition21));

		final RuleIrAttributeCondition attributeCondition211 = createRuleIrAttributeCondition("categoryCodes",
				RuleIrAttributeOperator.EQUAL, "902", "orderEntry");

		final RuleIrAttributeCondition attributeCondition213 = createRuleIrAttributeCondition("quantity",
				RuleIrAttributeOperator.GREATER_THAN_OR_EQUAL, Integer.valueOf(1), "orderEntry");
		final RuleIrAttributeRelCondition attributeRelCondition214 = createRuleIrAttributeRelCondition("entries",
				RuleIrAttributeOperator.CONTAINS, "cart", "orderEntry");

		groupCondition21.setChildren(
				Lists.newArrayList(attributeCondition211, attributeCondition213,
						attributeRelCondition214));

		final RuleIrGroupCondition groupCondition31 = createIrGroupCondition(RuleIrGroupOperator.AND);
		final RuleIrGroupCondition groupCondition3 = createIrGroupCondition(RuleIrGroupOperator.AND, Collections.singletonList(groupCondition31));

		ruleIrVariables.put(CART_VARIABLE_NAME, cartVariable);
		ruleIrVariables.put(ORDER_ENTRY_VARIABLE_NAME, orderEntryVariable);

		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(attributeCondition111)))
				.thenReturn("\"1234567\"");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(attributeCondition211)))
				.thenReturn("\"902\"");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(attributeCondition113)))
				.thenReturn("new Integer(1)");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(attributeCondition213)))
				.thenReturn("new Integer(1)");

		return Lists.newArrayList(groupCondition1, groupCondition2, groupCondition3);
	}

	private List<RuleIrCondition> createGroupNotConditions()
	{
		final RuleIrGroupCondition groupCondition1 = createIrGroupCondition(RuleIrGroupOperator.AND);

		final RuleIrGroupCondition groupCondition111 = createIrGroupCondition(RuleIrGroupOperator.AND);
		final RuleIrGroupCondition groupCondition11 = createIrGroupCondition(RuleIrGroupOperator.OR, Collections.singletonList(groupCondition111));

		final RuleIrAttributeCondition attributeCondition111 = createRuleIrAttributeCondition("currencyIsoCode",
				RuleIrAttributeOperator.EQUAL, "USD", "cart");
		final RuleIrAttributeCondition attributeCondition112 = createRuleIrAttributeCondition("total",
				RuleIrAttributeOperator.GREATER_THAN_OR_EQUAL, new BigDecimal("100"), "cart");

		groupCondition111.setChildren(Lists.newArrayList(attributeCondition111, attributeCondition112));

		final RuleIrLocalVariablesContainer localVariablesContainer12 = new RuleIrLocalVariablesContainer();
		localVariablesContainer12.setVariables(ImmutableMap.of("action", actionVariable));

		final RuleIrNotCondition notCondition12 = new RuleIrNotCondition();
		notCondition12.setVariablesContainer(localVariablesContainer12);

		final RuleIrAttributeCondition attributeCondition121 = createRuleIrAttributeCondition("firedRuleCode",
				RuleIrAttributeOperator.EQUAL, "test_rule_code", "action");
		notCondition12.setChildren(Collections.singletonList(attributeCondition121));

		groupCondition1.setChildren(Lists.newArrayList(groupCondition11, notCondition12));

		ruleIrVariables.put(CART_VARIABLE_NAME, cartVariable);
		ruleIrVariables.put(ACTION_VARIABLE_NAME, actionVariable);

		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(attributeCondition111)))
				.thenReturn("\"USD\"");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(attributeCondition112)))
				.thenReturn("new BigDecimal(\"100\")");
		Mockito.lenient().when(droolsRuleValueFormatter.formatValue(any(), eq(attributeCondition121)))
				.thenReturn("\"test_rule_code\"");

		return Lists.newArrayList(groupCondition1);
	}

	private RuleIrGroupCondition createIrGroupCondition(final RuleIrGroupOperator operator)
	{
		return createIrGroupCondition(operator, null);
	}

	private RuleIrGroupCondition createIrGroupCondition(final RuleIrGroupOperator operator, final List<RuleIrCondition> children)
	{
		final RuleIrGroupCondition groupCondition = new RuleIrGroupCondition();
		groupCondition.setOperator(operator);
		groupCondition.setChildren(children);
		return groupCondition;
	}

	private RuleIrAttributeRelCondition createRuleIrAttributeRelCondition(final String attribute,
			final RuleIrAttributeOperator operator, final String variable, final String targetVariable)
	{
		final RuleIrAttributeRelCondition condition = new RuleIrAttributeRelCondition();
		condition.setTargetVariable(targetVariable);
		condition.setAttribute(attribute);
		condition.setVariable(variable);
		condition.setOperator(operator);
		return condition;
	}

	private RuleIrAttributeCondition createRuleIrAttributeCondition(final String attribute, final RuleIrAttributeOperator operator,
			final Object value, final String variable)
	{
		final RuleIrAttributeCondition condition = new RuleIrAttributeCondition();
		condition.setAttribute(attribute);
		condition.setOperator(operator);
		condition.setValue(value);
		condition.setVariable(variable);
		return condition;
	}
}
