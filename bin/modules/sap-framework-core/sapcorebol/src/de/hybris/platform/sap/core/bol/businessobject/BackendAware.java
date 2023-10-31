/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.businessobject;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Allows implementing object interaction with the backend using the backend manager. <br>
 * Implementing this interface tells the BusinessObjectManager to give the object a reference to the
 * BackendObjectManager. You need this reference to call methods of the back end logic (BE).
 */
public interface BackendAware
{

	/**
	 * Returns the backend business object.
	 * 
	 * @return backend business object
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public BackendBusinessObject getBackendBusinessObject() throws BackendException;

	/**
	 * Returns the backend business object.
	 * 
	 * @param initialize
	 *           if true, backend object is re-created
	 * @return backend business object
	 * @throws BackendException
	 *            {@link BackendException}
	 */
	public BackendBusinessObject getBackendBusinessObject(boolean initialize) throws BackendException;

}
