/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.impl.erp;

import de.hybris.platform.sap.core.bol.backend.BackendBusinessObjectBase;
import de.hybris.platform.sap.sapordermgmtbol.transaction.salesdocument.backend.interf.erp.AdditionalPricing;


/**
 * Implementation of {@link AdditionalPricing}. <br>
 * 
 * @version 1.0
 */
public class AdditionalPricingImpl extends BackendBusinessObjectBase implements AdditionalPricing
{

	private boolean pricingCallCart;
	private boolean pricingCallOrder;
	private String priceType;


	/**
	 * @param isPricingCallCart
	 *           the isPricingCallCart to set
	 */
	public void setPricingCallCart(final boolean isPricingCallCart)
	{
		this.pricingCallCart = isPricingCallCart;
	}


	/**
	 * @param isPricingCallOrder
	 *           the isPricingCallOrder to set
	 */
	public void setPricingCallOrder(final boolean isPricingCallOrder)
	{
		this.pricingCallOrder = isPricingCallOrder;
	}

	/**
	 * @param priceType
	 *           the priceType to set
	 */
	public void setPriceType(final String priceType)
	{
		this.priceType = priceType;
	}

	@Override
	public String getPriceType()
	{
		return priceType;
	}

	@Override
	public boolean isPricingCallCart()
	{
		return pricingCallCart;
	}

	@Override
	public boolean isPricingCallOrder()
	{
		return pricingCallOrder;
	}



}
