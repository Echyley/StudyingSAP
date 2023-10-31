/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.messagemapping;

import de.hybris.platform.sap.core.bol.cache.exceptions.SAPHybrisCacheException;
import de.hybris.platform.sap.core.bol.cache.impl.CacheAccessImpl;


@SuppressWarnings("javadoc")
public class MockCacheAccess extends CacheAccessImpl
{
	public MockCacheAccess()
	{
		super("dummuCaAcheAcces", 10000);
	}

	Object obj;

	@Override
	public Object get(final Object key)
	{
		return obj;
	}

	@Override
	public void put(final Object key, final Object object) throws SAPHybrisCacheException
	{
		obj = object;
	}
}
