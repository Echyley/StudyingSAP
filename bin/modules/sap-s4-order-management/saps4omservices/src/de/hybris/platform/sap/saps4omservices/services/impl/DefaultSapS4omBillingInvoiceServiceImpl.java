/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hybris.sapbillinginvoiceservices.service.impl.SapBillingInvoiceServiceImpl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saps4omservices.order.services.impl.DefaultSapS4OMOrderService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultSapS4omBillingInvoiceServiceImpl extends SapBillingInvoiceServiceImpl {

	private BaseStoreService baseStoreService;
	private DefaultSapS4OMOrderService defaultSapS4OMOrderService;
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4omBillingInvoiceServiceImpl.class);

	@Override
	public SAPOrderModel getSapOrderBySapOrderCode(String sapOrderCode) {
		if (!(getSapS4OrderManagementConfigService().isS4SynchronousOrderEnabled()
				|| getSapS4OrderManagementConfigService().isS4SynchronousOrderHistoryEnabled())) {
			return super.getSapOrderBySapOrderCode(sapOrderCode);
		}
		LOG.debug("Entering method getSapOrderBySapOrderCode of DefaultSapS4omBillingInvoiceServiceImpl ");
		OrderModel orderModel = getDefaultSapS4OMOrderService().getOrderForCode(sapOrderCode,
				getBaseStoreService().getCurrentBaseStore());
		Set<SAPOrderModel> sapOrderSet = orderModel.getSapOrders();
		Optional<SAPOrderModel> model = sapOrderSet.stream().findFirst();
		return model.isPresent()?model.get():null;
	}

	protected BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

	public DefaultSapS4OMOrderService getDefaultSapS4OMOrderService() {
		return defaultSapS4OMOrderService;
	}

	public void setDefaultSapS4OMOrderService(DefaultSapS4OMOrderService defaultSapS4OMOrderService) {
		this.defaultSapS4OMOrderService = defaultSapS4OMOrderService;
	}
	
	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}

}