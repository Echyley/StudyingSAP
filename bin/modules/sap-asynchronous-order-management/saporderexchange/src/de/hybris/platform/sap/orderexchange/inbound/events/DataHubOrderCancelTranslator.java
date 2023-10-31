/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.inbound.events;


import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundOrderHelper;



/**
 * This class includes the translator for order confirmation
 */
public class DataHubOrderCancelTranslator extends DataHubTranslator<DataHubInboundOrderHelper>
{
	
	public static final String HELPER_BEAN = "sapDataHubInboundOrderHelper";
	
	
	public DataHubOrderCancelTranslator() {
		super(HELPER_BEAN);
	}
	
	@Override
	public void performImport(final String orderInformation, final Item processedItem) throws ImpExException
	{
		final String orderCode = getOrderCode(processedItem);

		getInboundHelper().cancelOrder(orderInformation, orderCode);
	}

	private String getOrderCode(final Item processedItem) throws ImpExException
	{
		String orderCode = null;

		try
		{
			orderCode = processedItem.getAttribute(DataHubInboundConstants.CODE).toString();
		}
		catch (final JaloSecurityException e)
		{
			throw new ImpExException(e);
		}
		return orderCode;
	}
}
