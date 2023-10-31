/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.impl.erp;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.sapordermgmtbol.transaction.order.backend.interf.OrderHistoryBackend;


/**
 * Back end Object representing an persisted ERP Order document used in order history (different connection compared to
 * checkout)
 */
@BackendType("ERP")
public class OrderHistoryERP extends OrderBaseERP implements OrderHistoryBackend
{
	//
}
