/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.sourcing.impl;

import com.sap.retail.oaa.commerce.services.rest.RestServiceConfiguration;
import com.sap.retail.oaa.commerce.services.sourcing.SourcingService;
import com.sap.retail.oaa.commerce.services.sourcing.jaxb.pojos.response.SourcingResponse;

import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 *	Dummy implementation of SourcingService
 */
public class DummySourcingService implements SourcingService {

	@Override
	public void setRestServiceConfiguration(RestServiceConfiguration restServiceConfiguration) {
		// do nothing, dummy implementation for setRestServiceConfiguration method
	}

	@Override
	public void setBackendDown(boolean backendDown) {
		// do nothing, dummy implementation for setBackendDown method
	}

	@Override
	public void callRestServiceAndPersistResult(AbstractOrderModel model) {
		// do nothing, dummy implementation for callRestServiceAndPersistResult method
	}

	@Override
	public SourcingResponse callRestService(AbstractOrderModel orderModel, boolean execAllStrategies, boolean reserve) {
		// do nothing, dummy implementation for callRestService method
		return null;
	}

}
