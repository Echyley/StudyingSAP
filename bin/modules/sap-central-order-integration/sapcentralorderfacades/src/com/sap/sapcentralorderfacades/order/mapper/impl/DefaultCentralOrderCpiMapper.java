/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.mapper.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiConfig;
import de.hybris.platform.sap.sapcpiadapter.data.SapCpiOrder;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.service.SapCpiOrderConversionService;
import de.hybris.platform.sap.sapcpiorderexchange.service.impl.SapCpiOmmOrderMapperService;

import com.sap.sapcentralorderfacades.order.populator.SapCpiOrderOutboundAdditionalAttributePopulator;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 * DefaultCentralOrderCpiMapper
 */
public class DefaultCentralOrderCpiMapper extends SapCpiOmmOrderMapperService
{
	private SapCpiOrderConversionService sapCpiOrderConversionService;
	private SapCpiOrderOutboundAdditionalAttributePopulator sapCpiOrderOutboundAdditionalAttributePopulator;
	private CoConfigurationService configurationService;


	/**
	 * Performs mapping from source to target.
	 *
	 * @param orderModel
	 *           Order Model
	 * @param sapCpiOutboundOrderModel
	 *           SAP CPI Outbound Order Model
	 */
	@Override
	public void map(final OrderModel orderModel, final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel)
	{
		mapSapCpiOrderToSAPCpiOrderOutbound(getSapCpiOrderConversionService().convertOrderToSapCpiOrder(orderModel),
				sapCpiOutboundOrderModel, orderModel);

		sapCpiOrderOutboundAdditionalAttributePopulator.addAdditionalAttributesToSapCpiOrder(orderModel, sapCpiOutboundOrderModel);

	}

	protected void mapSapCpiOrderToSAPCpiOrderOutbound(final SapCpiOrder sapCpiOrder,
			final SAPCpiOutboundOrderModel sapCpiOutboundOrder, final OrderModel orderModel)
	{

		sapCpiOutboundOrder.setOrderId(sapCpiOrder.getOrderId());
		sapCpiOutboundOrder.setBaseStoreUid(sapCpiOrder.getBaseStoreUid());
		sapCpiOutboundOrder.setCreationDate(sapCpiOrder.getCreationDate());
		sapCpiOutboundOrder.setCurrencyIsoCode(sapCpiOrder.getCurrencyIsoCode());
		sapCpiOutboundOrder.setPaymentMode(sapCpiOrder.getPaymentMode());
		sapCpiOutboundOrder.setDeliveryMode(sapCpiOrder.getDeliveryMode());
		sapCpiOutboundOrder.setChannel(sapCpiOrder.getChannel());
		sapCpiOutboundOrder.setPurchaseOrderNumber(sapCpiOrder.getPurchaseOrderNumber());
		sapCpiOutboundOrder.setTransactionType(sapCpiOrder.getTransactionType());
		sapCpiOutboundOrder.setSalesOrganization(sapCpiOrder.getSalesOrganization());
		sapCpiOutboundOrder.setDistributionChannel(sapCpiOrder.getDistributionChannel());
		sapCpiOutboundOrder.setDivision(sapCpiOrder.getDivision());
		sapCpiOutboundOrder.setShippingCondition(sapCpiOrder.getShippingCondition());

		sapCpiOutboundOrder.setSapCpiConfig(mapOrderConfigInfo(sapCpiOrder.getSapCpiConfig(), orderModel));
		sapCpiOutboundOrder.setSapCpiOutboundOrderItems(mapOrderItems(sapCpiOrder.getSapCpiOrderItems()));
		sapCpiOutboundOrder.setSapCpiOutboundPartnerRoles(mapOrderPartners(sapCpiOrder.getSapCpiPartnerRoles()));
		sapCpiOutboundOrder.setSapCpiOutboundAddresses(mapOrderAddresses(sapCpiOrder.getSapCpiOrderAddresses()));
		sapCpiOutboundOrder.setSapCpiOutboundPriceComponents(mapOrderPrices(sapCpiOrder.getSapCpiOrderPriceComponents()));
		sapCpiOutboundOrder.setSapCpiOutboundCardPayments(mapCreditCards(sapCpiOrder.getSapCpiCreditCardPayments()));

	}

	protected SAPCpiOutboundConfigModel mapOrderConfigInfo(final SapCpiConfig sapCpiConfig, final OrderModel orderModel)
	{
		if (getConfigurationService().isCoActiveFromBaseStore(orderModel))
		{
			return new SAPCpiOutboundConfigModel();
		}
		else
		{
			return super.mapOrderConfigInfo(sapCpiConfig);
		}
	}

	@Override
	public SapCpiOrderConversionService getSapCpiOrderConversionService()
	{
		return sapCpiOrderConversionService;
	}

	@Override
	public void setSapCpiOrderConversionService(final SapCpiOrderConversionService sapCpiOrderConversionService)
	{
		this.sapCpiOrderConversionService = sapCpiOrderConversionService;
	}

	public SapCpiOrderOutboundAdditionalAttributePopulator getSapCpiOrderOutboundAdditionalAttributePopulator()
	{
		return sapCpiOrderOutboundAdditionalAttributePopulator;
	}

	public void setSapCpiOrderOutboundAdditionalAttributePopulator(
			final SapCpiOrderOutboundAdditionalAttributePopulator sapCpiOrderOutboundAdditionalAttributePopulator)
	{
		this.sapCpiOrderOutboundAdditionalAttributePopulator = sapCpiOrderOutboundAdditionalAttributePopulator;
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
