/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.odata.util;

import java.net.URI;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.callback.OnWriteFeedContent;
import org.apache.olingo.odata2.api.ep.callback.WriteCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackContext;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackResult;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;


/**
 * @deprecated Since 6.4, replace with extension sapymktcommon
 */
@Deprecated(since= "6.4", forRemoval = true)
public class MyCallback implements OnWriteFeedContent
{
	protected DataStore dataStore;
	protected URI serviceRoot;

	@Override
	public WriteFeedCallbackResult retrieveFeedResult(final WriteFeedCallbackContext context) throws ODataApplicationException
	{
		return null;
	}

	public boolean isNavigationFromTo(final WriteCallbackContext context, final String entitySetName,
			final String navigationPropertyName) throws EdmException
	{
		return true;
	}

	public DataStore getDataStore()
	{
		return dataStore;
	}

	public URI getServiceRoot()
	{
		return serviceRoot;
	}

	public void setDataStore(DataStore dataStore)
	{
		this.dataStore = dataStore;
	}

	public void setServiceRoot(URI serviceRoot)
	{
		this.serviceRoot = serviceRoot;
	}
}
