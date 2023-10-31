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
package de.hybris.platform.ruleengineservices.maintenance.impl;

import static com.google.common.collect.Sets.newHashSet;
import static de.hybris.platform.ruleengineservices.maintenance.impl.DefaultRuleCompilationContext.WORKER_PRE_DESTROY_TIMEOUT;
import static de.hybris.platform.ruleengineservices.maintenance.impl.DefaultRuleCompilerSpliterator.JobProvider.getJob;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.ruleengine.ExecutionContext;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.RuleEngineService;
import de.hybris.platform.ruleengine.concurrency.GuardStatus;
import de.hybris.platform.ruleengine.concurrency.GuardedSuspension;
import de.hybris.platform.ruleengine.concurrency.RuleEngineSpliteratorStrategy;
import de.hybris.platform.ruleengine.concurrency.SuspendResumeTaskManager;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.dao.RulesModuleDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.event.RuleUpdatedEvent;
import de.hybris.platform.ruleengine.exception.RuleEngineRuntimeException;
import de.hybris.platform.ruleengine.init.ConcurrentMapFactory;
import de.hybris.platform.ruleengine.init.InitializationFuture;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.strategies.impl.DefaultRulesModuleResolver;
import de.hybris.platform.ruleengine.util.EngineRulesRepository;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerProblem;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerResult;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerService;
import de.hybris.platform.ruleengineservices.compiler.impl.DefaultRuleCompilerProblem;
import de.hybris.platform.ruleengineservices.compiler.impl.DefaultRuleCompilerResult;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilationContextProvider;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerPublisherResult;
import de.hybris.platform.ruleengineservices.maintenance.RuleCompilerPublisherResult.Result;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.services.RuleService;
import de.hybris.platform.ruleengineservices.rule.strategies.RulePublishRestriction;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.configuration.Configuration;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Maps;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultRuleMaintenanceServiceTest
{
	private static final String MODULE_NAME = "MODULE_NAME";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private ModelService modelService;
	@Mock
	private RuleEngineService ruleEngineService;
	@Mock
	private RuleCompilerService ruleCompilerService;
	@Mock
	private SourceRuleModel rule;
	@Mock
	private DroolsRuleModel engineRule;
	@Mock
	private DroolsKIEBaseModel kieBase;
	@Mock
	private DroolsKIEModuleModel ruleModule;
	@Mock
	private RulesModuleDao rulesModuleDao;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private RuleService ruleService;
	@Mock
	private InitializationFuture initializationFuture;
	@Mock
	private RuleCompilationContextProvider ruleCompilationContextProvider;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;
	@Mock
	private ConcurrentMapFactory concurrentMapFactory;
	@Mock
	private RuleEngineSpliteratorStrategy ruleEngineSpliteratorStrategy;
	@Mock
	private Tenant currentTenant;
	@Mock
	private ThreadFactory threadFactory;
	@Mock
	private GuardedSuspension<String> rulesCompilationGuardedSuspension;
	@Mock
	private EventService eventService;

	@InjectMocks
	private DefaultRuleCompilationContext ruleCompilationContext;
	@InjectMocks
	private DefaultRuleMaintenanceService service;
	@InjectMocks
	private DefaultRulesModuleResolver rulesModuleResolver;
	@Mock
	private SuspendResumeTaskManager suspendResumeTaskManager;
	@Mock
	private EngineRulesRepository engineRulesRepository;
	@Mock
	private RulePublishRestriction rulePublishRestriction;

	@Captor
	private ArgumentCaptor<RuleUpdatedEvent> ruleUpdatedEventCaptor;

	@Before
	public void setUp()
	{
		Mockito.lenient().when(rule.getCode()).thenReturn("sourceRuleCode");

		Mockito.lenient().when(concurrentMapFactory.createNew()).thenReturn(Maps.newConcurrentMap()).thenReturn(Maps.newConcurrentMap());

		ruleCompilationContext.setUp();

		service.setRulesModuleResolver(rulesModuleResolver);
		final ExecutionContext executionContext = new ExecutionContext();
		final RuleEngineActionResult publisherResult = new RuleEngineActionResult();
		publisherResult.setActionFailed(false);
		publisherResult.setExecutionContext(executionContext);

		Mockito.lenient().when(ruleModule.getName()).thenReturn(MODULE_NAME);
		Mockito.lenient().when(ruleModule.getRuleType()).thenReturn(RuleType.DEFAULT);

		Mockito.lenient().when(ruleService.getEngineRuleTypeForRuleType(rule.getClass())).thenReturn(RuleType.DEFAULT);

		Mockito.lenient().when(ruleEngineService.getRuleForCodeAndModule(Matchers.anyString(), eq(MODULE_NAME))).thenReturn(engineRule);
		Mockito.lenient().when(ruleEngineService.initialize(eq(singletonList(ruleModule)), eq(true), eq(false),
				any(ExecutionContext.class))).thenReturn(initializationFuture);
		Mockito.lenient().when(initializationFuture.getResults()).thenReturn(Lists.emptyList());

		final RuleCompilerResult compilerResult = new DefaultRuleCompilerResult(rule.getCode(), Collections.emptyList());
		Mockito.lenient().when(ruleCompilerService.compile(ruleCompilationContext, rule, MODULE_NAME)).thenReturn(compilerResult);

		Mockito.lenient().when(rulesModuleDao.findByName(MODULE_NAME)).thenReturn(ruleModule);
		Mockito.lenient().when(rulesModuleDao.findActiveRulesModulesByRuleType(RuleType.DEFAULT)).thenReturn(singletonList(ruleModule));

		Mockito.lenient().when(engineRule.getRuleType()).thenReturn(RuleType.DEFAULT);
		Mockito.lenient().when(engineRule.getActive()).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(engineRule.getKieBase()).thenReturn(kieBase);
		Mockito.lenient().when(kieBase.getKieModule()).thenReturn(ruleModule);

		Mockito.lenient().when(ruleCompilationContextProvider.getRuleCompilationContext()).thenReturn(ruleCompilationContext);
		Mockito.lenient().when(ruleEngineSpliteratorStrategy.getNumberOfThreads()).thenReturn(10);

		Mockito.lenient().when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.lenient().when(configuration.getLong(WORKER_PRE_DESTROY_TIMEOUT, 3600000L)).thenReturn(100L);

		final Thread threadStub = new Thread("Thread-0");
		Mockito.lenient().when(currentTenant.createAndRegisterBackgroundThread(any(Runnable.class), eq(threadFactory))).thenReturn(threadStub);

		Mockito.lenient().when(rulesCompilationGuardedSuspension.checkPreconditions(MODULE_NAME)).thenReturn(GuardStatus.of(GuardStatus.Type.GO));
	}

	@Test
	public void compileAndPublishRulesNull()
	{
		//expect
		expectedException.expect(IllegalArgumentException.class);

		//then
		service.compileAndPublishRules(null, MODULE_NAME, false);
	}

	@Test
	public void compileAndPublishRulesEmpty()
	{
		//given
		final List<SourceRuleModel> emptyList = Collections.emptyList();

		Mockito.when(rulePublishRestriction.isAllowedToPublish(MODULE_NAME, 0)).thenReturn(true);
		final RuleCompilerPublisherResult result = service.compileAndPublishRules(emptyList, MODULE_NAME, false);

		//then
		assertEquals(Result.SUCCESS, result.getResult());
	}

	@Test
	public void compileAndPublishRuleSomeRule()
	{
		//given

		Mockito.when(rulePublishRestriction.isAllowedToPublish(eq(MODULE_NAME), anyInt())).thenReturn(true);
		final RuleCompilerPublisherResult result = service.compileAndPublishRules(singletonList(rule), MODULE_NAME, false);

		//then
		assertEquals(Result.SUCCESS, result.getResult());
	}

	@Test
	public void compileAndPublishRuleCompilerProblem() throws InterruptedException
	{
		//given
		final RuleCompilerResult failedCompilerResult = new DefaultRuleCompilerResult(rule.getCode(),
				singletonList(new DefaultRuleCompilerProblem(RuleCompilerProblem.Severity.ERROR, "test message")));
		final ArrayList<RuleCompilerResult> ruleCompilerResults = Lists.newArrayList();
		final Runnable job = getJob(ruleCompilationContext, singletonList(rule), MODULE_NAME, ruleCompilerResults);
		Mockito.lenient().when(currentTenant.createAndRegisterBackgroundThread(any(Runnable.class), eq(threadFactory))).thenReturn(new Thread(job));

		Mockito.lenient().when(ruleCompilerService.compile(ruleCompilationContext, rule, MODULE_NAME)).thenReturn(failedCompilerResult);
		Mockito.lenient().when(ruleModule.getRuleType()).thenReturn(RuleType.DEFAULT);
		Mockito.lenient().when(ruleService.getEngineRuleTypeForRuleType(rule.getClass())).thenReturn(RuleType.DEFAULT);
		Mockito.when(rulePublishRestriction.isAllowedToPublish(eq(MODULE_NAME), anyInt())).thenReturn(true);
		//Mockito.lenient().when
		service.compileAndPublishRules(singletonList(rule), MODULE_NAME, false);

		// in rare cases the non-blocking call returns before the mocked compilation is finished
		// so we wait a little bit to allow for asynchronous compilation to finish first
		Thread.sleep(1000L);

		//then
		assertThat(ruleCompilerResults).isNotEmpty().hasSize(1);
		assertThat(ruleCompilerResults.get(0).getResult()).isEqualTo(RuleCompilerResult.Result.ERROR);
	}

	@Test
	public void compileAndPublishRulePublisherProblem()
	{
		//given
		final RuleEngineActionResult failedPublisherResult = new RuleEngineActionResult();
		failedPublisherResult.setActionFailed(true);
		Mockito.lenient().when(initializationFuture.getResults()).thenReturn(singletonList(failedPublisherResult));

		Mockito.lenient().when(ruleEngineService.initializeAllRulesModules()).thenReturn(singletonList(failedPublisherResult));
		Mockito.lenient().when(initializationFuture.getResults()).thenReturn(singletonList(failedPublisherResult));

		Mockito.when(rulePublishRestriction.isAllowedToPublish(eq(MODULE_NAME), anyInt())).thenReturn(true);
		final RuleCompilerPublisherResult result = service.compileAndPublishRules(singletonList(rule), MODULE_NAME, false);

		//then
		assertEquals(Result.PUBLISHER_ERROR, result.getResult());
	}

	@Test
	public void testCompileAndPublishRulesFailsCompilationAlreadyInProgress()
	{
		Mockito.lenient().when(rulesCompilationGuardedSuspension.checkPreconditions(MODULE_NAME)).thenReturn(GuardStatus.of(GuardStatus.Type.NO_GO));
		Mockito.when(rulePublishRestriction.isAllowedToPublish(eq(MODULE_NAME), anyInt())).thenReturn(true);
		expectedException.expect(IllegalStateException.class);
		expectedException.expectMessage("The compilation of rules is currently in progress for rules module [MODULE_NAME]");
		service.compileAndPublishRules(singletonList(rule), MODULE_NAME, false);
	}

	@Test
	public void testUndeployRules()
	{
		// given
		final List<SourceRuleModel> sourceRuleModels = mockForUndeployRules();
		//Mockito.lenient().when
		final Optional<RuleCompilerPublisherResult> result = service.undeployRules(sourceRuleModels, "kieModule1");

		//then
		assertThat(result).isPresent();
		assertThat(result.get().getResult()).isEqualTo(Result.SUCCESS);
		verify(ruleEngineService).archiveRules(anyCollectionOf(DroolsRuleModel.class));

	}

	@Test
	public void testUndeployRulesNoMatchingModuleName()
	{
		// given
		final List<SourceRuleModel> sourceRuleModels = mockForUndeployRules();
		//Mockito.lenient().when
		final Optional<RuleCompilerPublisherResult> result = service.undeployRules(sourceRuleModels, "kieModule");

		//then
		assertThat(result).isNotPresent();
		verify(ruleEngineService, times(0)).archiveRules(anyCollectionOf(DroolsRuleModel.class));

	}

	@Test
	public void testSynchronizeModulesWithDifferentRuleTypes()
	{
		// given
		final DroolsKIEModuleModel kieModule1 = newKieModule("kieModule1");
		final DroolsKIEModuleModel kieModule2 = newKieModule("kieModule2");
		//Mockito.lenient().when
		Mockito.lenient().when(kieModule1.getRuleType()).thenReturn(RuleType.DEFAULT);
		Mockito.lenient().when(kieModule2.getRuleType()).thenReturn(null);
		Mockito.lenient().when(rulesModuleDao.findByName("kieModule1")).thenReturn(kieModule1);
		Mockito.lenient().when(rulesModuleDao.findByName("kieModule2")).thenReturn(kieModule2);

		//then
		assertThatThrownBy(() -> service.synchronizeModules("kieModule1", "kieModule2"))
				.isInstanceOf(RuleEngineRuntimeException.class)
				.hasMessageContaining("Cannot synchronize modules with different rule types");

	}

	private List<SourceRuleModel> mockForUndeployRules()
	{
		final SourceRuleModel sourceRule1 = mock(SourceRuleModel.class);
		final SourceRuleModel sourceRule2 = mock(SourceRuleModel.class);
		final DroolsKIEBaseModel kieBase1 = newKieBase("kieModule1");
		final DroolsKIEBaseModel kieBase2 = newKieBase("kieModule2");
		final Set<AbstractRuleEngineRuleModel> sourceRuleDroolsRules1 = newHashSet(
				newDroolsRules(kieBase1, "droolsRule1", sourceRule1),
				newDroolsRules(kieBase2, "droolsRule2", sourceRule1));
		final Set<AbstractRuleEngineRuleModel> sourceRuleDroolsRules2 = newHashSet(
				newDroolsRules(kieBase1, "droolsRule3", sourceRule2));

		Mockito.lenient().when(sourceRule1.getEngineRules()).thenReturn(sourceRuleDroolsRules1);
		Mockito.lenient().when(sourceRule2.getEngineRules()).thenReturn(sourceRuleDroolsRules2);

		Mockito.lenient().when(initializationFuture.waitForInitializationToFinish()).thenReturn(initializationFuture);
		final RuleEngineActionResult ruleEngineActionResult = new RuleEngineActionResult();
		ruleEngineActionResult.setActionFailed(false);
		Mockito.lenient().when(initializationFuture.getResults()).thenReturn(singletonList(ruleEngineActionResult));

		Mockito.lenient().when(ruleEngineService.archiveRules(anyCollectionOf(DroolsRuleModel.class))).thenReturn(Optional.of(initializationFuture));

		return Arrays.asList(sourceRule1, sourceRule2);
	}

	private DroolsRuleModel newDroolsRules(final DroolsKIEBaseModel kieModel, final String ruleCode,
			final SourceRuleModel sourceRule)
	{
		final DroolsRuleModel droolsRule = mock(DroolsRuleModel.class);
		Mockito.lenient().when(droolsRule.getKieBase()).thenReturn(kieModel);
		Mockito.lenient().when(droolsRule.getCode()).thenReturn(ruleCode);
		Mockito.lenient().when(droolsRule.getActive()).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(droolsRule.getCurrentVersion()).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(droolsRule.getVersion()).thenReturn(Long.valueOf(0));
		Mockito.lenient().when(droolsRule.getRuleType()).thenReturn(RuleType.DEFAULT);
		Mockito.lenient().when(droolsRule.getSourceRule()).thenReturn(sourceRule);

		Mockito.lenient().when(engineRulesRepository.checkEngineRuleDeployedForModule(droolsRule, kieModel.getKieModule().getName())).thenReturn(true);

		return droolsRule;
	}

	private DroolsKIEBaseModel newKieBase(final String moduleName)
	{
		final DroolsKIEBaseModel kieBase = mock(DroolsKIEBaseModel.class);
		final DroolsKIEModuleModel kieModule = newKieModule(moduleName);
		Mockito.lenient().when(kieBase.getKieModule()).thenReturn(kieModule);
		Mockito.lenient().when(kieModule.getKieBases()).thenReturn(Collections.singletonList(kieBase));
		return kieBase;
	}

	private DroolsKIEModuleModel newKieModule(final String moduleName)
	{
		final DroolsKIEModuleModel kieModule = mock(DroolsKIEModuleModel.class);
		Mockito.lenient().when(kieModule.getName()).thenReturn(moduleName);
		return kieModule;
	}

}
