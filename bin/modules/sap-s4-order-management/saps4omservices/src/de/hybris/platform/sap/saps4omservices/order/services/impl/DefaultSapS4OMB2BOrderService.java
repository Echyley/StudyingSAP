/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.services.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.b2b.services.impl.DefaultB2BOrderService;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

/**
 *  
 *  Overrides {@link DefaultB2BOrderService} for Reorder 
 */
public class DefaultSapS4OMB2BOrderService extends DefaultB2BOrderService {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4OMB2BOrderService.class);

	private transient SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private transient CustomerAccountService customerAccountService;
	private transient BaseStoreService baseStoreService;


	@Override
	public OrderModel getOrderForCode(final String code)
	{

		if(!isSapS4OrderManagementEnabled()) {
			LOG.info("Synchronous order is disabled {}", code);
			return super.getOrderForCode(code);
		} 
		BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
		return getCustomerAccountService().getOrderForCode(code, baseStore);	
	}
	
	protected boolean isSapS4OrderManagementEnabled() {
		boolean isSapS4OrderManagementEnabled = getSapS4OrderManagementConfigService().isS4SynchronousOrderEnabled() || 
				getSapS4OrderManagementConfigService().isS4SynchronousOrderHistoryEnabled();
		
		LOG.debug("S4OM Synchronous Order Management enabled: {}", isSapS4OrderManagementEnabled);
		return isSapS4OrderManagementEnabled;
	}
	

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}


	public CustomerAccountService getCustomerAccountService() {
		return customerAccountService;
	}

	public void setCustomerAccountService(CustomerAccountService customerAccountService) {
		this.customerAccountService = customerAccountService;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

}
