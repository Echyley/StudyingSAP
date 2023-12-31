/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapimpeximportadapter.tasks;


import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.task.RetryLaterException;
import de.hybris.platform.task.TaskModel;
import de.hybris.platform.task.TaskRunner;
import de.hybris.platform.task.TaskService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapimpeximportadapter.services.ImpexImportService;




/**
 * SAP Impex import task running class.
 *
 * @deprecated since 1811, please use {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}
 *
 */
@Deprecated(since = "1811", forRemoval= true )
public class SapImpexImportTaskRunner implements TaskRunner<TaskModel>
{

	private static final Logger LOG = LoggerFactory.getLogger(SapImpexImportTaskRunner.class);


	private SessionService sessionService;
	private ImpexImportService impexImportService;

	/**
	 * Implementation for running the impex import task
	 *
	 * @param taskService
	 *           - task service class injected
	 *
	 * @param task
	 *           - task model
	 *
	 * @throws RetryLaterException
	 *
	 */
	@Override
	public void run(final TaskService taskService, final TaskModel task)
	{

		try
		{
			final ImpExMediaModel impexMedia = (ImpExMediaModel) task.getContext();
			if (impexMedia == null || impexMedia.getSize() == 0)
			{
				throw new IllegalArgumentException("No impex media present");
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(String.format("About to import from  [%s]", impexMedia.getRealFileName()));
			}

			final ImportResult result = impexImportService.importMedia(impexMedia);

			final ImpExImportCronJobModel cronjob = result.getCronJob();
			if (cronjob == null || CronJobResult.SUCCESS.equals(cronjob.getResult()))
			{
				LOG.debug("Successfully imported  with {} retries", task.getRetry());
			}
		}
		catch (final Exception e)
		{
			throw new SystemException("Could not close input stream", e);
		}
	}


	/**
	 * Method that handles the errors while running the task. Just logs the error messages
	 *
	 * @param taskService
	 *           - task service class
	 * @param task
	 *           - the task model object
	 */
	@Override
	public void handleError(final TaskService taskService, final TaskModel task, final Throwable error)
	{
		LOG.error("ItemImportTaskRunner.handleError()", error);
	}





	@Required
	public void setSessionService(final SessionService service)
	{
		this.sessionService = service;
	}

	/**
	 * @return the impexImportService
	 */
	public ImpexImportService getImpexImportService()
	{
		return impexImportService;
	}

	/**
	 * @param impexImportService
	 *           the impexImportService to set
	 */
	public void setImpexImportService(final ImpexImportService impexImportService)
	{
		this.impexImportService = impexImportService;
	}

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}


}
