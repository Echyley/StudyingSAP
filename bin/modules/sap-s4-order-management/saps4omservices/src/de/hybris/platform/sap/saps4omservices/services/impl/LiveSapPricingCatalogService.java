/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.commerceservices.price.impl.NetPriceService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMPricingService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
/**
 * 
 * Live pricing fetch for catalog
 *
 */
public class LiveSapPricingCatalogService  extends NetPriceService {
	
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private SapS4OMPricingService sapS4OMPricingService;
	private static final Logger LOG = LoggerFactory.getLogger(LiveSapPricingCatalogService.class);

	@Override
	public List<PriceInformation> getPriceInformationsForProduct(ProductModel model) throws OutboundServiceException 
	{
		LOG.debug("Method called getPriceInformationsForProduct");
		if (getSapS4OrderManagementConfigService().isCatalogPricingEnabled() && model.getSapProductTypes().contains(SAPProductType.PHYSICAL)) {
			
			try {
				LOG.debug("Synchronous catalog price is enabled. Fetch live price information for product");
				return getSapS4OMPricingService().getPriceForProduct(model);
			} catch (OutboundServiceException e) {
				throw  new UnknownIdentifierException(e.getMessage());	
			}
		}
		LOG.debug("Synchronous catalog price is {} , falling back to out of the box code to fetch live product price", getSapS4OrderManagementConfigService().isCatalogPricingEnabled());
		return super.getPriceInformationsForProduct(model);
	}

	public SapS4OMPricingService getSapS4OMPricingService() {
		return sapS4OMPricingService;
	}

	public void setSapS4OMPricingService(SapS4OMPricingService sapS4OMPricingService) {
		this.sapS4OMPricingService = sapS4OMPricingService;
	}

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}

}
