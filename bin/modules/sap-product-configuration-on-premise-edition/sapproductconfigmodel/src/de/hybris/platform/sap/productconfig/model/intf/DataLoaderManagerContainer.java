/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.intf;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;


/**
 * Spring managed wrapper for {@link DataloaderManager}
 */
public interface DataLoaderManagerContainer
{

	/**
	 * @param manager
	 *           SSC Data Loader Manager
	 */
	void setDataLoaderManager(DataloaderManager manager);

	/**
	 * @return SSC Data Loader Manager
	 */
	DataloaderManager getDataLoaderManager();

	/**
	 * Resume was triggered
	 *
	 * @param b
	 */
	void setResumePerformed(boolean b);

	/**
	 * @return Resume was triggered
	 */
	boolean isResumePerformed();

}
