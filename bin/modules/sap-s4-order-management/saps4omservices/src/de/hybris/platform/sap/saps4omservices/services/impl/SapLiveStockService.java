/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.sap.core.saps4omservices.cache.service.impl.SapS4OMProductAvailabilityCache;
import de.hybris.platform.sap.saps4omservices.services.SAPS4OMAvailabilityService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.services.SapS4SalesOrderSimulationService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.stock.impl.DefaultStockService;
import de.hybris.platform.store.services.BaseStoreService;
/**
 * Implementation for overriding the default stock levels
 */
public class SapLiveStockService extends DefaultStockService {
	private SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService;
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private BaseStoreService baseStoreService;
	

	private SapS4OMProductAvailabilityCache sapS4OMProductAvailabilityCache;
	private UserService userService;

	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private SAPS4OMAvailabilityService sapS4OMAvailabilityService;
	
	
	public SapS4SalesOrderSimulationService getSapS4SalesOrderSimulationService() {
		return sapS4SalesOrderSimulationService;
	}
	public void setSapS4SalesOrderSimulationService(SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService) {
		this.sapS4SalesOrderSimulationService = sapS4SalesOrderSimulationService;
	}
	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}
	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}
	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}
	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	protected B2BUnitService<B2BUnitModel, B2BCustomerModel> getB2bUnitService()
	{
		return b2bUnitService;
	}

	public void setB2bUnitService(final B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService)
	{
		this.b2bUnitService = b2bUnitService;
	}

	public SAPS4OMAvailabilityService getSapS4OMAvailabilityService() {
		return sapS4OMAvailabilityService;
	}
	public void setSapS4OMAvailabilityService(SAPS4OMAvailabilityService sapS4OMAvailabilityService) {
		this.sapS4OMAvailabilityService = sapS4OMAvailabilityService;
	}
	public SapS4OMProductAvailabilityCache getSapS4OMProductAvailabilityCache() {
		return sapS4OMProductAvailabilityCache;
	}
	public void setSapS4OMProductAvailabilityCache(SapS4OMProductAvailabilityCache sapS4OMProductAvailabilityCache) {
		this.sapS4OMProductAvailabilityCache = sapS4OMProductAvailabilityCache;
	}

}
