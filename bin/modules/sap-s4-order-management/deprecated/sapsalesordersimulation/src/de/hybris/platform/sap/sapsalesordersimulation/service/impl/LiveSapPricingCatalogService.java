/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import java.util.List;

import de.hybris.platform.commerceservices.price.impl.NetPriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.sapsalesordersimulation.service.PricingService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;
/**
 * 
 * Live pricing fetch for catalog
 *
 */
public class LiveSapPricingCatalogService  extends NetPriceService {
private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
private PricingService pricingService;
	
	@Override
	public List<PriceInformation> getPriceInformationsForProduct(ProductModel model)
	{
		if (getSapSimulateSalesOrderEnablementService().isCatalogPricingEnabled() && model.getSapProductTypes().contains(SAPProductType.PHYSICAL)) {
			
			return getPricingService().getPriceForProduct(model)  ;
			
		}
		
		return super.getPriceInformationsForProduct(model);
	}

	protected SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}


	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}


	public PricingService getPricingService() {
		return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	

}
