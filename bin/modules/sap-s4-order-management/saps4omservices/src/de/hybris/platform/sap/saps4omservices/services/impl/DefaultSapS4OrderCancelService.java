/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.services.impl;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.ordercancel.CancelDecision;
import de.hybris.platform.ordercancel.impl.DefaultOrderCancelService;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;

public class DefaultSapS4OrderCancelService extends DefaultOrderCancelService{
	
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4OrderCancelService.class);
	
	@Override
	public CancelDecision isCancelPossible(OrderModel order, PrincipalModel requestor, boolean partialCancel,
			boolean partialEntryCancel) {
		if(!getSapS4OrderManagementConfigService().isS4SynchronousOrderEnabled() && 
				!getSapS4OrderManagementConfigService().isS4SynchronousOrderHistoryEnabled()) {	
			LOG.debug("Calling out of the box isCancelPossible method.");
			return super.isCancelPossible(order, requestor, partialCancel, partialEntryCancel);
		}
		LOG.debug("Cancel is disabled as the the synchronous order management is enabled.");
		return new CancelDecision(false, Collections.emptyList());
	}

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}

}
