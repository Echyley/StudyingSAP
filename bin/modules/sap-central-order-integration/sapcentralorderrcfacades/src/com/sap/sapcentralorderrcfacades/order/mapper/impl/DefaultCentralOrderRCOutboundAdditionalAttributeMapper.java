/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderrcfacades.order.mapper.impl;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.subscriptionservices.model.SubscriptionPricePlanModel;
import de.hybris.platform.subscriptionservices.price.SubscriptionCommercePriceService;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.sap.sapcentralorderfacades.order.mapper.SapCpiOrderOutboundAdditionalAttributeMapper;
import com.sap.sapcentralorderrcfacades.constants.SapcentralorderrcfacadesConstants;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 * DefaultCentralOrderRCOutboundAdditionalAttributeMapper
 */
public class DefaultCentralOrderRCOutboundAdditionalAttributeMapper implements SapCpiOrderOutboundAdditionalAttributeMapper
{

	private static final Logger LOG = Logger.getLogger(DefaultCentralOrderRCOutboundAdditionalAttributeMapper.class);
	private SubscriptionCommercePriceService commercePriceService;
	private CMSSiteService cmsSiteService;
	private CommonI18NService commonI18NService;
	private CoConfigurationService configurationService;

	@Override
	public void mapAdditionalAttributes(final OrderModel orderModel, final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel)
	{
		if (getConfigurationService().isCoActiveFromBaseStore(orderModel))
		{
			for (final AbstractOrderEntryModel abstractOrderEntryModel : orderModel.getEntries())
			{
				//This confirms the product is Subscription Product
				if (null != abstractOrderEntryModel.getProduct().getSubscriptionCode())
				{
					populateSubscriptionAttributes(orderModel, sapCpiOutboundOrderModel, abstractOrderEntryModel);
				}
			}
		}

	}

	protected void populateSubscriptionAttributes(final OrderModel orderModel,
			final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel, final AbstractOrderEntryModel abstractOrderEntryModel)
	{
		for (final SAPCpiOutboundOrderItemModel sapCpiOutboundOrderItemModel : sapCpiOutboundOrderModel
				.getSapCpiOutboundOrderItems())
		{
			if (sapCpiOutboundOrderItemModel.getProductCode().equals(abstractOrderEntryModel.getProduct().getCode()))
			{
				//set subscription product code
				sapCpiOutboundOrderItemModel.setProductCode(abstractOrderEntryModel.getProduct().getSubscriptionCode());
				try
				{
					getCmsSiteService().setCurrentSiteAndCatalogVersions((CMSSiteModel) orderModel.getSite(), true);
				}
				catch (final CMSItemNotFoundException e)
				{
					LOG.error(e);
				}
				getCommonI18NService().setCurrentCurrency(orderModel.getCurrency());

				final SubscriptionPricePlanModel pricePlanModel = getCommercePriceService()
						.getSubscriptionPricePlanForProduct(abstractOrderEntryModel.getProduct());
				if (pricePlanModel != null)
				{
					//set price plan Id
					sapCpiOutboundOrderItemModel.setPricePlanId(pricePlanModel.getPricePlanId());
				}
				final TimeZone tzUTC = TimeZone.getTimeZone(ZoneId.of(SapcentralorderrcfacadesConstants.UTC)); // take current time
				final String validFromDate = ZonedDateTime.now().withZoneSameInstant(tzUTC.toZoneId())
						.format(SapcentralorderrcfacadesConstants.ISO8601_FORMATTER);
				//set valid from
				sapCpiOutboundOrderItemModel.setSubscriptionValidFrom(validFromDate);
				break;

			}
		}
	}

	public SubscriptionCommercePriceService getCommercePriceService()
	{
		return commercePriceService;
	}


	public void setCommercePriceService(final SubscriptionCommercePriceService commercePriceService)
	{
		this.commercePriceService = commercePriceService;
	}

	public CMSSiteService getCmsSiteService()
	{
		return cmsSiteService;
	}

	public void setCmsSiteService(final CMSSiteService cmsSiteService)
	{
		this.cmsSiteService = cmsSiteService;
	}

	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	public CoConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	public void setConfigurationService(final CoConfigurationService configurationService)
	{
		this.configurationService = configurationService;
	}


}
