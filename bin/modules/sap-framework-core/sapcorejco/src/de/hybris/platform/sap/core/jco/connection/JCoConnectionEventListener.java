/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.connection;

import de.hybris.platform.sap.core.jco.connection.impl.ConnectionEventListener;


/**
 * This interface should be implemented by a class which wants to be notified about JCo Calls to the SAP system.
 */
public interface JCoConnectionEventListener extends ConnectionEventListener
{
	/**
	 * Method is called when a connection event occurs.
	 * 
	 * @param event
	 *           Event
	 */
	public void connectionEvent(JCoConnectionEvent event);
}
