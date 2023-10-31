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
package de.hybris.platform.promotionengineservices.interceptors;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;

import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PromotionSourceRuleValidateInterceptorUnitTest
{

	@InjectMocks
	private PromotionSourceRuleValidateInterceptor validateInterceptor;
	@Mock
	private ConfigurationService configurationService;
	@Mock
	private Configuration configuration;

	private final String MAXIMUM_CONDITION_ACTION_LIMITATION = "promotionengineservices.maximum.limitation.perrule.enable";
	private final String MAXIMUM_CONDITION = "promotionengineservices.maximum.conditions.perrule";
	private final String MAXIMUM_ACTION = "promotionengineservices.maximum.actions.perrule";

	@Test(expected = InterceptorException.class)
	public void testValidateCondition() throws InterceptorException
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getBoolean(MAXIMUM_CONDITION_ACTION_LIMITATION, Boolean.FALSE)).thenReturn(true);
		Mockito.when(configuration.getInt(MAXIMUM_CONDITION, 10)).thenReturn(1);
		final PromotionSourceRuleModel model = new PromotionSourceRuleModel();
		model.setStatus(RuleStatus.UNPUBLISHED);
		model.setConditions("definitionId,definitionId,definitionId:y_group,definitionId:y_container");
		validateInterceptor.onValidate(model, null);
	}

	@Test(expected = InterceptorException.class)
	public void testValidateAction() throws InterceptorException
	{
		Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
		Mockito.when(configuration.getBoolean(MAXIMUM_CONDITION_ACTION_LIMITATION, Boolean.FALSE)).thenReturn(true);
		Mockito.when(configuration.getInt(MAXIMUM_ACTION, 2)).thenReturn(1);
		final PromotionSourceRuleModel model = new PromotionSourceRuleModel();
		model.setStatus(RuleStatus.UNPUBLISHED);
		model.setActions("definitionId,definitionId");
		validateInterceptor.onValidate(model, null);
	}

}
