/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.populator.impl;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderListResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PriceTotals;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants;


/**
 *
 */
public class DefaultCentralOrderHistoryPopulator implements Populator<CentralOrderListResponse, OrderHistoryData>
{
	/**
	 *
	 */

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCentralOrderHistoryPopulator.class);

	private PriceDataFactory priceDataFactory;
	private Map orderStatusDisplayMap;
	private String dateFormat = SapcentralorderfacadesConstants.DEFAULT_DATE_FORMAT;




	/**
	 * @return the orderStatusDisplayMap
	 */
	public Map getOrderStatusDisplayMap()
	{
		return orderStatusDisplayMap;
	}


	/**
	 * @param orderStatusDisplayMap
	 *           the orderStatusDisplayMap to set
	 */
	public void setOrderStatusDisplayMap(final Map orderStatusDisplayMap)
	{
		this.orderStatusDisplayMap = orderStatusDisplayMap;
	}


	/**
	 * @return the dateFormat
	 */
	public String getDateFormat()
	{
		return dateFormat;
	}


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


	/**
	 * @param dateFormat
	 *           the dateFormat to set
	 */
	public void setDateFormat(final String dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	/**
	 *
	 * @param source
	 * @param target
	 */
	public void populate(final CentralOrderListResponse source, final OrderHistoryData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		try
		{
			final String orderStatus = getStatusDisplay(source.getStatus(), getOrderStatusDisplayMap());
			target.setStatus(OrderStatus.valueOf(orderStatus));
			target.setStatusDisplay(orderStatus);
			target.setGuid(source.getId());


			if (source.getPrecedingDocument() != null)
			{
				target.setCode(source.getPrecedingDocument().getExternalSystemReference().getExternalNumber());
			}

			if (source.getMetadata() != null)
			{
				final DateFormat dateFormatter = new SimpleDateFormat(getDateFormat());
				Date pricingDate;
				Date pricingFormattedDate;

				pricingDate = new SimpleDateFormat(SapcentralorderfacadesConstants.DATE_FORMAT)
						.parse(source.getMetadata().getCreatedAt().substring(0, SapcentralorderfacadesConstants.DATE_SIZE));
				pricingFormattedDate = dateFormatter.parse(dateFormatter.format(pricingDate));
				target.setPlaced(pricingFormattedDate);
			}

			for (final PriceTotals priceTotal : source.getPrices())
			{				
				if (StringUtils.isNotBlank(priceTotal.getCategory())
						&& SapcentralorderfacadesConstants.CATEGORY_ONETIME.equalsIgnoreCase(priceTotal.getCategory()))
			{
				final BigDecimal totalPrice = BigDecimal.valueOf(priceTotal.getTotal().getFinalAmount());
				target.setTotal(getPriceDataFactory().create(PriceDataType.BUY, totalPrice, source.getMarket().getCurrency()));

			}
		}
		}
		catch (final ParseException e)
		{
			LOG.warn(String.format(e.getMessage()));
		}

	}


	protected String getStatusDisplay(final String orderStatus, final Map<String, String> orderDisplayStatusMap)
	{
		return orderDisplayStatusMap.get(orderStatus);

	}


}
