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
package de.hybris.platform.ruleengineservices.versioning.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.ruleengineservices.jalo.SourceRule;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;
import de.hybris.platform.ruleengineservices.model.SourceRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.ruleengineservices.versioning.HistoricalRuleContentProvider;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.singletonList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.Mockito;



@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SourceRuleModelHistoricalContentCreatorUnitTest
{
	private static final String RULE_UUID = "rule_uuid";
	private static final String RULE_CODE = "rule_code";
	private static final String CONDITIONS = "rule_conditions";
	private static final String ACTIONS = "rule_actions";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@InjectMocks
	private SourceRuleModelHistoricalContentCreator contentCreator;

	@Mock
	private RuleDao ruleDao;
	@Mock
	private InterceptorContext context;
	@Mock
	private ModelService modelService;
	@Mock
	private AbstractRuleModel optionalRule;

	private SourceRuleModel sourceRule;
	private SourceRuleModel historicalSourceRule;

	@Before
	public void setUp()
	{
		final HistoricalRuleContentProvider contentProvider = new DefaultSourceRuleHistoricalRuleContentProvider();
		contentCreator.setHistoricalRuleContentProviders(singletonList(contentProvider));

		Mockito.lenient().when(context.getModelService()).thenReturn(modelService);

		sourceRule = new SourceRuleModel();
		sourceRule.setUuid(RULE_UUID);
		sourceRule.setCode(RULE_CODE);
		sourceRule.setConditions(CONDITIONS);
		sourceRule.setActions(ACTIONS);
		sourceRule.setVersion(0L);

		historicalSourceRule = new SourceRuleModel();
		historicalSourceRule.setConditions(CONDITIONS);
		historicalSourceRule.setActions(ACTIONS);

		Mockito.lenient().when(context.isNew(sourceRule)).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(context.isRemoved(sourceRule)).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(context.isModified(sourceRule)).thenReturn(Boolean.TRUE);

		Mockito.lenient().when(modelService.clone(sourceRule)).thenReturn(historicalSourceRule);

		final Map<String, Set<Locale>> dirtyAttributes = newHashMap();
		dirtyAttributes.put("actions", null);

		Mockito.lenient().when(context.getDirtyAttributes(sourceRule)).thenReturn(dirtyAttributes);
	}

	@Test
	public void testCreateHistoricalVersionIllegalArgument1() throws InterceptorException
	{
		expectedException.expect(IllegalArgumentException.class);
		contentCreator.createHistoricalVersion(null, context);
	}

	@Test
	public void testCreateHistoricalVersionIllegalArgument2() throws InterceptorException
	{
		expectedException.expect(IllegalArgumentException.class);
		contentCreator.createHistoricalVersion(sourceRule, null);
	}

	@Test
	public void testCreateHistoricalVersionIfUnpublihed() throws InterceptorException
	{
		Mockito.lenient().when(modelService.getAttributeValue(sourceRule, SourceRule.STATUS)).thenReturn(RuleStatus.UNPUBLISHED);
		contentCreator = spy(contentCreator);

		contentCreator.createHistoricalVersion(sourceRule, context);

		verify(contentCreator, times(0)).historicalVersionMustBeCreated(sourceRule, context);
	}

	@Test
	public void testCreateHistoricalVersionIfPublished() throws InterceptorException
	{
		Mockito.lenient().when(modelService.getAttributeValue(sourceRule, SourceRule.STATUS)).thenReturn(RuleStatus.PUBLISHED);
		Mockito.lenient().when(ruleDao.findRuleByCodeAndStatus(RULE_CODE, RuleStatus.UNPUBLISHED)).thenReturn(Optional.empty());
		contentCreator = spy(contentCreator);

		doNothing().when(contentCreator).copyField(any(), any(), any());
		contentCreator.createHistoricalVersion(sourceRule, context);

		verify(contentCreator).historicalVersionMustBeCreated(sourceRule, context);
	}

	@Test
	public void testCreateHistoricalVersionIfModified() throws InterceptorException
	{
		Mockito.lenient().when(modelService.getAttributeValue(sourceRule, SourceRule.STATUS)).thenReturn(RuleStatus.MODIFIED);
		Mockito.lenient().when(ruleDao.findRuleByCodeAndStatus(RULE_CODE, RuleStatus.UNPUBLISHED)).thenReturn(Optional.empty());
		contentCreator = spy(contentCreator);

		doNothing().when(contentCreator).copyField(any(), any(), any());
		contentCreator.createHistoricalVersion(sourceRule, context);

		verify(contentCreator).historicalVersionMustBeCreated(sourceRule, context);
	}

	@Test
	public void testCreateHistoricalVersionExistsUnpublished() throws InterceptorException
	{
		Mockito.lenient().when(modelService.getAttributeValue(sourceRule, SourceRule.STATUS)).thenReturn(RuleStatus.PUBLISHED);
		Mockito.lenient().when(ruleDao.findRuleByCodeAndStatus(RULE_CODE, RuleStatus.UNPUBLISHED)).thenReturn(Optional.of(optionalRule));

		final Map<String, Set<Locale>> dirtyAttributes = newHashMap();
		dirtyAttributes.put("actions", null);

		Mockito.lenient().when(context.getDirtyAttributes(sourceRule)).thenReturn(dirtyAttributes);

		contentCreator = spy(contentCreator);

		expectedException.expect(InterceptorException.class);
		expectedException.expectMessage("The modifications are allowed to be made for version [0] only");
		contentCreator.createHistoricalVersion(sourceRule, context);
	}

	@Test
	public void testCreateHistoricalVersionOk() throws InterceptorException
	{
		Mockito.lenient().when(modelService.getAttributeValue(sourceRule, SourceRule.STATUS)).thenReturn(RuleStatus.PUBLISHED);
		Mockito.lenient().when(ruleDao.findRuleByCodeAndStatus(RULE_CODE, RuleStatus.UNPUBLISHED)).thenReturn(Optional.empty());

		contentCreator = spy(contentCreator);

		doNothing().when(contentCreator).copyField(any(), any(), any());
		contentCreator.createHistoricalVersion(sourceRule, context);

		verify(contentCreator).doCreateHistoricalVersion(sourceRule, context);
		verify(contentCreator).putOriginalValuesIntoHistoricalVersion(sourceRule, historicalSourceRule, context);

		assertThat(historicalSourceRule.getVersion()).isNotNull().isEqualTo(1l);
		assertThat(historicalSourceRule.getUuid()).isNotEmpty().isEqualTo(RULE_UUID);
		assertThat(historicalSourceRule.getCode()).isNotEmpty().isEqualTo(RULE_CODE);
	}

	@Test
	public void testEssentialFieldsAreModified()
	{
		final Map<String, Set<Locale>> dirtyAttributes = newHashMap();
		dirtyAttributes.put("status", null);

		Mockito.lenient().when(context.getDirtyAttributes(sourceRule)).thenReturn(dirtyAttributes);

		boolean essentialFieldsAreModified = contentCreator.essentialFieldsAreModified(sourceRule, context);
		assertThat(essentialFieldsAreModified).isFalse();

		dirtyAttributes.put("engineRules", null);
		dirtyAttributes.put("version", null);

		essentialFieldsAreModified = contentCreator.essentialFieldsAreModified(sourceRule, context);
		assertThat(essentialFieldsAreModified).isFalse();

		dirtyAttributes.put("essentialAttr", null);

		essentialFieldsAreModified = contentCreator.essentialFieldsAreModified(sourceRule, context);
		assertThat(essentialFieldsAreModified).isTrue();

	}

	@Test
	public void testEssentialFieldsAreModifiedEmptyProbes()
	{
		final Map<String, Set<Locale>> dirtyAttributes = newHashMap();

		Mockito.lenient().when(context.getDirtyAttributes(sourceRule)).thenReturn(dirtyAttributes);

		boolean essentialFieldsAreModified = contentCreator.essentialFieldsAreModified(sourceRule, context);
		assertThat(essentialFieldsAreModified).isFalse();

		Mockito.lenient().when(context.getDirtyAttributes(sourceRule)).thenReturn(null);

		essentialFieldsAreModified = contentCreator.essentialFieldsAreModified(sourceRule, context);
		assertThat(essentialFieldsAreModified).isFalse();

	}

}
