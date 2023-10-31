/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricingbol.backend.interf;


import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.core.bol.businessobject.CommunicationException;
import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;
import de.hybris.platform.sap.sappricingbol.converter.ConversionService;

import java.util.List;


/**
 * 
 */
public interface SapPricingBackend extends de.hybris.platform.sap.core.bol.backend.BackendBusinessObject
{
	/**
	 * 
	 * @param order order
	 * @param partnerFunction Partner Function
	 * @param conversionService Conversion Service
	 * @throws BackendException Backend Exception
	 * @throws CommunicationException Communication Exception
	 */
	public void readPricesForCart(AbstractOrderModel order, SapPricingPartnerFunction partnerFunction, ConversionService conversionService)
			throws BackendException, CommunicationException;

	/**
	 * @param productModels  products
	 * @param partnerFunction Partner Function
	 * @param conversionService Conversion Service
	 * @return List<PriceInformation> Price Information List
	 * @throws BackendException Backend Exception
	 */
	public List<PriceInformation> readPriceInformationForProducts(
			List<ProductModel> productModels, SapPricingPartnerFunction partnerFunction, ConversionService conversionService) throws BackendException;
	
}
