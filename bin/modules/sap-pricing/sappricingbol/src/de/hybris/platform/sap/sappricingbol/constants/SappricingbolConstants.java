/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricingbol.constants;

/**
 * Global class for all Sappricingbol constants. You can add global constants for your extension into this class.
 */
public final class SappricingbolConstants
{
	
	public static class Attributes
	{
		private Attributes()
		{
			//private constructer to hide public 
		}
		public static class SAPConfiguration
		{
			private SAPConfiguration()
			{
				//private constructer to hide public 
			}
			public static final String SAPPRICINGBOL_CACHEPRICE = "sappricingbol_cachePrice".intern();
			public static final String SAPPRICINGBOL_CARTPRICING = "sappricingbol_cartPricing".intern();
			public static final String SAPPRICINGBOL_CATALOGPRICING = "sappricingbol_catalogPricing".intern();
			public static final String SAPPRICINGBOL_DELIVERYSUB = "sappricingbol_deliverySub".intern();
			public static final String SAPPRICINGBOL_DISCOUNTSUB = "sappricingbol_discountSub".intern();
			public static final String SAPPRICINGBOL_PAYMENTSUB = "sappricingbol_paymentSub".intern();
			public static final String SAPPRICINGBOL_PRICESUB = "sappricingbol_priceSub".intern();
			public static final String SAPPRICINGBOL_TAXESSUB = "sappricingbol_taxesSub".intern();
		}
	}
	
	public static final String EXTENSIONNAME = "sappricingbol";
	public static final String YES = "X"; 
	public static final String NO = "";
	public static final String CALLER_ID = "PL";
	public static final String VBELN = "$1";
	public static final String MGAME = "1";
	public static final String KPOSN = "1";
	public static final String PERCENTAGE_CALCULATION_TYPE = "A";
	//BO Constants
	public static final String SAP_PRICING_BO = "sapPricingBo";

	public static final String CONF_PROP_IS_ACTIVE_SYNCHRONOUS_ORDER_MANAGEMENT = "sapordermgmt_enabled";
	public static final String CONF_PROP_IS_ACTIVE_CATALOG_PRICING = "sappricingbol_catalogPricing";
	public static final String CONF_PROP_IS_CACHED_CATALOG_PRICE = "sappricingbol_cachePrice";
	public static final String CONF_PROP_IS_ACTIVE_CART_PRICING = "sappricingbol_cartPricing";
	public static final String CONF_PROP_PRICE_SUBTOTAL = "sappricingbol_priceSub";
	public static final String CONF_PROP_DISCOUNTS_SUBTOTAL = "sappricingbol_discountSub";
	public static final String CONF_PROP_TAXES_SUBTOTAL = "sappricingbol_taxesSub";
	public static final String CONF_PROP_DELIVERY_SUBTOTAL = "sappricingbol_deliverySub";
	public static final String CONF_PROP_PAYMENT_COST_SUBTOTAL = "sappricingbol_paymentSub";


	public static final String CONF_PROP_DELIVERY_MODES = "sapDeliveryModes";
	public static final String CONF_PROP_PAYMENT_MODES = "sapPaymentModes";
	public static final String CACHEKEY_SAP_PRICING = "SAP_PRICING";


	// Back-end pricing function modules
	public static final String FM_PIQ_CALCULATE = "PIQ_CALCULATE";
	public static final String SCALE_LEVELS = "A";

	private SappricingbolConstants()
	{
		//empty to avoid instantiating this constant class
	}

	// implement here constants used by this extension
}
