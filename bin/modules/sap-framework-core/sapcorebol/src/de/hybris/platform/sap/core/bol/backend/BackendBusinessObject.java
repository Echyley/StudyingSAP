/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.backend;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Interface for backend business objects.
 */
public interface BackendBusinessObject
{
	/**
	 * This method is called by the SessionObjectManager to initialize object after all properties have been injected.
	 * 
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public void initBackendObject() throws BackendException;

	/**
	 * This method is called by the Spring framework before the object gets invalidated. It can be used to lean up
	 * resources allocated by the backend object.
	 */
	public void destroyBackendObject();

}
