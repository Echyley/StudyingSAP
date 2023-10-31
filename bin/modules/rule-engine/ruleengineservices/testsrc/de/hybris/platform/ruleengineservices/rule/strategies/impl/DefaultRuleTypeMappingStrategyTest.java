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
package de.hybris.platform.ruleengineservices.rule.strategies.impl;

import static org.junit.Assert.assertEquals;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.AbstractRuleTemplateModel;
import de.hybris.platform.ruleengineservices.rule.strategies.RuleTypeMappingException;
import de.hybris.platform.servicelayer.type.TypeService;


import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultRuleTypeMappingStrategyTest
{
	@Rule
	public final ExpectedException expectedException = ExpectedException.none();

	@Mock
	private TypeService typeService;
	@Mock
	private ComposedTypeModel composedType;

	@InjectMocks
	private final DefaultRuleTypeMappingStrategy strategy = new DefaultRuleTypeMappingStrategy();




	@Test
	public void testNullParameters() throws RuleTypeMappingException
	{
		//expect
		expectedException.expect(RuleTypeMappingException.class);

		//Mockito.lenient().when
		strategy.findRuleType(null);
	}

	@Test
	public void templateNoConvention() throws RuleTypeMappingException
	{
		//expect
		expectedException.expect(RuleTypeMappingException.class);

		//given
		Mockito.lenient().when(typeService.getComposedTypeForClass(WithoutConventionTestClass.class)).thenReturn(composedType);
		Mockito.lenient().when(composedType.getCode()).thenReturn("WithoutConventionTest");

		//Mockito.lenient().when
		strategy.findRuleType(WithoutConventionTestClass.class);
	}

	@Test
	public void sourceRuleTemplateTest() throws RuleTypeMappingException
	{
		//given
		Mockito.lenient().when(typeService.getComposedTypeForClass(ProperRuleTemplate.class)).thenReturn(composedType);
		Mockito.lenient().when(composedType.getCode()).thenReturn(ProperRuleTemplate.class.getName());
		Mockito.lenient().when(typeService.getModelClass(ProperRule.class.getName())).thenReturn((Class) ProperRule.class);

		//Mockito.lenient().when
		final Class<? extends AbstractRuleModel> result = strategy.findRuleType(ProperRuleTemplate.class);

		//then
		assertEquals(ProperRule.class.getName(), result.getName());
	}

	class WithoutConventionTestClass extends AbstractRuleTemplateModel
	{
	}

	class ProperRuleTemplate extends AbstractRuleTemplateModel
	{
	}

	class ProperRule extends AbstractRuleTemplateModel
	{
	}
}
