/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.commerceservices.futurestock.impl.DefaultFutureStockService;
import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.services.SAPS4OMAvailabilityService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;


/**
 * retrieve the future availability
 *
 */
public class DefaultSapFutureStockService extends DefaultFutureStockService
{
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private SAPS4OMAvailabilityService sapS4OMAvailabilityService;
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapFutureStockService.class);

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.futurestock.FutureStockService#getFutureAvailability(java.util.List)
	 */

	@Override
	public Map<String, Map<Date, Integer>> getFutureAvailability(final List<ProductModel> products)
	{
		if (getSapS4OrderManagementConfigService().isATPCheckActive()) {
			LOG.debug("Synchronous ATP enabled. Calling the method getFutureAvailability");
			return getLiveFutureAvailability(products);
		} else {
			LOG.debug("Synchronous ATP disabled. Falling back to out of the box ATP check");
			return super.getFutureAvailability(products);
		}
		
		
	}

	protected Map<String, Map<Date, Integer>> getLiveFutureAvailability(final List<ProductModel> products) {
		final Map<String, Map<Date, Integer>> productsMap = new HashMap<>();
		List<ProductModel> nonPhysicalProducts = new ArrayList<>();
		for (ProductModel product: products) {
			if (product.getSapProductTypes().contains(SAPProductType.PHYSICAL)) {
				LOG.debug("Product {} is {} product ", product.getCode(), SAPProductType.PHYSICAL);
				SapS4OMProductAvailability productAvailability = null;
				try {
					productAvailability = getSapS4OMAvailabilityService().getProductAvailability(product, null);
					final List<FutureStockModel> futureStocks = productAvailability.getFutureAvailability();
					LOG.debug("Future stock size {} "+ futureStocks.size());
					if (!CollectionUtils.isEmpty(futureStocks))
					{
						final HashMap<Date, Integer> futureAvailability = new HashMap<>();
						for (final FutureStockModel futureStock : futureStocks)
						{
							futureAvailability.put(futureStock.getDate(), futureStock.getQuantity());
							LOG.debug("Future stock date : {}  and quantity : {}  for product : {} ", futureStock.getDate(), futureStock.getQuantity(), product.getCode());
						}
						productsMap.put(product.getCode(), futureAvailability);
					}
				} catch (OutboundServiceException e) {
					LOG.error("DefaultSapFutureStockService : {}", e.getMessage());
				}
				
			} else {
				LOG.debug("Product {} is not {} produt", product.getCode(), SAPProductType.PHYSICAL);
				nonPhysicalProducts.add(product);
			}
		}
		if (!nonPhysicalProducts.isEmpty()) {
			LOG.debug("No PHYSICAL product present, falling back to fetch futureStockAvailability to out of the box code.");
			productsMap.putAll(super.getFutureAvailability(nonPhysicalProducts));
		}
		return productsMap;
	}

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}
	
	public SAPS4OMAvailabilityService getSapS4OMAvailabilityService() {
		return sapS4OMAvailabilityService;
	}
	public void setSapS4OMAvailabilityService(SAPS4OMAvailabilityService sapS4OMAvailabilityService) {
		this.sapS4OMAvailabilityService = sapS4OMAvailabilityService;
	}

}
