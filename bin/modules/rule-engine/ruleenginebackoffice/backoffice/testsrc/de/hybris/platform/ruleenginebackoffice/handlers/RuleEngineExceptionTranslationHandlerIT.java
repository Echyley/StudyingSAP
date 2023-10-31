/*
 * [y] hybris Platform
 *
 * Copyright (c) 2021 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.ruleenginebackoffice.handlers;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.ruleengine.dao.DroolsKIEBaseDao;
import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.model.RuleGroupModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.model.ModelService;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import java.util.*;


@IntegrationTest
public class RuleEngineExceptionTranslationHandlerIT extends ServicelayerTest
{
	@Resource
	private ModelService modelService;
	@Resource
	private DroolsKIEBaseDao droolsKIEBaseDao;

	private RuleEngineExceptionTranslationHandler handler;

	private SourceRuleModel sourceRuleModel;
	public static final String INVALID_CONDITIONS_JSON = "a1'@#";
	private DroolsRuleModel droolsRuleModel;
	private RuleGroupModel ruleGroup;
	private final static String NEW_RULE_CONTENT = "new rule content";
	private final static String RULE_CODE = "test_code";
	private final static String UUID_STRING = UUID.randomUUID().toString();

	@Before
	public void setUp() throws Exception
	{
		handler = new RuleEngineExceptionTranslationHandler();
		handler.setSupportedInterceptors(Arrays.asList("DroolsKIEBaseValidateInterceptor", "DroolsKIESessionValidateInterceptor",
				"DroolsRuleValidateInterceptor", "CampaignRuleValidateInterceptor", "RuleEngineRulePrepareInterceptor",
				"RuleEngineRuleRemoveInterceptor", "RuleEngineRuleValidateInterceptor", "RuleGroupRemoveInterceptor",
				"SourceRuleTemplateValidateInterceptor", "SourceRuleValidateInterceptor"));
		super.importCsv("/ruleenginebackoffice/test/condition-definitions.impex", "UTF-8");
	}

	@Test
	public void shouldWorkForSourceRuleValidateInterceptor() throws Exception
	{
		//given
		sourceRuleModel = createSourceRule();
		sourceRuleModel.setConditions(INVALID_CONDITIONS_JSON);

		//then
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> modelService.save(sourceRuleModel));
		//then
		Assertions.assertThat(handler.canHandle(throwable)).isTrue();
		Assertions.assertThat(handler.toString(throwable))
				.contains("Invalid conditions for rule with code " + sourceRuleModel.getCode());
	}

	@Test
	public void shouldWorkForRuleEngineRulePrepareInterceptor() throws Exception
	{
		//given
		droolsRuleModel = createDroolsRuleModel();
		droolsRuleModel.setKieBase(null);

		//then
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> modelService.save(droolsRuleModel));
		//then
		Assertions.assertThat(handler.canHandle(throwable)).isTrue();
		Assertions.assertThat(handler.toString(throwable)).contains("The Kie base should be assigned to the drools rule instance");
	}

	@Test
	public void shouldWorkForRuleEngineRuleRemoveInterceptor() throws Exception
	{
		//given
		droolsRuleModel = createDroolsRuleModel();
		droolsRuleModel.setCurrentVersion(false);
		modelService.save(droolsRuleModel);
		//then
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> modelService.remove(droolsRuleModel));
		//then
		Assertions.assertThat(handler.canHandle(throwable)).isTrue();
		Assertions.assertThat(handler.toString(throwable))
				.contains("Cannot remove " + droolsRuleModel + ". It is not a current rule version!");
	}

	@Test
	public void shouldWorkRuleGroupRemoveInterceptor() throws Exception
	{
		//given
		sourceRuleModel = createSourceRule();
		modelService.save(sourceRuleModel);
		ruleGroup = new RuleGroupModel();
		ruleGroup.setCode(UUID_STRING);
		final Set setA = new HashSet();
		setA.add(sourceRuleModel);
		ruleGroup.setRules(setA);
		modelService.save(ruleGroup);

		//then
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> modelService.remove(ruleGroup));
		//then
		Assertions.assertThat(handler.canHandle(throwable)).isTrue();
		Assertions.assertThat(handler.toString(throwable))
				.contains("Rule group " + ruleGroup.getCode() + " cannot be removed as it has rules assigned");
	}

	protected DroolsKIEBaseModel getKieBaseModel(final String kieBaseName)
	{
		return getDroolsKIEBaseDao().findAllKIEBases().stream().filter(b -> b.getName().equals(kieBaseName)).findFirst()
				.orElseThrow(() -> new IllegalStateException(
						"KieBaseModel with name " + kieBaseName + " was not found. Check your test setup"));
	}

	protected DroolsKIEBaseDao getDroolsKIEBaseDao()
	{
		return droolsKIEBaseDao;
	}

	protected SourceRuleModel createSourceRule()
	{
		SourceRuleModel sourceRuleModel = new SourceRuleModel();
		sourceRuleModel.setVersion(1l);
		sourceRuleModel.setCode(UUID.randomUUID().toString());
		sourceRuleModel.setPriority(100);
		sourceRuleModel.setStatus(RuleStatus.PUBLISHED);
		return sourceRuleModel;
	}

	protected DroolsRuleModel createDroolsRuleModel()
	{
		DroolsRuleModel droolsRuleModel = new DroolsRuleModel();
		droolsRuleModel.setCode(RULE_CODE);
		droolsRuleModel.setUuid(UUID_STRING);
		droolsRuleModel.setRuleContent(NEW_RULE_CONTENT);
		droolsRuleModel.setActive(Boolean.TRUE);
		droolsRuleModel.setKieBase(getKieBaseModel("base-junit"));
		droolsRuleModel.setRuleType(RuleType.DEFAULT);
		return droolsRuleModel;
	}
}
