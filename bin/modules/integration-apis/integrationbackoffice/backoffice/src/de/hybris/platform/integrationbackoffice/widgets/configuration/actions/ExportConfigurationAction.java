/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.configuration.actions;

import de.hybris.platform.integrationbackoffice.widgets.common.utility.EditorAccessRights;

import javax.annotation.Resource;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;

/**
 * Handler for the export configuration action.
 */
public class ExportConfigurationAction extends AbstractComponentWidgetAdapterAware
		implements CockpitAction<String, String>
{
	@Resource
	private EditorAccessRights editorAccessRights;

	@Override
	public ActionResult<String> perform(final ActionContext<String> ctx)
	{
		sendOutput("exportConfigurationPerform", "");
		return new ActionResult<>(ActionResult.SUCCESS, "");
	}

	@Override
	public boolean canPerform(final ActionContext<String> ctx)
	{
		return editorAccessRights.isUserAdmin();
	}
}
