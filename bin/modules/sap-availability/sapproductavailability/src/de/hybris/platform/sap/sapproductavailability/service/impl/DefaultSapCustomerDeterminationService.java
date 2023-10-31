/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductavailability.service.impl;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapproductavailability.service.SapCustomerDeterminationService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * retrive the sap Customer based on the ID
 *
 */
public class DefaultSapCustomerDeterminationService implements SapCustomerDeterminationService {

	private UserService userService;

	@Override
	public String readSapCustomerID() {

		if (!getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
			final CustomerModel currentCustomer = (CustomerModel) getUserService().getCurrentUser();
			final String customerId = currentCustomer != null ? currentCustomer.getCustomerID() : null;
			return customerId;
		}

		return null;

	}

	protected UserService getUserService() {
		return userService;
	}

	@Required
	public void setUserService(final UserService userService) {
		this.userService = userService;
	}

}
