/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.actions;

import de.hybris.platform.integrationbackoffice.widgets.common.utility.EditorAccessRights;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import javax.annotation.Resource;

import org.slf4j.Logger;

import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;

/**
 * Action responsible for sending current integration object to success output. The action is available only for Product type and its subtypes.
 */
public final class AddClassificationClassAction extends AbstractComponentWidgetAdapterAware
		implements CockpitAction<String, String>
{
	private static final Logger LOG = Log.getLogger(AddClassificationClassAction.class);
	@Resource
	private EditorAccessRights editorAccessRights;
	@Resource
	private ReadService readService;

	@Override
	public ActionResult<String> perform(final ActionContext<String> ctx)
	{
		String type = ctx.getData();
		return new ActionResult<>(ActionResult.SUCCESS, type);
	}

	@Override
	public boolean canPerform(ActionContext<String> ctx)
	{
		String type = ctx.getData();
		try
		{
			return type != null && readService.isProductType(type) && editorAccessRights.isUserAdmin();
		}
		catch (final UnknownIdentifierException ex)
		{
			LOG.error("cannot find type: {} in the type system.", type, ex);
			return false;
		}
	}

}
