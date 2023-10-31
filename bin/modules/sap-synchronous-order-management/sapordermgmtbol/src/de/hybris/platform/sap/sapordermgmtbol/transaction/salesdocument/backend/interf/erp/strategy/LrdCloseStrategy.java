/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.strategy;

import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * Closes a LO-API session in the back end
 * 
 */
public interface LrdCloseStrategy
{

	/**
	 * Closes a LO-API session to release the attached resources (reservations...)
	 * 
	 * @param connection
	 * @throws BackendException
	 *            Issue when releasing LO-API (e.g. still loaded)
	 */
	void close(JCoConnection connection) throws BackendException;

}
