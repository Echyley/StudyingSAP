/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.outbound.hook;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.hybris.platform.commerceservices.order.hook.impl.DefaultCommerceQuoteCartCalculationMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.enums.QuoteState;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.QuoteEntryModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.util.DiscountValue;


/**
 * Class to handle Quote header and item discounts while converting from Quote to Order
 *
 */
public class SapCpqQuoteCartCalculationMethodHook extends DefaultCommerceQuoteCartCalculationMethodHook {



	private static final Logger LOG = Logger.getLogger(SapCpqQuoteCartCalculationMethodHook.class);

	@Override
	public void afterCalculate(final CommerceCartParameter parameter) {

		validateParameterNotNullStandardMessage("CommerceCartParameter", parameter);
		final CartModel cartModel = parameter.getCart();
		if (cartModel == null) {
			throw new IllegalArgumentException("The cart model is null.");
		}
		QuoteModel quoteReference = cartModel.getQuoteReference();
		if(quoteReference == null || quoteReference.getState() == QuoteState.BUYER_ORDERED || quoteReference.getState() == QuoteState.BUYER_DRAFT) {
			super.afterCalculate(parameter);
		} else {

			final List<DiscountValue> quoteDiscounts = getOrderQuoteDiscountValuesAccessor()
					.getQuoteDiscountValues(cartModel);

			final List<DiscountValue> globalDiscounts = new ArrayList<>(cartModel.getGlobalDiscountValues());
			globalDiscounts.addAll(quoteDiscounts);
			cartModel.setGlobalDiscountValues(globalDiscounts);
			copyEntryDiscountList(cartModel, quoteReference);

			try {
				getCalculationService().calculateTotals(cartModel, true);
			} catch (final CalculationException e) {
				LOG.error("Failed to calculate cart totals [" + cartModel.getCode() + "]");
				throw new SystemException(
						"Could not calculate cart [" + cartModel.getCode() + "] due to : " + e.getMessage(), e);
			}
			getModelService().save(cartModel);

		}
	}
	
	/**
	 * @param cartModel
	 * @param quoteReference
	 */
	protected void copyEntryDiscountList(final CartModel cartModel, QuoteModel quoteReference) {
		List<AbstractOrderEntryModel> cartEntryModel = cartModel.getEntries();
		int counter = 0;
		int quoteSize = quoteReference.getEntries().size();
 		for (final AbstractOrderEntryModel eachCartEntry : cartEntryModel) {
 			if(counter<quoteSize)
 			{
 				final QuoteEntryModel qe = (QuoteEntryModel) quoteReference.getEntries().get(counter);
 		 		final List<DiscountValue> discountValues = qe.getDiscountValues();
 		 		eachCartEntry.setDiscountValues(discountValues);
 		 		counter++;
 			}
 			else
 			{
 				LOG.warn("Unable to find quote entry for cart entry [" + eachCartEntry.getEntryNumber() + "]");
 			}
 	
 		}
		
	}

}
