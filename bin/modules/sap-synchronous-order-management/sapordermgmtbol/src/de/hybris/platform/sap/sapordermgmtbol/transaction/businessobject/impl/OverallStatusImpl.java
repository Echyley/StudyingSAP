/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.OverallStatus;


/**
 * Represents the OverallStatus object.
 * 
 * @see OverallStatus
 */
public abstract class OverallStatusImpl extends BusinessStatusImpl implements OverallStatus
{

	/**
	 * Default constructor
	 */
	public OverallStatusImpl()
	{
		super();
	}

	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 * 
	 * @param key
	 *           status key
	 */
	public OverallStatusImpl(final EStatus key)
	{
		super(key);
	}

	@Override
	public boolean isNotRelevant()
	{
		return this.wecStatus == EStatus.NOT_RELEVANT;
	}

	@Override
	public boolean isNotProcessed()
	{
		return this.wecStatus == EStatus.NOT_PROCESSED;
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
