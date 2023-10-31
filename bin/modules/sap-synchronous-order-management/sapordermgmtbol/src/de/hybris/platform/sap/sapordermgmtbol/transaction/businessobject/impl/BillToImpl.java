/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.impl;

import de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf.BillTo;


/**
 * Bill To Partner information. <br>
 *
 */
public class BillToImpl extends PartnerBaseImpl implements BillTo
{

	private static final long serialVersionUID = 1L;

	@Override
	@SuppressWarnings("squid:S2975")
	public BillToImpl clone()
	{
		return (BillToImpl) super.clone();
	}

}