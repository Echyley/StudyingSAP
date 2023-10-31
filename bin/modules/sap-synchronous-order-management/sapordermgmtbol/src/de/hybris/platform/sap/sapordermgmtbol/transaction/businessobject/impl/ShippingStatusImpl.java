/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.core.common.util.GenericFactory;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.EStatus;
import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShippingStatus;


/**
 * Represents the ShippingStatus object.
 *
 * @see ShippingStatus
 */
public class ShippingStatusImpl extends BusinessStatusImpl implements ShippingStatus
{
	private EStatus giStatus;

	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 *
	 * @param key
	 *           status key
	 */
	public ShippingStatusImpl(final EStatus key)
	{
		super(key);
	}

	/**
	 * Default constructor
	 */
	public ShippingStatusImpl()
	{
		super();
	}

	/**
	 * Only for unit tests. Other callers need to instantiate via {@link GenericFactory} and call init method}.
	 *
	 * @param dlvStatus
	 *           delivery status
	 * @param giStatus
	 *           gi status
	 * @param rjStatus
	 *           rejection status
	 */
	@SuppressWarnings("squid:S1699")
	public ShippingStatusImpl(final EStatus dlvStatus, final EStatus giStatus, final EStatus rjStatus)
	{
		super(dlvStatus, rjStatus);
		this.giStatus = giStatus;
		this.determineStatus();
	}

	@Override
	public boolean isNotRelevant()
	{
		return this.dlvStatus == EStatus.NOT_RELEVANT || this.rjStatus == EStatus.PROCESSED;
	}

	@SuppressWarnings("squid:S1067")
	@Override
	public boolean isNotProcessed()
	{
		return (this.dlvStatus == EStatus.NOT_PROCESSED || this.giStatus == EStatus.NOT_PROCESSED)
				|| (this.dlvStatus == null && this.giStatus == null && this.wecStatus == EStatus.NOT_PROCESSED);
	}

	@Override
	public boolean isPartiallyProcessed()
	{
		return this.giStatus == EStatus.PARTIALLY_PROCESSED
				|| (this.giStatus == EStatus.PROCESSED && this.dlvStatus == EStatus.PARTIALLY_PROCESSED);
	}

	@Override
	public boolean isProcessed()
	{
		return this.giStatus == EStatus.PROCESSED && this.dlvStatus == EStatus.PROCESSED;
	}

	@Override
	@SuppressWarnings("squid:S2975")
	public Object clone()
	{
		// class only contains immutable objects, so super clone is sufficient
		return super.clone();
	}

	@Override
	public void init(final EStatus dlvStatus, final EStatus giStatus, final EStatus rjStatus)
	{
		super.init(dlvStatus, rjStatus);
		this.giStatus = giStatus;
		this.determineStatus();

	}

}
