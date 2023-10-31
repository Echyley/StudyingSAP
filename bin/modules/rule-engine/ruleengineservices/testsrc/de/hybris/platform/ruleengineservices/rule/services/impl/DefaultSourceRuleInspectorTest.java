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
package de.hybris.platform.ruleengineservices.rule.services.impl;


import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import org.mockito.Mockito;
import static org.mockito.Matchers.eq;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleActionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionData;
import de.hybris.platform.ruleengineservices.rule.data.RuleConditionDefinitionData;
import de.hybris.platform.ruleengineservices.rule.services.RuleActionsRegistry;
import de.hybris.platform.ruleengineservices.rule.services.RuleConditionsRegistry;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleActionsConverter;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleConditionsConverter;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultSourceRuleInspectorTest
{
	public static final String DEFINITION_ID = "myDefinition";
	@InjectMocks
	private DefaultSourceRuleInspector ruleConditionInspector;
	@Mock
	private RuleConditionsConverter ruleConditionsConverter;
	@Mock
	private RuleActionsConverter ruleActionsConverter;
	@Mock
	private RuleActionsRegistry ruleActionsRegistry;
	@Mock
	private RuleConditionsRegistry ruleConditionsRegistry;
	@Mock
	private SourceRuleModel sourceRule;
	private Map<String, RuleConditionDefinitionData> conditionsMap;
	private Map<String, RuleActionDefinitionData> actionsMap;

	@Before
	public void setUp() throws Exception
	{
		Mockito.lenient().when(sourceRule.getActions()).thenReturn("actions string");
		Mockito.lenient().when(sourceRule.getConditions()).thenReturn("conditions string");

		Mockito.lenient().when(ruleActionsRegistry.getActionDefinitionsForRuleTypeAsMap(sourceRule.getClass())).thenReturn(actionsMap);
		Mockito.lenient().when(ruleConditionsRegistry.getConditionDefinitionsForRuleTypeAsMap(sourceRule.getClass())).thenReturn(conditionsMap);

	}

	@Test
	public void shouldBeTrueIfActionDefinitionIsAssociatedWithSourceRule() throws Exception
	{
		//Mockito.lenient().when
		final List<RuleActionData> ruleActions = newArrayList(createRuleAction(DEFINITION_ID));
		Mockito.lenient().when(ruleActionsConverter.fromString(eq(sourceRule.getActions()), eq(actionsMap))).thenReturn(ruleActions);
		//when
		final boolean hasRuleAction = ruleConditionInspector.hasRuleAction(sourceRule, DEFINITION_ID);
		//then
		assertThat(hasRuleAction).isTrue();
	}

	@Test
	public void shouldBeFalseIfActionDefinitionIsNotAssociatedWithSourceRule() throws Exception
	{
		//Mockito.lenient().when
		final List<RuleActionData> ruleActions = newArrayList(createRuleAction(DEFINITION_ID));
		Mockito.lenient().when(ruleActionsConverter.fromString(sourceRule.getConditions(), actionsMap)).thenReturn(ruleActions);
		//when
		final boolean hasRuleAction = ruleConditionInspector.hasRuleAction(sourceRule, "random action definition name");
		//then
		assertThat(hasRuleAction).isFalse();
	}

	protected RuleActionData createRuleAction(final String actionId)
	{
		final RuleActionData actionData = new RuleActionData();
		actionData.setDefinitionId(actionId);
		return actionData;
	}

	@Test
	public void shouldBeTrueIfConditionDefinitionIsAssociatedWithSourceRule() throws Exception
	{
		//Mockito.lenient().when
		final List<RuleConditionData> ruleCondition = newArrayList(createRuleCondition(DEFINITION_ID));
		Mockito.lenient().when(ruleConditionsConverter.fromString(eq(sourceRule.getConditions()), eq(conditionsMap))).thenReturn(ruleCondition);
		//when
		final boolean hasRuleCondition = ruleConditionInspector.hasRuleCondition(sourceRule, DEFINITION_ID);
		//then
		assertThat(hasRuleCondition).isTrue();
	}

	@Test
	public void shouldBeFalseIfConditionDefinitionIsNotAssociatedWithSourceRule() throws Exception
	{
		//Mockito.lenient().when
		final List<RuleConditionData> ruleCondition = newArrayList(createRuleCondition(DEFINITION_ID));
		Mockito.lenient().when(ruleConditionsConverter.fromString(eq(sourceRule.getConditions()), eq(conditionsMap))).thenReturn(ruleCondition);
		//when
		final boolean hasRuleCondition = ruleConditionInspector.hasRuleCondition(sourceRule, "random condition definition name");
		//then
		assertThat(hasRuleCondition).isFalse();
	}


	@Test
	public void shouldBeTrueIfConditionDefinitionIsAssociatedInTheConditionsTreeWithSourceRule() throws Exception
	{
		//Mockito.lenient().when
		final List<RuleConditionData> ruleCondition = newArrayList(
				createRuleCondition("y_group", createRuleCondition("non_matching_definition"), createRuleCondition(DEFINITION_ID)));
		Mockito.lenient().when(ruleConditionsConverter.fromString(eq(sourceRule.getConditions()), eq(conditionsMap))).thenReturn(ruleCondition);
		//when
		final boolean hasRuleCondition = ruleConditionInspector.hasRuleCondition(sourceRule, DEFINITION_ID);
		//then
		assertThat(hasRuleCondition).isTrue();
	}

	protected RuleConditionData createRuleCondition(final String conditionId,final RuleConditionData... children)
	{
		final RuleConditionData ruleCondition = new RuleConditionData();
		ruleCondition.setDefinitionId(conditionId);
		ruleCondition.setChildren(asList(children));
		return ruleCondition;
	}
}
