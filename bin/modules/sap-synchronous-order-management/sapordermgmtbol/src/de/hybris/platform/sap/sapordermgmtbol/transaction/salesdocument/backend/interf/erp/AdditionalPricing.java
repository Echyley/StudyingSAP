/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp;

/**
 * Triggers explicit pricing calls to SD. Is called from ERP cart or order backend objects. <br>
 *
 * @version 1.0
 */
public interface AdditionalPricing
{

	/**
	 * For identifying the implementation in backendobject-config.xml.
	 */
	String TYPE = "AdditionalPricing";

	/**
	 * Do we do an additional pricing call in cart?<br>
	 * 
	 * @return Additional call required
	 */
	boolean isPricingCallCart();

	/**
	 * Do we do an additional pricing call in order?<br>
	 * 
	 * @return Additional call required
	 */
	boolean isPricingCallOrder();

	/**
	 * Returns price type for additional SD pricing calls. Example: <br>
	 * 'H' for freight redetermination <br>
	 * 'B' carry out new pricing <br>
	 * 
	 * @return Price type as in domain KNPRS in SD
	 */
	String getPriceType();

}
