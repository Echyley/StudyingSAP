/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saporderexchangeoms.datahub.inbound;

/**
 * OMS Data Hub Inbound Helper for Delivery and PGI related notifications
 */
public interface SapDataHubInboundHelper
{
	/**
	 * @param orderCode
	 * @param entryNumber
	 */
	void processDeliveryNotification(String orderCode, String entryNumber);

	/**
	 * @param orderCode
	 * @param entryNumber
	 * @param goodsIssueDate
	 */
	void processGoodsIssueNotification(String orderCode, String entryNumber, String quantity, String goodsIssueDate);

	/**
	 * @param deliveryInfo
	 * @return Goods issue date
	 */
	String determineGoodsIssueDate(String deliveryInfo);

	/**
	 * @param deliveryInfo
	 * @return EntryNumber
	 */
	String determineEntryNumber(String deliveryInfo);

	/**
	 * @param deliveryInfo
	 * @return Quantity
	 */
	String determineQuantity(String deliveryInfo);

}
