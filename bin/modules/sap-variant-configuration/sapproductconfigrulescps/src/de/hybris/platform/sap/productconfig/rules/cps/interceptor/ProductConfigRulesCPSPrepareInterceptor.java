/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.interceptor;

import de.hybris.platform.ruleengine.enums.RuleType;
import de.hybris.platform.ruleengine.model.AbstractRuleEngineRuleModel;
import de.hybris.platform.ruleengineservices.rule.dao.RuleDao;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import org.springframework.beans.factory.annotation.Required;



/**
 * CPS adds own attributes to the rules source model related to message handling. Those attributes are mapped from the
 * DTO into the model using this interceptor.
 */
public class ProductConfigRulesCPSPrepareInterceptor implements PrepareInterceptor<AbstractRuleEngineRuleModel>
{
	private RuleDao ruleDao;

	@Override
	public void onPrepare(final AbstractRuleEngineRuleModel model, final InterceptorContext context) throws InterceptorException
	{
		if (!RuleType.PRODUCTCONFIG.equals(model.getRuleType()))
		{
			return;
		}
		final ProductConfigSourceRuleModel rule = ruleDao.findRuleByCode(model.getCode());

		if (null != rule)
		{
			mapEndDate(model, rule);
		}
	}

	protected void mapEndDate(final AbstractRuleEngineRuleModel runtimeRule, final ProductConfigSourceRuleModel sourceRule)
	{
		runtimeRule.setValidUntilDate(sourceRule.getEndDate());
	}

	protected RuleDao getRuleDao()
	{
		return ruleDao;
	}

	/**
	 * @param ruleDao
	 */
	@Required
	public void setRuleDao(final RuleDao ruleDao)
	{
		this.ruleDao = ruleDao;
	}
}
