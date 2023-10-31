/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.inbound.events;


import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.sap.orderexchange.constants.DataHubInboundConstants;
import de.hybris.platform.sap.orderexchange.datahub.inbound.DataHubInboundDeliveryHelper;


/**
 * Translator for Goods Issue process. It updates the consignments and finalized status
 */
public class DataHubGoodsIssueTranslator extends DataHubTranslator<DataHubInboundDeliveryHelper>
{
	
	public static final String HELPER_BEAN = "sapDataHubInboundDeliveryHelper";
	
	
	public DataHubGoodsIssueTranslator() {
		super(HELPER_BEAN);
	}
	
	@Override
	public void performImport(final String delivInfo, final Item processedItem) throws ImpExException
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

		if (delivInfo != null && !delivInfo.equals(DataHubInboundConstants.IGNORE))
		{
			final String goodsIssueDate = getInboundHelper().determineGoodsIssueDate(delivInfo);
			final String warehouseId = getInboundHelper().determineWarehouseId(delivInfo);
			getInboundHelper().processDeliveryAndGoodsIssue(orderCode, warehouseId, goodsIssueDate);
		}
	}
}
