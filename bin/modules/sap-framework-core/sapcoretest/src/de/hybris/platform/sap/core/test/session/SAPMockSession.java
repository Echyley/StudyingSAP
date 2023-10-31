/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.test.session;

import de.hybris.platform.servicelayer.session.impl.DefaultSession;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Copy of MockSession enhanced by removeAttribute method.
 */
public class SAPMockSession extends DefaultSession
{
	private static final long serialVersionUID = 3949657850392397912L;

	private long sessionIdCounter = 1;

	private final transient Map<String, Object> attributes = new ConcurrentHashMap<String, Object>();
	private final String sessionId;

	/**
	 * Standard constructor.
	 */
	public SAPMockSession()
	{
		super();
		this.sessionId = String.valueOf(sessionIdCounter++);
	}

	@Override
	public String getSessionId()
	{
		return this.sessionId;
	}

	@Override
	public <T> Map<String, T> getAllAttributes()
	{
		return (Map<String, T>) Collections.unmodifiableMap(attributes);
	}

	@Override
	public <T> T getAttribute(final String name)
	{
		return (T) attributes.get(name);
	}


	@Override
	public void setAttribute(final String name, final Object value)
	{
		attributes.put(name, value);
	}

	@Override
	public void removeAttribute(final String name)
	{
		attributes.remove(name);
	}

}
