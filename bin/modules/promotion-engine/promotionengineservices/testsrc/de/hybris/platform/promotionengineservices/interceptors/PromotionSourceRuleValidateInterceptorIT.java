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

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.promotionengineservices.model.PromotionSourceRuleModel;
import de.hybris.platform.promotionengineservices.promotionengine.impl.PromotionEngineServiceBaseTestBase;
import de.hybris.platform.ruleengineservices.enums.RuleStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;



@IntegrationTest
public class PromotionSourceRuleValidateInterceptorIT extends PromotionEngineServiceBaseTestBase
{
	@Resource
	private ModelService modelService;
	@Resource
	private ConfigurationService configurationService;

	@Test
	public void testCreatePromotionSourceRuleFail()
	{
		configurationService.getConfiguration().setProperty("promotionengineservices.maximum.limitation.perrule.enable", "true");
		configurationService.getConfiguration().setProperty("promotionengineservices.maximum.conditions.perrule", String.valueOf(1));
		configurationService.getConfiguration().setProperty("promotionengineservices.maximum.actions.perrule", String.valueOf(2));
		final PromotionSourceRuleModel model = modelService.create(PromotionSourceRuleModel.class);
		model.setCode("testRule");
		model.setStatus(RuleStatus.UNPUBLISHED);
		model.setConditions("definitionId,definitionId:y_group,definitionId:y_container");
		model.setActions("definitionId12345678definitionId");
		modelService.save(model);
		Assert.assertNotNull(model.getUuid());
	}

}
