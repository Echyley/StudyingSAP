/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.populator.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.sapcentralorderfacades.order.mapper.SapCpiOrderOutboundAdditionalAttributeMapper;
import com.sap.sapcentralorderfacades.order.populator.SapCpiOrderOutboundAdditionalAttributePopulator;


/**
 * DefaultCentralOrderOutboundAdditionalAttributePopulator
 */
public class DefaultCentralOrderOutboundAdditionalAttributePopulator
		implements SapCpiOrderOutboundAdditionalAttributePopulator
{

	private static final Logger LOGGER = LoggerFactory
			.getLogger(DefaultCentralOrderOutboundAdditionalAttributePopulator.class);
	private List<SapCpiOrderOutboundAdditionalAttributeMapper> centralOrderAdditionalAttributeMappers;


	@Override
	public void addAdditionalAttributesToSapCpiOrder(final OrderModel orderModel,
			final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel)
	{
		if (!CollectionUtils.isEmpty(getCentralOrderAdditionalAttributeMappers()))
		{
			LOGGER.info("centralOrderAdditionalAttributeMappers list is not empty");
			for(final SapCpiOrderOutboundAdditionalAttributeMapper additionalAttributeMapper : getCentralOrderAdditionalAttributeMappers())
			{
				additionalAttributeMapper.mapAdditionalAttributes(orderModel, sapCpiOutboundOrderModel);
			}
		}
	}


	public List<SapCpiOrderOutboundAdditionalAttributeMapper> getCentralOrderAdditionalAttributeMappers()
	{
		return centralOrderAdditionalAttributeMappers;
	}

	public void setCentralOrderAdditionalAttributeMappers(
			final List<SapCpiOrderOutboundAdditionalAttributeMapper> centralOrderAdditionalAttributeMappers)
	{
		this.centralOrderAdditionalAttributeMappers = centralOrderAdditionalAttributeMappers;
	}




}
