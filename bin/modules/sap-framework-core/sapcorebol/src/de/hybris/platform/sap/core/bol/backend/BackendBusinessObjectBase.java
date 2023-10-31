/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.backend;

import de.hybris.platform.sap.core.bol.businessobject.BORuntimeException;
import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.core.constants.SapcoreConstants;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.module.ModuleConfigurationAccess;


/**
 * Base implementation of the BackendBusinessObject interface.
 */
public abstract class BackendBusinessObjectBase implements BackendBusinessObject
{

	/**
	 * Generic factory to create data container beans.
	 */
	protected GenericFactory genericFactory; //NOPMD

	/**
	 * {@link ModuleConfigurationAccess} interface to access configuration data.
	 */
	protected ModuleConfigurationAccess moduleConfigurationAccess = null; //NOPMD

	/**
	 * Sets the module configuration access.
	 * 
	 * @param moduleConfigurationAccess
	 *           the module configuration access
	 */
	public void setModuleConfigurationAccess(final ModuleConfigurationAccess moduleConfigurationAccess)
	{
		this.moduleConfigurationAccess = moduleConfigurationAccess;
	}

	/**
	 * Returns the module configuration access.
	 * 
	 * @return the moduleConfigurationAccess
	 */
	protected ModuleConfigurationAccess getModuleConfigurationAccess()
	{
		if (moduleConfigurationAccess == null)
		{
			throw new BORuntimeException("No module configuration access available! (missing injection?)");
		}
		return moduleConfigurationAccess;
	}

	@Override
	public void initBackendObject() throws BackendException
	{
	}


	@Override
	public void destroyBackendObject()
	{

	}

	/**
	 * Sets the generic factory.
	 * 
	 * @param genericFactory
	 *           the generic factory
	 */
	public void setGenericFactory(final GenericFactory genericFactory)
	{
		this.genericFactory = genericFactory;
	}


	@Override
	public String toString()
	{
		return super.toString() + SapcoreConstants.CRLF + "- ModuleConfigurationAccess: " + moduleConfigurationAccess;
	}

}
