/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.extensibility.request;


/**
 * Thread local container used for storing the Request Context.
 */
public class RequestContextContainer extends ThreadLocal<RequestContext>
{

	private static RequestContextContainer instance = new RequestContextContainer();

	/**
	 * Gets the RequestContextContainer instance.
	 * 
	 * @return RequestContextContainer
	 */
	public static RequestContextContainer getInstance()
	{
		return instance;
	}

	@Override
	protected RequestContext initialValue()
	{
		return new RequestContext();
	}

	/**
	 * Returns a request context. Scope is the current thread
	 * 
	 * @return a request context
	 */
	public RequestContext getRequestContext()
	{
		return get();
	}
}
