/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtservices.impl;

import de.hybris.platform.sap.sapordermgmtservices.BackendAvailabilityService;
import de.hybris.platform.sap.sapordermgmtservices.bolfacade.BolCartFacade;


/**
 * Allows to access availability of the backend
 * 
 */
public class DefaultBackendAvailabilityService implements BackendAvailabilityService
{

	BolCartFacade bolCartFacade;
	private boolean backendDownForUnitTests;
	private boolean backendStateSetFromOutside;

	@Override
	public boolean isBackendDown()
	{
		if (backendStateSetFromOutside)
		{
			return backendDownForUnitTests;
		}
		return bolCartFacade.isBackendDown();
	}

	/**
	 * @return the bolCartFacade
	 */
	protected BolCartFacade getBolCartFacade()
	{
		return bolCartFacade;
	}

	/**
	 * @param bolCartFacade
	 *           the bolCartFacade to set
	 */
	public void setBolCartFacade(final BolCartFacade bolCartFacade)
	{
		this.bolCartFacade = bolCartFacade;
	}

	/**
	 * Just for unit test purposes
	 * 
	 * @param b
	 */
	public void setBackendDown(final boolean b)
	{
		this.backendStateSetFromOutside = true;
		this.backendDownForUnitTests = b;

	}


}
