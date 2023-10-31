/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import com.sap.retail.sapppspricing.swagger.businessobject.dto.LineItemDomainSpecific;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculate;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.RetailPriceModifierDomainSpecific;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.SaleBase;


public interface RequestHelper {
	/**
	 * Create sale line item skeleton to be added to a price calculation
	 * request. Useful for local clients
	 *
	 * @param sequenceNumber
	 *            Sequence number of the line item
	 * @param itemIdentifier
	 *            Item ID of the line item
	 * @param uomCode
	 *            Unit of measure code of the line item
	 * @param quantity
	 *            Quantity of the line item
	 * @return line item skeleton. Can be enhanced by additional information,
	 *         e.g. an externally set base price
	 */
   	LineItemDomainSpecific createSaleLineItem(int sequenceNumber,
			String itemIdentifier, String uomCode, BigDecimal quantity);

	/**
	 * Create skeleton for a price calculation request. Useful for local clients
	 *
	 * @param businessUnitId
	 *            Business unit ID for which calculation shall take place
	 * @param priceDate
	 *            Date for which price calculation shall take place
	 * @return request skeleton
	 */
	PriceCalculate createCalculateRequestSkeleton(String businessUnitId,
			GregorianCalendar priceDate);

	/**
	 * Retrieve item content in case this is a real line item of the basket
	 * (i.e. e.g. no header discount, no coupon, no loyalty reward)
	 *
	 * @param lineItem
	 *            Line item to extract the information from
	 * @return {@link SaleBase}
	 */
	SaleBase getLineItemContent(LineItemDomainSpecific lineItem);


	/**
	 * Checks whether the given {@link RetailPriceModifierDomainSpecific} is
	 * specific to the line item it is attached to or whether is broken down
	 * from the whole shopping basket
	 *
	 * @param priceModifier
	 * @return true if this is a distributed price modifier
	 */
	boolean isDistributed(RetailPriceModifierDomainSpecific priceModifier);

}
