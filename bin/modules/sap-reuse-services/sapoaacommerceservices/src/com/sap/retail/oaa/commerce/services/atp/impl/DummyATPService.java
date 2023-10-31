/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.atp.impl;

import static java.util.Collections.emptyList;

import java.util.List;

import com.sap.retail.oaa.commerce.services.atp.ATPService;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPProductAvailability;

import de.hybris.platform.core.model.product.ProductModel;

/**
 *	Dummy implementation of ATPService
 */
public class DummyATPService implements ATPService {

	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProduct(String cartGuid, String itemId,
			ProductModel product) {
		// do nothing, dummy implementation for callRestAvailabilityServiceForProduct method
		return emptyList();
	}

	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(ProductModel product, String source) {
		// do nothing, dummy implementation for callRestAvailabilityServiceForProductAndSource method
		return emptyList();
	}

	@Override
	public List<ATPAvailability> callRestAvailabilityServiceForProductAndSource(String cartGuid, String itemId,
			ProductModel product, String source) {
		// do nothing, dummy implementation for callRestAvailabilityServiceForProductAndSource method
		return emptyList();
	}

	@Override
	public List<ATPProductAvailability> callRestAvailabilityServiceForProducts(String cartGuid, List<String> itemIdList,
			String productUnit, List<ProductModel> productList) {
		// do nothing, dummy implementation for callRestAvailabilityServiceForProducts method
		return emptyList();
	}

	@Override
	public List<ATPProductAvailability> callRestAvailabilityServiceForProductAndSources(String cartGuid, String itemId,
			ProductModel product, List<String> sourcesList) {
		// do nothing, dummy implementation for callRestAvailabilityServiceForProductAndSources method
		return emptyList();
	}

}
