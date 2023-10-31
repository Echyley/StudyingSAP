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
package de.hybris.platform.ruleengineservices.definitions.conditions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleConditionsTranslator;
import de.hybris.platform.ruleengineservices.compiler.RuleIrCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrFalseCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupCondition;
import de.hybris.platform.ruleengineservices.compiler.RuleIrGroupOperator;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleParameterData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class RuleGroupConditionTranslatorTest
{
	public static final String OPERATOR_PARAM = "test";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private RuleCompilerContext context;

	@Mock
	private RuleConditionsTranslator ruleConditionsTranslator;

	private RuleConditionData condition;
	private RuleConditionDefinitionData conditionDefinition;

	private RuleGroupConditionTranslator ruleGroupConditionTranslator;

	@Before
	public void setUp()
	{

		condition = new RuleConditionData();
		condition.setParameters(Collections.emptyMap());

		conditionDefinition = new RuleConditionDefinitionData();
		conditionDefinition.setTranslatorParameters(Collections.emptyMap());

		ruleGroupConditionTranslator = new RuleGroupConditionTranslator();
		ruleGroupConditionTranslator.setRuleConditionsTranslator(ruleConditionsTranslator);
	}

	@Test
	public void translateWithDefaultOperator() throws Exception
	{
		// Mockito.lenient().when
		final RuleIrCondition irCondition = ruleGroupConditionTranslator.translate(context, condition, conditionDefinition);

		// then
		assertNotNull(irCondition);
		assertTrue(irCondition instanceof RuleIrGroupCondition);
		assertEquals(RuleIrGroupOperator.AND, ((RuleIrGroupCondition) irCondition).getOperator());
	}

	@Test
	public void translateWithOperator() throws Exception
	{
		// given
		final RuleParameterData operatorParameter = new RuleParameterData();
		operatorParameter.setValue(RuleGroupOperator.OR);

		condition.setParameters(Collections.singletonMap(RuleGroupConditionTranslator.OPERATOR_PARAM, operatorParameter));

		// Mockito.lenient().when
		final RuleIrCondition irCondition = ruleGroupConditionTranslator.translate(context, condition, conditionDefinition);

		// then
		assertNotNull(irCondition);
		assertTrue(irCondition instanceof RuleIrGroupCondition);
		assertEquals(RuleIrGroupOperator.OR, ((RuleIrGroupCondition) irCondition).getOperator());
	}

	@Test
	public void translateWithChildren() throws Exception
	{
		// given
		final List<RuleConditionData> childrenConditions = Arrays.asList(condition);
		final List<RuleIrCondition> childrenIrConditions = Arrays.asList(new RuleIrFalseCondition());

		condition.setChildren(childrenConditions);

		Mockito.lenient().when(ruleConditionsTranslator.translate(context, childrenConditions)).thenReturn(childrenIrConditions);

		// Mockito.lenient().when
		final RuleIrCondition irCondition = ruleGroupConditionTranslator.translate(context, condition, conditionDefinition);

		// then
		assertNotNull(irCondition);
		assertTrue(irCondition instanceof RuleIrGroupCondition);
		assertSame(childrenIrConditions, ((RuleIrGroupCondition) irCondition).getChildren());
	}
}
