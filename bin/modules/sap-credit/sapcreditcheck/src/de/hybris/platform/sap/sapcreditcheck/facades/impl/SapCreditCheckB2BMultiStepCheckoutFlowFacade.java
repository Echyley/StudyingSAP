/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcreditcheck.facades.impl;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;

import org.springframework.beans.factory.annotation.Required;


/**
 * Mutli step checkout implementation of the {@link CheckoutFlowFacade} interface. Delegates resolving the checkout flow
 * to an injected {@link CheckoutFlowFacade}.
 */
public class SapCreditCheckB2BMultiStepCheckoutFlowFacade extends SapCreditCheckB2BAcceleratorCheckoutFacade implements CheckoutFlowFacade
{
	private CheckoutFlowFacade checkoutFlowFacade;

	@Override
	public CheckoutPciOptionEnum getSubscriptionPciOption()
	{
		return getCheckoutFlowFacade().getSubscriptionPciOption();
	}

	protected CheckoutFlowFacade getCheckoutFlowFacade()
	{
		return checkoutFlowFacade;
	}

	@Required
	public void setCheckoutFlowFacade(final CheckoutFlowFacade facade)
	{
		checkoutFlowFacade = facade;
	}
}
