/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.service.impl;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.time.TimeService;

import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.merchandising.client.ClientExceptionHandler;
import com.hybris.merchandising.dao.MerchSynchronizationDao;
import com.hybris.merchandising.model.MerchProductDirectoryConfigModel;
import com.hybris.merchandising.model.MerchSnConfigModel;
import com.hybris.merchandising.model.MerchSynchronizationModel;
import com.hybris.merchandising.service.MerchSyncService;

/**
 * Default implementation of {@link MerchSyncService}
 */
public class DefaultMerchSyncService implements MerchSyncService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMerchSyncService.class);
	private static final int LONG_STRING_LENGTH = 4000;
	private static final String OPERATION_ID_PARAM = "operationId";
	protected static final String MERCH_SYNC_SUCCESSFUL = "Merch synchronization completed successfully";

	private ModelService modelService;
	private MerchSynchronizationDao merchSynchronizationDao;
	private TimeService timeService;

	@Override
	public MerchSynchronizationModel createMerchSychronization(final MerchProductDirectoryConfigModel config, final String operationId, final String type)
	{
		validateParameterNotNull(config, "Merchandising configuration can not be null");
		validateParameterNotNull(operationId, "Merchandising operation identifier can not be null");
		validateParameterNotNull(type, "Merchandising operation type can not be null");

		final MerchSynchronizationModel merchSync = modelService.create(MerchSynchronizationModel.class);
		merchSync.setConfig(config);
		merchSync.setOperationId(operationId);
		merchSync.setStartTime(timeService.getCurrentTime());
		merchSync.setType(type);
		merchSync.setNumberOfProducts(0L);
		merchSync.setStatus(MerchSyncStatus.STARTED.name());
		modelService.save(merchSync);
		return merchSync;
	}

	@Override
	public MerchSynchronizationModel createMerchSychronization(final MerchSnConfigModel config, final String operationId, final String type)
	{
		validateParameterNotNull(config, "Merchandising configuration can not be null");
		validateParameterNotNull(operationId, "Merchandising operation identifier can not be null");
		validateParameterNotNull(type, "Merchandising operation type can not be null");

		final MerchSynchronizationModel merchSync = modelService.create(MerchSynchronizationModel.class);
		merchSync.setMerchSnConfig(config);
		merchSync.setOperationId(operationId);
		merchSync.setStartTime(timeService.getCurrentTime());
		merchSync.setType(type);
		merchSync.setNumberOfProducts(0L);
		merchSync.setStatus(MerchSyncStatus.STARTED.name());
		modelService.save(merchSync);
		return merchSync;
	}

	@Override
	public Optional<MerchSynchronizationModel> getMerchSynchronization(final String operationId)
	{
		validateParameterNotNullStandardMessage(OPERATION_ID_PARAM, operationId);

		return merchSynchronizationDao.findByOperationId(operationId);
	}

	@Override
	public boolean isMerchSyncFailed(final String operationId)
	{
		validateParameterNotNullStandardMessage(OPERATION_ID_PARAM, operationId);

		return merchSynchronizationDao.findByOperationId(operationId)
		                              .map(m -> MerchSyncStatus.FAILED.name().equals(m.getStatus()))
		                              .orElse(false);
	}

	@Override
	public void completeMerchSyncProcess(final String operationId, final Long numberOfProducts)
	{
		validateParameterNotNullStandardMessage(OPERATION_ID_PARAM, operationId);

		merchSynchronizationDao.findByOperationId(operationId)
		                       .ifPresentOrElse(merchSync -> {
					                       if (MerchSyncStatus.FAILED.name().equals(merchSync.getStatus()))
					                       {
						                       LOG.error("Merch synchronization failed. Check details in backoffice Merchandising->Merchandising Synchronization");
					                       }
					                       else
					                       {
						                       merchSync.setStatus(MerchSyncStatus.COMPLETED.name());
						                       merchSync.setDetails(MERCH_SYNC_SUCCESSFUL);
						                       merchSync.setNumberOfProducts(numberOfProducts);
					                       }
					                       merchSync.setEndTime(timeService.getCurrentTime());
					                       modelService.save(merchSync);
				                       }, () -> LOG.warn("MerchSynchronization could not be found for operationId :{}", operationId)
		                       );
	}

	@Override
	public void saveErrorInfo(final String operationId, final String errorMessage, final Exception exception)
	{
		validateParameterNotNullStandardMessage(OPERATION_ID_PARAM, operationId);
		validateParameterNotNullStandardMessage("errorMessage", errorMessage);


		merchSynchronizationDao.findByOperationId(operationId)
		                       .ifPresentOrElse(merchSync -> {
					                       if (!MerchSyncStatus.FAILED.name().equals(merchSync.getStatus()))
					                       {
						                       merchSync.setStatus(MerchSyncStatus.FAILED.name());
						                       merchSync.setDetails(getErrorMessage(errorMessage, exception));
						                       modelService.save(merchSync);
					                       }
				                       }, () -> LOG.warn("MerchSyncronization could not be found for operationId :{}", operationId)
		                       );
	}

	protected String getErrorMessage(final String errorMessage, final Exception exception)
	{
		String message = ClientExceptionHandler.getAndLogErrorMessage("ERROR : " + errorMessage + " : ", exception);
		if (exception != null)
		{
			message += " : STACKTRACE : " + ExceptionUtils.getStackTrace(exception);
		}
		return getStringValue(message, LONG_STRING_LENGTH);
	}

	protected String getStringValue(final String value, final int length)
	{
		if (value != null && value.length() > length)
		{
			return value.substring(0, length);
		}
		return value;
	}

	protected ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	protected MerchSynchronizationDao getMerchSynchronizationDao()
	{
		return merchSynchronizationDao;
	}

	@Required
	public void setMerchSynchronizationDao(final MerchSynchronizationDao merchSynchronizationDao)
	{
		this.merchSynchronizationDao = merchSynchronizationDao;
	}

	protected TimeService getTimeService()
	{
		return timeService;
	}

	@Required
	public void setTimeService(final TimeService timeService)
	{
		this.timeService = timeService;
	}

	public enum LogType
	{
		INFO, WARN, ERROR
	}

	public enum MerchSyncStatus
	{
		STARTED, COMPLETED, FAILED
	}
}
