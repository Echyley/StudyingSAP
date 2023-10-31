/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.datahub.inbound;


/**
 * Data Hub Inbound Helper for Delivery related notifications
 */
public interface DataHubInboundDeliveryHelper
{

	/**
	 * @param orderCode
	 * @param warehouseId
	 * @param goodsIssueDate
	 */
	void processDeliveryAndGoodsIssue(String orderCode, String warehouseId, String goodsIssueDate);

	/**
	 * @param deliveryInfo
	 * @return warehouse ID based on provided delivery information from Data Hub
	 */
	String determineWarehouseId(String deliveryInfo);

	/**
	 * @param deliveryInfo
	 * @return goods issue date based on provided delivery information from Data Hub
	 */
	String determineGoodsIssueDate(String deliveryInfo);
	

}
