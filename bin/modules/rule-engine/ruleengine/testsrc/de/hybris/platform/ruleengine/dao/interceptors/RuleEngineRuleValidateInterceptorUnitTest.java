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
package de.hybris.platform.ruleengine.dao.interceptors;

import static java.lang.Boolean.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.spy;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.DroolsKIEBaseModel;
import de.hybris.platform.ruleengine.model.DroolsKIEModuleModel;
import de.hybris.platform.ruleengine.model.DroolsRuleModel;
import de.hybris.platform.ruleengine.versioning.impl.RuleEngineRuleModelChecksumCalculator;
import de.hybris.platform.ruleengine.versioning.impl.RuleEngineRuleModelValidator;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import java.util.UUID;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class RuleEngineRuleValidateInterceptorUnitTest
{
	private final static String RULE_MODULE_NAME = "test_rule_module";
	private final static String RULE_CONTENT = "rule content";
	private final static String RULE_CHECKSUM = "RULE_CHECKSUM";
	private final static Long RULE_VERSION = 1l;
	private final static String RULE_CODE = "test_code";
	private final static String UUID_STRING = UUID.randomUUID().toString();

	@InjectMocks
	private RuleEngineRuleValidateInterceptor validateInterceptor;
	@InjectMocks
	private RuleEngineRuleModelValidator validator;
	private MockRuleModelChecksumCalculator ruleModelChecksumCalculator;
	@Mock
	private EngineRuleDao engineRuleDao;

	private DroolsRuleModel model;
	@Mock
	private InterceptorContext context;
	@Mock
	private DroolsKIEBaseModel kieBaseModel;
	@Mock
	private DroolsKIEModuleModel kieModuleModel;
	@Mock
	private AbstractRuleModel sourceRule;
	@Mock
	private L10NService l10NService;

	@Before
	public void setUp()
	{
		ruleModelChecksumCalculator = spy(new MockRuleModelChecksumCalculator());
		validator.setRuleModelChecksumCalculator(ruleModelChecksumCalculator);

		validateInterceptor.setValidator(validator);

		model = new DroolsRuleModel();
		model.setCode(RULE_CODE);
		model.setUuid(UUID_STRING);
		model.setVersion(RULE_VERSION);
		model.setRuleContent(RULE_CONTENT);
		model.setChecksum(RULE_CHECKSUM);
		model.setActive(Boolean.TRUE);
		model.setKieBase(kieBaseModel);
		model.setSourceRule(sourceRule);

		Mockito.lenient().when(kieBaseModel.getKieModule()).thenReturn(kieModuleModel);
		Mockito.lenient().when(kieModuleModel.getName()).thenReturn(RULE_MODULE_NAME);

		defineRegularUpdateBehaviour();
	}

	@Test
	public void testOnValidateNew() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isNull();
	}

	@Test
	public void testFailOnValidateNewActiveNotSet() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Active flag must be set"})).thenReturn("'Active flag must be set'");
		model.setActive(null);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("'Active flag must be set'");

	}

	@Test
	public void testFailOnValidateNewMustBeActive() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Non active rule version cannot increase overall knowledgebase snapshot version"})).
				thenReturn("'Non active rule version cannot increase overall knowledgebase snapshot version'");
		model.setActive(Boolean.FALSE);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("'Non active rule version cannot increase overall knowledgebase snapshot version'");
	}

	@Test
	public void testOnValidateNewNonActiveRuleWithContent() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(engineRuleDao.getCurrentRulesSnapshotVersion(kieModuleModel)).thenReturn(RULE_VERSION);

		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.FALSE);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isNull();
	}

	@Test
	public void testFailOnValidateNewNonActiveRuleWithContent() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(engineRuleDao.getCurrentRulesSnapshotVersion(kieModuleModel)).thenReturn(RULE_VERSION - 1);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Non active rule version cannot increase overall knowledgebase snapshot version"})).
				thenReturn("'Non active rule version cannot increase overall knowledgebase snapshot version'");
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);

		model.setActive(Boolean.FALSE);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("'Non active rule version cannot increase overall knowledgebase snapshot version'");
	}

	@Test
	public void testOnValidateNewNonActiveRuleWithContent2() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(engineRuleDao.getCurrentRulesSnapshotVersion(kieModuleModel)).thenReturn(RULE_VERSION + 1);

		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.FALSE);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isNull();
	}

	@Test
	public void testOnValidateNewNonActiveRuleContentNotSet() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(engineRuleDao.getCurrentRulesSnapshotVersion(kieModuleModel)).thenReturn(RULE_VERSION);
		model.setRuleContent(null);

		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.FALSE);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isNull();
	}

	@Test
	public void testFailOnValidateNewNonActiveRuleNoContent() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(engineRuleDao.getCurrentRulesSnapshotVersion(kieModuleModel)).thenReturn(RULE_VERSION + 1);
		model.setRuleContent(null);

		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.FALSE);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Non active rule version cannot increase overall knowledgebase snapshot version"})).
				thenReturn("'Non active rule version cannot increase overall knowledgebase snapshot version'");
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("'Non active rule version cannot increase overall knowledgebase snapshot version'");
	}

	@Test
	public void testFailOnValidateNewNonActiveRuleNoContent2() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(engineRuleDao.getCurrentRulesSnapshotVersion(kieModuleModel)).thenReturn(RULE_VERSION - 1);
		model.setRuleContent(null);

		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.FALSE);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Non active rule version cannot increase overall knowledgebase snapshot version"})).
				thenReturn("'Non active rule version cannot increase overall knowledgebase snapshot version'");
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("'Non active rule version cannot increase overall knowledgebase snapshot version'");
	}

	@Test
	public void testPassOnValidateModifiedNonActiveRuleVersionsEqual() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(engineRuleDao.getRuleVersion(RULE_CODE, RULE_MODULE_NAME)).thenReturn(RULE_VERSION);

		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.FALSE);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isNull();
	}

	@Test
	public void testFailOnValidateModifiedNonActiveRuleVersionsNotEqual() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(engineRuleDao.getRuleVersion(RULE_CODE, RULE_MODULE_NAME)).thenReturn(RULE_VERSION + 1);

		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.FALSE);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Historical version of the rule cannot be modified"})).
				thenReturn("'Historical version of the rule cannot be modified'");
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("'Historical version of the rule cannot be modified'");
	}

	@Test
	public void testFailOnValidateModifiedActiveRuleNoContent() throws InterceptorException
	{
		//Mockito.lenient().when
		model.setRuleContent(null);

		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.TRUE);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Checksum doesn't match the rule content. Expected null but was RULE_CHECKSUM"})).
				thenReturn("'Checksum doesn't match the rule content. Expected null but was RULE_CHECKSUM'");
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("'Checksum doesn't match the rule content. Expected null but was RULE_CHECKSUM'");
	}

	@Test
	public void testFailOnValidateModifiedActiveRuleWithContentWrongChecksum() throws InterceptorException
	{
		//Mockito.lenient().when
		model.setChecksum("WRONG_CHECKSUM");

		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.TRUE);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Checksum doesn't match the rule content. Expected RULE_CHECKSUM but was WRONG_CHECKSUM"})).
				thenReturn("'Checksum doesn't match the rule content. Expected RULE_CHECKSUM but was WRONG_CHECKSUM'");
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("'Checksum doesn't match the rule content. Expected RULE_CHECKSUM but was WRONG_CHECKSUM'");
	}

	@Test
	public void testOnValidateModifiedActiveRule() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.TRUE);
		model.setActive(Boolean.TRUE);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isNull();
	}

	@Test
	public void testFailOnValidateRemovedActiveRule() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(valueOf(context.isRemoved(model))).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Rule must be active."})).
				thenReturn("'Rule must be active.'");
		model.setActive(Boolean.TRUE);
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("'Rule must be active.'");
	}

	@Test
	public void testValidateNoKieBase() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(valueOf(context.isRemoved(model))).thenReturn(Boolean.FALSE);

		model.setKieBase(null);
		model.setActive(Boolean.TRUE);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"rule must have correct associated KieBase"})).
				thenReturn("rule must have correct associated KieBase");
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
					 .hasMessageContaining("rule must have correct associated KieBase");
	}

	@Test
	public void testValidateNoKieModule() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(valueOf(context.isRemoved(model))).thenReturn(Boolean.FALSE);

		Mockito.lenient().when(kieBaseModel.getKieModule()).thenReturn(null);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"rule must have correct associated KieModule"})).
				thenReturn("rule must have correct associated KieModule");
		model.setActive(Boolean.TRUE);

		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(IllegalArgumentException.class)
					 .hasMessageContaining("rule must have correct associated KieModule");
	}

	@Test
	public void testFailOnValidateNewManuallyCreatedRuleWithOldVersionVersion() throws InterceptorException
	{
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.FALSE);
		model.setActive(Boolean.FALSE);
		model.setSourceRule(null);
		Mockito.lenient().when(engineRuleDao.getRuleVersion(model.getCode(),kieModuleModel.getName())).thenReturn(RULE_VERSION + 1);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Rule must be created using latest rule module version."})).
				thenReturn("Rule must be created using latest rule module version.");
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("Rule must be created using latest rule module version.");
	}

	@Test
	public void testFailOnValidateNewManuallyCreatedRuleWithContentWrongChecksum() throws InterceptorException
	{
		//Mockito.lenient().when
		Mockito.lenient().when(valueOf(context.isNew(model))).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(valueOf(context.isModified(model))).thenReturn(Boolean.FALSE);
		model.setActive(Boolean.FALSE);
		model.setChecksum("WRONG_CHECKSUM");
		model.setSourceRule(null);
		Mockito.lenient().when(engineRuleDao.getRuleVersion(model.getCode(),kieModuleModel.getName())).thenReturn(RULE_VERSION);
		Mockito.lenient().when(l10NService.getLocalizedString("exception.ruleenginerulevalidateinterceptor", new Object[]{model,"Checksum doesn't match the content."})).
				thenReturn("Checksum doesn't match the content.");
		//when
		final Throwable throwable = ThrowableAssert.catchThrowable(() -> validateInterceptor.onValidate(model, context));
		//then
		assertThat(throwable).isInstanceOf(InterceptorException.class)
					 .hasMessageContaining("Checksum doesn't match the content.");
	}

	private void defineRegularUpdateBehaviour()
	{
		Mockito.lenient().when(ruleModelChecksumCalculator.calculateContentChecksum(RULE_CONTENT)).thenReturn(RULE_CHECKSUM);
	}

	public static class MockRuleModelChecksumCalculator extends RuleEngineRuleModelChecksumCalculator
	{
		@Override
		protected String calculateContentChecksum(final String ruleContent)
		{
			return super.calculateContentChecksum(ruleContent);
		}
	}

}
