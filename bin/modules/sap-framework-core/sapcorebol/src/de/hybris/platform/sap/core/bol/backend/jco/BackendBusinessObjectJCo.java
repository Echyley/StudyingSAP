/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.bol.backend.jco;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObject;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * This interface should be implemented by Backend Business Object which communicate with an SAP System using Java
 * Connector of SAP.
 */
public interface BackendBusinessObjectJCo extends BackendBusinessObject
{

	/**
	 * Returns the requested default JCo connection with the default connection name and the RFC connection defined in
	 * the SAP Base Store configuration.
	 * 
	 * @return JCo client connection to an SAP system
	 * @throws BackendException
	 *            Backend Exception
	 */
	public JCoConnection getDefaultJCoConnection() throws BackendException;

	/**
	 * Returns the requested JCo connection.
	 * 
	 * @param connectionName
	 *           name of the connection as defined in sapcorejco-spring.xml (e.g. JCoConnectionStateless)
	 * @param destinationName
	 *           name of the RFC destination to be used
	 * @return JCo client connection to an SAP system
	 * @throws BackendException
	 *            Backend Exception
	 */
	public JCoConnection getJCoConnection(String connectionName, String destinationName) throws BackendException;

	/**
	 * Returns the requested JCo connection for a given scope.
	 * 
	 * @param connectionName
	 *           name of the connection as defined in sapcorejco-spring.xml (e.g. JCoConnectionStateless)
	 * @param destinationName
	 *           name of the RFC destination to be used
	 * @param scopeId
	 *           scope id which is needed to identify different connections (sessions) to the same destination
	 * @return JCo client connection to an SAP system
	 * @throws BackendException
	 *            Backend Exception
	 */
	public JCoConnection getJCoConnection(String connectionName, String destinationName, String scopeId) throws BackendException;

	/**
	 * Sets the defaultConnectionName.
	 * 
	 * @param defaultConnectionName
	 *           default connection name to be set
	 */
	public void setDefaultConnectionName(String defaultConnectionName);

	/**
	 * Returns the defaultConnectionName.
	 * 
	 * @return default connection name
	 */
	public String getDefaultConnectionName();

	/**
	 * Sets the destinationName (optional).
	 * 
	 * @param destinationName
	 *           destination name to be used for JCo
	 */
	public void setDefaultDestinationName(String destinationName);

	/**
	 * Returns the destinationName.
	 * 
	 * @return destinationName to be used for JCo
	 */
	public String getDefaultDestinationName();

}
