/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapordermgmtb2baddon.checkout.steps.validation;

import de.hybris.platform.acceleratorfacades.flow.CheckoutFlowFacade;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.validation.ValidationResults;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2bacceleratoraddon.checkout.steps.validation.AbstractB2BCheckoutStepValidator;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 *
 */
public class SapB2BDeliveryMethodCheckoutStepValidator extends AbstractB2BCheckoutStepValidator
{

	private CheckoutFlowFacade sapB2BCheckoutFlowFacade;

	/**
	 * @return the sapB2BCheckoutFlowFacade
	 */
	public CheckoutFlowFacade getSapB2BCheckoutFlowFacade()
	{
		return sapB2BCheckoutFlowFacade;
	}

	/**
	 * @param sapB2BCheckoutFlowFacade
	 *           the sapB2BCheckoutFlowFacade to set
	 */
	@Required
	public void setSapB2BCheckoutFlowFacade(final CheckoutFlowFacade sapB2BCheckoutFlowFacade)
	{
		this.sapB2BCheckoutFlowFacade = sapB2BCheckoutFlowFacade;
	}



	@Override
	protected ValidationResults doValidateOnEnter(final RedirectAttributes redirectAttributes)
	{
		if (this.getSapB2BCheckoutFlowFacade().hasNoDeliveryAddress())
		{
			GlobalMessages.addFlashMessage(redirectAttributes, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"checkout.multi.deliveryAddress.notprovided");
			return ValidationResults.REDIRECT_TO_DELIVERY_ADDRESS;
		}

		return ValidationResults.SUCCESS;
	}
}
