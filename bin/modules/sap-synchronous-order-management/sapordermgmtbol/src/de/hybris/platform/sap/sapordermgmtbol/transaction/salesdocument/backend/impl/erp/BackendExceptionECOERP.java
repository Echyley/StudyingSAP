/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;


/**
 * This exception is meant to handle critical errors which must lead to an abortion of the program flow. It should not
 * occur in productive environments (i.e. does not indicate communication or resource issues) but reflects problems with
 * the call of the ECommerce ERP backend layer.
 * 
 */
public class BackendExceptionECOERP extends BackendException
{

	/**
	 * Serial version ID
	 */
	private static final long serialVersionUID = 5173890011152490823L;

	/**
	 * Meant to handle critical errors which must lead to an abortion of the program flow. It should not occur in
	 * productive environments (i.e. does not indicate communication or resource issues) but reflects problems with the
	 * call of the ECommerce ERP backend layer
	 * 
	 * @param msg
	 *           Message for the Exception
	 */
	public BackendExceptionECOERP(final String msg)
	{
		super(msg);
	}

}
