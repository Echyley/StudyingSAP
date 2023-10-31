/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.intf;

import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;


/**
 * Responsible to call SSC for initial and delta load
 */
public interface DataLoader
{

	/**
	 * Performs an initial dataload, so that all previous data will be erased.
	 *
	 * @param sapConfiguration
	 *           sap configuration containing the backend configuration
	 * @param dataloaderManager
	 *           delta load manager
	 * @param modelService
	 *           used to propagate download status information
	 */
	void performInitialLoad(SAPConfigurationModel sapConfiguration, DataloaderManager dataloaderManager, ModelService modelService);

	/**
	 * Performs an delta load, so that only the data changed since the last initial load is transfered.
	 *
	 * @param sapConfiguration
	 *           sap configuration containing the backend configuration
	 * @param dataloaderManager
	 *           delta load manager
	 * @param modelService
	 *           used to propagate download status information
	 */
	void performDeltaLoad(SAPConfigurationModel sapConfiguration, DataloaderManager dataloaderManager, ModelService modelService);


}
