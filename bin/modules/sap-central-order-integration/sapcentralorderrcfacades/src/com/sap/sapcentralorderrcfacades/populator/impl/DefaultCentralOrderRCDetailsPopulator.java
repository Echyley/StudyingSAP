/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderrcfacades.populator.impl;

import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.OrderItem;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PriceTotals;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.SubscriptionItemPrice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants;
import com.sap.sapcentralorderfacades.populator.SapCpiOrderDetailPopulator;


/**
 *
 */
public class DefaultCentralOrderRCDetailsPopulator implements SapCpiOrderDetailPopulator
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCentralOrderRCDetailsPopulator.class);
	private PriceDataFactory priceDataFactory;


	/**
	 * @return the priceDataFactory
	 */
	public PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}


	/**
	 * @param priceDataFactory
	 *           the priceDataFactory to set
	 */
	public void setPriceDataFactory(final PriceDataFactory priceDataFactory)
	{
		this.priceDataFactory = priceDataFactory;
	}


	@Override
	public void populate(final CentralOrderDetailsResponse source, final OrderData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		try
		{
			final List<ConsignmentData> consignments = new ArrayList();
			for (final ConsignmentData consignmentData : target.getConsignments())
			{
				final List<ConsignmentEntryData> consignmentEntryDataList = new ArrayList();
				for (final ConsignmentEntryData consignmentEntryData : consignmentData.getEntries())
				{
					final OrderEntryData orderEntryData = consignmentEntryData.getOrderEntry();
					populateOrderEntryData(source, orderEntryData);
					consignmentEntryData.setOrderEntry(orderEntryData);
					consignmentEntryDataList.add(consignmentEntryData);
				}
				consignmentData.setEntries(consignmentEntryDataList);
				consignments.add(consignmentData);
			}
			target.setConsignments(consignments);
		}

		catch (final Exception e)
		{
			LOG.warn(String.format(e.getMessage()));
		}

	}


	/**
	 * @param orderEntryData
	 *
	 */
	private void populateOrderEntryData(final CentralOrderDetailsResponse source, final OrderEntryData orderEntryData)
	{
		for (final OrderItem orderItem : source.getOrderItems())
		{
			final SubscriptionItemPrice subItemPrice = orderItem.getItemPrice().getItemPriceAspectData().getSubscriptionItemPrice();

			if ((subItemPrice != null && !subItemPrice.getPriceTotals().isEmpty())
					&& (orderItem.getProduct().getExternalSystemReferences().get(0).getExternalId()
							.equalsIgnoreCase(orderEntryData.getProduct().getSubscriptionCode())))
			{
				populateOrderTotal(subItemPrice, orderEntryData, orderItem, source);
			}

		}

	}


	/**
	 * @param source
	 *
	 */
	private void populateOrderTotal(final SubscriptionItemPrice subItemPrice, final OrderEntryData orderEntryData,
			final OrderItem orderItem, final CentralOrderDetailsResponse source)
	{
		for (final PriceTotals priceTotal : subItemPrice.getPriceTotals())
		{
			if (SapcentralorderfacadesConstants.CATEGORY_ONETIME.equals(priceTotal.getCategory()))
			{
				orderEntryData.setBasePrice(
						getPriceDataFactory().create(PriceDataType.BUY, BigDecimal.valueOf(priceTotal.getOriginalAmount()),
						source.getMarket().getCurrency()));
				orderEntryData.setTotalPrice(getPriceDataFactory().create(PriceDataType.BUY,
						BigDecimal.valueOf(priceTotal.getFinalAmount()),
						source.getMarket().getCurrency()));

			}
			orderEntryData.setQuantity(Long.valueOf(orderItem.getQuantity().getValue()));
		}

	}



}