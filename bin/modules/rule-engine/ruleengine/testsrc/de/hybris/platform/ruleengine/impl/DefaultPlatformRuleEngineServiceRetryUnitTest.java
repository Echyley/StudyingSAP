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
package de.hybris.platform.ruleengine.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.exception.DroolsInitializationException;
import de.hybris.platform.ruleengine.init.ConcurrentMapFactory;
import de.hybris.platform.ruleengine.init.MultiFlag;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.LinkedList;
import java.util.function.Supplier;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformRuleEngineServiceRetryUnitTest
{

	@Rule
	public ExpectedException thrownException = ExpectedException.none();

	@InjectMocks
	private ConcurrentMapFactory concurrentMapFactory;

	@InjectMocks
	private DefaultPlatformRuleEngineService service;

	@Mock
	private RuleEngineActionResult result;

	@InjectMocks
	private FixedBackOffPolicy backOffPolicy;

	@InjectMocks
	private RetryTemplate retryTemplate;

	@Mock
	private MultiFlag initialisationMultiFlag;

	@Mock
	private ModelService modelService;

	@Mock
	private EventService eventService;

	@Mock
	private DefaultRuleEngineKieModuleSwapper ruleEngineKieModuleSwapper;

	@Before
	public void setUp()
	{
		final RetryPolicy retryPolicy = new SimpleRetryPolicy(3, ImmutableMap.of(RuntimeException.class, true));
		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setBackOffPolicy(backOffPolicy);

		backOffPolicy.setBackOffPeriod(2000l);

		service.setRuleEnginePublishRetryTemplate(retryTemplate);
		service.setConcurrentMapFactory(concurrentMapFactory);
	}


	@Test
	public void testswitchKieModulePublishFailWithRetryAfterRetry()
	{
		final DroolsKIEModuleModel module = mock(DroolsKIEModuleModel.class);
		Mockito.lenient().when(module.getName()).thenReturn("myModuleName");
		final KieContainerListener listener = mock(KieContainerListener.class);
		Mockito.lenient().when(initialisationMultiFlag.compareAndSet("myModuleName", false, true)).thenReturn(false);
		final LinkedList<Supplier<Object>> postTaskList = Lists.newLinkedList();

		thrownException.expect(DroolsInitializationException.class);
		thrownException.expectMessage(
				"Kie container swapping is in progress, no rules updates are possible at this time");
		service.switchKieModule(module, listener, true, false, result, postTaskList);


		verify(initialisationMultiFlag, times(3)).compareAndSet("myModuleName", false, true);
	}


	@Test
	public void testswitchKieModulePublishWithRetryOk()
	{
		final DroolsKIEModuleModel module = mock(DroolsKIEModuleModel.class);
		Mockito.lenient().when(module.getName()).thenReturn("myModuleName");
		final KieContainerListener listener = mock(KieContainerListener.class);
		Mockito.lenient().when(initialisationMultiFlag.compareAndSet("myModuleName", false, true)).thenReturn(true);
		final LinkedList<Supplier<Object>> postTaskList = Lists.newLinkedList();
		service.switchKieModule(module,listener,true,false,result,postTaskList);

		verify(initialisationMultiFlag, times(1)).compareAndSet("myModuleName", false, true);
	}

}
