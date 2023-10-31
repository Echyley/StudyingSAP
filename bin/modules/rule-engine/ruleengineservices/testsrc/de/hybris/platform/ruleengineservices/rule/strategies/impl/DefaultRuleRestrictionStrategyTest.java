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
package de.hybris.platform.ruleengineservices.rule.strategies.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.ruleengine.dao.EngineRuleDao;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultRuleRestrictionStrategyTest
{
	@InjectMocks
	private DefaultRuleRestrictionStrategy strategy;
	@Mock
	private EngineRuleDao engineRuleDao;
	@Mock
 	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	private final String RULE_PUBLISH_LIMITATION_ENABLED = "ruleengineservices.maximum.limitation.published.rules.enable";
	private final String MAXIMUM_PUBLISHED_RULES="ruleengineservices.maximum.published.rules";

	@Test
	public void testMaxPublishedRulesFlagFalse()
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		assertTrue(strategy.isAllowedToPublish("test_module", 100));
	}

	@Test
	public void testCanPublish()
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getBoolean(RULE_PUBLISH_LIMITATION_ENABLED, Boolean.FALSE)).thenReturn(true);
		Mockito.when(configuration.getInt(MAXIMUM_PUBLISHED_RULES, 100)).thenReturn(2);
		final AbstractRuleEngineRuleModel publishedRule = new AbstractRuleEngineRuleModel();
		List<AbstractRuleEngineRuleModel> publishedRules = new ArrayList<>();
		publishedRules.add(publishedRule);
		Mockito.when(engineRuleDao.getActiveRules("test_module")).thenReturn(publishedRules);
		assertTrue(strategy.isAllowedToPublish("test_module", 1));
		assertFalse(strategy.isAllowedToPublish("test_module", 2));
	}

}
