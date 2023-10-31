/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.sap.core.saps4omservices.cache.service.impl.SapS4OMProductAvailabilityCache;
import de.hybris.platform.sap.saps4omservices.exceptions.OutboundServiceException;
import de.hybris.platform.sap.saps4omservices.services.SAPS4OMAvailabilityService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OMProductAvailability;
import de.hybris.platform.sap.saps4omservices.services.SapS4SalesOrderSimulationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
/**
 * 
 * Implementation for the Availability service.
 *
 */
public class DefaultSAPS4OMAvailabilityService implements SAPS4OMAvailabilityService {
	private static final String DEFAULT_PLANT = "defaultPlant";
	private BaseStoreService baseStoreService;
	private UserService userService;
	private B2BUnitService b2bUnitService;
	private SapS4OMProductAvailabilityCache sapS4OMProductAvailabilityCache;
	private SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService;
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSAPS4OMAvailabilityService.class);


	@Override
	public SapS4OMProductAvailability getProductAvailability(ProductModel productModel,BaseStoreModel baseStore) throws OutboundServiceException {
		final String  currentCustomerId = getCurrentCustomerID();
		final String plant = (productModel.getSapPlant()!=null)?productModel.getSapPlant().getCode():DEFAULT_PLANT;
		LOG.debug("Fetching stock for product: {}, with the plant: {} for customer: {} from method getProductAvailability", productModel.getCode(), plant, currentCustomerId);
		SapS4OMProductAvailability availability = getSapS4OMProductAvailabilityCache().readCachedProductAvailability(productModel,currentCustomerId,plant);
		LOG.debug("Fetch stock availability from cache");
		if(availability==null ) {
			availability =  getSapS4SalesOrderSimulationService().getStockAvailability(productModel, baseStore);
			if (availability != null ) {
				LOG.debug("Set stock availability from backend system to cache");
				getSapS4OMProductAvailabilityCache().cacheProductAvailability(availability, productModel, currentCustomerId, plant);
			}
		}
		return availability;
	}

	@Override
	public void removeProductAvailabilityFromCache(ProductModel productModel) throws OutboundServiceException {
		final String  currentCustomerId = getCurrentCustomerID();
		final String plant = (productModel.getSapPlant()!=null)?productModel.getSapPlant().getCode():DEFAULT_PLANT;
		LOG.debug("Remove stock availability from cache");
		getSapS4OMProductAvailabilityCache().invalidateProductAvailability(productModel, currentCustomerId, plant);
		
	}
	
	protected String getCurrentCustomerID() {

		UserModel userModel= getUserService().getCurrentUser();

		B2BCustomerModel b2bCustomer = (userModel instanceof B2BCustomerModel) ? (B2BCustomerModel) userModel : null;
		// if b2bcustomer is null then go for reference customer
		if (b2bCustomer == null) {
			LOG.debug("Not a B2B customer so using the reference customer");
			return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcommon_referenceCustomer();
		}
		final B2BUnitModel parent = (B2BUnitModel) getB2bUnitService().getParent(b2bCustomer);

		return parent.getUid();

	}
	

	protected BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	protected UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	protected B2BUnitService getB2bUnitService() {
		return b2bUnitService;
	}

	public void setB2bUnitService(B2BUnitService b2bUnitService) {
		this.b2bUnitService = b2bUnitService;
	}


	
	public SapS4SalesOrderSimulationService getSapS4SalesOrderSimulationService() {
		return sapS4SalesOrderSimulationService;
	}

	public void setSapS4SalesOrderSimulationService(SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService) {
		this.sapS4SalesOrderSimulationService = sapS4SalesOrderSimulationService;
	}

	public SapS4OMProductAvailabilityCache getSapS4OMProductAvailabilityCache() {
		return sapS4OMProductAvailabilityCache;
	}

	public void setSapS4OMProductAvailabilityCache(SapS4OMProductAvailabilityCache sapS4OMProductAvailabilityCache) {
		this.sapS4OMProductAvailabilityCache = sapS4OMProductAvailabilityCache;
	}
	
	
	

}
