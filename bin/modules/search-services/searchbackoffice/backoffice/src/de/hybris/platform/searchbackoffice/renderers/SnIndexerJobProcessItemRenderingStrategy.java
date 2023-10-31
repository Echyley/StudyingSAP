/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchbackoffice.renderers;

import de.hybris.platform.cronjob.model.CronJobHistoryModel;
import de.hybris.platform.searchservices.model.AbstractSnIndexerCronJobModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.backoffice.widgets.processes.renderer.DefaultProcessItemRenderingStrategy;
import com.hybris.cockpitng.core.user.CockpitUserService;


public class SnIndexerJobProcessItemRenderingStrategy extends DefaultProcessItemRenderingStrategy
{
	private CockpitUserService cockpitUserService;

	@Override
	public boolean canHandle(final CronJobHistoryModel cronJobHistory)
	{
		return cronJobHistory.getCronJob() instanceof AbstractSnIndexerCronJobModel;
	}

	@Override
	public boolean isProgressSupported(final CronJobHistoryModel cronJobHistory)
	{
		return true;
	}

	@Override
	public boolean isRerunApplicable(final CronJobHistoryModel cronJobHistory)
	{
		if (isFailed(cronJobHistory))
		{
			final String userUid = cronJobHistory.getUserUid();
			return StringUtils.equals(cockpitUserService.getCurrentUser(), userUid) || cockpitUserService.isAdmin(userUid);
		}

		return false;
	}

	public CockpitUserService getCockpitUserService()
	{
		return cockpitUserService;
	}

	@Required
	public void setCockpitUserService(final CockpitUserService cockpitUserService)
	{
		this.cockpitUserService = cockpitUserService;
	}
}
