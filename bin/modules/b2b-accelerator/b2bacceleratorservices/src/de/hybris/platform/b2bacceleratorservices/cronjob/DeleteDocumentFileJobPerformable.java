/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorservices.cronjob;


import de.hybris.platform.b2bacceleratorservices.company.B2BDocumentService;
import de.hybris.platform.b2bacceleratorservices.model.cronjob.DeleteDocumentFileCronJobModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import javax.annotation.Resource;

import org.apache.log4j.Logger;


public class DeleteDocumentFileJobPerformable extends AbstractJobPerformable<DeleteDocumentFileCronJobModel>
{

	@Resource(name = "b2bDocumentService")
	private B2BDocumentService b2bDocumentService;

	private static final Logger LOG = Logger.getLogger(DeleteDocumentFileJobPerformable.class.getName());

	@Override
	public PerformResult perform(final DeleteDocumentFileCronJobModel arg0)
	{

		// if arg0.getNumberOfDay() set to null by mistake, make sure not to delete any media files. so set the number of
		// day to 1000 years(365*1000)

		int numberOfDay = 365 * 1000;

		if (arg0.getNumberOfDay() == null)
		{
			LOG.error("[perform] number of days = null => set to 365*1000 in order to not delete any media files.");
		}
		else
		{
			numberOfDay = arg0.getNumberOfDay();
		}


		b2bDocumentService.deleteB2BDocumentFiles(numberOfDay, arg0.getDocumentTypeList(), arg0.getDocumentStatusList());

		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

	}

}
