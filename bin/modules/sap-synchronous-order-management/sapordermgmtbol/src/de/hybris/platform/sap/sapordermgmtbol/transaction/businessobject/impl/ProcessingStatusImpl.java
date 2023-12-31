/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ProcessingStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;


/**
 * Represents the ShippingStatus object.
 *
 * @see ShippingStatus
 */
public class ProcessingStatusImpl extends BusinessStatusImpl implements ProcessingStatus
{

	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 *
	 * @param key
	 *           status key
	 */
	public ProcessingStatusImpl(final EStatus key)
	{
		super(key);
	}

	/**
	 * Default constructor
	 */
	public ProcessingStatusImpl()
	{
		super();
	}

	@Override
	@SuppressWarnings("squid:S2975")
	public Object clone()
	{
		// class only contains immutable objects, so super clone is sufficient
		return super.clone();
	}

	@Override
	public boolean isNotProcessed()
	{
		return this.wecStatus == EStatus.NOT_PROCESSED;
	}

	@Override
	public boolean isNotRelevant()
	{
		return this.wecStatus == EStatus.NOT_RELEVANT;
	}

	@Override
	public boolean isPartiallyProcessed()
	{
		return this.wecStatus == EStatus.PARTIALLY_PROCESSED;
	}

	@Override
	public boolean isProcessed()
	{
		return this.wecStatus == EStatus.PROCESSED;
	}

}
