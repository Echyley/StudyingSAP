/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.cronjob;

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.sap.productconfig.model.enums.DataloadStatus;
import de.hybris.platform.sap.productconfig.model.model.CPQDataloadStatusModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.log4j.Logger;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderProgressListener;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderQueuesInfo;
import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.Progress;


/**
 * Listens to data loader progress and persists the data
 */
public class DefaultDataloaderProgressListenerImpl implements DataloaderProgressListener
{
	private static final Logger LOG = Logger.getLogger(DefaultDataloaderProgressListenerImpl.class);
	public static final int DEFAULT_SCALE_DATA_RECEIVED = 3;
	private SAPConfigurationModel sapConfiguration;

	private ModelService modelService;


	@Override
	public void progressReported(final Progress progress)
	{
		if (sapConfiguration == null)
		{
			throw new IllegalStateException("We need an instance of SAPConfiguration to proceed");
		}

		final double totalDataReceivedInMegabytes = progress.getTotalDataReceivedInMegabytes();
		BigDecimal totalDataReceivedInMegabytesAsBigDecimal = BigDecimal.valueOf(totalDataReceivedInMegabytes);
		totalDataReceivedInMegabytesAsBigDecimal = totalDataReceivedInMegabytesAsBigDecimal.setScale(DEFAULT_SCALE_DATA_RECEIVED, RoundingMode.HALF_UP);


		final DataloaderQueuesInfo dataloaderQueuesInfo = progress.getDataloaderQueuesInfo();
		if (LOG.isDebugEnabled())
		{
			LOG.debug("Progress MB: " + totalDataReceivedInMegabytes);
			LOG.debug("Progress Rate: " + progress.getProcessingRatePerHourInMegabytes());
			LOG.debug("Progress Queue Info: " + dataloaderQueuesInfo);
		}

		final CPQDataloadStatusModel datloadStatus = sapConfiguration.getSapproductconfig_cpqDataloadStatus();



		if (datloadStatus.getCpqDataloadStatus() == DataloadStatus.INITIAL_LOAD)
		{
			datloadStatus.setCpqCurrentInitialLoadTransferredVolume(totalDataReceivedInMegabytesAsBigDecimal);
		}
		else
		{
			datloadStatus.setCpqCurrentDeltaLoadTransferredVolume(totalDataReceivedInMegabytesAsBigDecimal);
			final int entriesInAllQueues = dataloaderQueuesInfo.getEntriesInConditionQueue() + //
					dataloaderQueuesInfo.getEntriesInCustomizingQueue() + //
					dataloaderQueuesInfo.getEntriesInKbQueue() + //
					dataloaderQueuesInfo.getEntriesInMaterialQueue();
			datloadStatus.setCpqNumberOfEntriesInDeltaLoadQueue(Integer.valueOf(entriesInAllQueues));
		}

		getModelService().save(datloadStatus);
	}

	/**
	 * @return the sapConfiguration
	 */
	public SAPConfigurationModel getSapConfiguration()
	{
		return sapConfiguration;
	}

	/**
	 * @param sapConfiguration
	 *           the sapConfiguration to set
	 */
	public void setSapConfiguration(final SAPConfigurationModel sapConfiguration)
	{
		this.sapConfiguration = sapConfiguration;
	}

	/**
	 * @param modelService
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}
}
