/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;

public class SalesOrderSimulationUtil {
	
	private UserService userService;
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	private BaseStoreService baseStoreService;
	
	protected String getSoldToParty(UserModel userModel) {
		if (userModel == null) {
			userModel = getUserService().getCurrentUser();
		}
		final B2BCustomerModel b2bCustomer = (userModel instanceof B2BCustomerModel) ? (B2BCustomerModel) userModel : null;
		// if b2bcustomer is null then go for reference customer
		if (b2bCustomer == null) {
			return getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcommon_referenceCustomer();
		}
		final B2BUnitModel parent = (B2BUnitModel) getB2bUnitService().getParent(b2bCustomer);
		return (parent.getUid().contains("_"))?parent.getUid().split("_")[0]:parent.getUid();

	}

	protected UserService getUserService() {
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

	protected BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}
	
	
}