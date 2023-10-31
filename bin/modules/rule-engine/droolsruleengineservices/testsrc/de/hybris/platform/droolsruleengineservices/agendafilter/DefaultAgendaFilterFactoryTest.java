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
package de.hybris.platform.droolsruleengineservices.agendafilter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.droolsruleengineservices.agendafilter.impl.DefaultAgendaFilterFactory;
import de.hybris.platform.droolsruleengineservices.agendafilter.impl.DefaultCompoundAgendaFilter;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.rule.AgendaFilter;
import org.mockito.Mock;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultAgendaFilterFactoryTest
{

	private DefaultAgendaFilterFactory factory;


	@Mock
	private DroolsRuleEngineContextModel context;

	@Mock
	private AgendaFilterCreationStrategy strategy;

	@Mock
	private AgendaFilter filter;


	@Before
	public void setUp()
	{
		factory = new DefaultAgendaFilterFactory();
		factory.setForceAllEvaluations(false);
		factory.setTargetClass(DefaultCompoundAgendaFilter.class);
		factory.setStrategies(Collections.singletonList(strategy));
		Mockito.lenient().when(strategy.createAgendaFilter(context)).thenReturn(filter);
	}

	@Test
	public void testNoStrategiesSet()
	{
		factory.setStrategies(Collections.EMPTY_LIST);
		assertNull(factory.createAgendaFilter(context));
		factory.setStrategies(null);
		assertNull(factory.createAgendaFilter(context));
	}

	@Test
	public void testCreateAgendaFilter()
	{
		final AgendaFilter resultFilter = factory.createAgendaFilter(context);
		assertNotNull(resultFilter);
		assertEquals(resultFilter.getClass(), DefaultCompoundAgendaFilter.class);
	}
}
