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

import static com.google.common.collect.Lists.newLinkedList;
import static de.hybris.platform.ruleengine.constants.RuleEngineConstants.RULE_ENGINE_ACTIVE;
import static de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService.SWAPPING_IS_BLOCKING;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_CONCURRENCY_LEVEL;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_INITIAL_CAPACITY;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_LOAD_FACTOR;
import static de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper.WORKER_PRE_DESTROY_TIMEOUT;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import com.google.common.collect.ImmutableMap;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.ruleengine.MessageLevel;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.cache.KIEModuleCacheBuilder;
import de.hybris.platform.ruleengine.cache.RuleEngineCacheService;
import de.hybris.platform.ruleengine.concurrency.SuspendResumeTaskManager;
import de.hybris.platform.ruleengine.dao.DroolsKIEModuleMediaDao;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.drools.impl.DefaultKieModuleService;
import de.hybris.platform.ruleengine.init.ConcurrentMapFactory;
import de.hybris.platform.ruleengine.init.RuleEngineBootstrap;
import de.hybris.platform.ruleengine.init.RuleEngineContainerRegistry;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper;
import de.hybris.platform.ruleengine.init.tasks.PostRulesModuleSwappingTasksProvider;
import de.hybris.platform.ruleengine.model.AbstractRulesModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleMediaModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.strategies.DroolsKIEBaseFinderStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.event.events.AbstractEvent;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.configuration.Configuration;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import static org.mockito.ArgumentMatchers.nullable;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformRuleEngineServiceInitializeTest
{
	private static final String OLD_RELEASE_ID_EXTERNAL_FORM = "OLD_RELEASE_ID_EXTERNAL_FORM";
	private static final String RELEASE_ID_EXTERNAL_FORM = "RELEASE_ID_EXTERNAL_FORM";
	private static final String MODULE_NAME = "MODULE_NAME";
	private static final String OLD_DEPLOYED_VERSION = "OLD_DEPLOYED_VERSION";
	private static final String DEPLOYED_VERSION = "DEPLOYED_VERSION";

	@Mock
	private ConfigurationService configurationService;
	@Mock
	private DroolsKIEBaseFinderStrategy droolsKIEBaseFinderStrategy;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private EventService eventService;
	@Mock
	private ModelService modelService;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private KieServices kieServices;
	@Mock
	private PostRulesModuleSwappingTasksProvider postRulesModuleSwappingTasksProvider;
	@InjectMocks
	DefaultPlatformRuleEngineService service;
	@InjectMocks
	private FixedBackOffPolicy backOffPolicy;
	@InjectMocks
	private RetryTemplate retryTemplate;

	@Mock
	private DroolsKIEModuleModel droolsModule;
	@Mock
	private RuleEngineActionResult result;
	@Mock
	private KieFileSystem kieFileSystem;
	@Mock
	private KieModuleModel kieModuleModel;
	@Mock
	private KieBaseModel baseKieSessionModel;
	@Mock
	private KieRepository kieRepository;
	@Mock
	ReleaseId releaseId;
	@Mock
	ReleaseId newReleaseId;
	@Mock
	private KieBuilder kieBuilder;
	@Mock
	private Results results;
	@InjectMocks
	private DefaultRuleEngineKieModuleSwapper ruleEngineKieModuleSwapper;
	@InjectMocks
	private ConcurrentMapFactory concurrentMapFactory;
	@Mock
	private Tenant currentTenant;
	@Mock
	private RuleEngineCacheService ruleEngineCacheService;
	@Mock
	private KIEModuleCacheBuilder cache;
	@Mock
	private MemoryFileSystem memoryFileSystem;
	@Mock
	private MemoryFileSystem origMemoryFileSystem;
	@Mock
	private SuspendResumeTaskManager suspendResumeTaskManager;

	private DroolsKIEModuleModel module;

	private final Function<AbstractRulesModuleModel, RuleEngineActionResult> serviceInitializerPropagate = m -> initializeWithBlocking(
			m, true);

	private final Function<AbstractRulesModuleModel, RuleEngineActionResult> serviceInitializerDefault = m -> initializeWithBlocking(
			m, true);

	private final Function<AbstractRulesModuleModel, RuleEngineActionResult> serviceInitializerDontPropagate = m -> initializeWithBlocking(
			m, false);

	@Mock
	private MemoryKieModule oldKieModule;
	@Mock
	private MemoryKieModule newKieModule;
	@Mock
	private KieContainer newKieContainer;
	@Mock
	private Configuration configuration;
	@Mock
	private RuleEngineBootstrap ruleEngineBootstrap;
	@Mock
	private RuleEngineContainerRegistry ruleEngineContainerRegistry;
	@InjectMocks
	private DefaultKieModuleService kieModuleService;
	@Mock
	private DroolsKIEModuleMediaDao droolsKIEModuleMediaDao;
	@Mock
	MediaService mediaService;

	private RuleEngineActionResult actionResult;


	@Before
	public void setUp()
	{

		final RetryPolicy retryPolicy = new SimpleRetryPolicy(3, ImmutableMap.of(RuntimeException.class, true));
		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setBackOffPolicy(backOffPolicy);

		backOffPolicy.setBackOffPeriod(2000l);
		service.setRuleEnginePublishRetryTemplate(retryTemplate);
		ruleEngineKieModuleSwapper.setConcurrentMapFactory(concurrentMapFactory);
		ruleEngineKieModuleSwapper.setKieModuleService(kieModuleService);
		service.setConcurrentMapFactory(concurrentMapFactory);
		service.setRuleEngineKieModuleSwapper(ruleEngineKieModuleSwapper);
		Mockito.lenient().when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.lenient().when(configuration.getLong(eq(WORKER_PRE_DESTROY_TIMEOUT), anyLong())).thenReturn(5000L);
		Mockito.lenient().when(configuration.getInt(eq(WORKER_MAP_INITIAL_CAPACITY), anyInt())).thenReturn(3);
		Mockito.lenient().when(configuration.getFloat(eq(WORKER_MAP_LOAD_FACTOR), anyFloat())).thenReturn(0.75F);
		Mockito.lenient().when(configuration.getInt(eq(WORKER_MAP_CONCURRENCY_LEVEL), anyInt())).thenReturn(2);
		Mockito.lenient().when(configuration.getBoolean(eq(SWAPPING_IS_BLOCKING), anyBoolean())).thenReturn(false);
		Mockito.lenient().when(ruleEngineCacheService.createKIEModuleCacheBuilder(droolsModule)).thenReturn(cache);

		activateRuleEngine(true);

		ruleEngineKieModuleSwapper.setup();
		service.setup();



		actionResult = new RuleEngineActionResult();
		//set up base configuration:
		Mockito.lenient().when(currentTenant.createAndRegisterBackgroundThread(nullable(Runnable.class), nullable(ThreadFactory.class)))
				.thenReturn(createTenantAwareThread(true));
		Mockito.lenient().when(kieBuilder.getResults()).thenReturn(results);
		Mockito.lenient().when(kieBuilder.getKieModule()).thenReturn(newKieModule);
		Mockito.lenient().when(kieServices.newKieModuleModel()).thenReturn(kieModuleModel);
		Mockito.lenient().when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
		Mockito.lenient().when(kieRepository.getKieModule(nullable(ReleaseId.class))).thenReturn(oldKieModule);
		Mockito.lenient().when(kieServices.getRepository()).thenReturn(kieRepository);
		Mockito.lenient().when(kieServices.newReleaseId(nullable(String.class), nullable(String.class), anyString())).thenReturn(newReleaseId);
		Mockito.lenient().when(kieServices.newKieBuilder(nullable(KieFileSystem.class))).thenReturn(kieBuilder);
		Mockito.lenient().when(kieServices.newKieContainer(newReleaseId)).thenReturn(newKieContainer);
		Mockito.lenient().when(newKieContainer.getReleaseId()).thenReturn(newReleaseId);
		Mockito.lenient().when(droolsModule.getName()).thenReturn(MODULE_NAME);
		Mockito.lenient().when(rulesModuleDao.findByName(MODULE_NAME)).thenReturn(droolsModule);
		Mockito.lenient().when(newKieModule.getReleaseId()).thenReturn(newReleaseId);
		Mockito.lenient().when(droolsModule.getDeployedMvnVersion()).thenReturn(DEPLOYED_VERSION);
		Mockito.lenient().when(releaseId.getVersion()).thenReturn(OLD_DEPLOYED_VERSION);
		Mockito.lenient().when(newReleaseId.getVersion()).thenReturn(DEPLOYED_VERSION);
		Mockito.lenient().when(releaseId.toExternalForm()).thenReturn(OLD_RELEASE_ID_EXTERNAL_FORM);
		Mockito.lenient().when(newReleaseId.toExternalForm()).thenReturn(RELEASE_ID_EXTERNAL_FORM);

		Mockito.lenient().when(newKieModule.getMemoryFileSystem()).thenReturn(memoryFileSystem);
		Mockito.lenient().when(oldKieModule.getMemoryFileSystem()).thenReturn(origMemoryFileSystem);
		Mockito.lenient().when(oldKieModule.getKieModuleModel()).thenReturn(kieModuleModel);

		Mockito.lenient().when(suspendResumeTaskManager.isSystemRunning()).thenReturn(Boolean.TRUE);

		Mockito.lenient().when(droolsKIEModuleMediaDao.findKIEModuleMedia(anyString(), anyString())).thenReturn(Optional.empty());
		Mockito.lenient().when(modelService.create(DroolsKIEModuleMediaModel.class)).thenReturn(new DroolsKIEModuleMediaModel());
	}

	private RuleEngineActionResult initializeWithBlocking(final AbstractRulesModuleModel module,
			final boolean propagareToOtherNodes)
	{
		final List<RuleEngineActionResult> results = service.initialize(singletonList(module), propagareToOtherNodes, false)
				.waitForInitializationToFinish().getResults();
		return isNotEmpty(results) ? results.get(0) : null;
	}

	private Thread createTenantAwareThread(final boolean propagateToOtherNodes)
	{
		final LinkedList<Supplier<Object>> postTasks = newLinkedList();
		postTasks.add(() -> Optional.of(releaseId).map(r -> ruleEngineKieModuleSwapper.removeKieModuleIfPresent(r, actionResult))
				.orElse(false));
		postTasks.addLast(() -> service.getInitializationMultiFlag().compareAndSet(MODULE_NAME, true, false));

		return new Thread(() -> ruleEngineKieModuleSwapper.switchKieModule(droolsModule, new KieContainerListener()
		{
			@Override
			public void onSuccess(final KieContainer kieContainer, final KIEModuleCacheBuilder cache)
			{
				service.doSwapKieContainers(kieContainer, cache, result, droolsModule, null, propagateToOtherNodes);
			}

			@Override
			public void onFailure(final RuleEngineActionResult result)
			{
				// Empty
			}
		}, postTasks, false, actionResult));
	}

	@Test
	public void testInitializeAbstractRulesModuleValidationExceptionsWithPropagation()
	{
		assertInitializeValidationExceptions(serviceInitializerPropagate);
	}

	@Test
	public void testInitializeAbstractRulesModuleValidationExceptionsWithoutPropagation()
	{
		assertInitializeValidationExceptions(serviceInitializerDontPropagate);
	}

	@Test
	public void testInitializeAbstractRulesModuleValidationExceptionsWithDefault()
	{
		assertInitializeValidationExceptions(serviceInitializerDefault);
	}

	private void assertInitializeValidationExceptions(
			final Function<AbstractRulesModuleModel, RuleEngineActionResult> serviceInitializer)
	{
		AbstractRulesModuleModel module = null;
		try
		{
			serviceInitializer.apply(module);
			fail("IllegalArgumentException expected: module must not be null");
		}
		catch (final IllegalArgumentException e)
		{
			assertThat(e.getMessage(), is("module must not be null"));
		}
		module = mock(AbstractRulesModuleModel.class);
		Mockito.lenient().when(module.getName()).thenReturn(MODULE_NAME);
		Mockito.lenient().when(module.getItemtype()).thenReturn(AbstractRulesModuleModel._TYPECODE);
		try
		{
			serviceInitializer.apply(module);
			fail("IllegalArgumentException expected: module must be DroolsKIEModule");
		}
		catch (final IllegalStateException e)
		{
			assertThat(e.getMessage(), is("module " + MODULE_NAME + " is not a DroolsKIEModule. "
					+ AbstractRulesModuleModel._TYPECODE + " is not supported."));
		}
	}

	@Test
	public void testInitializeAbstractRulesModuleModelWithPropagation() throws Exception
	{
		final Thread tenantAwareThread = createTenantAwareThread(true);
		Mockito.lenient().when(currentTenant.createAndRegisterBackgroundThread(nullable(Runnable.class), nullable(ThreadFactory.class)))
				.thenReturn(tenantAwareThread);
		assertInitializeSuccessTest(serviceInitializerPropagate);
		tenantAwareThread.join();
		final Thread tenantAwareThread1 = createTenantAwareThread(true);
		Mockito.lenient().when(currentTenant.createAndRegisterBackgroundThread(nullable(Runnable.class), nullable(ThreadFactory.class)))
				.thenReturn(tenantAwareThread1);
		assertInitializeSuccessTest(serviceInitializerDefault);
		tenantAwareThread1.join();
		final Thread tenantAwareThread2 = createTenantAwareThread(true);
		Mockito.lenient().when(currentTenant.createAndRegisterBackgroundThread(nullable(Runnable.class), nullable(ThreadFactory.class)))
				.thenReturn(tenantAwareThread2);
		assertInitializeSuccessTest(serviceInitializerDontPropagate);
	}

	private void assertInitializeSuccessTest(final Function<AbstractRulesModuleModel, RuleEngineActionResult> serviceInitializer)
	{
		setUpSuccessDroolsKIEModuleModel();
		final RuleEngineActionResult initialize = serviceInitializer.apply(module);
		assertFalse(initialize.isActionFailed());
		assertThat(initialize.getMessagesAsString(MessageLevel.INFO), is(not(nullValue())));

	}

	private DroolsKIEModuleModel setUpSuccessDroolsKIEModuleModel()
	{
		module = mock(DroolsKIEModuleModel.class);
		Mockito.lenient().when(module.getName()).thenReturn(MODULE_NAME);
		Mockito.lenient().when(module.getItemtype()).thenReturn(DroolsKIEModuleModel._TYPECODE);
		Mockito.lenient().when(module.getPk()).thenReturn(PK.parse("1234939953093"));
		return module;
	}

	@Test
	public void testInitializeAbstractRulesModuleModelWhenRuleEngineIsNotActiveWithoutPropagation()
	{
		assertInitializeWhenRuleEngineIsNotActive(serviceInitializerDontPropagate);

	}

	@Test
	public void testInitializeAbstractRulesModuleModelWhenRuleEngineIsNotActiveWithPropagation()
	{
		assertInitializeWhenRuleEngineIsNotActive(serviceInitializerPropagate);

	}

	@Test
	public void testInitializeAbstractRulesModuleModelWhenRuleEngineIsNotActiveDefaultPropagation()
	{
		assertInitializeWhenRuleEngineIsNotActive(serviceInitializerDefault);

	}

	private void assertInitializeWhenRuleEngineIsNotActive(
			final Function<AbstractRulesModuleModel, RuleEngineActionResult> serviceInitializer)
	{
		activateRuleEngine(false);
		activateRuleEngine(false);
		final RuleEngineActionResult initialize = serviceInitializer.apply(null);
		assertTrue(initialize.isActionFailed());
	}

	private void activateRuleEngine(final boolean b)
	{
		Mockito.lenient().when(configuration.getBoolean(RULE_ENGINE_ACTIVE)).thenReturn(b);
		Mockito.lenient().when(configuration.getBoolean(RULE_ENGINE_ACTIVE, true)).thenReturn(b);
	}

	@Test
	public void testInitializeAllRulesModulesFailWithAWarningMessage()
	{
		activateRuleEngine(false);

		final List<RuleEngineActionResult> initializeAllRulesModules = service.initializeAllRulesModules();

		assertThat(initializeAllRulesModules, is(not(nullValue())));
		assertEquals(1, initializeAllRulesModules.size());
		final RuleEngineActionResult raor = initializeAllRulesModules.get(0);
		assertTrue(raor.isActionFailed());
		assertThat(raor.getMessagesAsString(MessageLevel.WARNING), is(not(nullValue())));
		assertThat(raor.getModuleName(), is(nullValue()));
	}

	@Test
	public void testInitializeAllRulesModulesSucceed()
	{

		activateRuleEngine(true);

		final List<AbstractRulesModuleModel> rulesModuleList = new ArrayList<>();
		final DroolsKIEModuleModel moduleModel = setUpSuccessDroolsKIEModuleModel();
		rulesModuleList.add(moduleModel);
		Mockito.lenient().when(rulesModuleDao.findAll()).thenReturn(rulesModuleList);

		final List<RuleEngineActionResult> initializeAllRulesModules = service.initializeAllRulesModules();

		assertThat(initializeAllRulesModules, is(not(nullValue())));
		assertEquals(1, initializeAllRulesModules.size());
		final RuleEngineActionResult ruleEngineActionResult = initializeAllRulesModules.get(0);
		assertThat(ruleEngineActionResult, is(not(nullValue())));
		assertFalse(ruleEngineActionResult.isActionFailed());
		assertThat(ruleEngineActionResult.getMessagesAsString(MessageLevel.INFO), is(not(nullValue())));

		try
		{
			Thread.sleep(500);
		}
		catch (final InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}

		//each rule will now be activated in turn!
		verify(eventService, times(2)).publishEvent(any(AbstractEvent.class));
		verify(kieRepository).getKieModule(releaseId);
		verify(kieRepository).addKieModule(any(MemoryKieModule.class));
		verify(kieRepository).removeKieModule(releaseId);
		verify(kieServices).newKieContainer(newReleaseId);
	}

	@Test
	public void testInitializeAllRulesModulesWhenRuleEngineIsInactive()
	{
		activateRuleEngine(false);
		final List<RuleEngineActionResult> initializeAllRulesModules = service.initializeAllRulesModules();
		assertThat(initializeAllRulesModules, is(not(nullValue())));
		final RuleEngineActionResult ruleEngineActionResult = initializeAllRulesModules.get(0);
		assertThat(ruleEngineActionResult, is(not(nullValue())));
		assertTrue(ruleEngineActionResult.isActionFailed());
		assertThat(ruleEngineActionResult.getMessagesAsString(MessageLevel.WARNING), is(not(nullValue())));
	}



}
