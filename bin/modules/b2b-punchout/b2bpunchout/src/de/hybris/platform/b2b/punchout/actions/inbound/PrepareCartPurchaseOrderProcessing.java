/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.actions.inbound;

import de.hybris.platform.b2b.punchout.services.PunchOutConfigurationService;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.commercefacades.order.data.CartData;

/**
 * Prepares a cart for processing/populating by setting the required details.
 */
public class PrepareCartPurchaseOrderProcessing
{
	private CartFacade cartFacade;
	private PunchOutConfigurationService punchOutConfigurationService;

	public void process()
	{
		final CartData cartData = getCartFacade().getCurrentCart();

		final String paymentType = CheckoutPaymentType.ACCOUNT.getCode();
		final B2BPaymentTypeData paymentTypeData = new B2BPaymentTypeData();
		paymentTypeData.setCode(paymentType);
		cartData.setPaymentType(paymentTypeData);

		final String costCenterCode = getPunchOutConfigurationService().getDefaultCostCenter();
		final B2BCostCenterData costCenter = new B2BCostCenterData();
		costCenter.setCode(costCenterCode);
		cartData.setCostCenter(costCenter);

		getCartFacade().update(cartData);
	}

	protected CartFacade getCartFacade()
	{
		return cartFacade;
	}

	public void setCartFacade(final CartFacade cartFacade)
	{
		this.cartFacade = cartFacade;
	}

	protected PunchOutConfigurationService getPunchOutConfigurationService()
	{
		return punchOutConfigurationService;
	}

	public void setPunchOutConfigurationService(final PunchOutConfigurationService punchOutConfigurationService)
	{
		this.punchOutConfigurationService = punchOutConfigurationService;
	}

}
