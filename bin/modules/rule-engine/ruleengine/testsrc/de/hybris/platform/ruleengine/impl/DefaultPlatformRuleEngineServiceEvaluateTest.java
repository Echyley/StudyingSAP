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

import com.google.common.collect.ImmutableMap;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEvaluationContext;
import de.hybris.platform.ruleengine.RuleEvaluationResult;
import de.hybris.platform.ruleengine.cache.KIEModuleCacheBuilder;
import de.hybris.platform.ruleengine.cache.RuleEngineCacheService;
import de.hybris.platform.ruleengine.concurrency.SuspendResumeTaskManager;
import de.hybris.platform.ruleengine.dao.DroolsKIEModuleMediaDao;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.drools.impl.DefaultKieModuleService;
import de.hybris.platform.ruleengine.drools.impl.DefaultKieSessionHelper;
import de.hybris.platform.ruleengine.enums.DroolsSessionType;
import de.hybris.platform.ruleengine.init.ConcurrentMapFactory;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineBootstrap;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineContainerRegistry;
import de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper;
import de.hybris.platform.ruleengine.init.tasks.PostRulesModuleSwappingTasksProvider;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleMediaModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIESessionModel;
import de.hybris.platform.ruleengine.model.DroolsRuleEngineContextModel;
import de.hybris.platform.ruleengine.strategies.DroolsKIEBaseFinderStrategy;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.configuration.Configuration;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieContainerSessionsPool;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.collect.Lists.newLinkedList;
import static de.hybris.platform.ruleengine.constants.RuleEngineConstants.RULE_ENGINE_ACTIVE;
import static de.hybris.platform.ruleengine.impl.DefaultPlatformRuleEngineService.SWAPPING_IS_BLOCKING;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_CONCURRENCY_LEVEL;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_INITIAL_CAPACITY;
import static de.hybris.platform.ruleengine.init.ConcurrentMapFactory.WORKER_MAP_LOAD_FACTOR;
import static de.hybris.platform.ruleengine.init.impl.DefaultRuleEngineKieModuleSwapper.WORKER_PRE_DESTROY_TIMEOUT;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.nullable;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultPlatformRuleEngineServiceEvaluateTest
{
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private static final String MODULE_NAME = "MODULE_NAME";
	private static final String MODULE_MVN_VERSION = "MODULE_MVN_VERSION";
	private static final String KIE_SESSION_NAME = "KIE_SESSION_NAME";

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
	private KieServices kieServices;
	@InjectMocks
	private DefaultPlatformRuleEngineService service;
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
	private ReleaseId releaseId;
	@Mock
	private ReleaseId newReleaseId;
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
	private KieModule oldKieModule;
	@Mock
	private KieContainerImpl oldKieContainer;
	@Mock
	private KieContainerImpl newKieContainer;
	@Mock
	private KieContainerSessionsPool kieContainerSessionsPool;
	@Mock
	private RuleEvaluationContext ruleEvaluationContext;
	@Mock
	private DroolsRuleEngineContextModel ruleEngineContext;
	@Mock
	private DroolsKIESessionModel kieSessionModel;
	@Mock
	private DroolsKIEBaseModel kieBaseModel;
	@Mock
	private KieSession session;
	@Mock
	private Configuration configuration;
	@Mock
	private RuleEngineCacheService ruleEngineCacheService;
	@Mock
	private KIEModuleCacheBuilder cache;
	@Mock
	private MemoryKieModule newKieModule;
	@Mock
	private AgendaFilter agendaFilter;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private PostRulesModuleSwappingTasksProvider postRulesModuleSwappingTasksProvider;
	@Mock
	private SuspendResumeTaskManager suspendResumeTaskManager;
	@InjectMocks
	private DefaultRuleEngineBootstrap ruleEngineBootstrap;
	private DefaultRuleEngineContainerRegistry ruleEngineContainerRegistry;
	@InjectMocks
	private DefaultKieSessionHelper kieSessionHelper;
	@InjectMocks
	private DefaultKieModuleService kieModuleService;
	@Mock
	private DroolsKIEModuleMediaDao droolsKIEModuleMediaDao;
	@Mock
	MediaService mediaService;
	@Mock
	private Function<RuleEvaluationContext, Integer> maxRuleExecutionsFunction;

	@Mock
	private MemoryFileSystem memoryFileSystem;

	private RuleEngineActionResult actionResult;


	@Before
	public void setUp()
	{
		final RetryPolicy retryPolicy = new SimpleRetryPolicy(3, ImmutableMap.of(RuntimeException.class, true));
		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setBackOffPolicy(backOffPolicy);

		backOffPolicy.setBackOffPeriod(2000l);
		service.setRuleEnginePublishRetryTemplate(retryTemplate);
		ruleEngineContainerRegistry = new DefaultRuleEngineContainerRegistry();
		ruleEngineContainerRegistry.setConcurrentMapFactory(concurrentMapFactory);
		service.setRuleEngineContainerRegistry(ruleEngineContainerRegistry);
		service.setRuleEngineBootstrap(ruleEngineBootstrap);
		ruleEngineBootstrap.setRuleEngineKieModuleSwapper(ruleEngineKieModuleSwapper);
		ruleEngineBootstrap.setRuleEngineContainerRegistry(ruleEngineContainerRegistry);
		ruleEngineKieModuleSwapper.setConcurrentMapFactory(concurrentMapFactory);
		ruleEngineKieModuleSwapper.setRuleEngineBootstrap(ruleEngineBootstrap);
		ruleEngineKieModuleSwapper.setKieModuleService(kieModuleService);
		service.setConcurrentMapFactory(concurrentMapFactory);
		service.setRuleEngineKieModuleSwapper(ruleEngineKieModuleSwapper);
		service.setKieSessionHelper(kieSessionHelper);
		service.setMaxRuleExecutionsFunction(maxRuleExecutionsFunction);

		kieSessionHelper.setRuleEngineKieModuleSwapper(ruleEngineKieModuleSwapper);
		kieSessionHelper.setKieSessionPoolInitialCapacity(100);

		Mockito.lenient().when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.lenient().when(configuration.getLong(eq(WORKER_PRE_DESTROY_TIMEOUT), anyLong())).thenReturn(5000L);
		Mockito.lenient().when(configuration.getInt(eq(WORKER_MAP_INITIAL_CAPACITY), anyInt())).thenReturn(3);
		Mockito.lenient().when(configuration.getFloat(eq(WORKER_MAP_LOAD_FACTOR), anyFloat())).thenReturn(0.75F);
		Mockito.lenient().when(configuration.getInt(eq(WORKER_MAP_CONCURRENCY_LEVEL), anyInt())).thenReturn(2);
		Mockito.lenient().when(configuration.getBoolean(eq(SWAPPING_IS_BLOCKING), anyBoolean())).thenReturn(false);
		Mockito.lenient().when(ruleEngineCacheService.createKIEModuleCacheBuilder(droolsModule)).thenReturn(cache);
		activateRuleEngine();

		ruleEngineContainerRegistry.setup();
		ruleEngineKieModuleSwapper.setup();
		service.setup();

		actionResult = new RuleEngineActionResult();
		//set up base configuration:
		Mockito.lenient().when(currentTenant.createAndRegisterBackgroundThread(nullable(Runnable.class), nullable(ThreadFactory.class)))
				.thenReturn(createTenantAwareThread());
		Mockito.lenient().when(kieBuilder.getResults()).thenReturn(results);
		Mockito.lenient().when(kieBuilder.getKieModule()).thenReturn(newKieModule);
		Mockito.lenient().when(kieServices.newKieModuleModel()).thenReturn(kieModuleModel);
		Mockito.lenient().when(kieServices.newKieFileSystem()).thenReturn(kieFileSystem);
		Mockito.lenient().when(kieRepository.getKieModule(nullable(ReleaseId.class))).thenReturn(oldKieModule);
		Mockito.lenient().when(kieServices.getRepository()).thenReturn(kieRepository);
		Mockito.lenient().when(kieServices.newReleaseId(nullable(String.class), nullable(String.class), eq(MODULE_MVN_VERSION + ".0"))).thenReturn(releaseId);
		Mockito.lenient().when(kieServices.newReleaseId(nullable(String.class), nullable(String.class), eq(MODULE_MVN_VERSION + ".1"))).thenReturn(newReleaseId);
		Mockito.lenient().when(kieServices.newKieBuilder(nullable(KieFileSystem.class))).thenReturn(kieBuilder);

		Mockito.lenient().when(kieServices.newKieContainer(newReleaseId)).thenReturn(newKieContainer);

		Mockito.lenient().when(newKieContainer.getContainerReleaseId()).thenReturn(newReleaseId);
		Mockito.lenient().when(newKieContainer.newKieSessionsPool(anyInt())).thenReturn(kieContainerSessionsPool);
		Mockito.lenient().when(oldKieContainer.newKieSessionsPool(anyInt())).thenReturn(kieContainerSessionsPool);
		Mockito.lenient().when(kieContainerSessionsPool.newKieSession()).thenReturn(session);
		Mockito.lenient().when(newKieContainer.getReleaseId()).thenReturn(newReleaseId);
		Mockito.lenient().when(droolsModule.getName()).thenReturn(MODULE_NAME);
		Mockito.lenient().when(rulesModuleDao.findByName(MODULE_NAME)).thenReturn(droolsModule);
		Mockito.lenient().when(droolsModule.getDeployedMvnVersion()).thenReturn(MODULE_MVN_VERSION + ".0");
		Mockito.lenient().when(newKieModule.getReleaseId()).thenReturn(newReleaseId);
		Mockito.lenient().when(releaseId.getVersion()).thenReturn(MODULE_MVN_VERSION + ".0");
		Mockito.lenient().when(releaseId.toExternalForm()).thenReturn(MODULE_MVN_VERSION + ".0");
		Mockito.lenient().when(newReleaseId.getVersion()).thenReturn(MODULE_MVN_VERSION + ".1");
		Mockito.lenient().when(newReleaseId.toExternalForm()).thenReturn(MODULE_MVN_VERSION + ".1");
		Mockito.lenient().when(kieSessionModel.getName()).thenReturn(KIE_SESSION_NAME);
		Mockito.lenient().when(ruleEvaluationContext.getRuleEngineContext()).thenReturn(ruleEngineContext);
		Mockito.lenient().when(ruleEvaluationContext.getFilter()).thenReturn(agendaFilter);
		Mockito.lenient().when(ruleEngineContext.getKieSession()).thenReturn(kieSessionModel);
		Mockito.lenient().when(kieSessionModel.getSessionType()).thenReturn(DroolsSessionType.STATEFUL);
		Mockito.lenient().when(kieSessionModel.getKieBase()).thenReturn(kieBaseModel);
		Mockito.lenient().when(kieBaseModel.getKieModule()).thenReturn(droolsModule);
		Mockito.lenient().when(newKieModule.getMemoryFileSystem()).thenReturn(memoryFileSystem);

		Mockito.lenient().when(droolsModule.getMvnVersion()).thenReturn(MODULE_MVN_VERSION);

		Mockito.lenient().when(suspendResumeTaskManager.isSystemRunning()).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(droolsKIEModuleMediaDao.findKIEModuleMedia(anyString(), anyString())).thenReturn(Optional.empty());
		Mockito.lenient().when(modelService.create(DroolsKIEModuleMediaModel.class)).thenReturn(new DroolsKIEModuleMediaModel());
		Mockito.lenient().when(maxRuleExecutionsFunction.apply(ruleEvaluationContext)).thenReturn(-1);
	}

	private void setUpOldContainer()
	{
		Mockito.lenient().when(droolsModule.getVersion()).thenReturn(0L);
		Mockito.lenient().when(droolsModule.getDeployedMvnVersion()).thenReturn(null);
		Mockito.lenient().when(kieServices.newReleaseId(anyString(), anyString(), eq(null))).thenReturn(null);
		Mockito.lenient().when(kieServices.newKieContainer(releaseId)).thenReturn(oldKieContainer);
		Mockito.lenient().when(oldKieContainer.getContainerReleaseId()).thenReturn(releaseId);
		Mockito.lenient().when(oldKieContainer.getReleaseId()).thenReturn(releaseId);

	}

	private void setUpInitializedContainer()
	{
		Mockito.lenient().when(droolsModule.getVersion()).thenReturn(0L);
		Mockito.lenient().when(droolsModule.getDeployedMvnVersion()).thenReturn(MODULE_MVN_VERSION + ".0");
		Mockito.lenient().when(kieServices.newReleaseId(anyString(), anyString(), eq(MODULE_MVN_VERSION + ".0"))).thenReturn(releaseId);
		Mockito.lenient().when(kieServices.newKieContainer(releaseId)).thenReturn(oldKieContainer);
		Mockito.lenient().when(oldKieContainer.getContainerReleaseId()).thenReturn(releaseId);
		Mockito.lenient().when(oldKieContainer.getReleaseId()).thenReturn(releaseId);
		Mockito.lenient().when(oldKieContainer.newKieSession(anyString())).thenReturn(session);
	}

	private void setUpNewContainer()
	{
		Mockito.lenient().when(kieServices.newReleaseId(anyString(), anyString(), eq(MODULE_MVN_VERSION + ".1"))).thenReturn(newReleaseId);
		Mockito.lenient().when(kieServices.newKieContainer(newReleaseId)).thenReturn(newKieContainer);
		Mockito.lenient().when(newKieContainer.getContainerReleaseId()).thenReturn(newReleaseId);
		Mockito.lenient().when(newKieContainer.getReleaseId()).thenReturn(newReleaseId);
		Mockito.lenient().when(newKieContainer.newKieSession(anyString())).thenReturn(session);
	}

	private Thread createTenantAwareThread()
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
				service.doSwapKieContainers(kieContainer, cache, result, droolsModule, null, true);
			}

			@Override
			public void onFailure(final RuleEngineActionResult result)
			{
				// Empty
			}
		}, postTasks, false, actionResult));
	}

	@Test
	public void testEvaluateNoInitialization()
	{
		setUpOldContainer();
		final RuleEvaluationResult result = service.evaluate(ruleEvaluationContext);
		assertThat(result.isEvaluationFailed()).isTrue();
		assertThat(result.getErrorMessage()).contains("Cannot complete the evaluation: rule engine was not initialized for releaseId");
	}

	@Test
	public void testEvaluateAfterInitialization()
	{
		setUpOldContainer();
		service.initialize(Collections.singletonList(droolsModule), false, false).waitForInitializationToFinish();
		setUpInitializedContainer();
		service.evaluate(ruleEvaluationContext);
		verify(oldKieContainer).newKieSessionsPool(anyInt());
	}

	@Test
	public void testEvaluateAfterSecondInitialization()
	{
		final Thread tenantAwareThread = createTenantAwareThread();
		Mockito.lenient().when(currentTenant.createAndRegisterBackgroundThread(nullable(Runnable.class), nullable(ThreadFactory.class)))
				.thenReturn(tenantAwareThread);
		setUpOldContainer();
		service.initialize(Collections.singletonList(droolsModule), false, false).waitForInitializationToFinish();
		setUpInitializedContainer();
		final Thread tenantAwareThread1 = createTenantAwareThread();
		Mockito.lenient().when(currentTenant.createAndRegisterBackgroundThread(nullable(Runnable.class), nullable(ThreadFactory.class)))
				.thenReturn(tenantAwareThread1);
		Mockito.lenient().when(droolsModule.getVersion()).thenReturn(1L);
		service.initialize(Collections.singletonList(droolsModule), false, false).waitForInitializationToFinish();
		Mockito.lenient().when(droolsModule.getDeployedMvnVersion()).thenReturn(MODULE_MVN_VERSION + ".1");
		setUpNewContainer();
		service.evaluate(ruleEvaluationContext);
		verify(newKieContainer).newKieSessionsPool(anyInt());
	}

	private void activateRuleEngine()
	{
		Mockito.lenient().when(configuration.getBoolean(RULE_ENGINE_ACTIVE)).thenReturn(true);
		Mockito.lenient().when(configuration.getBoolean(RULE_ENGINE_ACTIVE, true)).thenReturn(true);
	}

}
