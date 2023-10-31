/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.returnsexchange.inbound.events;


import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;


/**
 *
 */
public class DataHubReturnOrderCreationTranslator extends DefaultSpecialValueTranslator
{

	@Override
	public void performImport(final String orderInfo, final Item processedItem) throws ImpExException
	{
		final String orderCode = getOrderCode(processedItem);

		getInboundHelper().processOrderConfirmationFromDataHub(orderCode);
	}
	


}