/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricingbol.businessobject.interf;



import java.util.List;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;


/**
 * 
 */
public interface SapPricing
{

	/**
	 * 
	 * @param order order
	 * @param partnerFunction Partner Function
	 * @param conversionService Conversion Service
	 */
	public void getPriceInformationForCart(AbstractOrderModel order, SapPricingPartnerFunction partnerFunction, ConversionService conversionService);

	/**
	 * @param partnerFunction Partner Function
	 * @param conversionService Conversion Service
	 * @param productModel products
	 * @return List<PriceInformation> Price Information List
	 */
	public List<PriceInformation> getPriceInformationForProducts(final List<ProductModel> productModels, final SapPricingPartnerFunction partnerFunction, ConversionService conversionService);

}
