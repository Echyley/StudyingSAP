/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.model.impl;

import de.hybris.platform.sap.productconfig.model.intf.DataLoaderManagerContainer;

import com.sap.custdev.projects.fbs.slc.dataloader.standalone.manager.DataloaderManager;


/**
 * Spring managed wrapper for {@link DataloaderManager}
 */
public class DataLoaderManagerContainerImpl implements DataLoaderManagerContainer
{

	private DataloaderManager dataLoaderManager;
	private boolean resumePerformed;


	@Override
	public DataloaderManager getDataLoaderManager()
	{
		return dataLoaderManager;
	}

	@Override
	public void setDataLoaderManager(final DataloaderManager manager)
	{
		this.dataLoaderManager = manager;

	}

	@Override
	public void setResumePerformed(final boolean b)
	{
		resumePerformed = b;

	}

	@Override
	public boolean isResumePerformed()
	{
		return resumePerformed;
	}

}
