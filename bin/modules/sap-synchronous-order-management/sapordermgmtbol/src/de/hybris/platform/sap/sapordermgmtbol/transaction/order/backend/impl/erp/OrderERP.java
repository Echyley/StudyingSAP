/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.impl.erp;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderBackend;


/**
 * Back end Object representing an ERP Order document used in checkout (different connection compared to history orders)
 */
@BackendType("ERP")
public class OrderERP extends OrderBaseERP implements OrderBackend
{
	//
}
