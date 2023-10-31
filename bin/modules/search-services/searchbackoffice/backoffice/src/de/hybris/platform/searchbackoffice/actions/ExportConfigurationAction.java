/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchbackoffice.actions;

import de.hybris.platform.searchservices.admin.service.SnCommonConfigurationService;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.model.SnIndexConfigurationModel;
import de.hybris.platform.searchservices.model.SnIndexTypeModel;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level;
import com.hybris.cockpitng.actions.ActionContext;
import com.hybris.cockpitng.actions.ActionResult;
import com.hybris.cockpitng.actions.CockpitAction;
import com.hybris.cockpitng.engine.impl.AbstractComponentWidgetAdapterAware;
import com.hybris.cockpitng.util.notifications.NotificationService;


public class ExportConfigurationAction extends AbstractComponentWidgetAdapterAware implements CockpitAction<Object, Object>
{
	private static final Logger LOG = LoggerFactory.getLogger(ExportConfigurationAction.class);

	protected static final String EVENT_TYPE = "SnExportConfiguration";

	@Resource
	private SnCommonConfigurationService snCommonConfigurationService;

	@Resource
	private NotificationService notificationService;

	@Override
	public ActionResult<Object> perform(final ActionContext<Object> context)
	{
		final Object data = context.getData();
		try
		{
			return internalPerformAction(context, data);
		}
		catch (final SnException e)
		{
			LOG.error(e.getMessage(), e);

			notificationService.notifyUser(context, EVENT_TYPE, Level.FAILURE);
			return new ActionResult<>(ActionResult.ERROR, null);
		}
	}

	protected ActionResult<Object> internalPerformAction(final ActionContext<Object> context, final Object data) throws SnException
	{
		if (data instanceof SnIndexConfigurationModel)
		{
			final SnIndexConfigurationModel indexConfiguration = (SnIndexConfigurationModel) data;

			snCommonConfigurationService.exportConfiguration(indexConfiguration.getId());

			notificationService.notifyUser(context, EVENT_TYPE, Level.SUCCESS);
			return new ActionResult<>(ActionResult.SUCCESS, null);
		}
		else if (data instanceof SnIndexTypeModel)
		{
			final SnIndexTypeModel indexType = (SnIndexTypeModel) data;
			final SnIndexConfigurationModel indexConfiguration = indexType.getIndexConfiguration();

			snCommonConfigurationService.exportConfiguration(indexConfiguration.getId());

			notificationService.notifyUser(context, EVENT_TYPE, Level.SUCCESS);
			return new ActionResult<>(ActionResult.SUCCESS, null);
		}
		else
		{
			notificationService.notifyUser(context, EVENT_TYPE, Level.FAILURE);
			return new ActionResult<>(ActionResult.ERROR, null);
		}
	}

	@Override
	public boolean canPerform(final ActionContext<Object> ctx)
	{
		final Object data = ctx.getData();

		if (data instanceof SnIndexConfigurationModel)
		{
			final SnIndexConfigurationModel indexConfiguration = (SnIndexConfigurationModel) data;
			return snCommonConfigurationService.canExportConfiguration(indexConfiguration.getId());
		}
		else if (data instanceof SnIndexTypeModel)
		{
			final SnIndexTypeModel indexType = (SnIndexTypeModel) data;

			if (indexType.getIndexConfiguration() == null)
			{
				return false;
			}

			final SnIndexConfigurationModel indexConfiguration = indexType.getIndexConfiguration();
			return snCommonConfigurationService.canExportConfiguration(indexConfiguration.getId());
		}

		return false;
	}
}
