/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.businessobject.interf;

/**
 * Represents the BillTo object
 *
 */
public interface BillTo extends PartnerBase
{

	/**
	 * @see PartnerBase#clone
	 */
	@Override
	@SuppressWarnings("squid:S2975")
	BillTo clone();
}