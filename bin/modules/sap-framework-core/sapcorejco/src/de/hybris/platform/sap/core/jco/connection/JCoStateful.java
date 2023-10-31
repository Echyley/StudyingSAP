/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;



/**
 * Interface for marking connections as stateful.
 */
public interface JCoStateful
{
	/**
	 * Gets called if connection should be destroyed.
	 * 
	 * @throws BackendException
	 *            in case of failure.
	 */
	public void destroy() throws BackendException;

}
