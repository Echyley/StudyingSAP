/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.actions;

import de.hybris.platform.integrationbackoffice.widgets.common.utility.EditorAccessRights;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectPresentationPopulator;

import javax.annotation.Resource;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;

public final class CreateVirtualAttributeAction extends AbstractComponentWidgetAdapterAware
		implements CockpitAction<String, String>
{

	@Resource
	private EditorAccessRights editorAccessRights;
	@Resource
	private IntegrationObjectPresentationPopulator populator;

	@Override
	public ActionResult<String> perform(final ActionContext<String> ctx)
	{
		sendOutput("createVirtualAttributePerform", "");
		return new ActionResult<>(ActionResult.SUCCESS, "");
	}

	@Override
	public boolean canPerform(final ActionContext<String> ctx)
	{
		return ctx.getData() != null && populator.getIOEditorPresentation().isValidIOSelected() && editorAccessRights.isUserAdmin();
	}

}
