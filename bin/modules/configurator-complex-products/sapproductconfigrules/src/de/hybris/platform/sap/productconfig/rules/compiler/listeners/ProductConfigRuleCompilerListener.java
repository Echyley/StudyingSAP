/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.compiler.listeners;

import de.hybris.platform.ruleengineservices.compiler.RuleCompilerContext;
import de.hybris.platform.ruleengineservices.compiler.RuleCompilerListener;
import de.hybris.platform.ruleengineservices.rao.ProcessStep;
import de.hybris.platform.ruleengineservices.rao.RuleEngineResultRAO;
import de.hybris.platform.sap.productconfig.rules.model.ProductConfigSourceRuleModel;
import de.hybris.platform.sap.productconfig.rules.rao.BaseStoreRAO;
import de.hybris.platform.sap.productconfig.rules.rao.ProductConfigProcessStepRAO;


/**
 * {@link RuleCompilerListener} for product configuration. Ensures that the {@link ProcessStep} is propagated to each
 * product config rule implicitly.
 */
public class ProductConfigRuleCompilerListener implements RuleCompilerListener
{

	@Override
	public void beforeCompile(final RuleCompilerContext context)
	{
		if (context.getRule() instanceof ProductConfigSourceRuleModel)
		{
			context.generateVariable(RuleEngineResultRAO.class);
			context.generateVariable(ProductConfigProcessStepRAO.class);
			context.generateVariable(BaseStoreRAO.class);
		}
		return;
	}

	@Override
	public void afterCompile(final RuleCompilerContext context)
	{
		return;
	}

	@Override
	public void afterCompileError(final RuleCompilerContext context)
	{
		return;
	}

}
