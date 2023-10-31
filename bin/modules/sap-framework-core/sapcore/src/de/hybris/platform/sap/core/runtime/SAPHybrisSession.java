/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.runtime;


import de.hybris.platform.sap.core.common.util.GenericFactoryProvider;

import java.io.Serializable;

/**
 * SAP hybris Session container which keeps the session relevant information for the SAP integration.
 */
public class SAPHybrisSession implements Serializable
{

	private static final long serialVersionUID = 1L;
	private transient SessionObjectFactory sessionObjectFactory;
	private String applicationName;
	private String sessionId;

	/**
	 * Default constructor.
	 */
	public SAPHybrisSession()
	{
		super();
	}

	/**
	 * Gets the session id.
	 * 
	 * @return the sessionId
	 */
	public String getSessionId()
	{
		return sessionId;
	}

	/**
	 * Sets the session id.
	 * 
	 * @param sessionId
	 *           the sessionId to set
	 */
	public void setSessionId(final String sessionId)
	{
		this.sessionId = sessionId;
	}

	/**
	 * Sets the session object factory.
	 * 
	 * @param sessionObjectFactory
	 *           Session object factory
	 */
	public void setSessionObjectFactory(final SessionObjectFactory sessionObjectFactory)
	{
		this.sessionObjectFactory = sessionObjectFactory;
	}

	/**
	 * Gets the session object factory.
	 * 
	 * @return Session object factory
	 */
	public SessionObjectFactory getSessionObjectFactory()
	{
		if (sessionObjectFactory == null){
			return GenericFactoryProvider.getInstance().getBean(SessionObjectFactory.class);
		}
		return sessionObjectFactory;
	}

	/**
	 * Gets the application name.
	 * 
	 * @return Application name
	 */
	public String getApplicationName()
	{
		return applicationName;
	}

	/**
	 * Sets the application name.
	 * 
	 * @param applicationName
	 *           Application name to set
	 */
	public void setApplicationName(final String applicationName)
	{
		this.applicationName = applicationName;
	}

	/**
	 * Frees up session relevant information.
	 */
	public void destroy()
	{
		getSessionObjectFactory().destroy();
	}

}
