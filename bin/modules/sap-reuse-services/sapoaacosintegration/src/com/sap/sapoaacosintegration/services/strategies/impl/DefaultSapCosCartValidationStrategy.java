/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapoaacosintegration.services.strategies.impl;

import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.atp.exception.ATPException;
import com.sap.retail.oaa.commerce.services.atp.pojos.ATPAvailability;
import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.order.impl.SapOaaCommerceCartModificationStatus;
import com.sap.retail.oaa.commerce.services.rest.util.exception.BackendDownException;
import com.sap.retail.oaa.commerce.services.stock.SapOaaCommerceStockService;
import com.sap.retail.oaa.commerce.services.strategies.impl.DefaultSapOaaCartValidationStrategy;


/**
 * Default Cart validation Strategy for COS.
 *
 */

public class DefaultSapCosCartValidationStrategy extends DefaultSapOaaCartValidationStrategy
{

	private static final Logger LOG = Logger.getLogger(DefaultSapCosCartValidationStrategy.class);
	private CommonUtils commonUtils;

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.hybris.platform.commerceservices.strategies.impl.DefaultCartValidationStrategy#validateCart(de.hybris.platform
	 * .commerceservices.service.data.CommerceCartParameter)
	 */
	@Override
	public List<CommerceCartModification> validateCart(final CommerceCartParameter parameter)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		List<CommerceCartModification> cartModificationList = new ArrayList<>();

		final CartModel cartModel = parameter.getCart();
		cleanCart(cartModel);

		if (cartModel != null && cartModel.getEntries() != null && !cartModel.getEntries().isEmpty())
		{
			cartModificationList = checkAvailabilityOfCartItems(cartModel);
		}
		return cartModificationList;
		}
		else
		{
			return super.validateCart(parameter);
		}
	}

	/**
	 * Checks the availability of all items in the specified cart.
	 *
	 * @param cart
	 * @return list of commerceCartModifications
	 */
	@Override
	protected List<CommerceCartModification> checkAvailabilityOfCartItems(final CartModel cart)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		final List<CommerceCartModification> cartModificationList = new ArrayList<>();
		LOG.debug("Checking Availability for all Cart Items...");

		// Check and validate determined availability for each cart item...
		for (final AbstractOrderEntryModel entry : cart.getEntries())
		{
			List<ATPAvailability> atpAvailabilityList = new ArrayList<>();

			try
			{
				if (entry.getDeliveryPointOfService() != null)
				{
					final String source = entry.getDeliveryPointOfService().getName();

					atpAvailabilityList = ((SapOaaCommerceStockService) getOaaStockService()).getAvailabilityForProductAndSource(
							cart.getGuid(), entry.getEntryNumber().toString(), entry.getProduct(), source);
				}
				else
				{
					atpAvailabilityList = ((SapOaaCommerceStockService) getOaaStockService()).getAvailabilityForProduct(cart.getGuid(),
							entry.getEntryNumber().toString(), entry.getProduct());
				}

			}
			catch (final ATPException e)
			{
				LOG.error("Failed to check availability for product: " + entry.getProduct().getCode(), e);

				// in case the ATP call fails for the product set OAA specific modification status
				final CommerceCartModification checkingATPFailedModification = new CommerceCartModification();
				checkingATPFailedModification.setEntry(entry);
				checkingATPFailedModification.setProduct(entry.getProduct());
				checkingATPFailedModification.setQuantity(entry.getQuantity().longValue());
				checkingATPFailedModification.setQuantityAdded(entry.getQuantity().longValue());
				checkingATPFailedModification.setStatusCode(SapOaaCommerceCartModificationStatus.ATP_VALIDATION_FAILURE);
				cartModificationList.add(checkingATPFailedModification);
				return cartModificationList;
			}
			catch (final BackendDownException e)
			{
				LOG.error(e.getMessage(), e);
				// return modification list when ATP call fails for first cart entry due to back end not responding
				return createModificationListForBackendDown(cart);
			}

			final Long aggregatedATPResult = getAtpAggregationStrategy().aggregateAvailability(cart.getGuid(), entry.getProduct(),
					entry.getDeliveryPointOfService(), atpAvailabilityList);
			cartModificationList.add(validateCartEntry(cart, entry, aggregatedATPResult.longValue()));
		}
		return cartModificationList;
		}
		else
		{
			return super.checkAvailabilityOfCartItems(cart);
		}
	}

	/**
	 * Validates the cart entry with the ATP results and make quantity adjustments if necessary.
	 *
	 * @param cartModel
	 * @param cartItem
	 * @param aggregatedATPResultQty
	 * @return CommerceCartModification
	 */
	@Override
	protected CommerceCartModification validateCartEntry(final CartModel cartModel, final AbstractOrderEntryModel cartItem,
			final long aggregatedATPResultQty)
	{
		if (getCommonUtils().isCOSEnabled())
		{
		final CartEntryModel cartEntryModel = (CartEntryModel) cartItem;
		final CommerceCartModification modification = new CommerceCartModification();

		// First verify that the product exists
		try
		{
			getProductService().getProductForCode(cartEntryModel.getProduct().getCode());
		}
		catch (final UnknownIdentifierException e)
		{
			LOG.error("Product could not be found", e);
			modification.setStatusCode(CommerceCartModificationStatus.UNAVAILABLE);
			modification.setQuantityAdded(0);
			modification.setQuantity(0);

			final CartEntryModel entry = new CartEntryModel();
			entry.setProduct(cartEntryModel.getProduct());

			modification.setEntry(entry);

			getModelService().remove(cartEntryModel);
			getModelService().refresh(cartModel);

			return modification;
		}

		// Stock quantity for this cartEntry
		final long cartEntryLevel = cartEntryModel.getQuantity().longValue();

		modification.setStatusCode(CommerceCartModificationStatus.SUCCESS);
		modification.setQuantityAdded(cartEntryLevel);
		modification.setQuantity(cartEntryLevel);
		modification.setEntry(cartEntryModel);
		return modification;
		}
		else
		{
			return super.validateCartEntry(cartModel, cartItem, aggregatedATPResultQty);
		}

	}

	@Override
	public CommonUtils getCommonUtils()
	{
		return commonUtils;
	}

	@Override
	public void setCommonUtils(final CommonUtils commonUtils)
	{
		this.commonUtils = commonUtils;
	}

}
