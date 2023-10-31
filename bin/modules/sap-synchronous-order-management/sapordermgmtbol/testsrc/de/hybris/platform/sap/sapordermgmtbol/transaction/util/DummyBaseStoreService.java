/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.util;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.util.List;

/**
 *	Dummy implementation of BaseStoreService
 */
public class DummyBaseStoreService implements BaseStoreService
{

	@Override
	public List<BaseStoreModel> getAllBaseStores()
	{
		return null;
	}

	@Override
	public BaseStoreModel getBaseStoreForUid(final String arg0) throws AmbiguousIdentifierException, UnknownIdentifierException
	{
		return null;
	}

	@Override
	public BaseStoreModel getCurrentBaseStore()
	{
		return null;
	}

}
