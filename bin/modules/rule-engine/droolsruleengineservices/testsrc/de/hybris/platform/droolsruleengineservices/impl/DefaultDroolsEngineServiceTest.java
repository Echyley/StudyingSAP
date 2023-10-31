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
package de.hybris.platform.droolsruleengineservices.impl;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.event.RuleEngineInitializedEvent;
import de.hybris.platform.ruleengine.event.RuleEngineModuleSwapCompletedEvent;
import de.hybris.platform.ruleengine.exception.DroolsRuleLoopException;
import de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.servicelayer.event.EventService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.SingletonMap;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.runner.RunWith;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;

@IntegrationTest
public class DefaultDroolsEngineServiceTest extends AbstractRuleEngineServicesTest
{
	@Resource
	private EventService eventService;

	@Mock
	private EventService mockedEventService;

	MockitoSession mockito;


	@Before
	public void setUp() throws ImpExException
	{
		mockito = Mockito.mockitoSession()
				.initMocks(this)
				.strictness(Strictness.STRICT_STUBS)
				.startMocking();
		init();
		importCsv("/droolsruleengineservices/test/ruleenginesetup.impex", "utf-8");

		if (getPlatformRuleEngineService() instanceof DefaultPlatformRuleEngineService)
		{
			((DefaultPlatformRuleEngineService) getPlatformRuleEngineService()).setEventService(mockedEventService);
		}
	}

	@After
	public void restore()
	{
		if (getPlatformRuleEngineService() instanceof DefaultPlatformRuleEngineService)
		{
			((DefaultPlatformRuleEngineService) getPlatformRuleEngineService()).setEventService(eventService);
		}
		mockito.finishMocking();
	}

	@Test
	public void testRuleEngineInitializedEvent() throws IOException
	{
		final DroolsRuleModel rule1 = getRuleForFile("raoRule01.drl", "/droolsruleengineservices/test/rules/rao/",
				"de.hybris.platform.ruleengineservices.test",
				new SingletonMap("ruleOrderFixedDiscountAction", "ruleOrderFixedDiscountAction"));
		final DroolsKIEModuleModel testRulesModule = getTestRulesModule(Collections.singleton(rule1));
		getPlatformRuleEngineService().initialize(singletonList(testRulesModule), false, false).waitForInitializationToFinish();
		final Matcher<RuleEngineInitializedEvent> matcher = new BaseMatcher()
		{

			@Override
			public boolean matches(final Object compareTo)
			{
				if (compareTo instanceof RuleEngineModuleSwapCompletedEvent)
				{
					final RuleEngineModuleSwapCompletedEvent event = (RuleEngineModuleSwapCompletedEvent) compareTo;

					return event.getRulesModuleName().equals(testRulesModule.getName());
				}
				return false;
			}

			@Override
			public void describeTo(final Description description)
			{
				description.appendText(
						String.format("RulesModuleName of RuleEngineInitializedEvent should be '%s'", testRulesModule.getName()));

			}
		};

		Mockito.verify(mockedEventService).publishEvent(MockitoHamcrest.argThat(matcher));
	}

	@Test
	public void testRuleInitializationError() throws IOException
	{
		final DroolsRuleModel rule1 = getRuleForFile("defective-raoRule01.drl", "/droolsruleengineservices/test/rules/rao/",
				"de.hybris.platform.ruleengineservices.test", null);
		final List<RuleEngineActionResult> results = getPlatformRuleEngineService()
				.initialize(singletonList(getTestRulesModule(Collections.singleton(rule1))), false, false).waitForInitializationToFinish().getResults();
		if(CollectionUtils.isEmpty(results))
		{
			Assert.fail("rule engine initialization failed: no results found");
		}
		final RuleEngineActionResult result = results.get(0);
		Assert.assertTrue(result.isActionFailed());
	}


	@Test
	public void testSingleRuleLoopDetection() throws IOException
	{
		final DroolsRuleModel rule1 = getRuleForFile("looping-rule01.drl", "/droolsruleengineservices/test/rules/loopdetection/",
				"de.hybris.platform.ruleengineservices.test", null);
		initializeRuleEngine(rule1);
		final RuleEvaluationContext context = prepareContext(buildRAOsForCartWithCode("ABC"));
		// make sure ruleFiringLimit is set before running the test
		assertNotNull(context.getRuleEngineContext());
		assertTrue(context.getRuleEngineContext() instanceof DroolsRuleEngineContextModel);
		final DroolsRuleEngineContextModel droolsContext = (DroolsRuleEngineContextModel) context.getRuleEngineContext();
		droolsContext.getKieSession().getKieBase();
		droolsContext.setRuleFiringLimit(Long.valueOf(5L));
		getModelService().save(droolsContext);

		try
		{
			getCommerceRuleEngineService().evaluate(context);
			fail("should have thrown a DroolsRuleLoopException");
		}
		catch (final DroolsRuleLoopException e)
		{
			final List<String> allRuleFirings = e.getAllRuleFirings();
			assertEquals(5L, e.getLimit());
			assertEquals(1, allRuleFirings.size());
		}
	}

	@Test
	public void testTwoRulesLoopDetection() throws IOException
	{
		final DroolsRuleModel rule2 = getRuleForFile("looping-rule02.drl", "/droolsruleengineservices/test/rules/loopdetection/",
				"de.hybris.platform.ruleengineservices.test", null);
		final DroolsRuleModel rule3 = getRuleForFile("looping-rule03.drl", "/droolsruleengineservices/test/rules/loopdetection/",
				"de.hybris.platform.ruleengineservices.test", null);
		initializeRuleEngine(rule2, rule3);
		final RuleEvaluationContext context = prepareContext(buildRAOsForCartWithCode("ABC"));
		// make sure ruleFiringLimit is set before running the test
		assertNotNull(context.getRuleEngineContext());
		assertTrue(context.getRuleEngineContext() instanceof DroolsRuleEngineContextModel);
		final DroolsRuleEngineContextModel droolsContext = (DroolsRuleEngineContextModel) context.getRuleEngineContext();
		droolsContext.getKieSession().getKieBase();
		droolsContext.setRuleFiringLimit(Long.valueOf(2L));
		getModelService().save(droolsContext);

		try
		{
			getCommerceRuleEngineService().evaluate(context);
			fail("should have thrown a DroolsRuleLoopException");
		}
		catch (final DroolsRuleLoopException e)
		{
			final List<String> allRuleFirings = e.getAllRuleFirings();
			assertEquals(2L, e.getLimit());
			assertEquals(2, allRuleFirings.size());
		}
	}

}
