/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4c.customer.service;

import java.io.IOException;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;


/**
 * Service for publishing Customer JSON to SCPI
 */
public interface SapC4cCustomerPublicationService
{

	/**
	 * Publishes Customer Data to SCPI
	 * @param customerJson customer data
	 * @throws IOException on publication failure
	 */
	public void publishCustomerToCloudPlatformIntegration(C4CCustomerData customerJson) throws IOException;
}
