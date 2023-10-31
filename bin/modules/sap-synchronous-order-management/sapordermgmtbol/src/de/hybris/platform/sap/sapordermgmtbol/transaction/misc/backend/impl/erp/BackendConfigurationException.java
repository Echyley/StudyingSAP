/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.erp;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Configuration related Backend layer exception
 */
@SuppressWarnings("squid:S2166")
public class BackendConfigurationException extends BackendException
{

	private static final long serialVersionUID = 5775612108542388947L;

	/**
	 * @param msg
	 *           Error message
	 */
	public BackendConfigurationException(final String msg)
	{
		super(msg);
	}

}
