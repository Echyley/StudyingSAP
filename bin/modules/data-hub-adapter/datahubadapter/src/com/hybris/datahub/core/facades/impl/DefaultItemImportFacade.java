/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package com.hybris.datahub.core.facades.impl;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.impex.ImportConfig;
import de.hybris.platform.servicelayer.impex.ImportResult;
import de.hybris.platform.servicelayer.impex.ImportService;

import com.hybris.datahub.core.config.ImportConfigStrategy;
import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.event.DatahubAdapterImportEvent;
import com.hybris.datahub.core.facades.ItemImportFacade;
import com.hybris.datahub.core.facades.ItemImportResult;
import de.hybris.platform.dataimportcommons.facades.impl.converter.ErrorLimitExceededException;
import de.hybris.platform.dataimportcommons.facades.impl.ImportResultConverter;
import com.hybris.datahub.core.services.impl.DataHubFacade;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default facade for the ImpEx import process.
 */
public class DefaultItemImportFacade implements ItemImportFacade
{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultItemImportFacade.class);

	private ImportService importService;
	private ImportResultConverter<ItemImportResult> resultConverter;
	private DataHubFacade dataHubFacade;
	private EventService eventService;
	private ImportConfigStrategy importConfigStrategy;
	private int errorLimit;

	@Override
	public ItemImportResult importItems(final ItemImportTaskData ctx)
	{
		if( ctx == null )
		{
			return null;
		}

		final ItemImportResult result = runImportAndHandleErrors(ctx);
		addHeaderErrorsToResult(ctx, result);

		fireDatahubAdapterImportEvent(ctx.getPoolName(), result.getStatus());
		callbackToDataHub(ctx, result);

		return result;
	}

	private static void addHeaderErrorsToResult(final ItemImportTaskData ctx, final ItemImportResult result)
	{
		if (ctx != null && !CollectionUtils.isEmpty(ctx.getHeaderErrors()))
		{
			result.addErrors(ctx.getHeaderErrors());
		}
	}

	private ItemImportResult runImportAndHandleErrors(final ItemImportTaskData ctx)
	{
		try
		{
			return runImport(ctx);
		}
		catch (final ErrorLimitExceededException e)
		{
			final String errorLimitExceededMessage = "Target System Publication " + ctx.getPublicationId()
					+ " has failed because the number of errors exceeds the configured threshold of "
					+ errorLimit + " errors.";
			LOGGER.error(errorLimitExceededMessage, e);
			return new ItemImportResult(new ErrorLimitExceededException(errorLimitExceededMessage +  " For more information on the errors, please see the Hybris Commerce log."));
		}
		catch (final ImpExException | RuntimeException e)
		{
			return new ItemImportResult(e);
		}
	}

	private ItemImportResult runImport(final ItemImportTaskData ctx) throws ImpExException
	{
		final ImportConfig config = importConfigStrategy.createImportConfig(ctx);
		final ImportResult result = importService.importData(config);
		return resultConverter.convert(result);
	}

	private void fireDatahubAdapterImportEvent(final String poolName, final ItemImportResult.DatahubAdapterEventStatus status)
	{
		eventService.publishEvent(new DatahubAdapterImportEvent(poolName, status));
	}

	private void callbackToDataHub(final ItemImportTaskData ctx, final ItemImportResult result)
	{
		dataHubFacade.returnImportResult(ctx.getResultCallbackUrl(), result);
	}

	/**
	 * Injects <code>ImportService</code> implementation to be used by this facade.
	 *
	 * @param impl implementation of the <code>ImportService</code> to use.
	 */
	@Required
	public void setImportService(final ImportService impl)
	{
		importService = impl;
	}

	/**
	 * Injects implementation of the <code>ImportResultConverter</code>
	 *
	 * @param converter a converter to use for changing import service result to this facade's result.
	 */
	@Required
	public void setResultConverter(final ImportResultConverter converter)
	{
		resultConverter = converter;
	}

	/**
	 * Injects implementation of <code>DataHubFacade</code>
	 *
	 * @param dataHubFacade The dataHubFacade.
	 */
	@Required
	public void setDataHubFacade(final DataHubFacade dataHubFacade)
	{
		this.dataHubFacade = dataHubFacade;
	}

	DataHubFacade getDataHubFacade()
	{
		return dataHubFacade;
	}

	/**
	 * Injects event service used by the system.
	 *
	 * @param eventService event service implementation to use.
	 */
	@Required
	public final void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	/**
	 * Injects the maximum number of errors that will be reported back to Data Hub in a single publication.
	 * @param errorLimit The error limit.
	 */
	@Required
	public void setErrorLimit(final int errorLimit)
	{
		this.errorLimit = errorLimit;
	}

	/**
	 * Injects the ImpEx import config strategy used
	 * @param importConfigStrategy the import config strategy to be used.
	 */
	@Required
	public void setImportConfigStrategy(final ImportConfigStrategy importConfigStrategy)
	{
		this.importConfigStrategy = importConfigStrategy;
	}
}
