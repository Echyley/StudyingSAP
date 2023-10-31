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
package de.hybris.platform.ruleenginebackoffice.actions;

import static java.util.Collections.singletonList;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import de.hybris.platform.ruleengineservices.model.AbstractRuleModel;

import javax.annotation.Resource;
import java.util.List;


/**
 * Action to compile a single rule for a user-specified rules module.
 */
public class RuleCompileForModuleAction extends AbstractRuleProcessingForModuleAction<AbstractRuleModel, Object>
{
	private static final String DEFAULT_DIALOG_TEMPLATE = "/rulecompileformodule.zul";
	private static final String TITLE_RULECOMPILEACTION = "title.rulecompileformoduleaction";

	@Resource
	private ObjectFacade objectFacade;

	@Override
	protected String getDialogTemplate(final ActionContext<AbstractRuleModel> context)
	{
		return DEFAULT_DIALOG_TEMPLATE;
	}

	@Override
	protected String getDialogTitle(final ActionContext<AbstractRuleModel> context)
	{
		return TITLE_RULECOMPILEACTION;
	}

	@Override
	protected List<AbstractRuleModel> getRulesToProcess(final ActionContext<AbstractRuleModel> context)
	{
		return singletonList(context.getData());
	}

	@Override
	public boolean canPerform(final ActionContext<AbstractRuleModel> context)
	{
		return super.canPerform(context) && !objectFacade.isModified(context.getData());
	}

}
