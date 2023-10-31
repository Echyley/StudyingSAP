/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.odata.impl;

import de.hybris.platform.odata2services.odata.EdmProviderFactory;
import de.hybris.platform.odata2services.odata.processor.ODataProcessorFactory;

import org.apache.olingo.odata2.api.ODataCallback;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.ODataServiceFactory;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataErrorCallback;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.springframework.beans.factory.annotation.Required;

public class DefaultServiceFactory extends ODataServiceFactory
{
	private EdmProviderFactory edmProviderFactory;
	private ODataProcessorFactory processorFactory;
	private ODataErrorCallback errorCallback;

	@Override
	public ODataService createService(final ODataContext context)
	{
		final ODataSingleProcessor processor = processorFactory.createProcessor(context);
		return createODataSingleProcessorService(edmProviderFactory.createInstance(context), processor);
	}

	@Override
	public <T extends ODataCallback> T getCallback(final Class<T> callbackInterface)
	{
		if (callbackInterface.isAssignableFrom(getErrorCallback().getClass()))
		{
			return callbackInterface.cast(errorCallback);
		}
		return super.getCallback(callbackInterface);
	}

	protected EdmProviderFactory getEdmProviderFactory()
	{
		return edmProviderFactory;
	}

	@Required
	public void setEdmProviderFactory(final EdmProviderFactory edmProviderFactory)
	{
		this.edmProviderFactory = edmProviderFactory;
	}

	protected ODataProcessorFactory getProcessorFactory()
	{
		return processorFactory;
	}

	@Required
	public void setProcessorFactory(final ODataProcessorFactory factory)
	{
		processorFactory = factory;
	}

	protected ODataErrorCallback getErrorCallback()
	{
		return errorCallback;
	}

	@Required
	public void setErrorCallback(final ODataErrorCallback callback)
	{
		errorCallback = callback;
	}
}
