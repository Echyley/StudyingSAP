/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.ShipTo;


/**
 * Default implementation of a ShipTo
 *
 */
public class ShipToImpl extends PartnerBaseImpl implements ShipTo
{

	private static final long serialVersionUID = 1L;

	@Override
	@SuppressWarnings("squid:S2975")
	public ShipToImpl clone()
	{
		final ShipToImpl partnerToClone = (ShipToImpl) super.clone();
		return partnerToClone;
	}
}