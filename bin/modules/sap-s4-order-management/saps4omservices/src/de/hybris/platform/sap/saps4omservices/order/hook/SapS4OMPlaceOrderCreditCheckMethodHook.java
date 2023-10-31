/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.order.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.sap.saps4omservices.services.SapS4SalesOrderSimulationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.localization.Localization;

public class SapS4OMPlaceOrderCreditCheckMethodHook implements CommercePlaceOrderMethodHook {

	private UserService userService;
	private SapS4SalesOrderSimulationService sapS4SalesOrderSimulationService;
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	private static final Logger LOG = LoggerFactory.getLogger(SapS4OMPlaceOrderCreditCheckMethodHook.class);

	@Override
	public void afterPlaceOrder(CommerceCheckoutParameter parameter, CommerceOrderResult orderModel)
			throws InvalidCartException {
		LOG.debug("unimplemented method");
		
	}
	@Override
	public void beforePlaceOrder(CommerceCheckoutParameter parameter) throws InvalidCartException {
		final CartModel cart = parameter.getCart();
		UserModel userModel = getUserService().getCurrentUser();
		if (getSapS4OrderManagementConfigService().isCreditCheckActive() && isB2BOrder(cart) && getSapS4SalesOrderSimulationService().checkCreditLimitExceeded(cart,userModel) ) {
			LOG.error("Credit limit is exceeded for the cart {}  ", cart.getCode());	
			throw new UnknownIdentifierException(Localization.getLocalizedString("cart.creditcheck.exceeded"));
		}	
	}
	@Override
	public void beforeSubmitOrder(CommerceCheckoutParameter parameter, CommerceOrderResult result)
			throws InvalidCartException {
		LOG.debug("unimplemented method");
		
	}
	
	protected boolean isB2BOrder(final CartModel cartModel)
	{
		return cartModel.getSite().getChannel() == SiteChannel.B2B;
	}
	
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
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

}


