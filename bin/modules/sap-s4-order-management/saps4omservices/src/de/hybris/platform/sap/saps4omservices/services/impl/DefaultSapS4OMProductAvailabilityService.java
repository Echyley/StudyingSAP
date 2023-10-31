/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.basecommerce.enums.StockLevelStatus;
import de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.services.SAPS4OMAvailabilityService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.store.BaseStoreModel;

/**
 * determined the product availability
 *
 */
public class DefaultSapS4OMProductAvailabilityService extends DefaultCommerceStockService {
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private SAPS4OMAvailabilityService sapS4OMAvailabilityService;
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4OMProductAvailabilityService.class);

		
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService#
	 * getStockLevelForProductAndBaseStore
	 * (de.hybris.platform.core.model.product.ProductModel,
	 * de.hybris.platform.store.BaseStoreModel)
	 */
	@Override
	public Long getStockLevelForProductAndBaseStore(final ProductModel product, final BaseStoreModel baseStore) {
		ServicesUtil.validateParameterNotNull(product, "product cannot be null");
		ServicesUtil.validateParameterNotNull(baseStore, "baseStore cannot be null");
		LOG.debug("ATP check enabled? {}", getSapS4OrderManagementConfigService().isATPCheckActive());
		if (getSapS4OrderManagementConfigService().isATPCheckActive() && product.getSapProductTypes().contains(SAPProductType.PHYSICAL)) {
			LOG.debug("Method call getStockLevelForProductAndBaseStore for product {} with product type {} ", product.getCode(), SAPProductType.PHYSICAL);
			// not available stock return 0
			SapS4OMProductAvailability productAvailability = null;
			try {
				productAvailability = getSapS4OMAvailabilityService().getProductAvailability(product, baseStore);
			} catch (OutboundServiceException e) {
				throw  new UnknownIdentifierException(e.getMessage());	
			}

			if (productAvailability == null) {
				LOG.debug("No stock level available");
				return Long.valueOf(0); // no stock available
			}
			LOG.debug("Stock level from backend {}", productAvailability.getCurrentStockLevel());
			return productAvailability.getCurrentStockLevel();
		} else {
			LOG.debug("Synchronous stock level fetch is {} for product {} , falling back to out of the box code", getSapS4OrderManagementConfigService().isATPCheckActive(), product.getCode());
			return super.getStockLevelForProductAndBaseStore(product,baseStore);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hybris.platform.commerceservices.stock.impl.DefaultCommerceStockService#
	 * getStockLevelStatusForProductAndBaseStore
	 * (de.hybris.platform.core.model.product.ProductModel,
	 * de.hybris.platform.store.BaseStoreModel)
	 */
	@Override
	public StockLevelStatus getStockLevelStatusForProductAndBaseStore(final ProductModel product,
			final BaseStoreModel baseStore) {
		if (getSapS4OrderManagementConfigService().isATPCheckActive() && product.getSapProductTypes().contains(SAPProductType.PHYSICAL)) {
			final Long stockLevel = getStockLevelForProductAndBaseStore(product, baseStore);

			if (stockLevel.compareTo(Long.valueOf(0)) > 0) {
				LOG.debug("Stock level status: {}", StockLevelStatus.INSTOCK);
				return StockLevelStatus.INSTOCK;
			} else {
				LOG.debug("Stock level status: {}", StockLevelStatus.OUTOFSTOCK);
				return StockLevelStatus.OUTOFSTOCK;
			}
		} else {
			LOG.debug("Synchronous stock status fetch is {} for product , falling back to out of the box code", getSapS4OrderManagementConfigService().isATPCheckActive());
			return super.getStockLevelStatusForProductAndBaseStore(product, baseStore);
		}
	}


	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(
			SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}


	public SAPS4OMAvailabilityService getSapS4OMAvailabilityService() {
		return sapS4OMAvailabilityService;
	}

	public void setSapS4OMAvailabilityService(SAPS4OMAvailabilityService sapS4OMAvailabilityService) {
		this.sapS4OMAvailabilityService = sapS4OMAvailabilityService;
	}
	

}
