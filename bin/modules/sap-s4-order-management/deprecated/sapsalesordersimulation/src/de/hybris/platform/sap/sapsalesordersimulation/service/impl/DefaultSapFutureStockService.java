/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.commerceservices.futurestock.impl.DefaultFutureStockService;
import de.hybris.platform.commerceservices.model.FutureStockModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.sapsalesordersimulation.service.AvailabilityService;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapProductAvailability;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;




/**
 * retrieve the future availability
 *
 */
public class DefaultSapFutureStockService extends DefaultFutureStockService
{
	private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;
	private AvailabilityService availabilityService;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.hybris.platform.acceleratorservices.futurestock.FutureStockService#getFutureAvailability(java.util.List)
	 */

	@Override
	public Map<String, Map<Date, Integer>> getFutureAvailability(final List<ProductModel> products)
	{
		if (getSapSimulateSalesOrderEnablementService().isATPCheckActive()) {
			return getLiveFutureAvailability(products);
		} else {
			return super.getFutureAvailability(products);
		}
		
		
	}



	protected Map<String, Map<Date, Integer>> getLiveFutureAvailability(final List<ProductModel> products) {
		final Map<String, Map<Date, Integer>> productsMap = new HashMap<>();
		List<ProductModel> nonPhysicalProducts = new ArrayList<>();
		for (ProductModel product: products) {
			if (product.getSapProductTypes().contains(SAPProductType.PHYSICAL)) {
				final SapProductAvailability productAvailability =
						getAvailabilityService().getProductAvailability(product, null);
				final List<FutureStockModel> futureStocks = productAvailability.getFutureAvailability();
				if (!CollectionUtils.isEmpty(futureStocks))
				{
					final HashMap<Date, Integer> futureAvailability = new HashMap<>();
					for (final FutureStockModel futureStock : futureStocks)
					{
						futureAvailability.put(futureStock.getDate(), futureStock.getQuantity());
					}
					productsMap.put(product.getCode(), futureAvailability);
				}
			} else {
				nonPhysicalProducts.add(product);
			}
		}
		if (!nonPhysicalProducts.isEmpty()) {
			productsMap.putAll(super.getFutureAvailability(nonPhysicalProducts));
		}
		return productsMap;
	}

	

	public SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}

	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}



	public AvailabilityService getAvailabilityService() {
		return availabilityService;
	}



	public void setAvailabilityService(AvailabilityService availabilityService) {
		this.availabilityService = availabilityService;
	}





}
