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
package de.hybris.platform.ruleengineservices.maintenance.tasks.impl;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.ExecutionContext;
import de.hybris.platform.ruleengine.RuleEngineActionResult;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.versioning.impl.DroolsKieModuleVersionResolver;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Sets;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ProvideModifiedRuleCodesPostTaskUnitTest
{
	private static final String MODULE_NAME = "MODULE_NAME";
	private static final String OLD_DEPLOYED_MVN_VERSION = "0.0.5";
	private static final String DEPLOYED_MVN_VERSION = "0.0.10";

	@InjectMocks
	private ProvideModifiedRuleCodesPostTask postTask;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
	private RuleEngineActionResult ruleEngineActionResult;

	@Before
	public void setUp()
	{
		final ExecutionContext executionContext = new ExecutionContext();
		Mockito.lenient().when(ruleEngineActionResult.getExecutionContext()).thenReturn(executionContext);

		postTask.setModuleVersionResolver(new DroolsKieModuleVersionResolver());
	}

	@Test
	public void testExecuteNoDeployedRules()
	{
		initializeRuleEngineActionResult(DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
		assertThat(ruleEngineActionResult.getExecutionContext().getModifiedRuleCodes()).isEmpty();
	}

	@Test
	public void testExecuteOldVersionIsNone()
	{
		initializeRuleEngineActionResult("NONE", DEPLOYED_MVN_VERSION);
		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
		verify(engineRuleDao, times(1)).getRulesForVersion(eq(MODULE_NAME), anyLong());
		verify(engineRuleDao, times(0)).getRulesBetweenVersions(eq(MODULE_NAME), anyLong(), anyLong());
	}

	@Test
	public void testExecuteNewVersionIsNone()
	{
		initializeRuleEngineActionResult(DEPLOYED_MVN_VERSION, "NONE");
		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
		verify(engineRuleDao, times(0)).getRulesForVersion(eq(MODULE_NAME), anyLong());
		verify(engineRuleDao, times(0)).getRulesBetweenVersions(eq(MODULE_NAME), anyLong(), anyLong());
	}

	@Test
	public void testExecuteOldAndNewVersionsOk()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
		verify(engineRuleDao, times(1)).getRulesBetweenVersions(eq(MODULE_NAME), anyLong(), anyLong());
	}

	@Test
	public void testExecutePublishAlreadyPublished()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final List<AbstractRuleEngineRuleModel> oldEngineRules = rulesForVersion("ruleCode", 5, 0, Boolean.TRUE);
		Mockito.lenient().when(engineRuleDao.getRulesForVersion(MODULE_NAME, 5)).thenReturn(oldEngineRules);
		final List<AbstractRuleEngineRuleModel> newEngineRules = rulesForVersion("ruleCode", 5, 5, Boolean.TRUE);
		Mockito.lenient().when(engineRuleDao.getRulesBetweenVersions(MODULE_NAME, 5, 10)).thenReturn(newEngineRules);

		final List<AbstractRuleModel> sourceRulesList = IntStream.range(0, 5).boxed()
				.map(i -> createSourceRule("ruleCode" + i, RuleStatus.PUBLISHED, oldEngineRules.get(i), newEngineRules.get(i)))
				.collect(toList());

		IntStream.range(0, 5).boxed().forEach(i -> Mockito.lenient().when(engineRuleDao.getRuleByCodeAndMaxVersion("ruleCode" + i, MODULE_NAME, 10L))
				.thenReturn(newEngineRules.get(i)));

		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();

		assertThat(ruleEngineActionResult.getExecutionContext().getModifiedRuleCodes()).containsExactlyInAnyOrder(sourceRulesList
				.stream().map(AbstractRuleModel::getCode).collect(toList()).toArray(new String[sourceRulesList.size()]));
	}

	@Test
	public void testExecuteInactiveToPublished()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final List<AbstractRuleEngineRuleModel> oldEngineRules = rulesForVersion("ruleCode", 5, 0, Boolean.FALSE);
		Mockito.lenient().when(engineRuleDao.getRulesForVersion(MODULE_NAME, 5)).thenReturn(oldEngineRules);
		final List<AbstractRuleEngineRuleModel> newEngineRules = rulesForVersion("ruleCode", 5, 5, Boolean.TRUE);
		Mockito.lenient().when(engineRuleDao.getRulesBetweenVersions(MODULE_NAME, 5, 10)).thenReturn(newEngineRules);

		final List<AbstractRuleModel> sourceRulesList = IntStream.range(0, 5).boxed()
				.map(i -> createSourceRule("ruleCode" + i, RuleStatus.INACTIVE, oldEngineRules.get(i), newEngineRules.get(i)))
				.collect(toList());

		IntStream.range(0, 5).boxed().forEach(i -> Mockito.lenient().when(engineRuleDao.getRuleByCodeAndMaxVersion("ruleCode" + i, MODULE_NAME, 10L))
				.thenReturn(newEngineRules.get(i)));

		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();

		assertThat(ruleEngineActionResult.getExecutionContext().getModifiedRuleCodes()).containsExactlyInAnyOrder(sourceRulesList
				.stream().map(AbstractRuleModel::getCode).collect(toList()).toArray(new String[sourceRulesList.size()]));
	}

	@Test
	public void testExecutePublishedToInactive()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, DEPLOYED_MVN_VERSION);
		final List<AbstractRuleEngineRuleModel> oldEngineRules = rulesForVersion("ruleCode", 5, 0, Boolean.TRUE);
		Mockito.lenient().when(engineRuleDao.getRulesForVersion(MODULE_NAME, 5)).thenReturn(oldEngineRules);
		final List<AbstractRuleEngineRuleModel> newEngineRules = rulesForVersion("ruleCode", 5, 5, Boolean.FALSE);
		Mockito.lenient().when(engineRuleDao.getRulesBetweenVersions(MODULE_NAME, 5, 10)).thenReturn(newEngineRules);

		final List<AbstractRuleModel> sourceRulesList = IntStream.range(0, 5).boxed()
				.map(i -> createSourceRule("ruleCode" + i, RuleStatus.PUBLISHED, oldEngineRules.get(i), newEngineRules.get(i)))
				.collect(toList());

		IntStream.range(0, 5).boxed().forEach(i -> Mockito.lenient().when(engineRuleDao.getRuleByCodeAndMaxVersion("ruleCode" + i, MODULE_NAME, 10L))
				.thenReturn(newEngineRules.get(i)));

		final boolean success = postTask.execute(ruleEngineActionResult);
		assertThat(success).isTrue();
		sourceRulesList.forEach(r -> verify(r, times(0)).getStatus());
	}

	@Test
	public void testExecuteRuleEngineActionFailed()
	{
		initializeRuleEngineActionResult(OLD_DEPLOYED_MVN_VERSION, OLD_DEPLOYED_MVN_VERSION);
		Mockito.lenient().when(ruleEngineActionResult.isActionFailed()).thenReturn(true);

		final List<AbstractRuleEngineRuleModel> oldEngineRules = rulesForVersion("ruleCode", 5, 0, Boolean.TRUE);
		Mockito.lenient().when(engineRuleDao.getRulesForVersion(MODULE_NAME, 5)).thenReturn(oldEngineRules);

		final List<AbstractRuleModel> sourceRulesList = IntStream.range(0, 5).boxed()
				.map(i -> createSourceRule("ruleCode" + i, RuleStatus.UNPUBLISHED, oldEngineRules.get(i))).collect(toList());

		final boolean result = postTask.execute(ruleEngineActionResult);

		assertThat(result).isFalse();
		sourceRulesList.forEach(r -> verify(r, times(0)).setStatus(any()));
	}

	private void initializeRuleEngineActionResult(final String oldMvnVersion, final String newMvnVersion)
	{
		Mockito.lenient().when(ruleEngineActionResult.getModuleName()).thenReturn(MODULE_NAME);
		Mockito.lenient().when(ruleEngineActionResult.getOldVersion()).thenReturn(oldMvnVersion);
		Mockito.lenient().when(ruleEngineActionResult.getDeployedVersion()).thenReturn(newMvnVersion);
	}

	private List<AbstractRuleEngineRuleModel> rulesForVersion(final String codePrefix, final int numOfRules,
			final int startVersion, final Boolean active)
	{
		return IntStream.range(startVersion, startVersion + numOfRules).boxed()
				.map(i -> createEngineRule(codePrefix + (i - startVersion), active, Long.valueOf(i))).collect(toList());
	}

	private AbstractRuleEngineRuleModel createEngineRule(final String code, final Boolean active, final Long version)
	{
		final DroolsKIEBaseModel kieBase = mock(DroolsKIEBaseModel.class);
		final DroolsKIEModuleModel kieModule = mock(DroolsKIEModuleModel.class);
		Mockito.lenient().when(kieModule.getName()).thenReturn(MODULE_NAME);
		Mockito.lenient().when(kieModule.getVersion()).thenReturn(10L);
		Mockito.lenient().when(kieBase.getKieModule()).thenReturn(kieModule);

		final DroolsRuleModel engineRule = mock(DroolsRuleModel.class);
		Mockito.lenient().when(engineRule.getActive()).thenReturn(active);
		Mockito.lenient().when(engineRule.getCode()).thenReturn(code);
		Mockito.lenient().when(engineRule.getVersion()).thenReturn(version);
		Mockito.lenient().when(engineRule.getKieBase()).thenReturn(kieBase);
		return engineRule;
	}

	private AbstractRuleModel createSourceRule(final String code, final RuleStatus ruleStatus,
			final AbstractRuleEngineRuleModel... engineRules)
	{
		final AbstractRuleModel rule = mock(AbstractRuleModel.class);
		if (ArrayUtils.isNotEmpty(engineRules))
		{
			final Set<AbstractRuleEngineRuleModel> engineRuleSet = Sets.newHashSet(engineRules);
			Mockito.lenient().when(rule.getEngineRules()).thenReturn(engineRuleSet);
			engineRuleSet.forEach(r -> Mockito.lenient().when(r.getSourceRule()).thenReturn(rule));
		}
		Mockito.lenient().when(rule.getCode()).thenReturn(code);
		Mockito.lenient().when(rule.getStatus()).thenReturn(ruleStatus);
		//Mockito.lenient().when(ruleService.getRuleForCode(code)).thenReturn(rule);
		return rule;
	}
}
